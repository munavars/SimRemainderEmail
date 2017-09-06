package ytc.com.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ytc.com.common.PersistenceManager;
import ytc.com.common.PropertyManager;
import ytc.com.constant.BatchConstant;
import ytc.com.dal.IDataAccessLayer;
import ytc.com.dal.jpa.BaseDaoImpl;
import ytc.com.dal.modal.DalEmployee;
import ytc.com.dal.modal.DalPricingHeader;
import ytc.com.dal.modal.DalProgramDetail;
import ytc.com.dal.modal.DalWorkflowStatus;
import ytc.com.mail.IPricingEmailService;
import ytc.com.mail.IPricingWorkflowService;
import ytc.com.mail.IProgramEmailService;
import ytc.com.mail.IProgramWorkflowService;
import ytc.com.mail.impl.EmailServiceImpl;
import ytc.com.mail.impl.PricingEmailServiceImpl;
import ytc.com.mail.impl.PricingWorkflowServiceImpl;
import ytc.com.mail.impl.ProgramEmailServiceImpl;
import ytc.com.mail.impl.ProgramWorkflowServiceImpl;
import ytc.com.modal.EmailInfo;
import ytc.com.modal.EmailTableContent;
import ytc.com.service.IYTCProgramService;
import ytc.com.service.helper.EmailHelper;

public class YTCProgramServiceImpl extends PersistenceManager implements IYTCProgramService{

	private static final Logger LOGGER = LoggerFactory.getLogger(YTCProgramServiceImpl.class);
	
	IDataAccessLayer baseDao = new BaseDaoImpl();
	
	EmailServiceImpl emailService = new EmailServiceImpl();
	
	IProgramWorkflowService programWorkflowService = new ProgramWorkflowServiceImpl();
	
	IPricingWorkflowService pricingWorkflowService = new PricingWorkflowServiceImpl();
	
	IProgramEmailService  programEmailService = new ProgramEmailServiceImpl();
	
	IPricingEmailService pricingEmailService = new PricingEmailServiceImpl();
	
	@Override
	public boolean prepareDataAndTriggerEmail(String environment) throws Exception {
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : prepareDataAndTriggerEmail method starts...");
		boolean returnValue = true;
		try{
			Map<String, Map<String, List<EmailTableContent>>> userData = null;
			/**1. Get the list of pending approvals from WORKFLOW_STATUS Table.*/
			Properties properites = PropertyManager.getInstance().getPropertyInstance(environment);
			List<DalWorkflowStatus> dalWorkflowStatusList = baseDao.getListFromNamedQuery("DalWorkflowStatus.getPendingRecords");
			
			userData = prepareDataForMailTrigger(dalWorkflowStatusList, properites);
			
			emailService.sendEmailData(userData);
			
			/** Update the counter for the mail records for which mails were sent.*/
			updateTheCounterValue(dalWorkflowStatusList, properites);
		}
		catch (Exception e){
			LOGGER.error(YTCProgramServiceImpl.class.getName() + " : prepareDataAndTriggerEmail method ends...");
			throw e;
		}
		finally{
			PersistenceManager.getInstance().closeEntityManagerFactoryInstance();
		}
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : prepareDataAndTriggerEmail method ends...");
		return returnValue;
	}

	public void updateTheCounterValue(List<DalWorkflowStatus> dalWorkflowStatusList, Properties properites) throws Exception {
		Set<Integer> programIdSet = new HashSet<Integer>();
		Set<Integer> pricingIdSet = new HashSet<Integer>();
		String mailThreshold = properites.getProperty("mail.skip.threshold");
		if(dalWorkflowStatusList != null && !dalWorkflowStatusList.isEmpty() && mailThreshold != null){
			for(DalWorkflowStatus dalWorkflowStatus : dalWorkflowStatusList){
				Integer counter = 0;
				if(dalWorkflowStatus.getMailCount() != null){
					counter = dalWorkflowStatus.getMailCount();
				}
				counter++;
				if(dalWorkflowStatus.getDalProgramDetailWf() != null && Integer.valueOf(mailThreshold) < counter){
					programIdSet.add(dalWorkflowStatus.getDalProgramDetailWf().getId());
				}
				else if(dalWorkflowStatus.getDalPricingHeaderWf() != null && Integer.valueOf(mailThreshold) < counter){
					pricingIdSet.add(dalWorkflowStatus.getDalPricingHeaderWf().getId());
				}
				dalWorkflowStatus.setMailCount(counter);
			}
			baseDao.update(dalWorkflowStatusList);
		}
		
		/**
		 * If there are any Pricing or program id's for which workflow status has to be skipped to next level.
		 */
		updateApproverDetails(programIdSet, pricingIdSet);
		
	}

	public void updateApproverDetails(Set<Integer> programIdSet, Set<Integer> pricingIdSet) throws Exception {
		/**
		 * Check for program id update.
		 */
		DalEmployee currentUser = null;
		Properties properties = null;
		if(programIdSet != null && !programIdSet.isEmpty()){
			/**
			 * Get the list of ProgramDetails entity and iterate each element and update its workflow status.
			 */
			List<Integer> programList = new ArrayList<Integer>(programIdSet);
			currentUser = baseDao.getById(DalEmployee.class, 1001);
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("programIds", programList);
			List<DalProgramDetail> dalProgramDetailList = baseDao.getListFromNamedQueryWithParameter("DalProgramDetail.getProgramDetails", inputMap);
			if(dalProgramDetailList != null && !dalProgramDetailList.isEmpty()){
				properties = PropertyManager.getInstance().getPropertyInstance(null);
				String fromAddress = properties.getProperty("mail.from.address");
				String url = properties.getProperty("mail.program.atf.url");
				String nonAtfUrl = properties.getProperty("mail.program.nonatf.url");
				for(DalProgramDetail dalProgramDetail : dalProgramDetailList){
					EmailInfo emailInfo = new EmailInfo();
					programWorkflowService.updateWorkflowDetails(dalProgramDetail, currentUser, emailInfo);
					baseDao.update(dalProgramDetail);
					if(BatchConstant.CALCULATED_PROGRAM_TYPE_CODE.equals(dalProgramDetail.getDalProgramType().getId())){
						emailInfo.setUrl(url);
					}
					else{
						emailInfo.setUrl(nonAtfUrl);
					}
					emailInfo.setFromAddress(fromAddress);
					programEmailService.sendEmailData(dalProgramDetail, emailInfo);
				}
			}			
		}
		
		if(pricingIdSet != null && !pricingIdSet.isEmpty()){
			/*
			 * Get the list of pricing details for the pricing id's for which workflow status has to be skipped.
			 * **/
			if(properties == null){
				properties = PropertyManager.getInstance().getPropertyInstance(null);	
			}
			List<Integer> pricingList = new ArrayList<Integer>(pricingIdSet);
			if(currentUser == null){
				currentUser = baseDao.getById(DalEmployee.class, 1001);
			}
			Map<String, Object> inputMap = new HashMap<String, Object>();
			inputMap.put("pricingIds", pricingList);
			List<DalPricingHeader> dalPricingHeaderList = baseDao.getListFromNamedQueryWithParameter("DalPricingHeader.getPricingDetails", inputMap);
			if(dalPricingHeaderList != null && !dalPricingHeaderList.isEmpty()){
				String fromAddress = properties.getProperty("mail.from.address");
				String url = properties.getProperty("mail.pricing.url");
				for(DalPricingHeader dalPricingHeader : dalPricingHeaderList){
					EmailInfo emailInfo = new EmailInfo();
					pricingWorkflowService.updateWorkflowDetails(dalPricingHeader, currentUser, emailInfo);
					baseDao.update(dalPricingHeader);
					emailInfo.setUrl(url);
					emailInfo.setFromAddress(fromAddress);
					pricingEmailService.sendEmailData(dalPricingHeader, emailInfo);
				}
			}
		}
	}

	private Map<String, Map<String, List<EmailTableContent>>> prepareDataForMailTrigger(
			List<DalWorkflowStatus> dalWorkflowStatusList, Properties properties) {
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : prepareDataForMailTrigger method starts...");
		Map<String, Map<String, List<EmailTableContent>>> userData = new HashMap<String, Map<String, List<EmailTableContent>>>();
		String maxMailCount = properties.getProperty("mail.skip.threshold");
		if(dalWorkflowStatusList != null && !dalWorkflowStatusList.isEmpty() && maxMailCount != null){
			for(DalWorkflowStatus dalWorkflowStatus : dalWorkflowStatusList){
				if(dalWorkflowStatus.getDalProgramDetailWf() != null && 
						(dalWorkflowStatus.getMailCount() == null|| Integer.valueOf(maxMailCount) > dalWorkflowStatus.getMailCount()) ){
					if(userData.get(dalWorkflowStatus.getApprover().getEmail()) != null){
						Map<String, List<EmailTableContent>> newProgramMap = userData.get(dalWorkflowStatus.getApprover().getEmail());
						if(newProgramMap == null){
							newProgramMap = new HashMap<String, List<EmailTableContent>>();
							List<EmailTableContent> programIdList = new ArrayList<EmailTableContent>();
							newProgramMap.put(BatchConstant.PROGRAM, programIdList);
							userData.put(dalWorkflowStatus.getApprover().getEmail(), newProgramMap);
						}
						List<EmailTableContent> programIdList = newProgramMap.get(BatchConstant.PROGRAM);
						if(programIdList == null){
							programIdList = new ArrayList<EmailTableContent>();
							newProgramMap.put(BatchConstant.PROGRAM, programIdList);
							userData.put(dalWorkflowStatus.getApprover().getEmail(), newProgramMap);
						}
						programIdList.add(getDetailsFromProgram(dalWorkflowStatus));
					}
					else{
						Map<String, List<EmailTableContent>> newProgramMap = new HashMap<String, List<EmailTableContent>>();
						List<EmailTableContent> programIdList = new ArrayList<EmailTableContent>();
						programIdList.add(getDetailsFromProgram(dalWorkflowStatus));
						newProgramMap.put(BatchConstant.PROGRAM, programIdList);
						userData.put(dalWorkflowStatus.getApprover().getEmail(), newProgramMap);
					}	
				}
				else if (dalWorkflowStatus.getDalPricingHeaderWf() != null && 
						(dalWorkflowStatus.getMailCount() == null || Integer.valueOf(maxMailCount) > dalWorkflowStatus.getMailCount()) ){
					if(userData.get(dalWorkflowStatus.getApprover().getEmail()) != null){
						Map<String, List<EmailTableContent>> newProgramMap = userData.get(dalWorkflowStatus.getApprover().getEmail());
						if(newProgramMap == null){
							newProgramMap = new HashMap<String, List<EmailTableContent>>();
							List<EmailTableContent> pricingIdList = new ArrayList<EmailTableContent>();
							newProgramMap.put(BatchConstant.PRICING, pricingIdList);
							userData.put(dalWorkflowStatus.getApprover().getEmail(), newProgramMap);
						}
						List<EmailTableContent> pricingIdList = newProgramMap.get(BatchConstant.PRICING);
						if(pricingIdList == null){
							pricingIdList = new ArrayList<EmailTableContent>();
							newProgramMap.put(BatchConstant.PRICING, pricingIdList);
							userData.put(dalWorkflowStatus.getApprover().getEmail(), newProgramMap);
						}
						pricingIdList.add(getDetailsFromPricing(dalWorkflowStatus));
					}
					else{
						Map<String, List<EmailTableContent>> newProgramMap = new HashMap<String, List<EmailTableContent>>();
						List<EmailTableContent> pricingIdList = new ArrayList<EmailTableContent>();
						pricingIdList.add(getDetailsFromPricing(dalWorkflowStatus));
						newProgramMap.put(BatchConstant.PRICING, pricingIdList);
						userData.put(dalWorkflowStatus.getApprover().getEmail(), newProgramMap);
					}	
				}
			}
		}
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : prepareDataForMailTrigger method ends...");
		return userData;
	}
	
	public EmailTableContent getDetailsFromProgram(DalWorkflowStatus dalWorkflowStatus){
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : getDetailsFromProgram method starts...");
		EmailTableContent emailTableContent = null;
		
		if(dalWorkflowStatus != null && dalWorkflowStatus.getDalProgramDetailWf() != null){
			emailTableContent = new EmailTableContent();
			emailTableContent.setId(dalWorkflowStatus.getDalProgramDetailWf().getId());
			emailTableContent.setProgramName(dalWorkflowStatus.getDalProgramDetailWf().getProgramMaster().getProgram());
			emailTableContent.setProgramType(dalWorkflowStatus.getDalProgramDetailWf().getDalProgramType().getType());
			emailTableContent.setBusinessUnit(getBUDescription(dalWorkflowStatus.getDalProgramDetailWf().getDalProgramHeader().getCustomer().getBu()));
			emailTableContent.setCreatedBy(dalWorkflowStatus.getDalProgramDetailWf().getCreatedBy().getFirtName() + " " 
											+ dalWorkflowStatus.getDalProgramDetailWf().getCreatedBy().getLastName());
			emailTableContent.setStatus(dalWorkflowStatus.getApprovalStatus().getType());
			emailTableContent.setSubmittedOn(EmailHelper.convertDateToString(dalWorkflowStatus.getCreatedDate().getTime(), BatchConstant.DATE_FORMAT_TIME));
		}
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : getDetailsFromProgram method ends...");
		return emailTableContent;
	}

	public EmailTableContent getDetailsFromPricing(DalWorkflowStatus dalWorkflowStatus){
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : getDetailsFromPricing method starts...");
		EmailTableContent emailTableContent = null;
		
		if(dalWorkflowStatus != null && dalWorkflowStatus.getDalPricingHeaderWf() != null){
			emailTableContent = new EmailTableContent();
			emailTableContent.setId(dalWorkflowStatus.getDalPricingHeaderWf().getId());
			emailTableContent.setCustomerNumber(dalWorkflowStatus.getDalPricingHeaderWf().getCustomer().getCustomerNumber());
			emailTableContent.setCustomerName(dalWorkflowStatus.getDalPricingHeaderWf().getCustomer().getCustomerName());
			emailTableContent.setBusinessUnit(getBUDescription(dalWorkflowStatus.getDalPricingHeaderWf().getCustomer().getBu()));
			emailTableContent.setCreatedBy(dalWorkflowStatus.getDalPricingHeaderWf().getCreatedBy().getFirtName() + " " 
											+ dalWorkflowStatus.getDalPricingHeaderWf().getCreatedBy().getLastName());
			emailTableContent.setStatus(dalWorkflowStatus.getApprovalStatus().getType());
			emailTableContent.setSubmittedOn(EmailHelper.convertDateToString(dalWorkflowStatus.getCreatedDate().getTime(), BatchConstant.DATE_FORMAT_TIME));
		}
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : getDetailsFromPricing method ends...");
		return emailTableContent;
	}
	
	public String getBUDescription(String code){
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : getDetailsFromPricing method starts...");
		String description = null;
		if(code != null){
			if("T".equalsIgnoreCase(code)){
				description =  "COMMERCIAL";
			}
			else if("P".equalsIgnoreCase(code)){
				description =  "CONSUMER";
			}
			else if("O".equalsIgnoreCase(code)){
				description =  "OTR";
			}
		}
		LOGGER.info(YTCProgramServiceImpl.class.getName() + " : getDetailsFromPricing method ends...");
		return description;
	}
}

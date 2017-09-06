package ytc.com.mail.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ytc.com.common.comparator.WorkflowStatusComparatorByModifiedDate;
import ytc.com.constant.BatchConstant;
import ytc.com.constant.EmailConstant;
import ytc.com.dal.modal.DalEmployee;
import ytc.com.dal.modal.DalPricingHeader;
import ytc.com.dal.modal.DalWorkflowStatus;
import ytc.com.enums.BusinessUnitDescriptionEnum;
import ytc.com.mail.IPricingEmailService;
import ytc.com.modal.EmailDetails;
import ytc.com.modal.EmailInfo;
import ytc.com.service.helper.EmailHelper;

/**
 * Class : PricingEmailServiceImpl
 * Purpose : This class prepare the data for the mail to be send and calls the email connector class.
 * @author Cognizant.
 *
 */
public class PricingEmailServiceImpl implements IPricingEmailService{
	
	ytc.com.mail.IYTCMailConnectorService mailService = new YTCMailConnectorServiceImpl();
	
	@Override
	public void sendEmailData(DalPricingHeader dalPricingHeader, EmailInfo emailInfo) throws Exception{
		EmailDetails emailDetails = new EmailDetails();
		
		emailDetails.setFromAddress(emailInfo.getFromAddress());
		buildToAndCCAddress(emailDetails, dalPricingHeader, emailInfo);
		buildSubject(emailDetails, dalPricingHeader);
		buildText(emailDetails, dalPricingHeader, emailInfo);
		
		mailService.sendEmail(emailDetails);
	}

	
	private void buildText(EmailDetails emailDetails, DalPricingHeader dalPricingHeader, EmailInfo emailInfo) {
		String bodyContent = null;
		if(BatchConstant.PENDING_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
			bodyContent = buildContentForApprover(emailDetails, dalPricingHeader, emailInfo);
		}
		/**Below things has to be modified. For now, gng to test pending status first*/
		else if(BatchConstant.APPROVED_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
/*			if(ProgramConstant.USER_LEVEL_2.equals(programHeader.getProgramButton().getUserLevel())){
				bodyContent = buildContentForApprover(emailDetails, dalProgramDetail, programHeader);	
			}
			else{*/
				/**If control is here, then approval is done by TBP user who is level 3 user.*/
				bodyContent = buildApprovedMailContent(emailDetails, dalPricingHeader, emailInfo);
			/*}*/
		}
		/*else if(ProgramConstant.REJECTED_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
			bodyContent = buildRejectedMailContent(emailDetails, dalPricingHeader, pricingHeader);
		}*/
		emailDetails.setText(bodyContent);	
	}

	private String buildApprovedMailContent(EmailDetails emailDetails, DalPricingHeader dalPricingHeader,
			EmailInfo emailInfo) {
		StringBuilder html = new StringBuilder();
		
		html.append(EmailConstant.HTML_BEGIN);
		html.append(String.format(EmailConstant.HTML_BODY_GREETING, emailDetails.getToNames() + EmailConstant.COMMA) );
		html.append(String.format(EmailConstant.ACTION_TAKEN_SYSTEM_APPROVED, emailInfo.getPreviousUserName()));
		html.append(String.format(EmailConstant.PRICING_HTML_BODY_ID, dalPricingHeader.getId()));
		html.append(String.format(EmailConstant.HTML_BODY_COMMENTS, EmailConstant.PRICING_AUTO_APPROVED_COMMENT));

		String link = null;
        if(emailInfo.getUrl() != null){
        	link = EmailConstant.LINK_BEGIN + emailInfo.getUrl()+ dalPricingHeader.getId() + EmailConstant.LINK_END;

        }
		html.append(link);
		html.append(EmailConstant.HTML_END);
		
		return html.toString();
	}

	private String buildContentForApprover(EmailDetails emailDetails, DalPricingHeader dalPricingHeader, EmailInfo emailInfo) {
		StringBuilder html = new StringBuilder();
		
		html.append(EmailConstant.HTML_BEGIN);
		html.append(String.format(EmailConstant.HTML_BODY_GREETING, emailDetails.getToNames() + EmailConstant.COMMA) );
		html.append(String.format(EmailConstant.ACTION_TAKEN_SYSTEM, emailInfo.getPreviousUserName()));
		html.append(String.format(EmailConstant.PRICING_HTML_BODY_ID, dalPricingHeader.getId()));
		html.append(String.format(EmailConstant.HTML_BODY_COMMENTS, EmailConstant.PRICING_AUTO_APPROVED_COMMENT));
		html.append(String.format(EmailConstant.HTML_BODY_ACTION_TO_BE_TAKEN, 
				EmailConstant.APPROVAL_OR_REJECT));
		/*html.append(EmailConstant.HTML_BODY_LINK_TITLE);*/
		String link = null;
        if(emailInfo.getUrl() != null){
        	link = EmailConstant.LINK_BEGIN + emailInfo.getUrl()+ dalPricingHeader.getId() + EmailConstant.LINK_END;

        }
    	
		html.append(link);
		html.append(EmailConstant.HTML_END);
		
		return html.toString();
	}

	private void buildSubject(EmailDetails emailDetails, DalPricingHeader dalPricingHeader) {
		String subject = null;
		if(BatchConstant.PENDING_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
			subject = String.format(EmailConstant.PRICING_SUBJECT_PENDING, dalPricingHeader.getId());
		}
		else if(BatchConstant.APPROVED_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
			/**If control is here, then approval is done by TBP user who is level 3 user.*/
			subject = String.format(EmailConstant.PRICING_SUBJECT_APPROVAL, dalPricingHeader.getId());
		}
		/*else if(BatchConstant.REJECTED_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
			subject = String.format(EmailConstant.PRICING_SUBJECT_REJECTED, dalPricingHeader.getId());
		}*/
		emailDetails.setSubject(subject);
	}

	/**
	 * Method to build the to addresses.
	 * @param emailDetails emailDetails
	 * @param dalProgramDetail dalProgramDetail
	 * @param programHeader programHeader
	 */
	private void buildToAndCCAddress(EmailDetails emailDetails, DalPricingHeader dalPricingHeader, EmailInfo emailInfo) {
		List<String> toEmailIdList = new ArrayList<String>();
		List<String> ccEmailIdList = null;
		
		if(dalPricingHeader != null){
			List<DalWorkflowStatus> dalWorkflowStatusList = dalPricingHeader.getDalWorkflowStatusForPricingList();
			Collections.sort(dalWorkflowStatusList, new WorkflowStatusComparatorByModifiedDate());
			if(dalWorkflowStatusList != null){
				if(BatchConstant.PENDING_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
					int size = dalWorkflowStatusList.size();
					DalWorkflowStatus dalWorkflowStatus = dalWorkflowStatusList.get(size-1);
					toEmailIdList.add(dalWorkflowStatus.getApprover().getEmail());
					appendToName(emailDetails, dalWorkflowStatus.getApprover());
				}
				else if(BatchConstant.APPROVED_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())){
					toEmailIdList.add(dalPricingHeader.getCreatedBy().getEmail());
					appendToName(emailDetails, dalPricingHeader.getCreatedBy());
					for(DalWorkflowStatus workflowStatus : dalWorkflowStatusList){
						/**Last updated person mail id should not be appended to the To list, so not equal to check is added in the below 
						 * condition. */
						if(BatchConstant.APPROVED_STATUS_CODE.equals(workflowStatus.getApprovalStatus().getId()) &&
								!dalPricingHeader.getModifiedBy().getId().equals(workflowStatus.getApprover().getId())){
							if(!toEmailIdList.contains(workflowStatus.getApprover().getEmail())){
								toEmailIdList.add(workflowStatus.getApprover().getEmail());
								appendToName(emailDetails, workflowStatus.getApprover());
							}
						}
					}
				}
				if(BusinessUnitDescriptionEnum.COMMERCIAL.getBusinessUnitDescription().equalsIgnoreCase(dalPricingHeader.getCustomer().getBu())){
					if(ccEmailIdList == null){
						ccEmailIdList = new ArrayList<String>();
					}
					if(BatchConstant.ONE.equals(dalPricingHeader.getSbm())){
						ccEmailIdList.add(EmailConstant.COMMERCIAL_SBM_PRICING_MAIL_GROUP);
					}
					else{
						ccEmailIdList.add(EmailConstant.COMMERCIAL_PRICING_MAIL_GROUP);
					}
				}
				else if(BusinessUnitDescriptionEnum.CONSUMER.getBusinessUnitCode().equalsIgnoreCase(dalPricingHeader.getCustomer().getBu())){
					if(ccEmailIdList == null){
						ccEmailIdList = new ArrayList<String>();
					}
					ccEmailIdList.add(EmailConstant.CONSUMER_PRICING_MAIL_GROUP);
				}
				else if(BusinessUnitDescriptionEnum.OTR.getBusinessUnitCode().equalsIgnoreCase(dalPricingHeader.getCustomer().getBu())){
					if(ccEmailIdList == null){
						ccEmailIdList = new ArrayList<String>();
					}
					ccEmailIdList.add(EmailConstant.OTR_PRICING_MAIL_GROUP);
				}
				if(emailInfo != null && emailInfo.getPreviousUserEmailId() != null){
					ccEmailIdList = new ArrayList<String>();
					ccEmailIdList.add(emailInfo.getPreviousUserEmailId());
				}
			}
			emailDetails.setToAddress(toEmailIdList);
			emailDetails.setCcAddress(ccEmailIdList);
		}
	}

	/**
	 * Method to get the cc name appended for the required user.	
	 * @param emailDetails  emailDetails
	 * @param approver approver
	 */
	private void appendToName(EmailDetails emailDetails, DalEmployee employee) {
		if(emailDetails != null && employee != null){
			if(emailDetails.getToNames() == null){
				emailDetails.setToNames(new StringBuilder());
			}
			if(!emailDetails.getToNames().toString().isEmpty()){
				emailDetails.getToNames().append(BatchConstant.FORWARD_SLASH);
			}
			emailDetails.getToNames().append(EmailHelper.getName(employee));
		}
	}
}

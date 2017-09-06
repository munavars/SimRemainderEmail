package ytc.com.mail.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ytc.com.common.comparator.WorkflowStatusComparatorByModifiedDate;
import ytc.com.constant.BatchConstant;
import ytc.com.constant.EmailConstant;
import ytc.com.dal.modal.DalEmployee;
import ytc.com.dal.modal.DalProgramDetail;
import ytc.com.dal.modal.DalWorkflowStatus;
import ytc.com.mail.IProgramEmailService;
import ytc.com.modal.EmailDetails;
import ytc.com.modal.EmailInfo;
import ytc.com.service.helper.EmailHelper;


/**
 * Class : ProgramEmailServiceImpl
 * Purpose : This class prepare the data for the mail to be send and calls the email connector class.
 * @author Cognizant.
 *
 */
public class ProgramEmailServiceImpl implements IProgramEmailService{

	/*private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);*/
	
	ytc.com.mail.IYTCMailConnectorService mailService = new YTCMailConnectorServiceImpl();
	
	/**
	 * This method is used to prepare the email data and calls the appropriate method to send the details. 
	 * @throws Exception 
	 */
	public void sendEmailData(DalProgramDetail dalProgramDetail, EmailInfo emailInfo) throws Exception {
		EmailDetails emailDetails = new EmailDetails();
		
		emailDetails.setFromAddress(emailInfo.getFromAddress());
		buildToAndCCAddress(emailDetails, dalProgramDetail, emailInfo);
		buildSubject(emailDetails, dalProgramDetail);
		buildText(emailDetails, dalProgramDetail, emailInfo);
		
		mailService.sendEmail(emailDetails);
	}

	private void buildText(EmailDetails emailDetails, DalProgramDetail dalProgramDetail, EmailInfo emailInfo) {
		String bodyContent = null;
		if(BatchConstant.PENDING_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
			bodyContent = buildContentForApprover(emailDetails, dalProgramDetail, emailInfo);
		}
		/**Below things has to be modified. For now, gng to test pending status first*/
		else if(BatchConstant.APPROVED_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
/*			if(ProgramConstant.USER_LEVEL_2.equals(programHeader.getProgramButton().getUserLevel())){
				bodyContent = buildContentForApprover(emailDetails, dalProgramDetail, programHeader);	
			}
			else{*/
				/**If control is here, then approval is done by TBP user who is level 3 user.*/
				bodyContent = buildApprovedMailContent(emailDetails, dalProgramDetail, emailInfo);
			/*}*/
		}
		/*else if(ProgramConstant.REJECTED_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
			bodyContent = buildRejectedMailContent(emailDetails, dalProgramDetail, programHeader);
		}*/
		emailDetails.setText(bodyContent);	
	}

	private String buildApprovedMailContent(EmailDetails emailDetails, DalProgramDetail dalProgramDetail, EmailInfo emailInfo) {
		StringBuilder html = new StringBuilder();
		
		html.append(EmailConstant.HTML_BEGIN);
		html.append(String.format(EmailConstant.HTML_BODY_GREETING, emailDetails.getToNames() + EmailConstant.COMMA) );
		html.append(String.format(EmailConstant.ACTION_TAKEN_SYSTEM_APPROVED, emailInfo.getPreviousUserName()));
		html.append(String.format(EmailConstant.HTML_BODY_PROGRAM_ID, dalProgramDetail.getId()));
		html.append(String.format(EmailConstant.HTML_BODY_COMMENTS, EmailConstant.AUTO_APPROVED_COMMENT));
		/*html.append(String.format(EmailConstant.HTML_BODY_ACTION_TO_BE_TAKEN, 
				EmailConstant.APPROVAL_OR_REJECT));*/
		/*html.append(EmailConstant.HTML_BODY_LINK_TITLE);*/
		String link = null;
        if(emailInfo.getUrl() != null){
        	link = EmailConstant.LINK_BEGIN + emailInfo.getUrl() + dalProgramDetail.getId() + EmailConstant.LINK_END;

        }
		html.append(link);
		html.append(EmailConstant.HTML_END);
		
		return html.toString();
	}

	private String buildContentForApprover(EmailDetails emailDetails, DalProgramDetail dalProgramDetail, EmailInfo emailInfo) {
		StringBuilder html = new StringBuilder();
		
		html.append(EmailConstant.HTML_BEGIN);
		html.append(String.format(EmailConstant.HTML_BODY_GREETING, emailDetails.getToNames() + EmailConstant.COMMA) );
		html.append(String.format(EmailConstant.ACTION_TAKEN_SYSTEM, emailInfo.getPreviousUserName()));
		html.append(String.format(EmailConstant.HTML_BODY_PROGRAM_ID, dalProgramDetail.getId()));
		html.append(String.format(EmailConstant.HTML_BODY_COMMENTS, EmailConstant.AUTO_APPROVED_COMMENT));
		html.append(String.format(EmailConstant.HTML_BODY_ACTION_TO_BE_TAKEN, 
				EmailConstant.APPROVAL_OR_REJECT));
		/*html.append(EmailConstant.HTML_BODY_LINK_TITLE);*/
		String link = null;
        if(emailInfo.getUrl() != null){
        	link = EmailConstant.LINK_BEGIN + emailInfo.getUrl() + dalProgramDetail.getId() + EmailConstant.LINK_END;

        }
		html.append(link);
		html.append(EmailConstant.HTML_END);
		
		return html.toString();
	}

	private void buildSubject(EmailDetails emailDetails, DalProgramDetail dalProgramDetail) {
		String subject = null;
		if(BatchConstant.PENDING_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
			subject = String.format(EmailConstant.SUBJECT_PENDING, dalProgramDetail.getId(), dalProgramDetail.getDalProgramHeader().getCustomer().getCustomerNumber());
		}
		else if(BatchConstant.APPROVED_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
			/**If control is here, then approval is done by TBP user who is level 3 user.*/
			subject = String.format(EmailConstant.SUBJECT_APPROVAL, dalProgramDetail.getId(),dalProgramDetail.getDalProgramHeader().getCustomer().getCustomerNumber());
		}
		/*else if(ProgramConstant.REJECTED_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
			subject = String.format(EmailConstant.SUBJECT_REJECTED, dalProgramDetail.getId(),programHeader.getCustomerName());
		}*/
		emailDetails.setSubject(subject);
	}

	/**
	 * Method to build the to addresses.
	 * @param emailDetails emailDetails
	 * @param dalProgramDetail dalProgramDetail
	 * @param programHeader programHeader
	 */
	private void buildToAndCCAddress(EmailDetails emailDetails, DalProgramDetail dalProgramDetail, EmailInfo emailInfo) {
		List<String> toEmailIdList = new ArrayList<String>();
		List<String> ccEmailIdList = null;
		
		if(dalProgramDetail != null){
			List<DalWorkflowStatus> dalWorkflowStatusList = dalProgramDetail.getDalWorkflowStatusList();
			if(dalWorkflowStatusList != null){
				Collections.sort(dalWorkflowStatusList, new WorkflowStatusComparatorByModifiedDate());
				if(BatchConstant.PENDING_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
					int size = dalWorkflowStatusList.size();
					DalWorkflowStatus dalWorkflowStatus = dalWorkflowStatusList.get(size-1);
					toEmailIdList.add(dalWorkflowStatus.getApprover().getEmail());
					appendToName(emailDetails, dalWorkflowStatus.getApprover());
				}
				else if(BatchConstant.APPROVED_STATUS_CODE.equals(dalProgramDetail.getStatus().getId())){
					toEmailIdList.add(dalProgramDetail.getCreatedBy().getEmail());
					appendToName(emailDetails, dalProgramDetail.getCreatedBy());
					for(DalWorkflowStatus workflowStatus : dalWorkflowStatusList){
						/**Last updated person mail id should not be appended to the To list, so not equal to check is added in the below 
						 * condition. */
						if(BatchConstant.APPROVED_STATUS_CODE.equals(workflowStatus.getApprovalStatus().getId()) &&
								!dalProgramDetail.getModifiedBy().getId().equals(workflowStatus.getApprover().getId())){
							if(!toEmailIdList.contains(workflowStatus.getApprover().getEmail())){
								toEmailIdList.add(workflowStatus.getApprover().getEmail());
								appendToName(emailDetails, workflowStatus.getApprover());
							}
						}
					}
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

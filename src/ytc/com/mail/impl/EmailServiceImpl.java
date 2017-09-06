package ytc.com.mail.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ytc.com.common.PropertyManager;
import ytc.com.constant.BatchConstant;
import ytc.com.constant.EmailConstant;
import ytc.com.mail.IYTCMailConnectorService;
import ytc.com.modal.EmailDetails;
import ytc.com.modal.EmailTableContent;


/**
 * Class : ProgramEmailServiceImpl
 * Purpose : This class prepare the data for the mail to be send and calls the email connector class.
 * @author Cognizant.
 *
 */
public class EmailServiceImpl {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	IYTCMailConnectorService mailService = new YTCMailConnectorServiceImpl();
	
	/**
	 * This method is used to prepare the email data and calls the appropriate method to send the details. 
	 * @throws Exception 
	 */
	public void sendEmailData(Map<String, Map<String, List<EmailTableContent>>> userData) throws Exception {
		LOGGER.info(EmailServiceImpl.class.getName() + " : sendEmailData method starts...");
		
		Properties properties = PropertyManager.getInstance().getPropertyInstance(null);
		String fromAddress = properties.getProperty("mail.from.address");
		String url = properties.getProperty("application.url");
		if(userData != null && !userData.isEmpty()){
			
  			for(Map.Entry<String, Map<String, List<EmailTableContent>>> data : userData.entrySet()){
				EmailDetails emailDetails = new EmailDetails();
				emailDetails.setFromAddress(fromAddress);
				buildToAndCCAddress(data.getKey(), emailDetails);
				buildSubject(emailDetails);	
				buildText(data.getValue(), emailDetails, url);
				
				mailService.sendEmail(emailDetails);
			}
			
		}
		LOGGER.info(EmailServiceImpl.class.getName() + " : sendEmailData method ends...");
	}

	private void buildText(Map<String, List<EmailTableContent>> dataMap, EmailDetails emailDetails, String url) {
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildText method starts...");
		String bodyContent = null;
		if(dataMap != null){
			bodyContent = buildContentForApprover(dataMap, url);
		}
		emailDetails.setText(bodyContent);
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildText method ends...");
	}

	private String buildContentForApprover(Map<String, List<EmailTableContent>> dataMap, String url) {
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildContentForApprover method starts...");
		StringBuilder html = new StringBuilder();
		
		html.append(EmailConstant.HTML_BEGIN);
		html.append(String.format(EmailConstant.HTML_BODY_GREETING, "" + EmailConstant.COMMA) );
		String action = null;
		StringBuilder programContent = new StringBuilder();
		StringBuilder pricingContent = new StringBuilder();
		if(dataMap.get(BatchConstant.PROGRAM) != null){
			action = BatchConstant.PROGRAM;
			List<EmailTableContent> temp = dataMap.get(BatchConstant.PROGRAM);
			programContent.append(EmailConstant.PROGRAM_TABLE);
			int count = 1;
			for(EmailTableContent content : temp){
				programContent.append("<tr align='center'>");
				programContent.append("<td>"+ count +"</td>");
				programContent.append("<td>"+ content.getId() +"</td>");
				programContent.append("<td>"+ content.getProgramName() +"</td>");
				programContent.append("<td>"+ content.getProgramType() +"</td>");
				programContent.append("<td>"+ content.getBusinessUnit() +"</td>");
				programContent.append("<td>"+ content.getCreatedBy() +"</td>");
				programContent.append("<td>"+ content.getSubmittedOn() +"</td>");
				programContent.append("<td>"+ content.getStatus() +"</td>");
				count++;
			}
			programContent.append(EmailConstant.TABLE_END);
		}
		if(dataMap.get(BatchConstant.PRICING) != null){
			if(action != null){
				action = action + " and ";
			}
			else {
				action = "";
			}
			action = action + BatchConstant.PRICING;
			List<EmailTableContent> temp = dataMap.get(BatchConstant.PRICING);
			pricingContent.append(EmailConstant.PRICING_TABLE);
			int count = 1;
			for(EmailTableContent content : temp){
				pricingContent.append("<tr align='center'>");
				pricingContent.append("<td>"+ count +"</td>");
				pricingContent.append("<td>"+ content.getId() +"</td>");
				pricingContent.append("<td>"+ content.getCustomerNumber() +"</td>");
				pricingContent.append("<td>"+ content.getCustomerName() +"</td>");
				pricingContent.append("<td>"+ content.getBusinessUnit() +"</td>");
				pricingContent.append("<td>"+ content.getCreatedBy() +"</td>");
				pricingContent.append("<td>"+ content.getSubmittedOn() +"</td>");
				pricingContent.append("<td>"+ content.getStatus() +"</td>");
				count++;
			}
			pricingContent.append(EmailConstant.TABLE_END);
		}
		html.append(String.format(EmailConstant.HTML_BODY_ACTION_TAKEN, 
					"Below "+ action + " request(s) are pending for your approval."));
		if(!programContent.toString().isEmpty()){
			html.append(EmailConstant.PROGRAM_HEADER);
			html.append(programContent);
		}
		
		if(!pricingContent.toString().isEmpty()){
			html.append(EmailConstant.PRICING_HEADER);
			html.append(pricingContent);	
		}
		
		html.append(String.format(EmailConstant.HTML_BODY_ACTION_TO_BE_TAKEN, 
				EmailConstant.APPROVAL_OR_REJECT));

		String link = null;
		if(url != null && !url.isEmpty()){
			link = EmailConstant.LINK_BEGIN + url + EmailConstant.LINK_END;
		}
		html.append(link);
		html.append(EmailConstant.HTML_END);
		
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildContentForApprover method ends...");
		return html.toString();
	}

	private void buildSubject(EmailDetails emailDetails) {
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildSubject method starts...");
		emailDetails.setSubject(EmailConstant.SUBJECT_PENDING);
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildSubject method ends...");
	}

	/**
	 * Method to build the to addresses.
	 * @param emailDetails emailDetails
	 * @param dalProgramDetail dalProgramDetail
	 * @param programHeader programHeader
	 */
	private void buildToAndCCAddress(String emailId, EmailDetails emailDetails) {
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildToAndCCAddress method starts...");
		List<String> toEmailIdList = new ArrayList<String>();
		
		if(emailId != null){
			toEmailIdList.add(emailId);
			emailDetails.setToAddress(toEmailIdList);
			/*emailDetails.setCcAddress(ccEmailIdList);*/
		}
		LOGGER.info(EmailServiceImpl.class.getName() + " : buildToAndCCAddress method ends...");
	}
}

package ytc.com.mail.impl;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ytc.com.common.PropertyManager;
import ytc.com.modal.EmailDetails;

public class YTCMailConnectorServiceImpl implements ytc.com.mail.IYTCMailConnectorService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(YTCMailConnectorServiceImpl.class);
	
	private YTCMailSenderServiceImpl ytcMailSenderService = new YTCMailSenderServiceImpl();
	
	public void sendEmail(EmailDetails emailDetails) throws Exception{
		LOGGER.info(YTCMailConnectorServiceImpl.class.getName() + " : sendEmail method starts...");
		Properties properties = PropertyManager.getInstance().getPropertyInstance(null);
		
		if(emailDetails != null && properties != null){
			emailDetails.setHost(properties.getProperty("mail.smtp.host"));
			emailDetails.setPort(properties.getProperty("mail.smtp.port"));
			emailDetails.setEnvironment(properties.getProperty("mail.environment"));
				
			ytcMailSenderService.sendMail(emailDetails);
		}
		LOGGER.info(YTCMailConnectorServiceImpl.class.getName() + " : sendEmail method ends...");
	}
}

package ytc.com.mail;

import ytc.com.modal.EmailDetails;

public interface IYTCMailConnectorService {
	void sendEmail(EmailDetails emailDetails) throws Exception;
}

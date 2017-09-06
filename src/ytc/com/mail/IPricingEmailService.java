package ytc.com.mail;

import ytc.com.dal.modal.DalPricingHeader;
import ytc.com.modal.EmailInfo;

public interface IPricingEmailService {
	public void sendEmailData(DalPricingHeader dalPricingHeader, EmailInfo emailInfo) throws Exception ;
}

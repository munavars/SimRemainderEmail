package ytc.com.mail;

import ytc.com.dal.modal.DalProgramDetail;
import ytc.com.modal.EmailInfo;

public interface IProgramEmailService {
	public void sendEmailData(DalProgramDetail dalProgramDetail, EmailInfo emailInfo) throws Exception ;
}

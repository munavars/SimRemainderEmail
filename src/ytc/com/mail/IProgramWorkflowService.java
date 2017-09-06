package ytc.com.mail;

import ytc.com.dal.modal.DalEmployee;
import ytc.com.dal.modal.DalProgramDetail;
import ytc.com.modal.EmailInfo;

/**
 * Interface - IProgramWorkflowService.java
 * Purpose - I/f class exposes methods related to workflow updated for the program details.
 * @author Cognizant.
 *
 */
public interface IProgramWorkflowService {
	
	/**
	 * Method to update the workflow details.
	 * @param dalProgramDet dalProgramDet
	 * @param modifiedUser modifiedUser
	 * @param emailInfo emailInfo
	 */
	void updateWorkflowDetails(DalProgramDetail dalProgramDet, DalEmployee modifiedUser, EmailInfo emailInfo) throws Exception;

	
}

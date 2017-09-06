package ytc.com.mail;

import ytc.com.dal.modal.DalEmployee;
import ytc.com.dal.modal.DalPricingHeader;
import ytc.com.modal.EmailInfo;

public interface IPricingWorkflowService {

	/**
	 * Method to update the work flow details.
	 * @param dalPricingHeader dalPricingHeader
	 * @param emailInfo emailInfo
	 */
	void updateWorkflowDetails(DalPricingHeader dalPricingHeader, DalEmployee currentUser, EmailInfo emailInfo) throws Exception;
	
}

package ytc.com.mail.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ytc.com.common.comparator.WorkflowStatusComparatorByModifiedDate;
import ytc.com.constant.BatchConstant;
import ytc.com.dal.IDataAccessLayer;
import ytc.com.dal.jpa.BaseDaoImpl;
import ytc.com.dal.modal.DalEmployee;
import ytc.com.dal.modal.DalPricingHeader;
import ytc.com.dal.modal.DalSimEmployeeHierarchy;
import ytc.com.dal.modal.DalStatus;
import ytc.com.dal.modal.DalWorkflowMatrix;
import ytc.com.dal.modal.DalWorkflowStatus;
import ytc.com.enums.BusinessUnitDescriptionEnum;
import ytc.com.mail.IPricingWorkflowService;
import ytc.com.modal.EmailInfo;
import ytc.com.modal.ProgramWorkflowMatrixDetail;
import ytc.com.service.helper.EmailHelper;


/**
 * Class - PricingWorkflowServiceImpl Purpose - This class contains method
 * related to work low updates.
 * 
 * @author Cognizant.
 *
 */
public class PricingWorkflowServiceImpl implements IPricingWorkflowService {

	private IDataAccessLayer baseDao = new BaseDaoImpl();

	@Override
	public void updateWorkflowDetails(DalPricingHeader dalPricingHeader, DalEmployee modifiedUser, EmailInfo emailInfo) throws Exception {
		Integer currentUser = null;
		List<DalStatus> dalStatusList = baseDao.getListFromNamedQuery("DalStatus.getAllDetails");

		/**
		 * Only if the user is submitting, then get the next approver from
		 * WorkFlow matrix table and make an entry in WorkFlow status table.
		 */
		if (!BatchConstant.IN_PROGRESS_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId())) {
			/***
			 * 1. Get WorkFlow Matrix 2. Get WorkFlow status entries (latest
			 * entry with Pending status has to be updated with Approved or
			 * Rejected status.) 3. Update WorkFlow Matrix
			 */
			List<DalWorkflowStatus> dalWorkflowStatusList = dalPricingHeader.getDalWorkflowStatusForPricingList();
			getWorkflowStatusListInOrder(dalWorkflowStatusList);
			if (dalWorkflowStatusList != null && !dalWorkflowStatusList.isEmpty()) {
				/**
				 * If we already have any approver list in workflow status list,
				 * then we have to find the next approver from workflow matrix
				 */
				int size = dalWorkflowStatusList.size();
				DalWorkflowStatus dalWorkflowStatus = dalWorkflowStatusList.get(size - 1);
				/**
				 * This condition will eliminate the need of updating the
				 * existing status during re submission of request by the
				 * creator.
				 */
				if (BatchConstant.PENDING_STATUS_CODE.equals(dalWorkflowStatus.getApprovalStatus().getId())) {
					dalWorkflowStatus.setApprovalComment(BatchConstant.APPROVAL_COMMENTS);
					String decision = BatchConstant.Y;
					dalWorkflowStatus.setDecisionMade(decision);
					currentUser = dalWorkflowStatus.getApprover().getId();
					emailInfo.setPreviousUserEmailId(dalWorkflowStatus.getApprover().getEmail());
					emailInfo.setPreviousUserName(EmailHelper.getName(dalWorkflowStatus.getApprover()));
					if (BatchConstant.Y.equals(decision)) {
						for (DalStatus dalStatus : dalStatusList) {
							if (BatchConstant.APPROVED_STATUS_CODE.equals(dalStatus.getId())) {
								dalWorkflowStatus.setApprovalStatus(dalStatus);
								break;
							}
						}

					}
					dalWorkflowStatus.setWfStatusDate(Calendar.getInstance());
					dalWorkflowStatus.setModifiedDate(Calendar.getInstance());
					dalWorkflowStatus.setModifiedBy(modifiedUser); // should be system 
				}
			}
			if (!(BatchConstant.REJECTED_STATUS_CODE.equals(dalPricingHeader.getDalStatus().getId()))) {
				/**
				 * Only if it s not rejected, next approver details will be
				 * updated.
				 */
				getNextApproverFromMatrix(dalPricingHeader, currentUser, dalStatusList, modifiedUser);
			}
		}
	}

	private void getNextApproverFromMatrix(DalPricingHeader dalPricingHeader, Integer currentId,
											List<DalStatus> dalStatusList, DalEmployee modifiedUser) throws Exception {

		ProgramWorkflowMatrixDetail programWorkflowMatrixDetail = null;

		programWorkflowMatrixDetail = getWorkflowMatrixManipulatedData(dalPricingHeader, currentId, dalStatusList);

		if (programWorkflowMatrixDetail != null) {

			if (programWorkflowMatrixDetail.getCurrentUserLevel() == programWorkflowMatrixDetail.getTotalLevel()) {
				/**
				 * There is no level after this. This is the final action , if
				 * it approved.
				 */
				for (DalStatus dalStatus : dalStatusList) {
					if (BatchConstant.APPROVED_STATUS_CODE.equals(dalStatus.getId())) {
						dalPricingHeader.setDalStatus(dalStatus);
						break;
					}
				}
			} else if (programWorkflowMatrixDetail.getCurrentUserLevel() < programWorkflowMatrixDetail
					.getTotalLevel()) {
				int nextUserLevel = programWorkflowMatrixDetail.getCurrentUserLevel() + 1;
				Integer[] nextApprover = null;
				if (programWorkflowMatrixDetail.getApproverLevelList().get(nextUserLevel) != null) {
					nextApprover = programWorkflowMatrixDetail.getApproverLevelList().get(nextUserLevel);
				}

				if (nextApprover != null) {
					/** new Row has to be updated */
					DalWorkflowStatus dalWorkflowStatus = new DalWorkflowStatus();

					for (DalStatus dalStatus : dalStatusList) {
						if (BatchConstant.PENDING_STATUS_CODE.equals(dalStatus.getId())) {
							dalWorkflowStatus.setApprovalStatus(dalStatus);
							break;
						}
					}
					dalWorkflowStatus.setApprover(baseDao.getById(DalEmployee.class, nextApprover[0]));
					dalWorkflowStatus.setWfStatusDate(Calendar.getInstance());
					dalWorkflowStatus.setWfMatrixId(nextApprover[1]);
					dalWorkflowStatus.setPgmTypeId(dalPricingHeader.getDalProgramType().getId());
					dalWorkflowStatus.setDalPricingHeaderWf(dalPricingHeader);
					
					dalWorkflowStatus.setCreatedBy(modifiedUser);
					dalWorkflowStatus.setCreatedDate(Calendar.getInstance());
					dalWorkflowStatus.setModifiedBy(modifiedUser);
					dalWorkflowStatus.setModifiedDate(Calendar.getInstance());
					
					if (dalPricingHeader.getDalWorkflowStatusForPricingList() == null) {
						dalPricingHeader.setDalWorkflowStatusForPricingList(new ArrayList<DalWorkflowStatus>());
					}
					dalPricingHeader.getDalWorkflowStatusForPricingList().add(dalWorkflowStatus);
				}
			}
		}

	}

	private ProgramWorkflowMatrixDetail getWorkflowMatrixManipulatedData(DalPricingHeader dalPricingHeader,
			Integer currentUser, List<DalStatus> dalStatusList) throws Exception {

		ProgramWorkflowMatrixDetail programWorkflowMatrixDetail = null;
		Map<String, Object> inputParameters = new HashMap<String, Object>();
		inputParameters.put("programType", "%" + dalPricingHeader.getDalProgramType().getType() + "%");
		inputParameters.put("businessUnit", dalPricingHeader.getCustomer().getBu());

		List<DalWorkflowMatrix> dalWorkflowMatrixList = baseDao.getListFromNamedQueryWithParameter("DalWorkflowMatrix.getMatrixForBU", inputParameters);

		if (dalWorkflowMatrixList != null && !dalWorkflowMatrixList.isEmpty()) {
			programWorkflowMatrixDetail = new ProgramWorkflowMatrixDetail();
			Integer employeeId = null;
			if (dalPricingHeader.getCreatedBy() != null) {
				employeeId = dalPricingHeader.getCreatedBy().getId();
			} 			
			inputParameters.clear();
			inputParameters.put("empId", String.valueOf( employeeId) );
			inputParameters.put("businessUnit", String.valueOf(BusinessUnitDescriptionEnum.getBUDescription(dalPricingHeader.getCustomer().getBu())) );
			
			/*Here for sure, there should be a record present in employee hierarchy. */
			List<DalSimEmployeeHierarchy> dalSimEmployeeHierarchyList = baseDao.getListFromNamedQueryWithParameter("DalSimEmployeeHierarchy.getHierarchyListBasedOnIdAndBU", 
																											inputParameters);
			
			DalSimEmployeeHierarchy dalSimEmployeeHierarchy = dalSimEmployeeHierarchyList.get(0);
			
			int currentUserLevel = 0;
			int userLevel = 0;
			Integer[] value = null;
			boolean skipRecord = false;
			for (DalWorkflowMatrix workflowMatrix : dalWorkflowMatrixList) {
				
				skipRecord = checkForException(workflowMatrix, dalPricingHeader);
				if(skipRecord){
					continue;
				}
				userLevel++;
				if (workflowMatrix.getHierarchyLevel() != null) {
					value = new Integer[2];
					Integer currentUserEmployeeId = EmailHelper.getEmployeeIdFromHierachy(dalSimEmployeeHierarchy, workflowMatrix.getHierarchyLevel());
					value[0] = currentUserEmployeeId;
					value[1] = workflowMatrix.getId();
					programWorkflowMatrixDetail.getApproverLevelList().put(userLevel, value);
					if (currentUser.equals(currentUserEmployeeId)) {
						currentUserLevel = userLevel;
					}
				} else if (workflowMatrix.getEmpId() != null) {
					value = new Integer[2];
					value[0] = workflowMatrix.getEmpId();
					value[1] = workflowMatrix.getId();
					programWorkflowMatrixDetail.getApproverLevelList().put(userLevel, value);
					if (currentUser.equals(workflowMatrix.getEmpId())) {
						currentUserLevel = userLevel;
					}
				}
			}

			programWorkflowMatrixDetail.setCurrentUserLevel(currentUserLevel);
			programWorkflowMatrixDetail.setTotalLevel(userLevel);
		}
		return programWorkflowMatrixDetail;
	}

	private boolean checkForException(DalWorkflowMatrix workflowMatrix, DalPricingHeader dalPricingHeader) {
		boolean skipRecord = false;
		
		if(workflowMatrix != null && workflowMatrix.getDalWorkflowExceptionMatrix() != null && dalPricingHeader != null){
			if(!workflowMatrix.getDalWorkflowExceptionMatrix().getCustomerNumbers().contains(dalPricingHeader.getCustomer().getCustomerNumber())){
				skipRecord = true;
			}
		}
		return skipRecord;
	}

	/**
	 * Method is used to sort the DalWorkFlowStatus object based on its modified
	 * date.
	 * 
	 * @param dalWorkflowStatusList
	 *            dalWorkflowStatusList
	 */
	private void getWorkflowStatusListInOrder(List<DalWorkflowStatus> dalWorkflowStatusList) {
		if (dalWorkflowStatusList != null && !dalWorkflowStatusList.isEmpty()) {
			Collections.sort(dalWorkflowStatusList, new WorkflowStatusComparatorByModifiedDate());
		}

	}

}

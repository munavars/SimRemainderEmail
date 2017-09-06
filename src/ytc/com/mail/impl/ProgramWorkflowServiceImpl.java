package ytc.com.mail.impl;

import java.math.BigDecimal;
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
import ytc.com.dal.modal.DalProgramDetail;
import ytc.com.dal.modal.DalSimEmployeeHierarchy;
import ytc.com.dal.modal.DalSimWorkflowMatrix;
import ytc.com.dal.modal.DalStatus;
import ytc.com.dal.modal.DalWorkflowStatus;
import ytc.com.enums.BusinessUnitDescriptionEnum;
import ytc.com.mail.IProgramWorkflowService;
import ytc.com.modal.EmailInfo;
import ytc.com.modal.ProgramWorkflowMatrixDetail;
import ytc.com.service.helper.EmailHelper;


/**
 * Class - ProgramWorkflowServiceImpl
 * Purpose - This class contains method related to workflow updates.
 * @author Cognizant.
 *
 */
public class ProgramWorkflowServiceImpl implements IProgramWorkflowService{

	IDataAccessLayer baseDao = new BaseDaoImpl();
	
	/**
	 * Method to update the WorkFlow details.
	 * @param dalProgramDet dalProgramDet.
	 * @param programHeader programHeader.
	 * @param employee employee.
	 * @throws Exception 
	 */
	@Override
	public void updateWorkflowDetails(DalProgramDetail dalProgramDet, DalEmployee modifiedUser, EmailInfo emailInfo) throws Exception {
		if(dalProgramDet != null){
			
			List<DalStatus> dalStatusList =  baseDao.getListFromNamedQuery("DalStatus.getAllDetails");
			Integer currentUser = null;
			/**Only if the user is submitting, then get the next approver from
			 * WorkFlow matrix table and make an entry in WorkFlow status table. */
			if(!BatchConstant.IN_PROGRESS_STATUS_CODE.equals(dalProgramDet.getStatus().getId())){
				/***
				 * 1. Get WorkFlow Matrix
				 * 2. Get WorkFlow status entries (latest entry with Pending status has to be updated with Approved or Rejected status.)
				 * 3. Update WorkFlow Matrix
				 */
				List<DalWorkflowStatus> dalWorkflowStatusList = dalProgramDet.getDalWorkflowStatusList();
				getWorkflowStatusListInOrder(dalWorkflowStatusList);
				if(dalWorkflowStatusList != null && !dalWorkflowStatusList.isEmpty()){
					/**If we already have any approver list in workflow status list, then
					 * we have to find the next approver from workflow matrix*/
					int size = dalWorkflowStatusList.size();
					DalWorkflowStatus dalWorkflowStatus = dalWorkflowStatusList.get(size-1);
					/**This condition will eliminate the need of updating the existing status during resubmission of request
					 * by the creator.*/
					if(BatchConstant.PENDING_STATUS_CODE.equals(dalWorkflowStatus.getApprovalStatus().getId())){
						dalWorkflowStatus.setApprovalComment(BatchConstant.APPROVAL_COMMENTS);
						String decision = BatchConstant.Y;
						dalWorkflowStatus.setDecisionMade(decision);
						currentUser = dalWorkflowStatus.getApprover().getId();
						emailInfo.setPreviousUserEmailId(dalWorkflowStatus.getApprover().getEmail());
						emailInfo.setPreviousUserName(EmailHelper.getName(dalWorkflowStatus.getApprover()));
						if(BatchConstant.Y.equals(decision)){
							for(DalStatus dalStatus : dalStatusList){
								if(BatchConstant.APPROVED_STATUS_CODE.equals(dalStatus.getId())){
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
				if(!(BatchConstant.REJECTED_STATUS_CODE.equals(dalProgramDet.getStatus().getId()))){
					/**Only if it s not rejected, next approver details will be updated.*/
					getNextApproverFromMatrix(dalProgramDet, currentUser, dalStatusList, modifiedUser);
				}
			}
		}
	}

	private void getNextApproverFromMatrix(DalProgramDetail dalProgramDetail, Integer currentUser,
											List<DalStatus> dalStatusList, DalEmployee modifiedUser) throws Exception {
		
		ProgramWorkflowMatrixDetail programWorkflowMatrixDetail = null;
				
		programWorkflowMatrixDetail = getWorkflowMatrixManipualtedData(dalProgramDetail, currentUser, dalStatusList);
		
		if(programWorkflowMatrixDetail != null){
			
			if(programWorkflowMatrixDetail.getCurrentUserLevel() == programWorkflowMatrixDetail.getTotalLevel()){
				/**There is no level after this. This is the final action , if it approved.*/
				for(DalStatus dalStatus : dalStatusList){
					if(BatchConstant.APPROVED_STATUS_CODE.equals(dalStatus.getId())){
						dalProgramDetail.setStatus(dalStatus);
						dalProgramDetail.getDalProgramHeader().setStatus(dalStatus);
						break;
					}
				}
			}
			else if(programWorkflowMatrixDetail.getCurrentUserLevel()  < programWorkflowMatrixDetail.getTotalLevel()){
				int nextUserLevel = programWorkflowMatrixDetail.getCurrentUserLevel() + 1;
				Integer[] nextApprover = null;
				if(programWorkflowMatrixDetail.getApproverLevelList().get(nextUserLevel) != null){
					nextApprover = programWorkflowMatrixDetail.getApproverLevelList().get(nextUserLevel);
				}
				
				if(nextApprover != null){
					/**new Row has to be updated*/
					DalWorkflowStatus dalWorkflowStatus = new DalWorkflowStatus();
					
					for(DalStatus dalStatus : dalStatusList){
						if(BatchConstant.PENDING_STATUS_CODE.equals(dalStatus.getId())){
							dalWorkflowStatus.setApprovalStatus(dalStatus);
							break;
						}
					}
					dalWorkflowStatus.setApprover(baseDao.getById(DalEmployee.class, nextApprover[0]));
					dalWorkflowStatus.setWfStatusDate(Calendar.getInstance());
					dalWorkflowStatus.setWfMatrixId(nextApprover[1]);
					dalWorkflowStatus.setPgmTypeId(dalProgramDetail.getDalProgramType().getId());
					dalWorkflowStatus.setDalProgramDetailWf(dalProgramDetail);
					dalWorkflowStatus.setCreatedBy(modifiedUser);
					dalWorkflowStatus.setCreatedDate(Calendar.getInstance());
					dalWorkflowStatus.setModifiedBy(modifiedUser);
					dalWorkflowStatus.setModifiedDate(Calendar.getInstance());
					
					if(dalProgramDetail.getDalWorkflowStatusList() == null){
						dalProgramDetail.setDalWorkflowStatusList(new ArrayList<DalWorkflowStatus>());
					}
					dalProgramDetail.addWorkflowDetails(dalWorkflowStatus);
				}
			}		
		}		

	}

	private ProgramWorkflowMatrixDetail getWorkflowMatrixManipualtedData(DalProgramDetail dalProgramDetail, Integer currentUser,
																		List<DalStatus> dalStatusList ) throws Exception {
		
		ProgramWorkflowMatrixDetail programWorkflowMatrixDetail = null;
		Map<String, Object> inputParameters = new HashMap<String, Object>();
		inputParameters.put("businessUnit", String.valueOf(BusinessUnitDescriptionEnum.getBUDescription(dalProgramDetail.getDalProgramHeader().getBu())) );
		
		List<DalSimWorkflowMatrix> dalSimWorkflowMatrixList =  baseDao.getListFromNamedQueryWithParameter("DalSimWorkflowMatrix.getMatrixForBU", inputParameters);
		
		if(dalSimWorkflowMatrixList != null && !dalSimWorkflowMatrixList.isEmpty() && currentUser != null){
			programWorkflowMatrixDetail = new ProgramWorkflowMatrixDetail();
			Integer employeeId = null;
			if(dalProgramDetail.getCreatedBy() != null){
				employeeId = dalProgramDetail.getCreatedBy().getId(); 
			}
			inputParameters.clear();
			inputParameters.put("empId", String.valueOf( employeeId) );
			inputParameters.put("businessUnit", String.valueOf(BusinessUnitDescriptionEnum.getBUDescription(dalProgramDetail.getDalProgramHeader().getBu())) );
			
			/*Here for sure, there should be a record present in employee hierarchy. */
			List<DalSimEmployeeHierarchy> dalSimEmployeeHierarchyList = baseDao.getListFromNamedQueryWithParameter("DalSimEmployeeHierarchy.getHierarchyListBasedOnIdAndBU", 
																											inputParameters);
			
			DalSimEmployeeHierarchy dalSimEmployeeHierarchy = dalSimEmployeeHierarchyList.get(0);
			int currentUserLevel = 0;
			int userLevel = 0;
			Integer[] value = null;
			/*boolean skipRecord = false;*/
			for(DalSimWorkflowMatrix simWorkflowMatrix : dalSimWorkflowMatrixList){
				
				/*if(skipRecord && !ProgramConstant.NO_LIMIT.equalsIgnoreCase(simWorkflowMatrix.getDollarLimit())){
					continue;
				}*/
				if(!BatchConstant.NO_LIMIT.equalsIgnoreCase(simWorkflowMatrix.getDollarLimit())){
					boolean isMatchingApprover = checkDollarLimit(simWorkflowMatrix, dalProgramDetail);
					if(!isMatchingApprover){
						continue;
					}
					/*else{
						continue;
					}*/
				}
				/*else{
					skipRecord = false;
				}*/
				
				if(simWorkflowMatrix.getHierarchyLevel() != null){
					value = new Integer[2]; 
					Integer currentUserEmployeeId = EmailHelper.getEmployeeIdFromHierachy(dalSimEmployeeHierarchy, simWorkflowMatrix.getHierarchyLevel());
					if (employeeId != null && currentUserEmployeeId != null && employeeId.equals(currentUserEmployeeId)){
						continue;
					}
					else if (currentUserEmployeeId == null){
						continue;
					}
					value[0] = currentUserEmployeeId;
					value[1] = simWorkflowMatrix.getId();
					userLevel++;
					programWorkflowMatrixDetail.getApproverLevelList().put(userLevel, value);
					if(currentUser.equals(currentUserEmployeeId)){
						currentUserLevel = userLevel;
					}
				}
				else if(simWorkflowMatrix.getEmpId() != null){
					value = new Integer[2]; 
					value[0] = simWorkflowMatrix.getEmpId();
					value[1] = simWorkflowMatrix.getId();
					userLevel++;
					programWorkflowMatrixDetail.getApproverLevelList().put(userLevel, value);
					if(currentUser.equals(simWorkflowMatrix.getEmpId())){
						currentUserLevel = userLevel;
					}
				}
			}
			programWorkflowMatrixDetail.setCurrentUserLevel(currentUserLevel);
			programWorkflowMatrixDetail.setTotalLevel(userLevel);
		}
		return programWorkflowMatrixDetail;
	}
	
	private boolean checkDollarLimit(DalSimWorkflowMatrix dalSimWorkflowMatrix, DalProgramDetail dalProgramDetail) {
		boolean isMatching = false;
		if(dalSimWorkflowMatrix != null && dalProgramDetail != null ){
			String dollerLimit = dalSimWorkflowMatrix.getDollarLimit();
			int numericIndex = 0;
			if(dollerLimit != null){
				/**
				 * Since, we have no clue @ what position amount will start, 
				 * below logic checks for first digit occurrence and based on that, operator and amount is separated. 
				 */
				for(int i = 0; i < dollerLimit.length(); i++){
					if(Character.isDigit(dollerLimit.charAt(i))){
						numericIndex = i;
						break;
					}
				}
				String comparisonOperator = dollerLimit.substring(0, numericIndex).trim();
				BigDecimal amount = new BigDecimal(dollerLimit.substring(numericIndex, dollerLimit.length())); 
				
				if(BatchConstant.OPERATOR_G.equals(comparisonOperator)){
					BigDecimal userAmount = new BigDecimal(dalProgramDetail.getEstimatedAccrual());
					if( userAmount.compareTo(amount) > 0){
						isMatching = true;
					}
				}
				else if(BatchConstant.OPERATOR_GE.contains(comparisonOperator)){
					BigDecimal userAmount = new BigDecimal(dalProgramDetail.getEstimatedAccrual());
					if( userAmount.compareTo(amount) >= 0){
						isMatching = true;
					}
				}
				else if(BatchConstant.OPERATOR_LE.contains(comparisonOperator)){
					BigDecimal userAmount = new BigDecimal(dalProgramDetail.getEstimatedAccrual());
					if( userAmount.compareTo(amount) <= 0){
						isMatching = true;
					}					
				}
			}
		}
		return isMatching;
	}

	/**
	 * Method is used to sort the DalWorkFlowStatus object based on its modified date.
	 * @param dalWorkflowStatusList dalWorkflowStatusList
	 */
	private void getWorkflowStatusListInOrder(List<DalWorkflowStatus> dalWorkflowStatusList) {
		if(dalWorkflowStatusList != null && !dalWorkflowStatusList.isEmpty()){
			Collections.sort(dalWorkflowStatusList, new WorkflowStatusComparatorByModifiedDate());	
		}
		
	}
}

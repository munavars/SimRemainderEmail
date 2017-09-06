package ytc.com.service.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

import ytc.com.constant.BatchConstant;
import ytc.com.dal.modal.DalEmployeeHierarchy;
import ytc.com.dal.modal.DalSimEmployeeHierarchy;


public class EmailHelper {
	public static String convertDateToString(Date inputDate, String format){
		String convertedDateString = null;
		
		if(inputDate != null && format != null){
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			//convertedDateString = sdf.format(new Date());
			convertedDateString = sdf.format(inputDate);
		}
		return convertedDateString;
	}
	
	public static Integer getEmployeeIdFromHierachy(DalEmployeeHierarchy dalEmployeeHierarchy, Integer hierarchyLevel){
		Integer employeeId = null;
		
		if(dalEmployeeHierarchy != null){
			if(BatchConstant.EMP_HIERARCHY_1 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl1EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_2 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl2EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_3 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl3EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_4 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl4EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_5 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl5EmpId());
			}
		}
		
		return employeeId;
	}
	
	public static Integer getEmployeeIdFromHierachy(DalSimEmployeeHierarchy dalEmployeeHierarchy, Integer hierarchyLevel){
		Integer employeeId = null;
		
		if(dalEmployeeHierarchy != null){
			if(BatchConstant.EMP_HIERARCHY_1 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl1EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_2 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl2EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_3 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl3EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_4 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl4EmpId());
			}
			else if(BatchConstant.EMP_HIERARCHY_5 == hierarchyLevel){
				employeeId = Integer.valueOf(dalEmployeeHierarchy.getLvl5EmpId());
			}
		}
		
		return employeeId;
	}
	
	public static String getName(ytc.com.dal.modal.DalEmployee dalEmployee){
		String name = null;
		if(dalEmployee != null){
			name = dalEmployee.getFirtName() + BatchConstant.NAME_DELIMITER + dalEmployee.getLastName();
		}
		return name;
	}
}

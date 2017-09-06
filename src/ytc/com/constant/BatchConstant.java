package ytc.com.constant;

public interface BatchConstant {
	String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	String PROPERTY_FILE_NAME = "sim-batch.properties";
	
	String PROGRAM = "program";
	
	String PRICING = "pricing";
	
	String DATE_FORMAT_TIME = "MM/dd/yyyy HH:mm:ss";
	
	Integer ACTIVE_STATUS_CODE = 0;
	
	Integer APPROVED_STATUS_CODE = 4;
	
	Integer IN_PROGRESS_STATUS_CODE = 3;
	
	Integer PENDING_STATUS_CODE = 2;
	
	Integer WAITING_STATUS_CODE = 5;
	
	Integer REJECTED_STATUS_CODE = 1;
	
	String OPERATOR_G = ">";
	
	String OPERATOR_GE = ">=";
	
	String OPERATOR_LE = "<=";
	
	String APPROVAL_COMMENTS = "Auto Approved by System";
	
	String Y = "Y";
	
	String NO_LIMIT = "No Limit";
	
	Integer EMP_HIERARCHY_1 = 1;
	
	Integer EMP_HIERARCHY_2 = 2;
	
	Integer EMP_HIERARCHY_3 = 3;
	
	Integer EMP_HIERARCHY_4 = 4;
	
	Integer EMP_HIERARCHY_5 = 5;
	
	String FORWARD_SLASH = "/";
	
	String NAME_DELIMITER = " ";
	
	Integer CALCULATED_PROGRAM_TYPE_CODE = 1;
	
	String ONE="1";
}

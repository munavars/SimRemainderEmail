package ytc.com.constant;

public class EmailConstant {
	
	public static final String FROM_ADDRESS = "no-reply@yokohamatire.com";
	
	public static final String HTML_BEGIN = "<html><body>";
	
	public static final String HTML_BODY_GREETING = "<p> Hi %s </p>";
	
	public static final String HTML_BODY_ACTION_TAKEN = "<h4> %s </h4>";
	
	public static final String HTML_BODY_PROGRAM_ID = "<p> <b> Program Id: </b> %s </p>";
	
	public static final String PRICING_HTML_BODY_ID = "<p> <b> Pricing Id: </b> %s </p>";
	
	public static final String PROGRAM_HEADER = "<p><b> Program details </b></p>";
	
	public static final String PRICING_HEADER = "<p><b> Pricing details </b></p>";
	
	public static final String HTML_BODY_COMMENTS = "<p><b> Comments: </b> %s </p>";
	
	public static final String HTML_BODY_ACTION_TO_BE_TAKEN = "<p> <b> Action to be taken: </b> %s </p>";
	
	public static final String HTML_BODY_LINK_TITLE = "<p> Click below link to view the program details </p>";
	
	public static final String HTML_END ="</body></html>";
	
	public static final String APPROVAL_OR_REJECT = "Approve/Reject";
	
	public static final String RE_SUBMIT = "Re submit";
	
	public static final String SUBJECT_PENDING = "Action Required: Pending For Approval";
	
	public static final String SUBJECT_APPROVAL = "Attention: Program id %s is Approved";
	
	public static final String SUBJECT_REJECTED = "Action Required: Program id %s is rejected";
	
	public static final String PRICING_SUBJECT_PENDING = "Action Required: Pending For Approval - Pricing id %s";
	
	public static final String PRICING_SUBJECT_APPROVAL = "Attention: Pricing id %s is Approved";
	
	public static final String PRICING_SUBJECT_REJECTED = "Action Required: Pricing id %s is rejected";
	
	public static final String LINK_CALCULATED_PGM_1 = "/programDetail?pgmId=";
	
	public static final String LINK_DDF_COOP_PGM_1 = "/programddfcoop?pgmId=";
	
	public static final String PRICING_LINK_PAGE = "/pricingRequest?pricingId=";
	
	public static final String LINK_BEGIN = "<a href=";
	
	public static final String LINK_END ="> Click here to take neccessary action</a>";
	
	public static final String COMMA = ",";
	
	public static final String L_AND = " and "; //Prefix L denotes Literal.
	
	public static final String HTML_ENVIRONMENT_BODY = "<br><br><br> <h5><i>This mail is generated in %s environment.</i></h5>";
	
	public static final String COLON = " : ";
	
	public static final String PROGRAM_TABLE = "<table width='100%' border='1' align='center'>"
									            + "<tr align='center'>"
									            + "<td><b>S.No<b></td>"
									            + "<td><b>Program Id<b></td>"
									            + "<td><b>Program Name<b></td>"
									            + "<td><b>Program Type<b></td>"
									            + "<td><b>Business Unit<b></td>"
									            + "<td><b>Created By<b></td>"
									            + "<td><b>Submitted On<b></td>"
									            + "<td><b>Program Status<b></td>"
									            + "</tr>";
	public static final String PRICING_TABLE = "<table width='100%' border='1' align='center'>"
									            + "<tr align='center'>"
									            + "<td><b>S.No<b></td>"
									            + "<td><b>Pricing Id<b></td>"
									            + "<td><b>Customer Id<b></td>"
									            + "<td><b>Customer Name<b></td>"
									            + "<td><b>Business Unit<b></td>"
									            + "<td><b>Created By<b></td>"
									            + "<td><b>Submitted On<b></td>"
									            + "<td><b>Pricing Status<b></td>"
									            + "</tr>";
	
	public static final String TABLE_END = "</table>";
	
	public static final String AUTO_APPROVED_COMMENT = "Program Details has been Auto-approved by System";
	
	public static final String PRICING_AUTO_APPROVED_COMMENT = "Pricing Details has been Auto-approved by System";
	
	public static final String ACTION_TAKEN_SYSTEM = "Request approval pending with %s is Auto Approved by System. This request is currently pending with you.";
	
	public static final String ACTION_TAKEN_SYSTEM_APPROVED = "Request approval pending with %s is Auto Approved by System."; 
	
	public static final String CONSUMER_PRICING_MAIL_GROUP = "manju.vubra@yokohamatire.com"; //"ConsumerPricingFT@yokohamatire.com"
	
	public static final String OTR_PRICING_MAIL_GROUP = "manju.vubra@yokohamatire.com"; //"otrpricing@yokohamatire.com"
	
	public static final String COMMERCIAL_SBM_PRICING_MAIL_GROUP = "manju.vubra@yokohamatire.com"; //"CommercialSBM@yokohamatire.com"
	
	public static final String COMMERCIAL_PRICING_MAIL_GROUP = "manju.vubra@yokohamatire.com"; //"commercialpricing@yokohamatire.com"
}

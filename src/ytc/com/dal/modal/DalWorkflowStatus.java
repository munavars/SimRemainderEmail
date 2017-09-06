package ytc.com.dal.modal;

import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ytc.com.dal.jpa.listeners.WorkflowListener;

@Entity
@Table(name = "WORKFLOW_STATUS")
@NamedQueries({
	@NamedQuery(name="DalWorkflowStatus.getPendingRecords", query = "select o from DalWorkflowStatus o "
																+ "where o.approvalStatus.id = 2 order by o.id"),
})
@EntityListeners(WorkflowListener.class)
public class DalWorkflowStatus{


	private Integer id;
	
	private DalProgramDetail dalProgramDetailWf;
	
	private DalPricingHeader dalPricingHeaderWf;

	private Integer pgmTypeId;
	
	private Integer wfMatrixId;

	private DalEmployee approver;
	
	private DalStatus approvalStatus;
	
	private Calendar wfStatusDate;

	private String approvalComment;

	private String decisionMade;
	
	private Integer mailCount;

	private DalEmployee createdBy;
	
	private Calendar createdDate;
	
	private DalEmployee modifiedBy;
	
	private Calendar modifiedDate;

	@Id
	@Column(name="ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name = "PGM_DETAIL_ID", referencedColumnName = "ID")	
	public DalProgramDetail getDalProgramDetailWf() {
		return dalProgramDetailWf;
	}

	public void setDalProgramDetailWf(DalProgramDetail dalProgramDetailWf) {
		this.dalProgramDetailWf = dalProgramDetailWf;
	}
	
	@ManyToOne
	@JoinColumn(name = "PRICING_HDR_ID", referencedColumnName = "ID")
	public DalPricingHeader getDalPricingHeaderWf() {
		return dalPricingHeaderWf;
	}

	public void setDalPricingHeaderWf(DalPricingHeader dalPricingHeaderWf) {
		this.dalPricingHeaderWf = dalPricingHeaderWf;
	}

	@Column(name="PGM_TYPE_ID")
	public Integer getPgmTypeId() {
		return pgmTypeId;
	}

	public void setPgmTypeId(Integer pgmTypeId) {
		this.pgmTypeId = pgmTypeId;
	}

	@Column(name="WF_MATRIX_ID")
	public Integer getWfMatrixId() {
		return wfMatrixId;
	}

	public void setWfMatrixId(Integer wfMatrixId) {
		this.wfMatrixId = wfMatrixId;
	}

	@OneToOne
	@JoinColumn(name="APPROVER_ID", referencedColumnName = "ID")
	public DalEmployee getApprover() {
		return approver;
	}

	public void setApprover(DalEmployee approver) {
		this.approver = approver;
	}

	@OneToOne
	@JoinColumn(name = "APPROVAL_STATUS", referencedColumnName = "ID")
	public DalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(DalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	@Column(name="WF_STATUS_DATE")
	public Calendar getWfStatusDate() {
		return wfStatusDate;
	}

	public void setWfStatusDate(Calendar wfStatusDate) {
		this.wfStatusDate = wfStatusDate;
	}

	@Column(name="APPROVAL_COMMENT")
	public String getApprovalComment() {
		return approvalComment;
	}

	public void setApprovalComment(String approvalComment) {
		this.approvalComment = approvalComment;
	}

	@Column(name="DECISION_MADE")
	public String getDecisionMade() {
		return decisionMade;
	}

	public void setDecisionMade(String decisionMade) {
		this.decisionMade = decisionMade;
	}
	
	@Column(name="MAIL_COUNT")
	public Integer getMailCount() {
		return mailCount;
	}

	public void setMailCount(Integer mailCount) {
		this.mailCount = mailCount;
	}

    @OneToOne(targetEntity = DalEmployee.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "CREATED_BY", updatable = false, insertable = true)
	public DalEmployee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(DalEmployee createdBy) {
		this.createdBy = createdBy;
	}

	@Column(name = "CREATED_DATE", columnDefinition = "timestamp",updatable = true, insertable = true)
	public Calendar getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}

    @OneToOne(targetEntity = DalEmployee.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "MODIFIED_BY", updatable = true, insertable = true)
	public DalEmployee getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(DalEmployee modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	@Column(name = "MODIFIED_DATE", columnDefinition = "timestamp",updatable = true, insertable = true)
	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
}


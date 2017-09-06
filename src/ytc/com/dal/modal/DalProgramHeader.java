package ytc.com.dal.modal;

import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PROGRAM_HEADER")
public class DalProgramHeader{

	private Integer id;
	
	private DalCustomer customer;
	
	private Integer accessPgmId;
	
	private String bu;
	
	private Integer requestTypeId;
	
	private DalEmployee request;
	
	private Calendar requestDate;
	
	private String closedBy;
	
	private Calendar closedDate;
	
	private DalStatus status;
	
	private List<DalProgramDetail> dalProgramDetailList;
	
	@Id
	@Column(name="ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	@OneToOne
	@JoinColumn(name = "CUSTOMER_ID", referencedColumnName ="ID")
	public DalCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(DalCustomer customer) {
		this.customer = customer;
	}
	
	@OneToMany(mappedBy = "dalProgramHeader", fetch=FetchType.EAGER)
	public List<DalProgramDetail> getDalProgramDetailList() {
		return dalProgramDetailList;
	}

	public void setDalProgramDetailList(List<DalProgramDetail> dalProgramDetailList) {
		this.dalProgramDetailList = dalProgramDetailList;
	}

	@Column(name = "ACCESS_PGM_ID")
	public Integer getAccessPgmId() {
		return accessPgmId;
	}

	public void setAccessPgmId(Integer accessPgmId) {
		this.accessPgmId = accessPgmId;
	}

	@Column(name = "BU")
	public String getBu() {
		return bu;
	}

	public void setBu(String bu) {
		this.bu = bu;
	}

	@Column(name = "REQUEST_TYPE_ID")
	public Integer getRequestTypeId() {
		return requestTypeId;
	}

	public void setRequestTypeId(Integer requestTypeId) {
		this.requestTypeId = requestTypeId;
	}

	@OneToOne
	@JoinColumn(name = "REQUEST_ID", referencedColumnName = "ID")
	public DalEmployee getRequest() {
		return request;
	}

	public void setRequest(DalEmployee request) {
		this.request = request;
	}

	@Column(name = "REQUEST_DATE")
	public Calendar getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}

	@Column(name = "CLOSED_BY")
	public String getClosedBy() {
		return closedBy;
	}

	public void setClosedBy(String closedBy) {
		this.closedBy = closedBy;
	}

	@Column(name = "CLOSED_DATE")
	public Calendar getClosedDate() {
		return closedDate;
	}

	
	public void setClosedDate(Calendar closedDate) {
		this.closedDate = closedDate;
	}

	@OneToOne
	@JoinColumn(name = "STATUS_ID", referencedColumnName = "ID")
	public DalStatus getStatus() {
		return status;
	}

	public void setStatus(DalStatus status) {
		this.status = status;
	}
}

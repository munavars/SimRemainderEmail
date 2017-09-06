/**
 * 
 */
package ytc.com.dal.modal;


import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PRICING_HEADER")
@NamedQueries({
	@NamedQuery(name="DalPricingHeader.getPricingDetails", query = "select o from DalPricingHeader o "
																				+ "where o.id in :pricingIds")
})
public class DalPricingHeader{

	private Integer id;
	private DalCustomer customer;
	private Integer customerGroup;

	private String userComments;
	private DalStatus dalStatus;
	private DalProgramType dalProgramType;
	private String sbm;
	private List<DalWorkflowStatus> dalWorkflowStatusForPricingList;
	
	private DalEmployee createdBy;
	
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
	
	/**
	 * @return the customerId
	 */
	@OneToOne
	@JoinColumn(name = "CUSTOMER_ID", referencedColumnName ="ID")
	public DalCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(DalCustomer customer) {
		this.customer = customer;
	}
	
	/**
	 * @return the userComments
	 */
	@Column(name = "USER_COMMENTS")
	public String getUserComments() {
		return userComments;
	}
	/**
	 * @param userComments the userComments to set
	 */
	public void setUserComments(String userComments) {
		this.userComments = userComments;
	}

	

	@OneToOne
	@JoinColumn(name = "STATUS", referencedColumnName ="ID")
	public DalStatus getDalStatus() {
		return dalStatus;
	}
	
	public void setDalStatus(DalStatus dalStatus) {
		this.dalStatus = dalStatus;
	}
	
	@OneToOne
	@JoinColumn(name = "PGM_TYPE_ID", referencedColumnName ="ID")
	public DalProgramType getDalProgramType() {
		return dalProgramType;
	}
	
	public void setDalProgramType(DalProgramType dalProgramType) {
		this.dalProgramType = dalProgramType;
	}
	
	@Column(name = "CUSTOMER_GROUP")
	public Integer getCustomerGroup() {
		return customerGroup;
	}
	public void setCustomerGroup(Integer customerGroup) {
		this.customerGroup = customerGroup;
	}
	
	@OneToMany(mappedBy = "dalPricingHeaderWf", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public List<DalWorkflowStatus> getDalWorkflowStatusForPricingList() {
		return dalWorkflowStatusForPricingList;
	}
	
	public void setDalWorkflowStatusForPricingList(List<DalWorkflowStatus> dalWorkflowStatusForPricingList) {
		this.dalWorkflowStatusForPricingList = dalWorkflowStatusForPricingList;
	}
	
    @OneToOne(targetEntity = ytc.com.dal.modal.DalEmployee.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "CREATED_BY", updatable = false, insertable = true)
    public DalEmployee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DalEmployee createdBy) {
        this.createdBy = createdBy;
    }
    
    @OneToOne(targetEntity = ytc.com.dal.modal.DalEmployee.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "MODIFIED_BY", updatable = false, insertable = true)
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
	
	@Column(name = "SBM")
	public String getSbm() {
		return sbm;
	}
	public void setSbm(String sbm) {
		this.sbm = sbm;
	}
}
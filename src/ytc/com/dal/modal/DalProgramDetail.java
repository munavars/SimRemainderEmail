package ytc.com.dal.modal;

import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PROGRAM_DETAIL")
@NamedQueries({
	@NamedQuery(name="DalProgramDetail.getProgramDetails", query = "select o from DalProgramDetail o "
																				+ "where o.id in :programIds")
})
public class DalProgramDetail {
	private Integer id;

	private DalProgramMaster programMaster;
	private DalProgramHeader dalProgramHeader;
    
    private DalStatus status;
    private DalProgramType dalProgramType;
    private List<DalWorkflowStatus> dalWorkflowStatusList;
    private Integer estimatedAccrual;
    
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
    * @return the programMaster
    */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="PGM_ID")
    public DalProgramMaster getProgramMaster() {
                return programMaster;
    }
    /**
    * @param programMaster the programMaster to set
    */
    public void setProgramMaster(DalProgramMaster programMaster) {
        this.programMaster = programMaster;
    }
                
    @ManyToOne
    @JoinColumn(name = "PGM_HDR_ID", referencedColumnName = "ID")
    public DalProgramHeader getDalProgramHeader() {
                    return dalProgramHeader;
    }

    public void setDalProgramHeader(DalProgramHeader dalProgramHeader) {
                    this.dalProgramHeader = dalProgramHeader;
    }
 
	@OneToOne
	@JoinColumn(name = "STATUS_ID", referencedColumnName = "ID")
	public DalStatus getStatus() {
		return status;
	}

	public void setStatus(DalStatus status) {
		this.status = status;
	}
	
	@OneToOne
	@JoinColumn(name = "PGM_TYPE_ID", referencedColumnName = "ID")
	public DalProgramType getDalProgramType() {
		return dalProgramType;
	}
	
	public void setDalProgramType(DalProgramType dalProgramType) {
		this.dalProgramType = dalProgramType;
	}

	@OneToMany(mappedBy = "dalProgramDetailWf", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	public List<DalWorkflowStatus> getDalWorkflowStatusList() {
		return dalWorkflowStatusList;
	}
	
	public void setDalWorkflowStatusList(List<DalWorkflowStatus> dalWorkflowStatusList) {
		this.dalWorkflowStatusList = dalWorkflowStatusList;
	}
	
    @Column(name = "EST_ACCRUAL")
	public Integer getEstimatedAccrual() {
		return estimatedAccrual;
	}
	/**
	 * @param estimatedAccrual the estimatedAccrual to set
	 */
	public void setEstimatedAccrual(Integer estimatedAccrual) {
		this.estimatedAccrual = estimatedAccrual;
	}
	
    @OneToOne(targetEntity = DalEmployee.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "CREATED_BY", updatable = false, insertable = true)
    public DalEmployee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(DalEmployee createdBy) {
        this.createdBy = createdBy;
    }

    @OneToOne(targetEntity = DalEmployee.class, fetch = FetchType.EAGER)
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
	
	public void addWorkflowDetails(DalWorkflowStatus dalWorkflowStatus){
		if(this.dalWorkflowStatusList != null){
			this.dalWorkflowStatusList.add(dalWorkflowStatus);
		}
	}
}

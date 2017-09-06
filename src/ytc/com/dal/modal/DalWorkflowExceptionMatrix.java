package ytc.com.dal.modal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "EXCEPTION_WF_MATRIX")
public class DalWorkflowExceptionMatrix{
	
	private Integer id;

	private String customerNumbers;
	
	private DalWorkflowMatrix dalWorkflowMatrix;

	@Column(name = "CUSTOMER_NO")
	public String getCustomerNumbers() {
		return customerNumbers;
	}

	public void setCustomerNumbers(String customerNumbers) {
		this.customerNumbers = customerNumbers;
	}

	@Id
	@Column(name="ID")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne
	@JoinColumn(name = "WF_MATRIX_ID", referencedColumnName = "ID")
	public DalWorkflowMatrix getDalWorkflowMatrix() {
		return dalWorkflowMatrix;
	}

	public void setDalWorkflowMatrix(DalWorkflowMatrix dalWorkflowMatrix) {
		this.dalWorkflowMatrix = dalWorkflowMatrix;
	}
}

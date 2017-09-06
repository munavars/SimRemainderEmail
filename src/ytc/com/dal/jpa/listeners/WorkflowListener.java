package ytc.com.dal.jpa.listeners;

import javax.persistence.PrePersist;

import ytc.com.dal.IDataAccessLayer;
import ytc.com.dal.jpa.BaseDaoImpl;
import ytc.com.dal.modal.DalWorkflowStatus;

public class WorkflowListener {
	IDataAccessLayer baseDao = new BaseDaoImpl();
	private static final String  GET_MAX_COUNT = "SELECT MAX(ID) FROM WORKFLOW_STATUS";
	@PrePersist
	public void updateId(DalWorkflowStatus obj){
		if(obj != null && obj.getId() == null){
			//generate id and set here.
			try {
				int count = baseDao.executeNativeQuery(GET_MAX_COUNT);
				obj.setId(count+1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

package ytc.com.dal;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;


public interface IDataAccessLayer {
	public <T> List<T> getListFromNamedQuery(String namedQueryString) throws Exception;
	
	public <T> T update(T item) throws Exception;
	
	public <T> boolean update(List<T> item) throws Exception;
	
	public <T> List<T> getListFromNamedQueryWithParameter(String namedQueryString, Map<String, Object> parameters) throws Exception;
	
	public <T> T getById(Class<T> clazz, Integer id) throws Exception;
	
	public int executeNativeQuery(String query) throws Exception;
	
	public <T> T updateWithoutTransaction(T item) throws Exception;
	
	public EntityManager getEntityManager() throws Exception;
}

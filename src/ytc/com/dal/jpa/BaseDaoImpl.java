package ytc.com.dal.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import ytc.com.common.PersistenceManager;
import ytc.com.dal.IDataAccessLayer;

public class BaseDaoImpl implements IDataAccessLayer {
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getListFromNamedQuery(String namedQueryString) throws Exception {
		Query query = null;
		List<T> returnList = null;
		if(namedQueryString != null){
			query = getEntityManager().createNamedQuery(namedQueryString);
			returnList = query.getResultList();
		}
		return returnList;
	}
	
	public EntityManager getEntityManager() throws Exception{
		EntityManager em = null;
		EntityManagerFactory emf = PersistenceManager.getInstance().getEntityManagerFactoryInstance();
		em = emf.createEntityManager();
		return em;
	}
	
	@Override
	public <T> T update(T item) throws Exception {
		EntityManager em = getEntityManager();
		em.getTransaction().begin();
		
		
		T t = em.merge(item);
		
		em.flush();
		
		em.getTransaction().commit();
		return t;
	}

	@Override
	public <T> boolean update(List<T> items) throws Exception {
		
		if(items != null ){
			EntityManager em = getEntityManager();
			em.getTransaction().begin();;
			
			for(T item : items){
				em.merge(item);
			}
			em.flush();
			
			em.getTransaction().commit();
		}

		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getListFromNamedQueryWithParameter(String namedQueryString, Map<String, Object> parameters) throws Exception {
		Query query = null;
		List<T> returnList = null;
		if(namedQueryString != null){
			query = getEntityManager().createNamedQuery(namedQueryString);
			for(Map.Entry<String, Object> param : parameters.entrySet()){
				query.setParameter(param.getKey(), param.getValue());
			}
			returnList = query.getResultList();
		}
		return returnList;
	}
	
	@Override
	public <E> E getById(Class<E> clazz, Integer id) throws Exception{
		E entity = null;
		
		if(id != null){
			entity = getEntityManager().find(clazz, id);	
		}
		
		return entity;
	}

	@Override
	public int executeNativeQuery(String queryStr) throws Exception {
		Query query = null;
		int count = 0;
		if(queryStr != null){
			query = getEntityManager().createNativeQuery(queryStr);
			count = (int)query.getSingleResult();
		}
		return count;
	}

	@Override
	public <T> T updateWithoutTransaction(T item) throws Exception {
		/*EntityManager em = getEntityManager();
		em.getTransaction().begin();*/
		
		T t = getEntityManager().merge(item);
		
		/*em.flush();
		
		em.getTransaction().commit();*/
		return t;
	}
}

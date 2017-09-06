package ytc.com.common;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ytc.com.constant.BatchConstant;


/**
 * This class is used to get the Entity Manager factory instance in a singleton
 * factory pattern.
 * @author Cognizant.
 */
public class PersistenceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceManager.class);
    
    private static final PersistenceManager singleton = new PersistenceManager();
    
    protected EntityManagerFactory entityManagerFactory;
    
    /**
     * Static method to get the instance of the PersistenceManager class.
     * @return PersistenceManger instance.
     */
    public static PersistenceManager getInstance(){
        LOGGER.info(PersistenceManager.class.getName() + " : PersistenceManager start");
        
        return singleton;
    }
    
    /**
     * This method is used to get the entityManager instance.
     * @return
     */
    public EntityManagerFactory getEntityManagerFactoryInstance() throws Exception {
        LOGGER.info(PersistenceManager.class.getName() + " : EntityManagerFactory start");
        
        if(entityManagerFactory == null){
            createEntityManagerFactoryInstance();
        }
        
        LOGGER.info(PersistenceManager.class.getName() + " : EntityManagerFactory end");
        return entityManagerFactory;
    }

    /**
     * creates entity manager factory instance.
     * @throws Exception
     */
    protected void createEntityManagerFactoryInstance() throws Exception {
        LOGGER.info(PersistenceManager.class.getName() + " : createEntityManagerFactoryInstance start");
        
        /* Hard coding the environment value to null, as it has no effect in the code here.
         * this is a singleton class and reading the property file might have
         * instantiating with proper environment variable from main method.
         */
        Properties properties = PropertyManager.getInstance().getPropertyInstance(null);
        String userName = null;
        String password = null;
        String url = null;
        if(properties != null){
            LOGGER.info("Property not null. setting the database details..");
            userName = properties.getProperty("USERNAME");
            password = properties.getProperty("PASSWORD");
            url = properties.getProperty("DATABASE_URL");
            
            Map<String, String> persistenceMap = new HashMap<String, String>();
            LOGGER.info("Connecting to database.. name -"+url);
            persistenceMap.put("javax.persistence.jdbc.url", url);
            persistenceMap.put("javax.persistence.jdbc.user", userName);
            persistenceMap.put("javax.persistence.jdbc.password", password);
            persistenceMap.put("javax.persistence.jdbc.driver", BatchConstant.DRIVER);
            
            this.entityManagerFactory = Persistence.createEntityManagerFactory("SalesIncentiveManagementBatch", persistenceMap);
        }
        else{
            LOGGER.info("Property details not set.");
            throw new Exception("Property details not set.");
        }
        LOGGER.info(PersistenceManager.class.getName() + " : createEntityManagerFactoryInstance end");
    }
    
    /**
     * closes the entity manager factory instance.
     */
    public void closeEntityManagerFactoryInstance(){
        if(this.entityManagerFactory != null){
            entityManagerFactory.close();
        }
    }
}
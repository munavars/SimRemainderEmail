package ytc.com.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertyManager.class);
    
    private static final PropertyManager singleton = new PropertyManager();

    protected Properties properties;

    /**
     * Static method to get the instance of the PersistenceManager class.
     * @return PersistenceManger instance.
     */
    public static PropertyManager getInstance(){
        LOGGER.info(PropertyManager.class.getName() + " : inside PropertyManager getInstance");

        return singleton;
    }
    
    /**
     * Method to get the drop down values from property file.
     * @return Properties.
     * @throws Exception
     */
    public Properties getPropertyInstance(String environment) throws Exception {
        LOGGER.info(PropertyManager.class.getName() + " : getPropertyInstance start");
        
        if(properties == null){
            properties = new Properties();
            InputStream in = null;
            try {
                Thread currentThread = Thread.currentThread();
                ClassLoader classLoader = currentThread.getContextClassLoader();
                LOGGER.info(PropertyManager.class.getName() + " current environment :"+environment);
                if(environment == null){
                    LOGGER.info("Environment variable is null. picking the default property file.");
                    in = classLoader.getResourceAsStream("dev-sim-batch.properties");
                }
                else{
                    LOGGER.info("Loading the environment specific property file.");
                    in = classLoader.getResourceAsStream("resources/"+environment + "-sim-batch.properties");
//                    in = ClassLoader.getSystemClassLoader().getResourceAsStream("resources/"+environment + "-sim-batch.properties");
                }
                properties.load(in);
            } catch (IOException e) {
                LOGGER.error("Exception in reading property file", e);
                throw new Exception(e);
            }
        }
        else{
            LOGGER.info("Properity file already read.");
        }

        LOGGER.info(PropertyManager.class.getName() + " : getPropertyInstance end");
        return properties;
    }
}

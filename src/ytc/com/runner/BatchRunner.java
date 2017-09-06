package ytc.com.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ytc.com.common.PersistenceManager;
import ytc.com.service.IYTCProgramService;
import ytc.com.service.impl.YTCProgramServiceImpl;

public class BatchRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(BatchRunner.class);
	
	public static void main(String args[]){
		LOGGER.info(PersistenceManager.class.getName() + " : Batch program starts...");
		//read the inputs from arguments.
		String environment = null;
		if(args != null && args.length > 0){
			LOGGER.debug(PersistenceManager.class.getName() + " : environment to run in :"+args[0]);
			environment = args[0];
		}
		if(environment == null){
			environment = "dev";
			LOGGER.debug(PersistenceManager.class.getName() + " : Loaded default environment :"+environment);
		}
		
		IYTCProgramService ytcProgramService = new YTCProgramServiceImpl();
		
		try {
			ytcProgramService.prepareDataAndTriggerEmail(environment);
			LOGGER.info(PersistenceManager.class.getName() + " : Batch program ended normally...");
		} catch (Exception e) {
			LOGGER.error(PersistenceManager.class.getName() + " : Batch program exited with errors. Please check...");
			e.printStackTrace();
		}
	}
}

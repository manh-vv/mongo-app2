package mvv.app;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mongodb.MongoClient;

public class AppMain {
    private static final Logger log = LogManager.getLogger(AppMain.class);
    
    private MongoClient mongoClient;

    public static void main(String[] args) {
    	long t1 = System.currentTimeMillis();
    	log.info("Start at {}", t1);
    	
    	
    	AppMain main = new AppMain();
    	
    	
    	try {
            log.info("wait 10 s");
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    	
    	log.info("start run");
    	
    	main.mongoClient = new MongoClient("172.16.14.35", 27017); /* comment this line */
    	
//    	main.mongoClient = new MongoClient("10.10.10.186", 27017);
    	
    	
    	
    	UpdateStoresHelper.mongoDatabase = main.mongoClient.getDatabase("newmomovn_db");
    	
    	UpdateStoresHelper updateStoresHelper = new UpdateStoresHelper();
    	List<String> phones = updateStoresHelper.searchStores();
    	
    	
    	// close
    	main.mongoClient.close();
    	
    	// open connector 34
    	main.mongoClient = new MongoClient("172.16.14.34", 27017); /* comment this line */
    	UpdateStoresHelper.mongoDatabase = main.mongoClient.getDatabase("bignoti_db");
    	
    	
    	for (String phone : phones) {
    		updateStoresHelper.detectNotiCtimeBefore30(phone);
    	}
    	
    	
    	// close
    	main.mongoClient.close();
    	
    	long t2 = System.currentTimeMillis();
    	log.info("End at {}, time execute: {}", t2, t2 - t1);
    }
}

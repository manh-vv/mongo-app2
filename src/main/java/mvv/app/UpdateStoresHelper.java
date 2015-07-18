package mvv.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class UpdateStoresHelper {
	private static final Logger log = LogManager
			.getLogger(UpdateStoresHelper.class);

	public static MongoClient mongoClient;
	public static MongoDatabase mongoDatabase;

	//
	// search stores where status = 0
	public List<String> searchStores() {
		log.debug("Open collection: {}", "stores");
		MongoCollection<Document> dbCollection = mongoDatabase
				.getCollection("stores");

		BasicDBObject query = new BasicDBObject();
		query.append("status", 0);
		
		log.debug("Query store: {}", query);

		List<String> phoneHolder = new ArrayList<>(2500);
		FindIterable<Document> storex = dbCollection.find(query);
		String phone;
		for (Document d : storex) {
			phone = d.getString("phone");
			phoneHolder.add(phone);
		}

		log.info("Find store with status = 0: {}", phoneHolder);

		return phoneHolder;
	}

	private long MIL_30_DAY = 30 * 24 * 60 * 60 * 1000L;

	private BasicDBObject tranQuery;

	private void detectTranCtimeBefore30(String phone) {
		if (phone == null || phone.length() < 1) {
			log.warn("[IGNORE] phone = {}", phone);
			return;
		}

		String f;

		if (phone.startsWith("0")) {
			f = phone.substring(1);
		} else {
			log.warn("Phone {} is not begin with 0", phone);
			f = phone;
		}

		String tranTable = "tran_" + f;

		log.info("Open tran table: {}", tranTable);

		MongoCollection<Document> dbCollection = mongoDatabase
				.getCollection(tranTable);

		if (tranQuery == null) {
			long current = System.currentTimeMillis();
			log.debug("Current: {}", current);
			tranQuery = new BasicDBObject();
			tranQuery.append("ctime", new BasicDBObject("$lte", current
					- MIL_30_DAY));

			log.info("Query Trans = {}", tranQuery);
		}

		FindIterable<Document> tranx = dbCollection.find(tranQuery);

//		Map<Object, Long> tranIdHolder = new HashMap<>(100);
		List<BasicDBObject> delQueryHolder = new ArrayList<>(100);


		for (Document d : tranx) {
			Object tId = d.get("_id");
			
			if (! ((tId instanceof String) || (tId instanceof ObjectId))) {
				log.error("[IGNORE] Unknown -- id {}", tId);
				continue;
			}

//			ctime = d.getLong("ctime");

//			tranIdHolder.put(tId, ctime);

			// TODO delete tranID
			BasicDBObject delTranQuery = new BasicDBObject("_id", tId);
			delQueryHolder.add(delTranQuery);

			dbCollection.deleteOne(delTranQuery);
			log.info("---- delete document: {}", d.toJson());
		}

		StringBuilder sb = new StringBuilder(delQueryHolder.size() * 40);
		for (BasicDBObject bo : delQueryHolder) {
			sb.append(bo.toString()).append('|');
		}
		
		log.debug("Delete queries: {}", sb.toString());
		log.info("Delete {} trans ID", delQueryHolder.size());
	}
	
	
	private BasicDBObject notiQuery;
	
	public void detectNotiCtimeBefore30(String phone) {
		if (phone == null || phone.length() < 1) {
			log.warn("[IGNORE] phone = {}", phone);
			return;
		}

		String f;

		if (phone.startsWith("0")) {
			f = phone.substring(1);
		} else {
			log.warn("Phone {} is not begin with 0", phone);
			f = phone;
		}

		String tranTable = "noti_" + f;

		log.info("Open tran table: {}", tranTable);

		MongoCollection<Document> dbCollection = mongoDatabase
				.getCollection(tranTable);

		if (notiQuery == null) {
			long current = System.currentTimeMillis();
			log.debug("Current: {}", current);
			notiQuery = new BasicDBObject();
			notiQuery.append("time", new BasicDBObject("$lte", current
					- MIL_30_DAY));

			log.info("Query Notis = {}", tranQuery);
		}

		FindIterable<Document> notix = dbCollection.find(notiQuery);

//		Map<Object, Long> tranIdHolder = new HashMap<>(100);
		List<BasicDBObject> delQueryHolder = new ArrayList<>(100);


		for (Document d : notix) {
			Object tId = d.get("_id");
			
			if (! ((tId instanceof String) || (tId instanceof ObjectId))) {
				log.error("[IGNORE] Unknown -- id {}", tId);
				continue;
			}

//			ctime = d.getLong("ctime");

//			tranIdHolder.put(tId, ctime);

			// TODO delete tranID
			BasicDBObject delTranQuery = new BasicDBObject("_id", tId);
			delQueryHolder.add(delTranQuery);

			dbCollection.deleteOne(delTranQuery);
			log.info("---- delete document: {}", d.toJson());
		}

		StringBuilder sb = new StringBuilder(delQueryHolder.size() * 40);
		for (BasicDBObject bo : delQueryHolder) {
			sb.append(bo.toString()).append('|');
		}
		
		log.debug("Delete queries: {}", sb.toString());
		log.info("Delete {} noti ID", delQueryHolder.size());
	}
}

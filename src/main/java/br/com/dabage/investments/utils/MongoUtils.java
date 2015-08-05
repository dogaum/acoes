package br.com.dabage.investments.utils;

import org.springframework.stereotype.Component;

import com.mongodb.DBObject;

@Component
public class MongoUtils {


	public static Object getValueFrom(DBObject obj, String key) {
		if (obj == null) {
			return null;
		}
		
		String[] keys = key.split("\\.");
		
		for (int i = 0; i < keys.length - 1; i ++) {
			DBObject temp = (DBObject) obj.get(keys[i]);
			if (temp == null) {
				return null;
			}
			else {
				obj = temp;
			}
		}
		
		return obj.get(keys[keys.length - 1]);
	}
	
}

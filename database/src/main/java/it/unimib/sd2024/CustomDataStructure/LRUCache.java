package it.unimib.sd2024.CustomDataStructure;

import java.io.File;
import java.math.BigInteger;
import java.util.LinkedHashMap;

import it.unimib.sd2024.Database.DBError;
import it.unimib.sd2024.Database.Model.Model;
import it.unimib.sd2024.Database.Model.Order;
import it.unimib.sd2024.Parser.JSONParser;

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
	private final int max_size;
	private final File dir;

	public LRUCache(int initial_capacity, int max_size, File permanent_storage_directory) {
		super(initial_capacity, 0.75f, true);
		this.max_size = max_size;
		this.dir = permanent_storage_directory;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		if (super.size() > max_size) {
			try {
				if (eldest.getKey() instanceof Model) {
					((Model) eldest.getKey()).write2disk(dir);
				} else if (eldest.getValue() instanceof Model) {
					((Model) eldest.getValue()).write2disk(dir);
				}
			} catch (DBError e) {
				e.printStackTrace();
			}

			return true;
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean containsKey(Object key) {
		if (super.containsKey(key)) {
			System.out.println("\t[LRUCache::containsKey::super] order found.");
			return true;
		}

		if (key instanceof String || key instanceof BigInteger) {
			var id = key.toString();
			var file = new File(dir, id + ".json");

			if (!file.exists()) {
				System.err.println("\t[LRUCache::containsKey] Request key `" + key
						+ "` doesn't exists neither in memory nor on the FS. Returning false.");
				return false;
			}

			this.put((K) key, (V) JSONParser.parse(file));
		} else if (key instanceof Order) {
			var order = (Order) key;
			var file = new File(dir, order.getId().toString() + ".json");
			System.out.println("\t[LRUCache::containsKey] received Order: " + order);

			if (!file.exists()) {
				System.err.println(
						"\t[LRUCache::containsKey] Request order doesn't exists neither in memory nor on the FS. Return false.");
				return false;
			}

			this.put((K) JSONParser.parse(file), (V) Boolean.TRUE);
		} else {
			return false;
		}

		return true;
	}
}

package it.unimib.sd2024.Database;

import java.io.File;
import java.util.Map;

import it.unimib.sd2024.CustomDataStructure.LRUCache;
import it.unimib.sd2024.Database.Model.Domain;

public class DatabaseDomainHandler {
	private File dir;
	private Map<String, Domain> domains;

	public DatabaseDomainHandler() {
		this.dir = new File("./db-content/domains");
		this.domains = new LRUCache<>(100, 5000, this.dir);
	}

	public void add_domain(String domain, Domain instance) {
		domains.put(domain, instance);
	}

	public Domain getDomain(String domain_name) {
		if (domains.containsKey(domain_name)) {
		    return domains.get(domain_name);
		}

		return null;
	}

	public void write2fs() throws DBError {
		if (!dir.exists() && !dir.mkdirs()) {
			throw new DBError(DBErrorVariant.CannotCreateDirectoryStructure);
		}

		for (var entry : domains.entrySet()) {
			entry.getValue().write2disk(dir);
		}
	}

	@Override
	public String toString() {
		String str = "\n\t============ Domains ============\n";

		for (var entry : domains.entrySet()) {
			str += entry.getValue().toString() + "\n";
		}

		return str + "\t=================================";
	}
}

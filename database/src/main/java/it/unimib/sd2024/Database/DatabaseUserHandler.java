package it.unimib.sd2024.Database;

import java.io.File;
import java.util.Map;

import it.unimib.sd2024.CustomDataStructure.LRUCache;
import it.unimib.sd2024.Database.Model.User;

public class DatabaseUserHandler {
	private File dir;
	private Map<String, User> users;

	public DatabaseUserHandler() {
		this.dir = new File("./db-content/users");
		this.users = new LRUCache<>(100, 5000, this.dir);
	}

	public User getByEmail(String email) {
		if (users.containsKey(email)) {
			return users.get(email);
		}

		return null;
	}

	public void try_add(String name, String surname, String email) throws DBError {
		if (users.containsKey(email)) {
			throw new DBError(DBErrorVariant.UserAlreadyExists);
		}

		User new_user = new User(name, surname, email);
		users.put(email, new_user);
	}

	public void write2fs() throws DBError {
		if (!dir.exists() && !dir.mkdirs()) {
			throw new DBError(DBErrorVariant.CannotCreateDirectoryStructure);
		}

		for (var entry : users.entrySet()) {
			entry.getValue().write2disk(dir);
		}
	}

	@Override
	public String toString() {
		String str = "\n\t============= Users =============\n";

		for (var entry : users.entrySet()) {
			str += entry.getValue().toString() + "\n";
		}

		return str + "\t=================================";
	}
}

package it.unimib.sd2024.Database;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;

import it.unimib.sd2024.CustomDataStructure.LRUCache;
import it.unimib.sd2024.Database.Model.Domain;
import it.unimib.sd2024.Database.Model.Registration;
import it.unimib.sd2024.Database.Model.User;

public class DatabaseRegistrationHandler {
	private File dir;
	private BigInteger last_id;
	private Map<BigInteger, Registration> registrations;
	private DatabaseUserHandler user_handler;

	public DatabaseRegistrationHandler(DatabaseUserHandler user_handler) {
		this.user_handler = user_handler;
		this.dir = new File("./db-content/registrations");
		this.registrations = new LRUCache<>(100, 5000, this.dir);

		if (dir.exists()) {
			this.last_id = new BigInteger(Integer.toString(dir.listFiles().length));
		} else {
			this.last_id = BigInteger.ZERO;
		}
	}

	public BigInteger getLast_id() {
		return last_id;
	}

	public Registration get_registration(BigInteger id) {
		if (registrations.containsKey(id)) {
			return registrations.get(id);
		}

		return null;
	}

	public Registration try_add(String email, Domain domain, int rent_for) throws DBError {
		if (rent_for > 10) {
			throw new DBError(DBErrorVariant.RegisteredForMoreThanTenYears);
		}

		User user = user_handler.getByEmail(email);
		if (user == null) {
			System.out.println("\t[DatabaseRegistrationHandler::try_add] user not found.");
			throw new DBError(DBErrorVariant.UserDoesNotExists);
		}

		Registration new_registration = new Registration(last_id, email, domain, rent_for);

		user.add_registration(new_registration);
		registrations.put(last_id, new_registration);

		last_id = last_id.add(BigInteger.ONE);

		return new_registration;
	}

	@Override
	public String toString() {
		String str = "\n\t========== Registration =========\n";

		for (Registration reg : registrations.values()) {
			str += reg.toString() + "\n";
		}

		return str + "\t=================================";
	}

	public void write2fs() throws DBError {
		if (!dir.exists() && !dir.mkdirs()) {
			throw new DBError(DBErrorVariant.CannotCreateDirectoryStructure);
		}

		for (var reg : registrations.values()) {
			reg.write2disk(dir);
		}
	}
}

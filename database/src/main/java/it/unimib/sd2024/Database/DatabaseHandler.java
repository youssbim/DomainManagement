package it.unimib.sd2024.Database;

import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import it.unimib.sd2024.Database.Model.Domain;
import it.unimib.sd2024.Database.Model.OrderType;
import it.unimib.sd2024.Database.Model.Registration;
import it.unimib.sd2024.Database.Model.User;

public class DatabaseHandler {
	private static DatabaseHandler instance;

	private DatabaseUserHandler user_handler;
	private DatabaseOrderHandler order_handler;
	private DatabaseDomainHandler domain_handler;
	private DatabaseRegistrationHandler registration_handler;

	// This gets called only once because its a singleton.
	private DatabaseHandler() {
		user_handler = new DatabaseUserHandler();
		domain_handler = new DatabaseDomainHandler();
		registration_handler = new DatabaseRegistrationHandler(user_handler);
		order_handler = new DatabaseOrderHandler(user_handler, domain_handler, registration_handler);

		long delay_1h = 60 * 60 * 1000;

		// Write the DB state to disk every hour.
		new Timer().schedule(new TimerTask() {
			public void run() {
				write2fs();
			}
		}, delay_1h, delay_1h); // First wait 1h then repeat every 1h.

		// Make sure that before exiting the application
		// the DB state is written to disk.
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				write2fs();
			}
		}));
	}

	public static synchronized DatabaseHandler get_instance() {
		if (instance == null) {
			instance = new DatabaseHandler();
		}

		return instance;
	}

	private void write2fs() {
		try {
			user_handler.write2fs();
			domain_handler.write2fs();
			order_handler.write2fs();
			registration_handler.write2fs();
		} catch (DBError e) {
			e.printStackTrace();
		}
	}

	public void log_status() {
		System.out.println(user_handler);
		System.out.println(order_handler);
		System.out.println(registration_handler);
		System.out.println(domain_handler);
	}

	public synchronized void try_add_user(String name, String surname, String email) throws DBError {
		user_handler.try_add(name, surname, email);
		log_status();
	}

	public synchronized void try_add_order(String email, String domain, OrderType type, double payment_amount,
			int rent_for)
			throws DBError {
		order_handler.try_add(email, domain, type, payment_amount, rent_for);
		log_status();
	}

	public String get_domain(String domain_name) {
		Domain domain = domain_handler.getDomain(domain_name);
		if (domain == null) {
			return "{ \"available\": true }";
		}

		User owner = user_handler.getByEmail(domain.getOwner());
		Registration reg = registration_handler.get_registration(domain.getRegistrationId());

		if (reg.has_expired()) {
			return "{ \"available\": true }";
		} else {
			return "{" +
			       "\"available\": false, " +
			       "\"owner\": " + owner + ", " +
			       "\"registered_on\": \"" + reg.getRegistered_on() + "\", " +
			       "\"expires_on\": \"" + reg.getExpires_on() + "\"" +
			       "}";
		}
	}

	public String get_orders_done_by(String email) throws DBError {
	    return order_handler.filter(email);
	}

	public String get_domains_owned_by(String owner) throws DBError {
		User user = user_handler.getByEmail(owner);
		if (user == null) {
			throw new DBError(DBErrorVariant.UserDoesNotExists);
		}

		return "{\"response\": [" + user.getDomain_registrations().stream()
				.map(reg -> String.format("{ \"name\": \"%s\", \"owner\": \"%s\", \"registration\": %s }",
						reg.getReferring_to().getName(), owner, reg.toString()))
				.collect(Collectors.joining(", "))
				+ "]}";
	}

	public String get_user_info(String email) throws DBError {
		var user = user_handler.getByEmail(email);
		if (user == null) {
			throw new DBError(DBErrorVariant.UserDoesNotExists);
		}

		return user.toString();
	}
}

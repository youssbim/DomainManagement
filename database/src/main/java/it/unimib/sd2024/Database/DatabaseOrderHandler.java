package it.unimib.sd2024.Database;

import java.io.File;
import java.math.BigInteger;
import java.util.Map;
import java.util.stream.Collectors;

import it.unimib.sd2024.CustomDataStructure.LRUCache;
import it.unimib.sd2024.Database.Model.Domain;
import it.unimib.sd2024.Database.Model.OrderType;
import it.unimib.sd2024.Database.Model.Registration;
import it.unimib.sd2024.Database.Model.Order;
import it.unimib.sd2024.Database.Model.User;

public class DatabaseOrderHandler {
	private File dir;
	private BigInteger last_id;
	private Map<Order, Boolean> orders;
	private DatabaseUserHandler user_handler;
	private DatabaseDomainHandler domain_handler;
	private DatabaseRegistrationHandler registration_handler;

	public DatabaseOrderHandler(DatabaseUserHandler users, DatabaseDomainHandler domains,
			DatabaseRegistrationHandler registration_handler) {
		this.dir = new File("./db-content/orders");
		this.orders = new LRUCache<>(100, 5000, this.dir);

		this.user_handler = users;
		this.domain_handler = domains;
		this.registration_handler = registration_handler;

		if (dir.exists()) {
			this.last_id = new BigInteger(Integer.toString(dir.listFiles().length));
		} else {
			this.last_id = BigInteger.ZERO;
		}
	}

	@Override
	public String toString() {
		String str = "\n\t============= Orders ============\n";

		for (Order order : orders.keySet()) {
			str += order.toString() + "\n";
		}

		return str + "\t=================================";
	}

	public void try_add(String email, String domain_name, OrderType type, double payment_amount, int rent_for)
			throws DBError {
		User user = user_handler.getByEmail(email);
		if (user == null) {
			System.out.println("\t[DatabaseOrderHandler::try_add] user not found.");
			throw new DBError(DBErrorVariant.UserDoesNotExists);
		}

		Order new_order = null;
		switch (type) {
			case NewRegistration: {
				System.out.println("\t[DatabaseOrderHandler::try_add] NewRegistration request.");

				{
					Domain domain = domain_handler.getDomain(domain_name);
					if (domain != null) {
						System.out.println("\t[DatabaseOrderHandler::try_add] domain found: `" + domain + "`.");
						Registration prev_registration = registration_handler
								.get_registration(domain.getRegistrationId());

						if (prev_registration.has_expired()) {
							System.out.println("\t[DatabaseOrderHandler::try_add] domain has expired.");
							Registration new_registration = registration_handler.try_add(email, domain, rent_for);

							domain.setOwner(email);
							domain.bind(new_registration);
							user.add_order(make_order(email, type, payment_amount, domain));

							System.out.println("\t[DatabaseOrderHandler::try_add] domain registered again.");
							return;
						} else {
							System.out.println("\t[DatabaseOrderHandler::try_add] domain has not expired. Throwing.");
							throw new DBError(DBErrorVariant.DomainAlreadyRegistered);
						}
					}
				}

				Domain new_domain = new Domain(domain_name, email, registration_handler.getLast_id());
				new_order = make_order(email, type, payment_amount, new_domain);

				registration_handler.try_add(email, new_domain, rent_for);
				domain_handler.add_domain(domain_name, new_domain);
			}
				break;
			case Renewal: {
				System.out.println("\t[DatabaseOrderHandler::try_add] Renewal request.");
				Domain domain = domain_handler.getDomain(domain_name);
				if (domain == null) {
					System.out.println("\t[DatabaseOrderHandler::try_add] domain not found.");
					throw new DBError(DBErrorVariant.DomainNotRegistered);
				}

				System.out.println("\t[DatabaseOrderHandler::try_add] domain found: `" + domain + "`.");
				if (!domain.getOwner().equals(email)) {
					System.out.println("\t[DatabaseOrderHandler::try_add] domain not owned by `" + email + "`.");
					throw new DBError(DBErrorVariant.DomainOwnedByDifferentUser);
				}

				Registration registration = registration_handler.get_registration(domain.getRegistrationId());
				if (registration.has_expired()) {
					System.out.println("\t[DatabaseOrderHandler::try_add] domain has expired and cannot be renewed. Register it again. Throwing.");
					throw new DBError(DBErrorVariant.RenewalRequestedForExpiredDomain);
				}

				System.out.println("\t[DatabaseOrderHandler::try_add] domain hasn't expired and can be renewed.");
				registration.update_registration_period(rent_for);
				new_order = make_order(email, type, payment_amount, domain);

				System.out.println("\t[DatabaseOrderHandler::try_add] ok.");
			}
				break;
		}

		user.add_order(new_order);
		orders.put(new_order, true);

		last_id = last_id.add(BigInteger.ONE);
	}

	public Order make_order(String email, OrderType type, double payment_amount, Domain domain) throws DBError {
		var new_order = new Order(last_id, email, type, payment_amount, domain);

		if (orders.containsKey(new_order)) {
			System.out.println("\t[DatabaseOrderHandler::try_add] order found: `"
					+ orders.get(new_order) + "`.");
			throw new DBError(DBErrorVariant.OrderAlreadyExists);
		}

		return new_order;
	}

	public void write2fs() throws DBError {
		if (!dir.exists() && !dir.mkdirs()) {
			throw new DBError(DBErrorVariant.CannotCreateDirectoryStructure);
		}

		for (var order : orders.keySet()) {
			order.write2disk(dir);
		}
	}

	public String filter(String email) throws DBError {
		var user = user_handler.getByEmail(email);
		if (user == null) {
			throw new DBError(DBErrorVariant.UserDoesNotExists);
		}

		return "{\"response\": [" + user.getOrders().stream().map(Order::toString).collect(Collectors.joining(", "))
				+ "]}";
	}
}

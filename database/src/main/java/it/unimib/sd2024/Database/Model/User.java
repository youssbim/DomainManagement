package it.unimib.sd2024.Database.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.unimib.sd2024.Database.DBError;

public class User extends Model {
	String name;
	String surname;
	String email;
	List<Registration> domain_registrations;
	List<Order> orders;

	public User(String name, String surname, String email) {
		this(name, surname, email, new ArrayList<>(), new ArrayList<>());
	}

	public User(String name, String surname, String email, List<Registration> domain_registrations,
			List<Order> orders) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.domain_registrations = domain_registrations;
		this.orders = orders;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getEmail() {
		return email;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public List<Registration> getDomain_registrations() {
		return domain_registrations;
	}

	public List<Domain> getDomains() {
		return new ArrayList<>() {
			{
				domain_registrations.forEach((reg) -> add(reg.getReferring_to()));
			}
		};
	}

	public void add_order(Order new_order) {
		orders.add(new_order);
	}

	public void add_registration(Registration new_registration) {
		domain_registrations.add(new_registration);
	}

	@Override
	public String toString() {
		StringBuilder res = new StringBuilder("{" +
				"\"name\": \"" + name + "\"," +
				"\"surname\": \"" + surname + "\"," +
				"\"email\": \"" + email + "\"," +
				"\"registered_domains\": [");

		res.append(domain_registrations.stream().map(Registration::toString).collect(Collectors.joining(",")));
		res.append("],\"orders\": [");
		res.append(orders.stream().map(Order::toString).collect(Collectors.joining(",")));
		res.append("]}");

		return res.toString();
	}

	@Override
	public File write2disk(File dir) throws DBError {
		return super.write2disk(dir, this.email);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (surname == null) {
			if (other.surname != null)
				return false;
		} else if (!surname.equals(other.surname))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
}

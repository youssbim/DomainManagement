package it.unimib.sd2024.Database.Model;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;

import it.unimib.sd2024.Database.DBError;

public class Order extends Model {
	BigInteger id;
	LocalDateTime orded_on;
	OrderType type;
	double payment_amount;
	Domain referring_to;
	String done_by;

	public Order(BigInteger id, String done_by, OrderType type, double payment_amount, Domain referring_to) {
		this(id, done_by, type, payment_amount, referring_to, LocalDateTime.now());
	}

	public Order(BigInteger id, String done_by, OrderType type, double payment_amount, Domain referring_to,
			LocalDateTime ordered_on) {
		this.id = id;
		this.done_by = done_by;
		this.orded_on = ordered_on;
		this.type = type;
		this.payment_amount = payment_amount;
		this.referring_to = referring_to;
	}

	public BigInteger getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());

		long temp = Double.doubleToLongBits(payment_amount);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((orded_on == null) ? 0 : orded_on.hashCode());
		result = prime * result + ((referring_to == null) ? 0 : referring_to.hashCode());
		result = prime * result + ((done_by == null) ? 0 : done_by.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Order other = (Order) obj;
		if (type != other.type)
			return false;

		switch (type) {
			case NewRegistration: {
				if (referring_to == null && other.referring_to != null) {
					return false;
				} else if (!referring_to.equals(other.referring_to))
					return false;
				if (done_by == null && other.done_by != null) {
					return false;
				} else if (!done_by.equals(other.done_by))
					return false;
			}
				break;
			case Renewal:
				if (Double.doubleToLongBits(payment_amount) != Double.doubleToLongBits(other.payment_amount))
					return false;
				if (!orded_on.equals(other.orded_on))
					return false;
				if (referring_to == null && other.referring_to != null) {
					return false;
				} else if (!referring_to.equals(other.referring_to))
					return false;
				if (done_by == null && other.done_by != null) {
					return false;
				} else if (!done_by.equals(other.done_by))
					return false;
				break;
		}

		return true;
	}

	@Override
	public String toString() {
		return "{" +
				"\"id\": " + id + "," +
				"\"ordered_on\": \"" + orded_on + "\"," +
				"\"type\": " + type + "," +
				"\"payment_amount\": " + payment_amount + "," +
				"\"referring_to\": " + referring_to + "," +
				"\"done_by\": \"" + done_by + "\"" +
				"}";
	}

	@Override
	public File write2disk(File dir) throws DBError {
		return super.write2disk(dir, this.id.toString());
	}
}

package it.unimib.sd2024.Database.Model;

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDateTime;

import it.unimib.sd2024.Database.DBError;
import it.unimib.sd2024.Database.DBErrorVariant;

public class Registration extends Model {
	private BigInteger id;
	private String done_by;
	private Domain referring_to;
	private LocalDateTime registered_on;
	private LocalDateTime expires_on;

	public Registration(BigInteger id, String done_by, Domain referring_to, int registered_for_years) {
		this(id, done_by, referring_to, registered_for_years, LocalDateTime.now());
	}

	public Registration(BigInteger id, String done_by, Domain referring_to, int registered_for_years,
			LocalDateTime registered_on) {
		this(id, done_by, referring_to, LocalDateTime.now(), LocalDateTime.now().plusYears(registered_for_years));
	}

	public Registration(BigInteger id, String done_by, Domain referring_to, LocalDateTime registered_on,
			LocalDateTime expires_on) {
		this.id = id;
		this.done_by = done_by;
		this.referring_to = referring_to;
		this.registered_on = registered_on;
		this.expires_on = expires_on;
	}

	public BigInteger getId() {
		return id;
	}

	public LocalDateTime getRegistered_on() {
		return registered_on;
	}

	public LocalDateTime getExpires_on() {
		return expires_on;
	}

	public Domain getReferring_to() {
		return referring_to;
	}

	public void update_registration_period(int rent_for) throws DBError {
		if (expires_on.plusYears(rent_for).getYear() - registered_on.getYear() > 10) {
			throw new DBError(DBErrorVariant.RenewedForMoreThanTenYears);
		}

		expires_on = expires_on.plusYears(rent_for);
	}

	@Override
	public String toString() {
		return "{" +
				"\"id\": " + id + ", " +
				"\"done_by\": \"" + done_by + "\", " +
				"\"referring_to\": " + referring_to + ", " +
				"\"registered_on\": \"" + registered_on + "\", " +
				"\"expires_on\": \"" + expires_on + "\"" +
				"}";
	}

	@Override
	public File write2disk(File dir) throws DBError {
		return super.write2disk(dir, this.id.toString());
	}

	public boolean has_expired() {
		return expires_on.isBefore(LocalDateTime.now());
	}
}

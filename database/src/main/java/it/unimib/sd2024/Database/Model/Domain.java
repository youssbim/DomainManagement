package it.unimib.sd2024.Database.Model;

import java.io.File;
import java.math.BigInteger;

import it.unimib.sd2024.Database.DBError;

public class Domain extends Model {
	String name;
	String owner;
	BigInteger registration_id;

	public Domain(String name, String owner_email, BigInteger registration_id) {
		this.name = name;
		this.owner = owner_email;
		this.registration_id = registration_id;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public BigInteger getRegistrationId() {
		return registration_id;
	}

	public void setOwner(String email) {
		this.owner = email;
	}

	public void bind(Registration registration) {
		registration_id = registration.getId();
	}

	@Override
	public String toString() {
		return "{" +
				"\"name\": \"" + name + "\"," +
				"\"owner\": \"" + owner + "\"," +
				"\"registration_id\": " + registration_id + "" +
				"}";
	}

	@Override
	public File write2disk(File dir) throws DBError {
		return super.write2disk(dir, this.name);
	}
}

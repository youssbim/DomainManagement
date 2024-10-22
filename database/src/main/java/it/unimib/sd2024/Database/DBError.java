package it.unimib.sd2024.Database;

public class DBError extends Throwable {
	DBErrorVariant err;

	public DBError(DBErrorVariant err) {
		this.err = err;
	}

	@Override
	public String toString() {
		return err.toString();
	}
}

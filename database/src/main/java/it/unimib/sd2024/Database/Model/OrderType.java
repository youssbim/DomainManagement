package it.unimib.sd2024.Database.Model;

public enum OrderType {
	NewRegistration,
	Renewal;

	@Override
	public String toString() {
		switch (this) {
			case NewRegistration:
				return "0";
			case Renewal:
				return "1";
			default:
				return "InvalidOrder";
		}
	}
}

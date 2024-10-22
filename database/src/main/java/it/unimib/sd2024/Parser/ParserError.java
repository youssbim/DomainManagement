package it.unimib.sd2024.Parser;

class ParserError extends Exception {
	ParserErrorVariant err;

	public ParserError(ParserErrorVariant err) {
		this.err = err;
	}

	@Override
	public String toString() {
		return err.toString();
	}

	@Override
	public void printStackTrace() {
		System.err.println(err);
	}
}

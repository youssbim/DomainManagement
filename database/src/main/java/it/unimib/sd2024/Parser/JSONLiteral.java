package it.unimib.sd2024.Parser;

class JSONLiteral extends JSONValue {
	String literal;

	public JSONLiteral(String literal) {
		this.literal = literal;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JSONLiteral other = (JSONLiteral) obj;
		if (literal == null) {
			if (other.literal != null)
				return false;
		} else if (!literal.equals(other.literal))
			return false;
		return true;
	}
}

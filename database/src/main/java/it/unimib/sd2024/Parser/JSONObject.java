package it.unimib.sd2024.Parser;

import java.util.HashMap;
import java.util.Map;

class JSONObject extends JSONValue {
	private Map<String, JSONValue> values;

	public JSONObject() {
		this.values = new HashMap<>();
	}

	public Map<String, JSONValue> getValues() {
		return values;
	}

	public void addKeyValuePair(String key, JSONValue value) {
		values.put(key, value);
	}

	public JSONValue getValue(String key) {
		return values.get(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
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
		JSONObject other = (JSONObject) obj;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}
}

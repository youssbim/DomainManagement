package it.unimib.sd2024.Parser;

import java.util.List;

class JSONArray extends JSONValue {
	private List<JSONObject> childrens;

	public JSONArray(List<JSONObject> childrens) {
		this.childrens = childrens;
	}

	public List<JSONObject> getChildrens() {
		return childrens;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JSONArray other = (JSONArray) obj;
		if (childrens == null) {
			if (other.childrens != null)
				return false;
		} else if (!childrens.equals(other.childrens))
			return false;
		return true;
	}
}

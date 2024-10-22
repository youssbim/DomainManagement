package it.unimib.sd2024.Parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class ParserTest {
	@Test
	public void emptyJSON() throws ParserError {
		String content = "{}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		assertEquals(0, found.size());
	}

	@Test
	public void invalidJSON() {
		String content = "{\"prova\"}";

		try {
			JSONParser.parse(content);
		} catch (ParserError e) {
			if (e.err == ParserErrorVariant.INVALID_JSON_ENTRY) {
				return;
			}
		}

		fail();
	}

	@Test
	public void validJSONWithIntegerLiteralvalue() throws ParserError {
		String content = "{\"prova\": 23}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				put("prova", new JSONLiteral("23"));
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}

	@Test
	public void validJSONWithDoubleLiteralvalue() throws ParserError {
		String content = "{\"prova\": 23.42}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				put("prova", new JSONLiteral("23.42"));
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}

	@Test
	public void validJSONWithStringLiteralvalue() throws ParserError {
		String content = "{\"prova\": \"valore prova\"}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				put("prova", new JSONLiteral("valore prova"));
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}

	@Test
	public void validJSONWithMultipleKeyLiteralValue() throws ParserError {
		String content = "{\"prova\": \"valore prova\", \"prova2\": 23, \"prova3\": 23.42}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				put("prova", new JSONLiteral("valore prova"));
				put("prova2", new JSONLiteral("23"));
				put("prova3", new JSONLiteral("23.42"));
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}

	@Test
	public void complexJSONObject() throws ParserError {
		String content = "{\"prova\": {\"uno\": 1, \"due\": 2, \"tre\": 3.0}}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				var obj = new JSONObject();
				obj.addKeyValuePair("uno", new JSONLiteral("1"));
				obj.addKeyValuePair("due", new JSONLiteral("2"));
				obj.addKeyValuePair("tre", new JSONLiteral("3.0"));

				put("prova", obj);
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}

	@Test
	public void invalidComplexJSONObject() throws ParserError {
		String content = "{\"prova\": {\"uno\": 1, \"due\": 2, \"tre\": 3.0}";
		try {
			JSONParser.parse(content).getValues();
		} catch (ParserError e) {
			// Parsing didn't even start.
			if (e.err != ParserErrorVariant.UNBALANCED_BRACES) {
				fail();
			}
		}
	}

	@Test
	public void complexJSONObjectWithArray() throws ParserError {
		String content = "{\"prova\": [{\"uno\": 1}, {\"due\": 2}, {\"tre\": 3.0}]}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				var obj = new JSONObject();
				obj.addKeyValuePair("uno", new JSONLiteral("1"));
				var obj2 = new JSONObject();
				obj2.addKeyValuePair("due", new JSONLiteral("2"));
				var obj3 = new JSONObject();
				obj3.addKeyValuePair("tre", new JSONLiteral("3.0"));

				put("prova", new JSONArray(new ArrayList<>() {
					{
						add(obj);
						add(obj2);
						add(obj3);
					}
				}));
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}

	@Test
	public void JSONObjectWithEmptyArray() throws ParserError {
		String content = "{\"prova\": []}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				put("prova", new JSONArray(new ArrayList<>()));
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}

	@Test
	public void complexJSONObjectWithNestedArray() throws ParserError {
		String content = "{\"prova\": [{\"uno\": [{\"due\": 2}]}, {\"tre\": [{\"quattro\": 4}]}]}";
		Map<String, JSONValue> found = JSONParser.parse(content).getValues();

		Map<String, JSONValue> expected = new HashMap<>() {
			{
				var obj = new JSONObject();
				obj.addKeyValuePair("uno", new JSONArray(new ArrayList<>() {
					{
						var obj = new JSONObject();
						obj.addKeyValuePair("due", new JSONLiteral("2"));

						add(obj);
					}
				}));

				var obj3 = new JSONObject();
				obj3.addKeyValuePair("tre", new JSONArray(new ArrayList<>() {
					{
						var obj = new JSONObject();
						obj.addKeyValuePair("quattro", new JSONLiteral("4"));

						add(obj);
					}
				}));

				put("prova", new JSONArray(new ArrayList<>() {
					{
						add(obj);
						add(obj3);
					}
				}));
			}
		};

		assertTrue(found.size() == expected.size());
		assertEquals(expected, found);
	}
}

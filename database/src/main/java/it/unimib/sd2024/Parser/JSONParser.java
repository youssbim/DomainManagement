package it.unimib.sd2024.Parser;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import it.unimib.sd2024.Database.Model.Domain;
import it.unimib.sd2024.Database.Model.Order;
import it.unimib.sd2024.Database.Model.OrderType;
import it.unimib.sd2024.Database.Model.Registration;
import it.unimib.sd2024.Database.Model.User;

public final class JSONParser {
	private JSONParser() {
		throw new UnsupportedOperationException("JSONParser cannot be instantiated.");
	}

	public static Object parse(File file) {
		Object res = null;
		String path = file.getPath();

		try {
			JSONObject obj = parse(new String(Files.readAllBytes(file.toPath())));

			if (path.contains(String.format("db-content%susers", File.separator))) {
				res = make_user(obj);
			} else if (path.contains(String.format("db-content%sdomains", File.separator))) {
				res = make_domain(obj);
			} else if (path.contains(String.format("db-content%sorders", File.separator))) {
				res = make_order(obj);
			} else if (path.contains(String.format("db-content%sregistrations", File.separator))) {
				res = make_registration(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	private static Registration make_registration(JSONObject obj) throws ParserError {
		try {
			var id = ((JSONLiteral) obj.getValue("id")).literal;
			var done_by = ((JSONLiteral) obj.getValue("done_by")).literal;
			var referring_to = make_domain((JSONObject) obj.getValue("referring_to"));
			var registered_on = LocalDateTime.parse(((JSONLiteral) obj.getValue("registered_on")).literal);
			var expires_on = LocalDateTime.parse(((JSONLiteral) obj.getValue("expires_on")).literal);

			return new Registration(new BigInteger(id), done_by, referring_to, registered_on, expires_on);
		} catch (Exception e) {
			System.out.println("\n\t\tOrder creation");
			throw new ParserError(ParserErrorVariant.OBJECT_MISSING_EXPECTED_KEY);
		}
	}

	private static User make_user(JSONObject obj) throws ParserError {
		try {
			String name = ((JSONLiteral) obj.getValue("name")).literal;
			String surname = ((JSONLiteral) obj.getValue("surname")).literal;
			String email = ((JSONLiteral) obj.getValue("email")).literal;
			List<JSONObject> json_registrations = ((JSONArray) obj.getValue("registered_domains")).getChildrens();
			List<JSONObject> json_orders = ((JSONArray) obj.getValue("orders")).getChildrens();

			List<Registration> registered_domains = new ArrayList<>();
			for (JSONObject json_domain : json_registrations) {
				registered_domains.add(make_registration(json_domain));
			}

			List<Order> orders = new ArrayList<>();
			for (JSONObject json_order : json_orders) {
				orders.add(make_order(json_order));
			}

			return new User(name, surname, email, registered_domains, orders);
		} catch (Exception e) {
			System.out.println("\n\t\tUser creation");
			throw new ParserError(ParserErrorVariant.OBJECT_MISSING_EXPECTED_KEY);
		}
	}

	private static Domain make_domain(JSONObject obj) throws ParserError {
		try {
			var domain_name = ((JSONLiteral) obj.getValue("name")).literal;
			var owner = ((JSONLiteral) obj.getValue("owner")).literal;
			var registration_id = new BigInteger(((JSONLiteral) obj.getValue("registration_id")).literal);

			return new Domain(domain_name, owner, registration_id);
		} catch (Exception e) {
			System.out.println("\n\t\tDomain creation");
			throw new ParserError(ParserErrorVariant.OBJECT_MISSING_EXPECTED_KEY);
		}
	}

	private static Order make_order(JSONObject obj) throws ParserError {
		try {
			var id = ((JSONLiteral) obj.getValue("id")).literal;
			var ordered_on = ((JSONLiteral) obj.getValue("ordered_on")).literal;
			var type = Integer.parseInt(((JSONLiteral) obj.getValue("type")).literal);
			var payment_amount = Double.parseDouble(((JSONLiteral) obj.getValue("payment_amount")).literal);
			var done_by = ((JSONLiteral) obj.getValue("done_by")).literal;
			var referring_to = make_domain((JSONObject) obj.getValue("referring_to"));

			return new Order(new BigInteger(id), done_by, type == 0 ? OrderType.NewRegistration : OrderType.Renewal,
					payment_amount, referring_to, LocalDateTime.parse(ordered_on));
		} catch (Exception e) {
			System.out.println("\n\t\tOrder creation");
			throw new ParserError(ParserErrorVariant.OBJECT_MISSING_EXPECTED_KEY);
		}
	}

	static JSONObject parse(String content) throws ParserError {
		var token_iter = tokenize(content).iterator();
		if (token_iter.next().type != TokenType.LEFT_BRACE) {
			throw new ParserError(ParserErrorVariant.INVALID_JSON_OBJECT);
		}

		return parse_object(token_iter, new JSONObject());
	}

	private static JSONObject parse_object(Iterator<Token> token_iter, JSONObject obj) throws ParserError {
		var key = token_iter.next();
		if (key.type == TokenType.RIGHT_BRACE) {
			return obj;
		}
		if (key.type != TokenType.STRING) {
			throw new ParserError(ParserErrorVariant.INVALID_JSON_ENTRY);
		}

		if (token_iter.next().type != TokenType.COLON) {
			throw new ParserError(ParserErrorVariant.INVALID_JSON_ENTRY);
		}

		var value = token_iter.next();
		switch (value.type) {
			case INTEGER:
			case DOUBLE:
			case STRING: {
				obj.addKeyValuePair(key.lexeme, new JSONLiteral(value.lexeme));
			}
				break;
			case LEFT_SQUARE: {
				List<JSONObject> childrens = new ArrayList<>();

				Token token;
				while ((token = token_iter.next()).type != TokenType.RIGHT_SQUARE) {
					if (token.type != TokenType.LEFT_BRACE) {
						throw new ParserError(ParserErrorVariant.INVALID_JSON_OBJECT);
					}

					childrens.add(parse_object(token_iter, new JSONObject()));

					token = token_iter.next();
					if (token.type == TokenType.COMMA) {
						continue;
					} else if (token.type == TokenType.RIGHT_SQUARE) {
						break;
					} else {
						throw new ParserError(ParserErrorVariant.INVALID_JSON_OBJECT);
					}
				}

				obj.addKeyValuePair(key.lexeme, new JSONArray(childrens));
			}
				break;
			case LEFT_BRACE: {
				obj.addKeyValuePair(key.lexeme, parse_object(token_iter, new JSONObject()));
			}
				break;
			default:
				throw new ParserError(ParserErrorVariant.INVALID_JSON_ENTRY);
		}

		switch (token_iter.next().type) {
			case COMMA:
				parse_object(token_iter, obj);
				break;
			case RIGHT_BRACE:
				break;
			default:
				throw new ParserError(ParserErrorVariant.INVALID_JSON_ENTRY);
		}

		return obj;
	}

	static List<Token> tokenize(String content) throws ParserError {
		List<Token> tokens = new ArrayList<Token>();
		Stack<Boolean> braceStack = new Stack<>();
		Stack<Boolean> squareStack = new Stack<>();

		for (int i = 0; i < content.length(); i++) {
			String lexeme = "";
			char ch = content.charAt(i);

			switch (ch) {
				case ' ':
				case '\t':
				case '\n':
				case '\r':
					continue;
			}

			if (is_number(ch)) {
				do {
					lexeme += ch;
				} while (++i < content.length() && is_number(ch = content.charAt(i)));

				if (ch == '.' && is_number(content.charAt(i + 1))) {
					do {
						lexeme += ch;
					} while (++i < content.length() && is_number(ch = content.charAt(i)));

					tokens.add(new Token(TokenType.DOUBLE, lexeme));
				} else {
					tokens.add(new Token(TokenType.INTEGER, lexeme));
				}

				i--;
				continue;
			}

			switch (ch) {
				case '{': {
					braceStack.push(true);
					tokens.add(new Token(TokenType.LEFT_BRACE, null));
				}
					break;
				case '}': {
					try {
						braceStack.pop();
					} catch (Exception e) {
						throw new ParserError(ParserErrorVariant.UNBALANCED_BRACES);
					}

					tokens.add(new Token(TokenType.RIGHT_BRACE, null));
				}
					break;
				case '[': {
					squareStack.push(true);
					tokens.add(new Token(TokenType.LEFT_SQUARE, null));
				}
					break;
				case ']': {
					try {
						squareStack.pop();
					} catch (Exception e) {
						throw new ParserError(ParserErrorVariant.UNBALANCED_SQUARED);
					}

					tokens.add(new Token(TokenType.RIGHT_SQUARE, null));
				}
					break;
				case ',': {
					tokens.add(new Token(TokenType.COMMA, null));
				}
					break;
				case ':': {
					tokens.add(new Token(TokenType.COLON, null));
				}
					break;
				case '"': {
					while (++i < content.length() && content.charAt(i) != '"') {
						lexeme += content.charAt(i);
					}

					if (i >= content.length()) {
						throw new ParserError(ParserErrorVariant.UNTERMINATED_STRING);
					}

					tokens.add(new Token(TokenType.STRING, lexeme));
				}
					break;
				default: {
					throw new ParserError(ParserErrorVariant.INVALID_SYMBOL);
				}
			}
		}

		if (!braceStack.empty()) {
			throw new ParserError(ParserErrorVariant.UNBALANCED_BRACES);
		}
		if (!squareStack.empty()) {
			throw new ParserError(ParserErrorVariant.UNBALANCED_SQUARED);
		}

		return tokens;
	}

	private static boolean is_number(char ch) {
		return ch >= '0' && ch <= '9';
	}
}

package it.unimib.sd2024.Parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TokenizationTest {
	@Test
	public void invalidJSON() {
		String content = "prova";

		try {
			JSONParser.tokenize(content);
		} catch (ParserError e) {
			if (e.err == ParserErrorVariant.INVALID_SYMBOL) {
				return;
			}
		}

		fail();
	}

	@Test
	public void invalidJSONString() {
		String content = "\"Hello, World!";

		try {
			JSONParser.tokenize(content);
		} catch (ParserError e) {
			if (e.err == ParserErrorVariant.UNTERMINATED_STRING) {
				return;
			}
		}

		fail();
	}

	@Test
	public void validJSONString() throws ParserError {
		String content = "\"Hello, World!\"";

		List<Token> found = JSONParser.tokenize(content);
		List<Token> expected = new ArrayList<>() {
			{
				add(new Token(TokenType.STRING, "Hello, World!"));
			}
		};

		assertEquals(expected.size(), found.size());
		for (int i = 0; i < found.size(); i++) {
			assertEquals(expected.get(i).type, found.get(i).type);
			assertEquals(expected.get(i).lexeme, found.get(i).lexeme);
		}
	}

	@Test
	public void JSONDoubleWith2Dots() throws ParserError {
		String content = "42..42";

		try {
			JSONParser.tokenize(content);
		} catch (ParserError e) {
			if (e.err == ParserErrorVariant.INVALID_SYMBOL) {
				return;
			}
		}

		fail();
	}

	@Test
	public void validJSONDouble() throws ParserError {
		String content = "42.42";

		List<Token> found = JSONParser.tokenize(content);
		List<Token> expected = new ArrayList<>() {
			{
				add(new Token(TokenType.DOUBLE, "42.42"));
			}
		};

		assertEquals(expected.size(), found.size());
		for (int i = 0; i < found.size(); i++) {
			assertEquals(expected.get(i).type, found.get(i).type);
			assertEquals(expected.get(i).lexeme, found.get(i).lexeme);
		}
	}

	@Test
	public void validJSONInteger() throws ParserError {
		String content = "42";

		List<Token> found = JSONParser.tokenize(content);
		List<Token> expected = new ArrayList<>() {
			{
				add(new Token(TokenType.INTEGER, "42"));
			}
		};

		assertEquals(expected.size(), found.size());
		for (int i = 0; i < found.size(); i++) {
			assertEquals(expected.get(i).type, found.get(i).type);
			assertEquals(expected.get(i).lexeme, found.get(i).lexeme);
		}
	}

	@Test
	public void stringWithALotOfSpaces() throws ParserError {
		String content = "                                   \"Hello, World!\"";

		List<Token> found = JSONParser.tokenize(content);
		List<Token> expected = new ArrayList<>() {
			{
				add(new Token(TokenType.STRING, "Hello, World!"));
			}
		};

		assertEquals(expected.size(), found.size());
		for (int i = 0; i < found.size(); i++) {
			assertEquals(expected.get(i).type, found.get(i).type);
			assertEquals(expected.get(i).lexeme, found.get(i).lexeme);
		}
	}

	@Test
	public void unbalancedSquareParenthesis() throws ParserError {
		String content = "[[[[]]";

		try {
			JSONParser.tokenize(content);
		} catch (ParserError e) {
			if (e.err == ParserErrorVariant.UNBALANCED_SQUARED) {
				return;
			}
		}

		fail();
	}

	@Test
	public void unbalancedBraces() throws ParserError {
		String content = "{{{{}";

		try {
			JSONParser.tokenize(content);
		} catch (ParserError e) {
			if (e.err == ParserErrorVariant.UNBALANCED_BRACES) {
				return;
			}
		}

		fail();
	}

	@Test
	public void balancedBraces() throws ParserError {
		String content = "{\"prova\"}";

		List<Token> found = JSONParser.tokenize(content);
		List<Token> expected = new ArrayList<>() {
			{
				add(new Token(TokenType.LEFT_BRACE, null));
				add(new Token(TokenType.STRING, "prova"));
				add(new Token(TokenType.RIGHT_BRACE, null));
			}
		};

		assertEquals(expected.size(), found.size());
		for (int i = 0; i < found.size(); i++) {
			assertEquals(expected.get(i).type, found.get(i).type);
			assertEquals(expected.get(i).lexeme, found.get(i).lexeme);
		}
	}
}

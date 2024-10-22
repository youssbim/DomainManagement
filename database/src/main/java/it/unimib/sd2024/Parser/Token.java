package it.unimib.sd2024.Parser;

class Token {
	TokenType type;
	String lexeme;

	public Token(TokenType type, String lexeme) {
		this.type = type;
		this.lexeme = lexeme;
	}
}

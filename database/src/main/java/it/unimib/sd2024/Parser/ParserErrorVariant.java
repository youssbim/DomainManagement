package it.unimib.sd2024.Parser;

enum ParserErrorVariant {
	UNTERMINATED_STRING,
	INVALID_SYMBOL,
	UNBALANCED_BRACES,
	UNBALANCED_SQUARED,
	INVALID_JSON_OBJECT,
	INVALID_JSON_ENTRY,
	OBJECT_MISSING_EXPECTED_KEY,
}

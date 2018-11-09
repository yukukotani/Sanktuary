package land.mog.sanktuary

import java.io.Reader

/**
 *  @author Mon_chi
 */
class Lexer(private val reader: Reader) {

    companion object {
        private val TOKEN_EOF = Token(TokenType.EOF, null)
        private val TOKEN_EQUAL = Token(TokenType.EQUAL, null)
        private val TOKEN_LEFT_BRACKET = Token(TokenType.LEFT_BRACKET, null)
        private val TOKEN_RIGHT_BRACKET = Token(TokenType.RIGHT_BRACKET, null)
        private val TOKEN_LEFT_BRACE = Token(TokenType.LEFT_BRACE, null)
        private val TOKEN_RIGHT_BRACE = Token(TokenType.RIGHT_BRACE, null)
        private val TOKEN_COMMA = Token(TokenType.COMMA, null)
    }

    private var next = reader.read().let {
        if (it == -1) null else it.toChar()
    }
    private var index = 0

    private fun isEOF() = next == null
    private fun isWhitespace() = next == ' '
    private fun isNewLine() = next == '\n' || next == '\r'

    fun nextToken(): Token = findToken()

    private fun findToken(): Token {
        val char = readNext() ?: return TOKEN_EOF

        return when(char) {
            ' ', '\r', '\n' -> findToken()
            '=' -> TOKEN_EQUAL
            '[' -> TOKEN_LEFT_BRACKET
            ']' -> TOKEN_RIGHT_BRACKET
            '{' -> TOKEN_LEFT_BRACE
            '}' -> TOKEN_RIGHT_BRACE
            ',' -> TOKEN_COMMA
            '\'' -> lexQuotedText()
            '\"' -> lexDoubleQuotedText()
            '#' -> lexComment()
            else -> lexValue(char)
        }
    }

    private fun lexComment(): Token {
        val value = buildString {
            do {
                append(next)
                readNext()
            } while (!isEOF() && !isNewLine())
        }

        return Token(TokenType.COMMENT, value)
    }

    private fun lexQuotedText(): Token {
        val value = buildString {
            do {
                if (isNewLine()) TODO("multi-line string")
                append(next)
                readNext()
            } while (next != '\'')
        }

        readNext()
        return Token(TokenType.QUOTED_TEXT, value)
    }

    private fun lexDoubleQuotedText(): Token {
        val value = buildString {
            do {
                if (isNewLine()) TODO("multi-line string")
                append(next)
                readNext()
            } while (next != '\"')
        }

        readNext()
        return Token(TokenType.DOUBLE_QUOTED_TEXT, value)
    }

    private fun lexValue(char: Char): Token {
        val value = buildString {
            append(char)
            do {
                append(next)
                readNext()
            } while (!isEOF() && !isWhitespace() && !isNewLine() && next != ',')
        }

        return Token(TokenType.TEXT, value)
    }

    private fun readNext(): Char? {
        return next.apply {
            next = reader.read().let {
                if (it == -1) null else it.toChar()
            }
            index += 1
        }
    }
}

enum class TokenType {
    EOF,
    COMMA,
    EQUAL,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMENT,
    TEXT,
    QUOTED_TEXT,
    DOUBLE_QUOTED_TEXT
}

data class Token(val type: TokenType, val value: Any?)


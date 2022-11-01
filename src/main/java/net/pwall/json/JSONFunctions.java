/*
 * @(#) JSONFunctions.java
 *
 * json-functions  Functions for use in JSON parsing and formatting
 * Copyright (c) 2021, 2022 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.pwall.json;

import java.io.IOException;
import java.util.function.IntConsumer;

import net.pwall.text.TextMatcher;
import net.pwall.util.IntOutput;

/**
 * A set of static functions used in conversion to and from JSON string representations.
 *
 * @author  Peter Wall
 */
public class JSONFunctions {

    public static final String UNTERMINATED_STRING = "Unterminated JSON string";
    public static final String ILLEGAL_CHAR = "Illegal character in JSON string";
    public static final String ILLEGAL_UNICODE_SEQUENCE = "Illegal Unicode sequence in JSON string";
    public static final String ILLEGAL_ESCAPE_SEQUENCE = "Illegal escape sequence in JSON string";

    /**
     * Append a {@link CharSequence} to an {@link Appendable} in JSON quoted string form (applying JSON escaping rules).
     * The characters above the ASCII range ({@code 0x20} to {@code 0x7E}) are output as Unicode escape sequences unless
     * the {@code includeNonASCII} flag is set to {@code true}.
     *
     * @param   a                   the {@link Appendable}
     * @param   cs                  the {@link CharSequence}
     * @param   includeNonASCII     if {@code true}, output the characters above the ASCII range without escaping
     * @throws  IOException         if thrown by the {@link Appendable}
     */
    public static void appendString(Appendable a, CharSequence cs, boolean includeNonASCII) throws IOException {
        a.append('"');
        for (int i = 0, n = cs.length(); i < n; i++)
            appendChar(a, cs.charAt(i), includeNonASCII);
        a.append('"');
    }

    /**
     * Output a {@link CharSequence} using an {@link IntConsumer} in JSON quoted string form (applying JSON escaping
     * rules).  The characters above the ASCII range ({@code 0x20} to {@code 0x7E}) are output as Unicode escape
     * sequences unless the {@code includeNonASCII} flag is set to {@code true}.
     *
     * @param   cs                  the {@link CharSequence}
     * @param   includeNonASCII     if {@code true}, output the characters above the ASCII range without escaping
     * @param   consumer            the {@link IntConsumer}
     */
    public static void outputString(CharSequence cs, boolean includeNonASCII, IntConsumer consumer) {
        consumer.accept('"');
        for (int i = 0, n = cs.length(); i < n; i++)
            outputChar(cs.charAt(i), includeNonASCII, consumer);
        consumer.accept('"');
    }

    /**
     * Convert a {@link CharSequence} to a {@link String} in JSON quoted string form (applying JSON escaping rules).
     * The characters above the ASCII range ({@code 0x20} to {@code 0x7E}) are output as Unicode escape sequences unless
     * the {@code includeNonASCII} flag is set to {@code true}.
     *
     * @param   cs                  the {@link CharSequence}
     * @param   includeNonASCII     if {@code true}, output the characters above the ASCII range without escaping
     * @return  the converted string
     */
    public static String escapeString(CharSequence cs, boolean includeNonASCII) {
        StringBuilder sb = new StringBuilder();
        try {
            appendString(sb, cs, includeNonASCII);
        }
        catch (IOException ignore) {
            // Can't happen - StringBuilder doesn't throw exception
        }
        return sb.toString();
    }

    /**
     * Convert a {@link CharSequence} to a {@link String}, applying JSON escaping rules with without enclosing quotes.
     * The characters above the ASCII range ({@code 0x20} to {@code 0x7E}) are output as Unicode escape sequences unless
     * the {@code includeNonASCII} flag is set to {@code true}.  If there are no characters requiring conversion, the
     * original string is returned unmodified.
     *
     * @param   cs                  the {@link CharSequence}
     * @param   includeNonASCII     if {@code true}, output the characters above the ASCII range without escaping
     * @return  the converted string
     */
    public static String escapeStringUnquoted(CharSequence cs, boolean includeNonASCII) {
        for (int i = 0, n = cs.length(); i < n; i++) {
            char ch = cs.charAt(i);
            if (ch < ' ' || ch >= '\u007F' || ch == '"' || ch == '\\') {
                try {
                    StringBuilder sb = new StringBuilder();
                    if (i > 0)
                        sb.append(cs, 0, i);
                    while (true) {
                        appendChar(sb, ch, includeNonASCII);
                        if (++i >= n)
                            break;
                        ch = cs.charAt(i);
                    }
                    return sb.toString();
                }
                catch (IOException e) {
                    // Can't happen - StringBuilder doesn't throw exception
                }
            }
        }
        return cs.toString();
    }

    /**
     * Convert a {@link CharSequence} to a {@link String}, applying JSON escaping rules with without enclosing quotes.
     * If there are no characters requiring conversion, the original string is returned unmodified.
     *
     * @param   cs                  the {@link CharSequence}
     * @return  the converted string
     */
    public static String escapeStringUnquoted(CharSequence cs) {
        return escapeStringUnquoted(cs, false);
    }

    /**
     * Append a single character to an {@link Appendable} applying JSON escaping rules.  The characters above the ASCII
     * range ({@code 0x20} to {@code 0x7E}) are output as Unicode escape sequences unless the {@code includeNonASCII}
     * flag is set to {@code true}.
     *
     * @param   a                   the {@link Appendable}
     * @param   ch                  the character
     * @param   includeNonASCII     if {@code true}, output the characters above the ASCII range without escaping
     * @throws  IOException         if thrown by the {@link Appendable}
     */
    public static void appendChar(Appendable a, char ch, boolean includeNonASCII) throws IOException {
        if (ch == '"' || ch == '\\') {
            a.append('\\');
            a.append(ch);
        }
        else if (ch == '\b')
            a.append("\\b");
        else if (ch == '\f')
            a.append("\\f");
        else if (ch == '\n')
            a.append("\\n");
        else if (ch == '\r')
            a.append("\\r");
        else if (ch == '\t')
            a.append("\\t");
        else if (ch < 0x20 || ch >= 0x7F && ch < 0xA0 || ch >= 0xA0 && !includeNonASCII) {
            a.append("\\u");
            IntOutput.append4Hex(a, ch);
        }
        else
            a.append(ch);
    }

    /**
     * Output a single character using an {@link IntConsumer} applying JSON escaping rules.  The characters above the
     * ASCII range ({@code 0x20} to {@code 0x7E}) are output as Unicode escape sequences unless the
     * {@code includeNonASCII} flag is set to {@code true}.
     *
     * @param   ch                  the character
     * @param   includeNonASCII     if {@code true}, output the characters above the ASCII range without escaping
     * @param   consumer            the {@link IntConsumer}
     */
    public static void outputChar(char ch, boolean includeNonASCII, IntConsumer consumer) {
        if (ch == '"' || ch == '\\') {
            consumer.accept('\\');
            consumer.accept(ch);
        }
        else if (ch == '\b') {
            consumer.accept('\\');
            consumer.accept('b');
        }
        else if (ch == '\f') {
            consumer.accept('\\');
            consumer.accept('f');
        }
        else if (ch == '\n') {
            consumer.accept('\\');
            consumer.accept('n');
        }
        else if (ch == '\r') {
            consumer.accept('\\');
            consumer.accept('r');
        }
        else if (ch == '\t') {
            consumer.accept('\\');
            consumer.accept('t');
        }
        else if (ch < 0x20 || ch >= 0x7F && ch < 0xA0 || ch >= 0xA0 && !includeNonASCII) {
            consumer.accept('\\');
            consumer.accept('u');
            IntOutput.output4Hex(ch, consumer);
        }
        else
            consumer.accept(ch);
    }

    /**
     * Create a display form of a string, usually for error reporting.  The string is constrained to a maximum number of
     * characters, and if it exceeds that number the string is split and "<code> ... </code>" is inserted in the middle.
     *
     * @param   str         the string
     * @param   maxChars    the maximum number of characters
     * @return              the display string
     */
    public static String displayString(String str, int maxChars) {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        try {
            int i = 0;
            int n = str.length();
            if (maxChars > 7 && n > maxChars) {
                int m = (maxChars - 4) >> 1;
                while (i < m)
                    appendChar(sb, str.charAt(i++), true);
                sb.append(" ... ");
                i = n - ((maxChars - 5) >> 1);
            }
            while (i < n)
                appendChar(sb, str.charAt(i++), true);
        }
        catch (IOException ignore) {
            // can't happen - StringBuilder doesn't throw exception
        }
        sb.append('"');
        return sb.toString();
    }

    /**
     * Parse a JSON string from the current position of a {@link TextMatcher} (which must be positioned after the
     * opening double quote).  The index is left positioned after the closing double quote.
     *
     * @param   tm              a {@link TextMatcher}
     * @return                  the JSON string
     * @throws  IllegalArgumentException  if there are any errors in the JSON
     */
    public static String parseString(TextMatcher tm) {
        int start = tm.getIndex();
        while (true) {
            if (tm.isAtEnd())
                throw new IllegalArgumentException(UNTERMINATED_STRING);
            char ch = tm.nextChar();
            if (ch == '"')
                return tm.getString(start, tm.getStart());
            if (ch == '\\')
                break;
            if (ch < 0x20)
                throw new IllegalArgumentException(ILLEGAL_CHAR);
        }
        StringBuilder sb = new StringBuilder(tm.getCharSeq(start, tm.getStart()));
        while (true) {
            if (tm.isAtEnd())
                throw new IllegalArgumentException(UNTERMINATED_STRING);
            char ch = tm.nextChar();
            if (ch == '"')
                sb.append('"');
            else if (ch == '\\')
                sb.append('\\');
            else if (ch == '/')
                sb.append('/');
            else if (ch == 'b')
                sb.append('\b');
            else if (ch == 'f')
                sb.append('\f');
            else if (ch == 'n')
                sb.append('\n');
            else if (ch == 'r')
                sb.append('\r');
            else if (ch == 't')
                sb.append('\t');
            else if (ch == 'u') {
                if (!tm.matchHex(4, 4))
                    throw new IllegalArgumentException(ILLEGAL_UNICODE_SEQUENCE);
                sb.append((char)tm.getResultHexInt());
            }
            else
                throw new IllegalArgumentException(ILLEGAL_ESCAPE_SEQUENCE);
            while (true) {
                if (tm.isAtEnd())
                    throw new IllegalArgumentException(UNTERMINATED_STRING);
                ch = tm.nextChar();
                if (ch == '"')
                    return sb.toString();
                if (ch == '\\')
                    break;
                if (ch < 0x20)
                    throw new IllegalArgumentException(ILLEGAL_CHAR);
                sb.append(ch);
            }
        }
    }

    /**
     * Test whether a given character is a space, according to the JSON specification.
     *
     * @param   ch          the character
     * @return              {@code true} if the character is a space
     */
    public static boolean isSpaceCharacter(int ch) {
        return ch == ' ' || ch == '\n' || ch == '\t' || ch == '\r';
    }

}

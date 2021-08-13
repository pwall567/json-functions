/*
 * @(#) JSONFunctions.java
 *
 * json-functions  Functions for use in JSON parsing and formatting
 * Copyright (c) 2021 Peter Wall
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

import net.pwall.text.TextMatcher;

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

    private static final char[] digits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'
    };

    private static final char[] tensDigits = {
            '0', '0', '0', '0', '0', '0', '0', '0', '0', '0',
            '1', '1', '1', '1', '1', '1', '1', '1', '1', '1',
            '2', '2', '2', '2', '2', '2', '2', '2', '2', '2',
            '3', '3', '3', '3', '3', '3', '3', '3', '3', '3',
            '4', '4', '4', '4', '4', '4', '4', '4', '4', '4',
            '5', '5', '5', '5', '5', '5', '5', '5', '5', '5',
            '6', '6', '6', '6', '6', '6', '6', '6', '6', '6',
            '7', '7', '7', '7', '7', '7', '7', '7', '7', '7',
            '8', '8', '8', '8', '8', '8', '8', '8', '8', '8',
            '9', '9', '9', '9', '9', '9', '9', '9', '9', '9'
    };

    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    /**
     * Append an {@code int} to an {@link Appendable}.  This method outputs the digits left to right, avoiding the need
     * to allocate a separate object to hold the string form.
     *
     * @param   a           the {@link Appendable}
     * @param   i           the {@code int}
     * @throws IOException if thrown by the {@link Appendable}
     */
    public static void appendInt(Appendable a, int i) throws IOException {
        if (i < 0) {
            if (i == Integer.MIN_VALUE)
                a.append("-2147483648");
            else {
                a.append('-');
                appendPositiveInt(a, -i);
            }
        }
        else
            appendPositiveInt(a, i);
    }

    /**
     * Append a positive {@code int} to an {@link Appendable}.  This method outputs the digits left to right, avoiding
     * the need to allocate a separate object to hold the string form.
     *
     * @param   a           the {@link Appendable}
     * @param   i           the {@code int}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendPositiveInt(Appendable a, int i) throws IOException {
        if (i >= 100) {
            int n = i / 100;
            appendPositiveInt(a, n);
            i -= n * 100;
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else if (i >= 10) {
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else
            a.append(digits[i]);
    }

    /**
     * Append a {@code long} to an {@link Appendable}.  This method outputs the digits left to right, avoiding the need
     * to allocate a separate object to hold the string form.
     *
     * @param   a           the {@link Appendable}
     * @param   n           the {@code long}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendLong(Appendable a, long n) throws IOException {
        if (n < 0) {
            if (n == Long.MIN_VALUE)
                a.append("-9223372036854775808");
            else {
                a.append('-');
                appendPositiveLong(a, -n);
            }
        }
        else
            appendPositiveLong(a, n);
    }

    /**
     * Append a positive {@code long} to an {@link Appendable}.  This method outputs the digits left to right, avoiding
     * the need to allocate a separate object to hold the string form.
     *
     * @param   a           the {@link Appendable}
     * @param   n           the {@code long}
     * @throws  IOException if thrown by the {@link Appendable}
     */
    public static void appendPositiveLong(Appendable a, long n) throws IOException {
        if (n >= 100) {
            long m = n / 100;
            appendPositiveLong(a, m);
            int i = (int)(n - m * 100);
            a.append(tensDigits[i]);
            a.append(digits[i]);
        }
        else {
            int i = (int)n;
            if (i >= 10)
                a.append(tensDigits[i]);
            a.append(digits[i]);
        }
    }

    /**
     * Append a {@link CharSequence} to an {@link Appendable} in JSON quoted string form (using JSON escaping rules).
     * The characters above the ASCII range ({@code 0x20} to {@code 0x7E}) are output as Unicode escape sequences unless
     * the {@code includeNonASCII} flag is set to {@code true}.
     *
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
        else if (ch < 0x20 || ch >= 0x7F && !includeNonASCII) {
            a.append("\\u");
            a.append(hexDigits[(ch >> 12) & 0xF]);
            a.append(hexDigits[(ch >> 8) & 0xF]);
            a.append(hexDigits[(ch >> 4) & 0xF]);
            a.append(hexDigits[ch & 0xF]);
        }
        else
            a.append(ch);
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

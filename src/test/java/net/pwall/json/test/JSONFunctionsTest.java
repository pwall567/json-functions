/*
 * @(#) JSONFunctionsTest.java
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

package net.pwall.json.test;

import java.io.IOException;
import java.util.function.IntConsumer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.pwall.json.JSONFunctions;
import net.pwall.text.TextMatcher;

public class JSONFunctionsTest {

    @Test
    public void shouldCorrectlyTestForSpace() {
        assertTrue(JSONFunctions.isSpaceCharacter(' '));
        assertTrue(JSONFunctions.isSpaceCharacter('\t'));
        assertTrue(JSONFunctions.isSpaceCharacter('\n'));
        assertTrue(JSONFunctions.isSpaceCharacter('\r'));
        assertFalse(JSONFunctions.isSpaceCharacter('A'));
        assertFalse(JSONFunctions.isSpaceCharacter('0'));
        assertFalse(JSONFunctions.isSpaceCharacter('\f'));
        assertFalse(JSONFunctions.isSpaceCharacter('\0'));
    }

    @Test
    public void shouldFormatStringCorrectly() throws IOException {
        StringBuilder sb = new StringBuilder();
        JSONFunctions.appendString(sb, "hello", false);
        assertEquals("\"hello\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.appendString(sb, "hello\n", false);
        assertEquals("\"hello\\n\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.appendString(sb, "", false);
        assertEquals("\"\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.appendString(sb, "mdash \u2014 \r\n", false);
        assertEquals("\"mdash \\u2014 \\r\\n\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.appendString(sb, "mdash \u2014 \r\n", true);
        assertEquals("\"mdash \u2014 \\r\\n\"", sb.toString());
    }

    @Test
    public void shouldFormatStringCorrectlyUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        JSONFunctions.outputString("hello", false, ic);
        assertEquals("\"hello\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.outputString("hello\n", false, ic);
        assertEquals("\"hello\\n\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.outputString("", false, ic);
        assertEquals("\"\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.outputString("mdash \u2014 \r\n", false, ic);
        assertEquals("\"mdash \\u2014 \\r\\n\"", sb.toString());
        sb.setLength(0);
        JSONFunctions.outputString("mdash \u2014 \r\n", true, ic);
        assertEquals("\"mdash \u2014 \\r\\n\"", sb.toString());
    }

    @Test
    public void shouldFormatSingleChar() throws IOException {
        StringBuilder sb = new StringBuilder();
        JSONFunctions.appendChar(sb, 'A', false);
        assertEquals("A", sb.toString());
        sb.setLength(0);
        JSONFunctions.appendChar(sb, '\t', false);
        assertEquals("\\t", sb.toString());
        sb.setLength(0);
        JSONFunctions.appendChar(sb, '\u2014', false);
        assertEquals("\\u2014", sb.toString());
        sb.setLength(0);
        JSONFunctions.appendChar(sb, '\u2014', true);
        assertEquals("\u2014", sb.toString());
    }

    @Test
    public void shouldFormatSingleCharUsingLambda() {
        StringBuilder sb = new StringBuilder();
        IntConsumer ic = ch -> sb.append((char)ch);
        JSONFunctions.outputChar('A', false, ic);
        assertEquals("A", sb.toString());
        sb.setLength(0);
        JSONFunctions.outputChar('\t', false, ic);
        assertEquals("\\t", sb.toString());
        sb.setLength(0);
        JSONFunctions.outputChar('\u2014', false, ic);
        assertEquals("\\u2014", sb.toString());
        sb.setLength(0);
        JSONFunctions.outputChar('\u2014', true, ic);
        assertEquals("\u2014", sb.toString());
    }

    @Test
    public void shouldFormatDisplayString() {
        assertEquals("\"A\"", JSONFunctions.displayString("A", 20));
        assertEquals("\"ABCDEF\"", JSONFunctions.displayString("ABCDEF", 20));
        assertEquals("\"ABCDEFGHIJKLMNOPQRST\"", JSONFunctions.displayString("ABCDEFGHIJKLMNOPQRST", 20));
        assertEquals("\"ABCDEFGH ... OPQRSTU\"", JSONFunctions.displayString("ABCDEFGHIJKLMNOPQRSTU", 20));
        assertEquals("\"ABCDEFGH ... TUVWXYZ\"", JSONFunctions.displayString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 20));
        assertEquals("\"ABCDEFGH ... OPQRSTUV\"", JSONFunctions.displayString("ABCDEFGHIJKLMNOPQRSTUV", 21));
        assertEquals("\"ABCDEFGH ... STUVWXYZ\"", JSONFunctions.displayString("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 21));
    }

    @Test
    public void shouldParseSimpleString() {
        TextMatcher tm = new TextMatcher("\"simple\"");
        tm.setIndex(1);
        assertEquals("simple", JSONFunctions.parseString(tm));
        assertTrue(tm.isAtEnd());
    }

    @Test
    public void shouldParseStringWithEscapeSequences() {
        TextMatcher tm = new TextMatcher("\"tab\\tnewline\\nquote\\\" \"");
        tm.setIndex(1);
        assertEquals("tab\tnewline\nquote\" ", JSONFunctions.parseString(tm));
        assertTrue(tm.isAtEnd());
    }

    @Test
    public void shouldParseStringWithUnicodeEscapeSequence() {
        TextMatcher tm = new TextMatcher("\"mdash \\u2014\"");
        tm.setIndex(1);
        assertEquals("mdash \u2014", JSONFunctions.parseString(tm));
        assertTrue(tm.isAtEnd());
    }

    @Test
    public void shouldThrowExceptionOnMissingClosingQuote() {
        TextMatcher tm = new TextMatcher("\"abc");
        tm.setIndex(1);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> JSONFunctions.parseString(tm));
        assertEquals("Unterminated JSON string", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnBadEscapeSequence() {
        String text = "\"ab\\c\"";
        TextMatcher tm = new TextMatcher(text);
        tm.setIndex(1);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> JSONFunctions.parseString(tm));
        assertEquals("Illegal escape sequence in JSON string", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnBadUnicodeSequence() {
        String text = "\"ab\\uxxxx\"";
        TextMatcher tm = new TextMatcher(text);
        tm.setIndex(1);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> JSONFunctions.parseString(tm));
        assertEquals("Illegal Unicode sequence in JSON string", e.getMessage());
    }

    @Test
    public void shouldThrowExceptionOnIllegalCharacter() {
        String text = "\"ab\u0001\"";
        TextMatcher tm = new TextMatcher(text);
        tm.setIndex(1);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> JSONFunctions.parseString(tm));
        assertEquals("Illegal character in JSON string", e.getMessage());
    }

}

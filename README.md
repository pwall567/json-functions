# json-functions

[![Build Status](https://travis-ci.com/pwall567/json-functions.svg?branch=main)](https://app.travis-ci.com/github/pwall567/json-functions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/net.pwall.json/json-functions?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.pwall.json%22%20AND%20a:%22json-functions%22)

Functions for use in JSON parsing and formatting

## Usage

These functions are used by other libraries to perform common functions related to JSON parsing and formatting.

The output functions take two alternative forms:

- functions that append to an `Appendable` (usually a `StringBuilder`, but possibly a `Writer` or some other class
  implementing that interface)
- functions that output characters to an `IntConsumer` (the `IntConsumer` is always the final parameter, allowing Kotlin
  users to provide a lambda in the Kotlin idiomatic form)

The functions that use `Appendable` are all declared as throwing `IOException`, but they only do so if the `Appendable`
implementation class does so.
If the functions are used with a `StringBuilder`, the exception may safely be ignored.

There is also an `escapeString` function that returns the string in its escaped form, along with a version of this
function that omits the enclosing quotes.

The parsing functions make use of the [`textmatcher`](https://github.com/pwall567/textmatcher) library.
The `TextMatcher` class holds a string to be parsed, with indexes to represent the start and end of the current parsed
element.

---

## `JSONFunctions`

The functions are static members of the `JSONFunctions` class.

### `appendString`

This function appends a JSON string to an `Appendable`, enclosing it in quotes and escaping special characters
according to the [JSON specification](https://www.rfc-editor.org/rfc/rfc8259.html#section-7).
The parameters are:
- the `Appendable`
- the string to be output (actually a `CharSequence`)
- a boolean indicator &ndash; if set to `true`, characters above the base ASCII set will be output unencoded; if set to
  `false`, any such characters will be output as Unicode escape sequences

Example:
```java
    JSONFunctions.appendString(stringBuilder, "Müller Straße", false);
```
will output:
```
"M\u00FCller Stra\u00DFe"
```

### `outputString`

This function outputs a string to an `IntConsumer` character at a time, preceded and followed by quote characters, and
with special characters escaped according to the
[JSON specification](https://www.rfc-editor.org/rfc/rfc8259.html#section-7).
The parameters are:
- the string to be output (actually a `CharSequence`)
- a boolean indicator (see the description above under `appendString`)
- the `IntConsumer` output function

Example:
```java
    JSONFunctions.outputString("Müller Straße", false, ch -> outputStream.write(ch));
```

### `escapeString`

This function converts a JSON string to another string, enclosing it in quotes and escaping special characters
according to the [JSON specification](https://www.rfc-editor.org/rfc/rfc8259.html#section-7).
The parameters are:
- the string to be output (actually a `CharSequence`)
- a boolean indicator &ndash; if set to `true`, characters above the base ASCII set will be output unencoded; if set to
  `false`, any such characters will be output as Unicode escape sequences

The function returns the converted string.

Example:
```java
    String escaped = JSONFunctions.escapeString("Müller Straße", false);
```
will return:
```
"M\u00FCller Stra\u00DFe"
```

### `escapeStringUnquoted`

This function converts a JSON string to an escaped form of the string like the above function, but without the enclosing
quotes.
The parameters are:
- the string to be output (actually a `CharSequence`)
- a boolean indicator &ndash; if set to `true`, characters above the base ASCII set will be output unencoded; if set to
  `false`, any such characters will be output as Unicode escape sequences

The function returns the converted string, or if no characters in the string require conversion, it returns the original
string unmodified (avoiding the need for object allocations).

Example:
```java
    String escaped = JSONFunctions.escapeStringUnquoted("Müller Straße", false);
```
will return:
```
M\u00FCller Stra\u00DFe
```

### `appendChar`

This appends a single character to an `Appendable` with the appropriate escaping.
The parameters are:
- the `Appendable`
- the character to be output
- a boolean indicator (see the description above under `appendString`)

Example:
```java
    JSONFunctions.appendChar(stringBuilder, '£', false);
```
will output:
```
\u00A3
```

### `outputChar`

This outputs a single character to an `IntConsumer` with the appropriate escaping.
The parameters are:
- the character to be output
- a boolean indicator (see the description above under `appendString`)
- the `IntConsumer` output function

Example:
```java
    JSONFunctions.outputChar('£', false, ch -> outputStream.write(ch));
```
will output:
```
\u00A3
```

### `displayString`

There is often a requirement to display a JSON string in an error message, limiting the output to a maximum number of
characters.
This function converts a string a form suitable for display, abbreviating it if necessary by splicing the characters
&ldquo;` ... `&rdquo; into the middle.
The parameters are:
- the string
- the maximum number of characters 

Example:
```java
    String message = JSONFunctions.displayString("the quick brown fox jumps over the lazy dog", 21);
```
will return:
```
"the quic ... lazy dog"
```

### `parseString`

The `parseString` function parses an input string, decoding JSON escape sequences and terminating on an unescaped double
quote character.
The function makes use of the [`textmatcher`](https://github.com/pwall567/textmatcher) library; the `TextMatcher` class
allows a parse position index to be maintained along with the text, and this is used by the `parseString` function to
determine where to start decoding, and to indicate where parsing completed.

The single parameter to the function is:
- the `TextMatcher`, with the `index` positioned **after** the opening double quote character

The function returns the decoded string, and the `index` of the `TextMatcher` is left positioned **after** the closing
double quote.

### `isSpaceCharacter`

This function simply tests whether a character is a whitespace character according to the
[JSON specification](https://www.rfc-editor.org/rfc/rfc8259.html#section-2).

---

## Dependency Specification

The latest version of the library is 1.6, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.json</groupId>
      <artifactId>json-functions</artifactId>
      <version>1.6</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.json:json-functions:1.6'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.json:json-functions:1.6")
```

Peter Wall

2022-11-01

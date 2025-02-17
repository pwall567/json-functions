# Change Log

The format is based on [Keep a Changelog](http://keepachangelog.com/).

## [2.0] - 2025-01-28
### Added
- `build.yml`, `deploy.yml`: converted project to GitHub Actions
### Changed
- `pom.xml`: moved to `io.jstuff` (package amd Maven group)
- `pom.xml`: updated dependency versions
- `JSONFunctions`: improved comments on `displayString()`
### Removed
- `.travis.yml`

## [1.9] - 2023-12-02
### Changed
- `pom.xml`: updated dependency versions

## [1.8] - 2023-11-10
### Changed
- `JSONFunctions`: use lower case for hexadecimal \uxxxx constructs
- `pom.xml`: removed JUnit5 (it was causing build failures)
- `pom.xml`: updated plugin and dependency versions

## [1.7.2] - 2022-11-23
### Changed
- `pom.xml`: bumped dependency version

## [1.7.1] - 2022-11-21
### Changed
- `pom.xml`: bumped dependency version

## [1.7] - 2022-11-19
### Changed
- `JSONFunctions`: changed `isSpaceCharacter()` to take `char` argument
- `pom.xml`: bumped dependency version

## [1.6] - 2022-11-01
### Changed
- `JSONFunctions`: added `escapeString` and `escapeStringUnquoted`
- `pom.xml`: bumped dependency version

## [1.5] - 2022-05-31
### Changed
- `JSONFunctions`: added output functions taking `IntConsumer`

## [1.4.2] - 2022-05-01
### Changed
- `pom.xml`: bumped dependency version

## [1.4.1] - 2022-04-11
### Changed
- `pom.xml`: bumped dependency version

## [1.4] - 2022-01-27
### Changed
- `pom.xml`: bumped dependency versions

## [1.3.1] - 2021-09-16
### Changed
- `pom.xml`: bumped dependency version

## [1.3] - 2021-08-25
### Changed
- `JSONFunctions`: removed functions transferred to `int-output`

## [1.2] - 2021-08-21
### Changed
- `JSONFunctions`: add `append2Digits` and `append3Digits`
- `JSONFunctions`: add `displayString`

## [1.1] - 2021-08-14
### Changed
- `JSONFunctions`: split `appendChar` out into separate function

## [1.0] - 2021-07-29
### Added
- all files: initial versions

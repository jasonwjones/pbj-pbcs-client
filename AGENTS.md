# AGENTS.md

These instructions apply to the entire repository.

## Project and Toolchain

- This is a Java client library built with Maven.
- Use Java 17. Keep `maven.compiler.release` and CI Java versions aligned at 17 unless a compatibility change is explicitly requested.
- Use the included Maven Wrapper (`./mvnw`, or `mvnw.cmd` on Windows) so local, CI, and release builds use the pinned Maven version.
- Do not add frameworks, build plugins, or dependencies unless they provide clear value for the requested work.

## Standard Maven Commands

- Run local unit/component tests: `./mvnw test`
- Run the normal CI-equivalent build: `./mvnw verify -Dgpg.skip=true`
- Generate Javadoc: `./mvnw javadoc:javadoc`
- Build without signing local artifacts: `./mvnw package -Dgpg.skip=true`
- Follow the release process documented in `README.md`. Do not run `release:perform`; tag-triggered GitHub Actions owns deployment.

The normal build must not contact a live EPM Cloud environment.

## Tests and Live-System Safety

- Name isolated unit and component tests `*Test`. Maven Surefire runs these during routine builds.
- Name tests that require a live service `*IT`. Maven Failsafe runs them only through an explicit integration-test profile.
- Categorize every live integration test with exactly the appropriate JUnit category:
  - `ReadOnlyIntegrationTest` for operations that only inspect live state.
  - `DestructiveIntegrationTest` for operations that may change data, metadata, jobs, variables, files, configuration, or any other live state.
- If an operation's effects are uncertain, classify it as destructive.
- Do not activate either integration profile unless the user explicitly authorizes live-server testing for the current task.
- Never run destructive integration tests without both explicit user authorization and the required `-DallowDestructiveEpmTests=true` acknowledgement.
- Read-only live tests use:
  `./mvnw verify -Pintegration-read-only -Dgpg.skip=true`
- Destructive live tests use:
  `./mvnw verify -Pintegration-destructive -DallowDestructiveEpmTests=true -Dgpg.skip=true`
- Live credentials normally come from `~/pbcs-client.properties`; an alternate file may be selected with `-Dpbcs.test.credentials=/path/to/file`.
- Missing credentials should produce an explicit skip or failure before any client operation. Do not silently fall back to placeholder values.
- Prefer unit tests with fixtures, stubs, or local serialization checks over adding live integration coverage.

## Java Style and Formatting

- Follow the existing package structure, naming, indentation, imports, and nearby code style.
- Long Java lines are acceptable. Do not wrap logger calls, exception messages, fluent chains, method invocations, or argument lists merely to satisfy an arbitrary line-length limit.
- Break a long statement only when the split materially improves readability or clarifies logical structure.
- Avoid broad mechanical reformatting. Keep diffs focused on the requested change.
- Use descriptive names and straightforward control flow. Avoid clever abstractions that obscure REST behavior.
- Preserve existing null-handling and validation conventions unless the task explicitly improves them.

## Javadoc

- Public API classes, interfaces, enums, constructors, and methods should have complete, valid, useful Javadoc.
- Document parameters, return values, thrown exceptions, important nullability expectations, side effects, and live-service behavior where applicable.
- Javadoc should explain API contracts rather than restating names or implementation details.
- Keep links and inline code valid, and run `./mvnw javadoc:javadoc` when changing public API documentation.
- Do not introduce new Javadoc warnings in touched public APIs. Fix directly related warnings when practical.

## Public API Compatibility

- Treat public and protected types, constructors, methods, constants, generic signatures, serialized field names, and documented behavior as compatibility-sensitive.
- Do not remove or incompatibly change public API without explicit approval.
- Prefer additive changes, overloads, default methods, or deprecation before removal.
- Preserve Jackson annotations and REST payload shapes unless an API correction is intentional and tested.
- When changing equals/hashCode, enum values, exception types, validation, or default behavior, consider downstream binary and source compatibility.
- Add focused tests for public behavior and serialization/deserialization changes.

## Secrets and Sensitive Data

- Never commit credentials, access tokens, refresh tokens, passwords, private keys, tenant identifiers intended to be private, or populated connection-property files.
- Never print secret values in commands, test output, logs, diffs, examples, or responses.
- Do not inspect or export private-key material unless the user explicitly requests that operation. Public key IDs and fingerprints may be used when needed.
- GitHub Actions secret values must remain in GitHub Secrets. Workflows currently expect:
  - `MAVEN_GPG_PRIVATE_KEY`
  - `MAVEN_GPG_PASSPHRASE`
  - `OSSRH_USERNAME`
  - `OSSRH_TOKEN`
- Pass secrets through action inputs or `env`, never command-line literals.
- Keep example configuration fictitious and obviously nonfunctional.
- Treat `pbcs-client.properties`, HTTP client environment files, local Maven settings, and GPG keyrings as sensitive even when they are outside the repository.

## Change Discipline

- Preserve unrelated user changes and keep the working tree scope narrow.
- Do not commit, tag, push, publish, deploy, or run a Maven release unless the user explicitly requests it.
- Before handing off code changes, run the smallest relevant tests and `git diff --check`. Use the normal CI-equivalent build when the change affects build configuration, packaging, or public APIs.

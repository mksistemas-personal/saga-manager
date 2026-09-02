# Forbidden Actions

Never:

- change database schema without migration
- remove tests to make the build pass
- disable security mechanisms
- introduce dependencies without justification
- modify authentication configuration without approval
- change public API contracts without approval
- perform large refactoring during a feature
- delete existing functionality to solve compilation problems
- expose credentials or tokens
- log JWTs
- commit secrets
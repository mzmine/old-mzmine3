
These tests cannot be placed in the mzmine-core module itself, because they
depend on other modules functionality (e.g. raw data import). Since
the other modules also depend on the mzmine-core module, adding these tests
to that module would create a circular dependency.


# Clojure test runner

The test runner takes three parameters:

1. The slug of the exercise (e.g. `two-fer`).
2. A path to an input directory (with a trailing slash) containing the submitted solution file(s) and any other exercise file(s).
3. A path to an output directory (with a trailing slash).

- A file named `results.json` will be written to the output directory.
- The runner will exit with an exit code of 0 if it has run successfully, regardless of the status of the tests.

```bash
$ ./bin/run.sh <SLUG> <INPUT> <OUTPUT>
```

To run this project's tests (i.e. test the test runner):

    $ ./bin/run-tests-in-docker.sh

To test against the full clojure track and compare with with the results snapshots:

1. Clone the `exercism/clojure` repo to your local machine if you don't have it already
2. Run: `./bin/run-exercise-tests.clj path-to-clojure-repo` (defaults to `../clojure`)

Build an uberjar:

    $ clojure -A:uberjar

Run that uberjar:

    $ java -jar clojure-test-runner.jar

## Documentation

The specification of the interface that the test runners must conform to is found in the [track-tooling](https://github.com/exercism/v3-docs/tree/master/anatomy/track-tooling) section of the v3-docs repo.

## Copyright

All content in this repository is copyright to Exercism and licenced under AGPL

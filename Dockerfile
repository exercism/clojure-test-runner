FROM clojure:tools-deps-alpine
LABEL Name=clojure-test-runner Version=0.0.1
WORKDIR /opt/test-runner

COPY deps.edn .
RUN clojure

COPY . .
ENTRYPOINT ["/opt/test-runner/bin/run.sh"]

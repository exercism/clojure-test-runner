FROM clojure:openjdk-17-lein-2.9.5-alpine
LABEL Name=clojure-test-runner Version=0.0.1

RUN apk add --no-cache jq coreutils

WORKDIR /opt/test-runner

COPY project.clj .
RUN lein deps

COPY . .
ENTRYPOINT ["/opt/test-runner/bin/run.sh"]

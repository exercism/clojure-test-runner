FROM babashka/babashka:0.8.1-SNAPSHOT-alpine
LABEL Name=clojure-test-runner Version=0.0.3

RUN apk add --no-cache jq coreutils bash openjdk8

WORKDIR /opt/test-runner

COPY . .
ENTRYPOINT ["/opt/test-runner/bin/run.sh"]

FROM babashka/babashka:0.10.164-SNAPSHOT-alpine
LABEL Name=clojure-test-runner Version=0.0.4

RUN apk add --no-cache jq coreutils bash

WORKDIR /opt/test-runner

COPY . .
ENTRYPOINT ["/opt/test-runner/bin/run.sh"]

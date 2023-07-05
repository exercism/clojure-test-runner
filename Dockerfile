FROM babashka/babashka:1.3.182-SNAPSHOT-alpine
LABEL Name=clojure-test-runner Version=0.0.4

RUN apk add --no-cache jq coreutils bash

WORKDIR /opt/test-runner

COPY . .
ENTRYPOINT ["/opt/test-runner/bin/run.sh"]

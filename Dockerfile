FROM babashka/babashka:0.5.1-alpine
LABEL Name=clojure-test-runner Version=0.0.2

RUN apk add --no-cache jq coreutils bash

WORKDIR /opt/test-runner

COPY . .
ENTRYPOINT ["/opt/test-runner/bin/run.sh"]

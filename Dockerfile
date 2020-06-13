FROM clojure:tools-deps-alpine
LABEL Name=clojure-test-runner Version=0.0.1
COPY . /opt/test-runner
WORKDIR /opt/test-runner
ENTRYPOINT ["/opt/test-runner/bin/run.sh"]

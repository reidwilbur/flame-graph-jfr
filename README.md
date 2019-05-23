# flame-graph-jfr

Convert java 11 JFR to folded format for https://github.com/brendangregg/FlameGraph

## Usage

Requires java 11 jdk or higher.

`mvn clean package`

`java -jar target/flame-graph-1.0-SNAPSHOT-shaded.jar -i some-j11-flight-recorder.flr -o output.fld`

Clone https://github.com/brendangregg/FlameGraph

In FlameGraph dir

`./flamegraph.pl <some path>/output.fld > flamegraph.svg`

Open flamegraph.svg in your browser to explore!

Should see something like
[![Example](http://www.brendangregg.com/FlameGraphs/cpu-bash-flamegraph.svg)](http://www.brendangregg.com/FlameGraphs/cpu-bash-flamegraph.svg)

* Assemble VLC Player Runner JAR file
```shell script
./gradlew build && \
cp build/libs/vlc-player-runner-*-all.jar ~/vlc-player-runner.jar
```
* Run using Java 8+ on a system having `vlc` installed
```shell script
java -jar vlc-player-runner.jar --help
```
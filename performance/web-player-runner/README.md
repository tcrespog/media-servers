* Assemble Web Player Runner JAR file
```shell script
./gradlew build && \
cp build/libs/web-player-runner-*-all.jar ~/web-player-runner.jar
```
* Run using Java 8+ (choose a browser installed in your system)
```shell script
java -jar web-player-runner.jar --help
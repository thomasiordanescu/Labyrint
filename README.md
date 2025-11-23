# Labyrint

## How to run
### Create bin directory:
mkdir bin
### Compile sources:
javac --release 17 -cp "lib/*" -d bin $(find . -name "*.java")

"--release 17" can be ommited if using early java version

### Build jar:
jar cfm Labyrint.jar manifest.txt -C bin .

### Run application:
java -jar Labyrint.jar _path_to_json_

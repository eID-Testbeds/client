Build eID Client Conformance Testbed on Windows.

# Prerequisites
1. Install Java 8 SDK.
2. Install Cygwin.
3. Install Maven (see https://maven.apache.org/download.cgi#Installation).
  1. Add Maven `bin` directory to PATH variable.
  2. Set JAVA_HOME variable (to installed JDK home directory).
  3. Configure Maven ...

# Create build
1. (optional) Convert `build_from_source.sh` with dos2unix in Cygwin terminal.
2. (optional) Install Cygwin package "xmlstarlet".
3. (conditional) Install CommonTestbedUtilities via "mvn clean install"
4. Run `build_from_source.sh` in Cygwin terminal.

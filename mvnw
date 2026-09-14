#!/bin/sh

set -e

MVNW_REPOURL="https://repo.maven.apache.org/maven2"
WRAPPER_JAR=".mvn/wrapper/maven-wrapper.jar"
WRAPPER_PROPERTIES=".mvn/wrapper/maven-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ]; then
  mkdir -p .mvn/wrapper
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar" -o "$WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget -q "$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar" -O "$WRAPPER_JAR"
  else
    echo "Neither curl nor wget is available to download Maven Wrapper."
    exit 1
  fi
fi

if [ ! -f "$WRAPPER_PROPERTIES" ]; then
  mkdir -p .mvn/wrapper
  cat <<'EOF' > "$WRAPPER_PROPERTIES"
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar
EOF
fi

exec java -classpath "$WRAPPER_JAR" -Dmaven.multiModuleProjectDirectory="$PWD" org.apache.maven.wrapper.MavenWrapperMain "$@"

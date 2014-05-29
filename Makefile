ARCH=macosx/x86_64

all: jd-core-java-1.1.jar libjd-intellij.jnilib

jd-intellij:
	hg clone https://bitbucket.org/bric3/jd-intellij

libjd-intellij.jnilib: jd-intellij
	cp jd-intellij/src/main/native/nativelib/${ARCH}/libjd-intellij.jnilib .

target/jd-core-java-1.1.jar:
	mvn package

jd-core-java-1.1.jar: target/jd-core-java-1.1.jar
	cp $< $@

clean:
	rm -rf jd-core-java-*.jar libjd-intellij.jnilib target jd-intellij

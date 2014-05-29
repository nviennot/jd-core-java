# TODO detect 32/64 bit platforms

ifeq ($(OS),Windows_NT)
	ARCH=win32/x86_64
	LIBEXT=dll
else ifeq ($(shell uname),Darwin)
	ARCH=macosx/x86_64
	LIBEXT=jnilib
else
	ARCH=linux/x86_64
	LIBEXT=so
endif

all: jd-core-java-1.1.jar libjd-intellij
	@echo ${OS}

jd-intellij:
	hg clone https://bitbucket.org/bric3/jd-intellij

libjd-intellij: jd-intellij
	cp jd-intellij/src/main/native/nativelib/${ARCH}/libjd-intellij.${LIBEXT} .

target/jd-core-java-1.1.jar:
	mvn package

jd-core-java-1.1.jar: target/jd-core-java-1.1.jar
	cp $< $@

clean:
	rm -rf jd-core-java-*.jar libjd-intellij.${LIBEXT} target jd-intellij

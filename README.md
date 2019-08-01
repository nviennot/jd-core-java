JD-Core-java
============

JD-Core-java is a thin-wrapper for the [Java Decompiler](http://jd.benow.ca/).

This is hack around the IntelliJ IDE plugin. It fakes the interfaces of the
IDE, and provides access to JD-Core.

Since the Author of JD-Core is not willing to provide a library, and we all want
to batch decompilation, this is pretty much our only option.

I hope this will motivate the author to release a proper library: https://github.com/java-decompiler/jd-core

Supported Platforms
-------------------

JD supports:

- Linux 32/64-bit
- Windows 32/64-bit
- Mac OSX 32/64-bit on x86 hardware

Build
-----

Clone the repository and run <code>./gradlew assemble</code>.
This will download the necessary native libraries from the JD Intellij
repository on Bitbucket, compile the wrapper code, and generate a jar file
containing both in the <code>build/libs</code> directory.

This is a self-contained jar that you may include in your own Java project, or
alternatively you may run it from the command line.

Usage
------

Programmatically:

```java
/* Returns the source of SomeClass from compiled.jar as a String */
new jd.core.Decompiler().decompileClass("compiled.jar", "com/namespace/SomeClass.class");

/*
 * Returns the sources of all the classes in compiled.jar as a Map<String, String>
 * where the key is the class name (full path) and the value is the source
 */
new jd.core.Decompiler().decompile("compiled.jar");

/*
 * Returns the number of classes decompiled and saved into out_dir
 */
new jd.core.Decompiler().decompileToDir("compiled.jar", "out_dir");
```

From the command line:
```shell
# Outputs all the sources of compiled.jar into out_dir
java -jar jd-core-java-1.2.jar [options] <compiled.jar> [<destination>]
Options:
    -z - save sources into a zip file
    -n - add line numbers into sources; (false by default)
    -r - not realign line numbers (true by default)
```


Contributors
------------

[Leslie Hoare](https://github.com/lesleh) wrote the cross platform and self contrained jar code.

License
-------

JD-Core-java is released under the GPLv3 License.

JD-Core, JD-GUI & JD-Eclipse are open source projects released under the GPLv3 License.

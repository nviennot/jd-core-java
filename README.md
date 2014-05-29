JD-Core-java
============

JD-Core-java is a thin-wrapper for the [Java Decompiler](http://jd.benow.ca/).

This is hack around the IntelliJ IDE plugin. It fakes the interfaces of the
IDE, and provides access to JD-Core.

Since the Author of JD-Core is not willing to provide a library, and we all want
to batch decompilation, this is pretty much our only option.

I hope this will motivate the author to release a proper library.

Supported Platforms
-------------------

JD supports:

- Linux 32/64-bit
- Windows 32/64-bit
- Mac OSX 32/64-bit on x86 hardware

But this wrapper only supports Linux 64-bit. Please ask if you want more, or
better, make a pull request.

Build
-----

Simply use:

    make

I do not include the JD-Core library since the author of JD does not want to
publish a library.

Building will download the JD-IntelliJ plugin from bitbucket, and copy the
libjd-intellij.so library in the root directory of the project.

Once the project is built, the two files jd-core-java-1.1.jar libjd-intellij.so
will be in the root directory of the project.

Usage
------

Programmatically:

```java
/* Returns the source of SomeClass from compiled.jar as a String */
new jd.core.Decompiler().decompile("compiled.jar", "com/namespace/SomeClass.class");

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
java -jar jd-core-java-1.1.jar <compiled.jar> <out_dir>
```

License
-------

JD-Core-java is released under the MIT license.

JD-IntelliJ is free for non-commercial use. This means that JD-IntelliJ shall
not be included or embedded into commercial software products. Nevertheless,
this project may be freely used for personal needs in a commercial or
non-commercial environments.

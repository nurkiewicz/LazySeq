# `LazySeq` - lazy sequence implementation for Java 8

## Troubleshooting

### Error `invalid target release: 1.8` during maven build

If you see this error message during maven build:

	[INFO] BUILD FAILURE
	...
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project lazyseq: Fatal error compiling: invalid target release: 1.8 -> [Help 1]

it means you are not compiling using Java 8. [Download JDK 8 with lambda support](https://jdk8.java.net/lambda/) and let maven use it:

	$ export JAVA_HOME=/path/to/jdk

### I get `StackOverflowError` or program hangs infinitely
 
	java.lang.StackOverflowError
		at sun.misc.Unsafe.allocateInstance(Native Method)
		at java.lang.invoke.DirectMethodHandle.allocateInstance(DirectMethodHandle.java:426)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.iterate(LazySeq.java:118)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.lambda$0(LazySeq.java:118)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq$$Lambda$2.get(Unknown Source)
		at com.blogspot.nurkiewicz.lazyseq.Cons.tail(Cons.java:32)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.blogspot.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)

## Possible improvements
* Just like `FixedCons` is used when tail is known up-front, consider `IterableCons` that wraps existing `Iterable` in one node rather than building `FixedCons` hierarchy. This can be used for all `concat` methods.

## License
This project is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

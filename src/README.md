# `LazySeq` - lazy sequence implementation for Java 8

## Troubleshooting

* `invalid target release: 1.8` during maven build

	If you see this error message during maven build:

		[INFO] BUILD FAILURE
		...
		[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project lazyseq: Fatal error compiling: invalid target release: 1.8 -> [Help 1]

	it means you are not compiling using Java 8. [Download JDK 8 with lambda support](https://jdk8.java.net/lambda/) and let maven use it:

		$ export JAVA_HOME=/path/to/jdk


## TODO
* `equals()`/`hashCode()`
* More complex test cases:
** CRON expressions `LazySeq`
** [Collatz conjecture](http://en.wikipedia.org/wiki/Collatz_conjecture)
** Fibonacci numbers using `zip` and `map`
** (In)finite `LazySeq` of database records, fetched page at a time (buffering/caching)

## Possible improvements
* Just like `FixedCons` is used when tail is known up-front, consider `IterableCons` that wraps existing `Iterable` in one node rather than building `FixedCons` hierarchy. This can be used for all `concat` methods.
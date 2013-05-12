# `LazySeq` - lazy sequences implementation for Java 8

## Introduction

*Lazy sequence* is a data structure that is being computed only when its elements are actually needed. All operations on lazy sequences are lazy as well, postponing invocation up to the moment when it is really necessary. Lazy sequence is always traversed from the beginning using very cheap `head()`/`tail()` decomposition (*first* and *rest*). An important property of lazy sequences is that they can represent infinite streams of data, e.g. all natural numbers or never-ending measurement values.

Lazy sequence remembers already computed values so if you access Nth element, all elements from `1` to `N-1` are computed as well. Despite that `LazySeq`, being at the core of many functional languages and algorithms, is immutable and thread-safe.

## Rational

TODO

## Getting started

Building working with lazy sequences

### All natural numbers infinite sequence

In order to create a lazy sequence you use `LazySeq.cons()` factory method that accepts first element (*head*) and a function that might be later used to compute rest (*tail*). For example in order to produce lazy sequence of natural numbers with given start element you simply say:

	private LazySeq<Integer> naturals(int from) {
		return LazySeq.cons(from, () -> naturals(from + 1));
	}

There is really no recursion here. If there was, calling `naturals()` would quickly result in `StackOverflowError` as it calls itself without stop condition. However `() -> naturals(from + 1)` expression defines a *function* (`Supplier` to be precise) that this data structure will invoke, but only if needed. Look at the code below, how many times do you think `naturals()` function was called (except the first line)?

	final LazySeq<Integer> ints = naturals(2);

	final LazySeq<String> strings = ints.
			map(n -> n + 10).
			filter(n -> n % 2 == 0).
			take(10).
			flatMap(n -> Arrays.asList(0x10000 + n, n)).
			distinct().
			map(Integer::toHexString);

First invocation of `naturals(2)` returns lazy sequence starting from `2` but rest (`3`, `4`, `5`, ...) is not computed yet. Later we `map()` over this sequence, `filter()` it, `take()` first 10 elements, remove duplicates, etc. All these are operations do *not* evaluate the sequence are are as lazy as possible. For example `take(10)` doesn't evaluate first 10 elements eagerly to return them. Instead new lazy sequence is returned which remembers that it should truncate original sequence at 10th element.

Same applies to `distinct()`. It doesn't evaluate the whole sequence to extract all unique values (otherwise code above would explode quickly, traversing infinite amount of natural numbers). Instead it returns a new sequence with only the first element. If you ever ask for the second unique element, it will lazily evaluate tail, but only as much as possible. Check out `toString()` output:

	System.out.println(strings);
	//[1000c, ?]

Question mark (`?`) says: *"There is something more in that collection, but I don't know yet"*. Do you understand where did `1000c` came from? Look carefully:

1. Start from an infinite stream of natural numbers starting from `2`
2. Add `10` to each element (so the first element becomes `12` or `c` in hex)
3. `filter()` out odd numbers (`12` is even so it stays)
4. `take()` first `10` elements from sequence so far
5. Each element is replaced by that element plus 0x1000 and the element itself (`flatMap()`)
6. We ensure only distinct elements will be returned (not much sense here since natural numbers are already unique)
7. In the end we turn integers to strings.

As you can see none of these operations really require evaluating the whole stream. Only head is being transormed and this is what we see in the end

So when this data structure is actually evaluated? When it absolutely must, e.g. during side-effect traversal:

	strings.forEach(System.out::println);
	
	//or

	final List<String> list = strings.toList();
	
	//or

	for (String s : strings) {
		System.out.println(s);
	}

All the statements above alone will force evaluation of whole lazy sequence. Our `strings` sequence was limited to first 10 elements so it will not run infinitely. BTW have you noticed that we can iterate over `LazySeq strings` using standard Java 5 for-each syntax? That's because `LazySeq` implements [`List`](http://docs.oracle.com/javase/7/docs/api/java/util/List.html) interface, thus plays nicely with [Java Collections Framework](http://docs.oracle.com/javase/7/docs/technotes/guides/collections/) ecosystem:

	import java.util.AbstractList;

	public abstract class LazySeq<E> extends AbstractList<E> 

Please keep in mind that once lazy sequence is evaluated (computed) it will cache (*memoize*) them for later use. This makes lazy sequences great for representing infinite or very long streams of data that are expensive to compute.

### `iterate()`

Building an infinite lazy sequence very often boils down to providing an initial element and a function that produces next item based on the previous one. In other words second element is a function of the first one, third element is a function of the second one, etc. Convenience `LazySeq.iterate()` function is provided for such circumstances. `ints` definition can now look like this:

	final LazySeq<Integer> ints = LazySeq.iterate(2, n -> n + 1);

We start from `2` and each subsequent element is represented as previous element + 1.

### More examples: Fibonacci sequence and Collatz conjecture

No article about lazy data structure can be left without [Fibonacci numbers](http://en.wikipedia.org/wiki/Fibonacci_number) example:

	private static LazySeq<Integer> lastTwoFib(int first, int second) {
		return 	LazySeq.<Integer>of(
				first,
				() -> lastTwoFib(second, first + second)
		);

	}

Fibonacci sequence is infinite as well but we are free to transform it in multiple ways:

	System.out.println(
			fib.
					drop(5).
					take(10).
					toList()
	);
	//[5, 8, 13, 21, 34, 55, 89, 144, 233, 377]

	final int firstAbove1000 = fib.
			filter(n -> (n > 1000)).
			head();

	fib.get(45);

See how easy and natural it is to work with infinite stream of numbers? `drop(5).take(10)` skips first 5 elements and displays next 10. At this point first 15 numbers are already computed and will never by computed again.

Finding first Fibonacci number above 1000 (happens to be `1597`) is very straightforward. `head()` is always precomputed by `filter()` , so no further evaluation is needed. Last but not least we can simply just ask for [45th Fibonacci number](http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibtable.html) (0-based) and get `1134903170`. If you ever try to access any Fibonacci number between up to this one, they are precomputed and fast to retrieve.

---

#### Finite sequences

[Collatz conjecture](http://en.wikipedia.org/wiki/Collatz_conjecture) is also quite interesting problem. For each positive integer `n` we compute next integer using following algorithm:

* `n/2` is `n` is even
* `3n + 1` if `n` is odd

For example starting from `10` series looks as follows: 10, 5, 16, 8, 4, 2, 1. The series ends when it reaches 1. Mathematicians believe that starting from any integer we will eventually reach 1 but it's not yet proven.

Let us create a lazy sequence that generates Collatz numbers, but only as many as needed. As stated above, this time our sequence will be finite:



## Data structure details

Each lazy sequence is built around the idea of eagerly computed *head* and lazily evaluated *tail* represented as function. This is very similar to classic single-linked list recursive definition:

	class List<T> {
		private final T head;
		private final List<T> tail;
		//...
	}

However in case of lazy sequence *tail* is given as a function, not a value. Invocation of that function is postponed as long as possible:

	class Cons<E> extends LazySeq<E> {
		private final E head;
		private LazySeq<E> tailOrNull;
		private final Supplier<LazySeq<E>> tailFun;

		@Override
		public LazySeq<E> tail() {
			if (!(tailOrNull != null)) {
				tailOrNull = tailFun.get();
			}
			return tailOrNull;
		}

## Pitfalls and common dangers

## Performance, time and space complexity

## Troubleshooting

### Error `invalid target release: 1.8` during maven build

If you see this error message during maven build:

	[INFO] BUILD FAILURE
	...
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project lazyseq: 
	Fatal error compiling: invalid target release: 1.8 -> [Help 1]

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

* Multi-threading processing support (implementing spliterator?)

## License
This project is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

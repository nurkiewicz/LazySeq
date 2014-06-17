# Lazy sequences implementation for Java 8 [![Build Status](https://travis-ci.org/nurkiewicz/LazySeq.svg?branch=master)](https://travis-ci.org/nurkiewicz/LazySeq)

## Introduction

*Lazy sequence* is a data structure that is being computed only when its elements are actually needed. All operations on lazy sequences, like `map()` and `filter()` are lazy as well, postponing invocation up to the moment when it is really necessary. Lazy sequence is always traversed from the beginning using very cheap *first*/*rest* decomposition (`head()` and `tail()`). An important property of lazy sequences is that they can represent infinite streams of data, e.g. all natural numbers or temperature measurements over time.

Lazy sequence remembers already computed values so if you access Nth element, all elements from `1` to `N-1` are computed as well and cached. Despite that `LazySeq` (being at the core of many functional languages and algorithms) is immutable and thread-safe (assuming elements are, e.g. `String`, primitive wrappers, `BigDecimal`, etc.) `LazySeq` does not allow `null` elements.

## Rationale

This library is heavily inspired by [`scala.collection.immutable.Stream`](http://www.scala-lang.org/api/current/index.html#scala.collection.immutable.Stream) and aims to provide immutable, thread-safe and easy to use lazy sequence implementation, possibly infinite. See [*Lazy sequences in Scala and Clojure*](http://nurkiewicz.blogspot.no/2013/05/lazy-sequences-in-scala-and-clojure.html) for some use cases.

[`Stream`](http://download.java.net/lambda/b88/docs/api/java/util/stream/Stream.html) class name is already used in Java 8, therefore `LazySeq` was chosen, similar to [`lazy-seq` in Clojure](http://clojuredocs.org/clojure_core/clojure.core/lazy-seq). Speaking of `Stream`, at first it looks like a lazy sequence implementation available out-of-the-box. However, quoting Javadoc:

> Streams are not data structures

and:

> Once an operation has been performed on a stream, it is considered consumed and no longer usable for other operations.

In other words `java.util.stream.Stream` is just a thin wrapper around existing collection, suitable for one time use. More akin to `Iterator` than to `Stream` in Scala. This library attempts to fill this niche.

Of course implementing lazy sequence data structure was possible prior to Java 8, but lack of lambdas makes working with such data structure tedious and too verbose.

## Getting started

Building and working with lazy sequences in 10 minutes.

### Maven coordinates (available in Central):

```xml
<dependency>
	<groupId>com.nurkiewicz.lazyseq</groupId>
	<artifactId>lazyseq</artifactId>
	<version>0.0.1</version>
</dependency>
```

### Infinite sequence of all natural numbers

In order to create a lazy sequence you use `LazySeq.cons()` factory method that accepts first element (*head*) and a function that might be later used to compute rest (*tail*). For example in order to produce lazy sequence of natural numbers with given start element you simply say:

```java
private LazySeq<Integer> naturals(int from) {
	return LazySeq.cons(from, () -> naturals(from + 1));
}
```

There is really no recursion here. If there was, calling `naturals()` would quickly result in `StackOverflowError` as it calls itself without stop condition. However `() -> naturals(from + 1)` expression defines a *function* returning `LazySeq<Integer>` (`Supplier` to be precise) that this data structure will invoke, but only if needed. Look at the code below, how many times do you think `naturals()` function was called (except the first line)?

```java
final LazySeq<Integer> ints = naturals(2);

final LazySeq<String> strings = ints.
		map(n -> n + 10).
		filter(n -> n % 2 == 0).
		take(10).
		flatMap(n -> Arrays.asList(0x10000 + n, n)).
		distinct().
		map(Integer::toHexString);
```

First invocation of `naturals(2)` returns lazy sequence starting from `2` but rest (`3`, `4`, `5`, ...) is not computed yet. Later we `map()` over this sequence, `filter()` it, `take()` first 10 elements, remove duplicates, etc. All these operations do *not* evaluate the sequence and are as lazy as possible. For example `take(10)` doesn't evaluate first 10 elements eagerly to return them. Instead new lazy sequence is returned which remembers that it should truncate original sequence at 10th element.

Same applies to `distinct()`. It doesn't evaluate the whole sequence to extract all unique values (otherwise code above would explode quickly, traversing infinite amount of natural numbers). Instead it returns a new sequence with only the first element. If you ever ask for the second unique element, it will lazily evaluate tail, but only as much as possible. Check out `toString()` output:

```java
System.out.println(strings);
//[1000c, ?]
```

Question mark (`?`) says: *"there might be something more in that collection, but I don't know it yet"*. Do you understand where did `1000c` came from? Look carefully:

1. Start from an infinite stream of natural numbers starting from `2`
2. Add `10` to each element (so the first element becomes `12` or `C` in hex)
3. `filter()` out odd numbers (`12` is even so it stays)
4. `take()` first `10` elements from sequence so far
5. Each element is replaced by two elements: that element plus 0x1000 and the element itself (`flatMap()`). This does not yield a sequence of pairs, but a sequence of integers that is twice as long
6. We ensure only `distinct()` elements will be returned
7. In the end we turn integers to hex strings.

As you can see none of these operations really require evaluating the whole stream. Only head is being transformed and this is what we see in the end. So when this data structure is actually evaluated? When it absolutely must, e.g. during side-effect traversal:

```java
strings.force();

//or
strings.forEach(System.out::println);

//or
final List<String> list = strings.toList();

//or
for (String s : strings) {
	System.out.println(s);
}
```

All the statements above alone will force evaluation of whole lazy sequence. Not very smart if our sequence was infinite, but `strings` was limited to first 10 elements so it will not run infinitely. If you want to force only part of the sequence, simply call `strings.take(5).force()`. BTW have you noticed that we can iterate over `LazySeq strings` using standard Java 5 for-each syntax? That's because `LazySeq` implements [`List`](http://docs.oracle.com/javase/7/docs/api/java/util/List.html) interface, thus plays nicely with [Java Collections Framework](http://docs.oracle.com/javase/7/docs/technotes/guides/collections/) ecosystem:

```java
import java.util.AbstractList;

public abstract class LazySeq<E> extends AbstractList<E>
```

Please keep in mind that once lazy sequence is evaluated (computed) it will cache (*memoize*) them for later use. This makes lazy sequences great for representing infinite or very long streams of data that are expensive to compute.

### `iterate()`

Building an infinite lazy sequence very often boils down to providing an initial element and a function that produces next item based on the previous one. In other words second element is a function of the first one, third element is a function of the second one, and so on. Convenience `LazySeq.iterate()` function is provided for such circumstances. `ints` definition can now look like this:

```java
final LazySeq<Integer> ints = LazySeq.iterate(2, n -> n + 1);
```

We start from `2` and each subsequent element is represented as previous element + 1.

### More examples: Fibonacci sequence and Collatz conjecture

No article about lazy data structure can be left without [Fibonacci numbers](http://en.wikipedia.org/wiki/Fibonacci_number) example:

```java
private static LazySeq<Integer> lastTwoFib(int first, int second) {
	return LazySeq.cons(
			first,
			() -> lastTwoFib(second, first + second)
	);
}
```

Fibonacci sequence is infinite as well but we are free to transform it in multiple ways:

```java
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
```

See how easy and natural it is to work with infinite stream of numbers? `drop(5).take(10)` skips first 5 elements and displays next 10. At this point first 15 numbers are already computed and will never by computed again.

Finding first Fibonacci number above 1000 (happens to be `1597`) is very straightforward. `head()` is always precomputed by `filter()` , so no further evaluation is needed. Last but not least we can simply just ask for [45th Fibonacci number](http://www.maths.surrey.ac.uk/hosted-sites/R.Knott/Fibonacci/fibtable.html) (0-based) and get `1134903170`. If you ever try to access any Fibonacci number up to this one, they are precomputed and fast to retrieve.

---

#### Finite sequences (Collatz conjecture)

[Collatz conjecture](http://en.wikipedia.org/wiki/Collatz_conjecture) is also quite interesting problem. For each positive integer `n` we compute next integer using following algorithm:

* `n/2` if `n` is even
* `3n + 1` if `n` is odd

For example starting from `10` series looks as follows: 10, 5, 16, 8, 4, 2, 1. The series ends when it reaches 1. Mathematicians believe that starting from any integer we will eventually reach 1 but it's not yet proven.

Let us create a lazy sequence that generates Collatz series for given `n`, but only as many as needed. As stated above, this time our sequence will be finite:

```java
private LazySeq<Long> collatz(long from) {
	if (from > 1) {
		final long next = from % 2 == 0 ? from / 2 : from * 3 + 1;
		return LazySeq.cons(from, () -> collatz(next));
	} else {
		return LazySeq.of(1L);
	}
}
```

This implementation is driven directly by the definition. For each number greater than `1` return that number + lazily evaluated (`() -> collatz(next)`) rest of the stream. As you can see if `1` is given, we return single element lazy sequence using special `of()` factory method. Let's test it with aforementioned `10`:

```java
final LazySeq<Long> collatz = collatz(10);

collatz.filter(n -> (n > 10)).head();
collatz.size();
```

`filter()` allows us to find first number in the sequence that is greater than `10`. Remember that lazy sequence will have to traverse the contents (evaluate itself), but only to the point where it finds first matching element. Then it stops, ensuring it computes as little as possible.

However `size()`, in order to calculate total number of elements, must traverse the whole sequence. Of course this can only work with finite lazy sequences, calling `size()` on an infinite sequence will end up poorly.

If you play a bit with this sequence you will quickly realize that sequences for different numbers [share the same suffix](http://en.wikipedia.org/wiki/File:Collatz-graph-all-30-no27.svg) (always end with the same sequence of numbers). This begs for some caching/structural sharing. See [`CollatzConjectureTest`](https://github.com/nurkiewicz/LazySeq/blob/master/src/test/java/com/nurkiewicz/lazyseq/samples/CollatzConjectureTest.java) for details.

## But can it be used to something, you know... useful? Real life?

Infinite sequences of numbers are great, but not very practical in real life. Maybe some more down to earth examples? Imagine you have a collection and you need to pick few items from that collection randomly. Instead of collection I will use a function returning random latin characters:

```java
private char randomChar() {
	return (char) ('A' + (int) (Math.random() * ('Z' - 'A' + 1)));
}
```

But there is a twist. You need N (N < 26, number of latin characters) unique values. Simply calling `randomChar()` few times doesn't guarantee uniqueness. There are few approaches to this problem, with `LazySeq` it's pretty straightforward:

```java
LazySeq<Character> charStream = LazySeq.<Character>continually(this::randomChar);
LazySeq<Character> uniqueCharStream = charStream.distinct();
```

`continually()` simply invokes given function for each element when needed. Thus `charStream` will be an infinite stream of random characters. Of course they can't be unique. However `uniqueCharStream` guarantees that its output is unique. It does so by examining next element of underlying `charStream` and rejecting items that already appeared. We can now say `uniqueCharStream.take(4)` and be sure that no duplicates will appear.

Once again notice that `continually(this::randomChar).distinct().take(4)` really calls `randomChar()` only once! As long as you don't consume this sequence, it remains lazy and postpones evaluation as long as possible.

---

Another example involves loading batches (pages) of data from database. Using [`ResultSet`](http://docs.oracle.com/javase/7/docs/api/java/sql/ResultSet.html) or `Iterator` is cumbersome but loading whole data set into memory often not feasible. An alternative involves loading first batch of data eagerly and then providing a function to load next batches. Data is loaded only when it's really needed and we don't suffer performance or scalability issues.

First let's define abstract API for loading batches of data from database:

```java
public List<Record> loadPage(int offset, int max) {
	//load records from offset to offset + max
}
```

I abstract from the technology entirely, but you get the point. Imagine that we now define `LazySeq<Record>` that starts from row 0 and loads next pages only when needed:

```java
public static final int PAGE_SIZE = 5;

private LazySeq<Record> records(int from) {
	return LazySeq.concat(
		loadPage(from, PAGE_SIZE),
		() -> records(from + PAGE_SIZE)
	);
}
```

When creating new `LazySeq<Record>` instance by calling `records(0)` first page of 5 elements is loaded. This means that first 5 sequence elements are already computed. If you ever try to access 6th or above, sequence will automatically load all missing record and cache them. In other words you never compute the same element twice.

More useful tools when working with sequences are `grouped()` and `sliding()` methods. First partitions input sequence into groups of equal size. Take this as an example, also proving that these methods are as always lazy:

```java
final LazySeq<Character> chars = LazySeq.of('A', 'B', 'C', 'D', 'E', 'F', 'G');

chars.grouped(3);
//[[A, B, C], ?]

chars.grouped(3).force();		//force evaluation
//[[A, B, C], [D, E, F], [G]]
```

and similarly for `sliding()`:

```java
chars.sliding(3);
//[[A, B, C], ?]

chars.sliding(3).force();		//force evaluation
//[[A, B, C], [B, C, D], [C, D, E], [D, E, F], [E, F, G]]
```

These two methods are *extremely* useful. You can look at your data through sliding window (e.g. to compute [moving average](https://en.wikipedia.org/wiki/Moving_average)) or partition it to equal-length buckets.

Last interesting utility method you may find useful is `scan()` that iterates (lazily, of course) the input stream and constructs every element of output by applying a function on previous and current element of input. Code snippet is worth a thousand words:

```java
LazySeq<Integer> list = LazySeq.
	numbers(1).
	scan(0, (a, x) -> a + x);

list.take(10).force();	//[0, 1, 3, 6, 10, 15, 21, 28, 36, 45]
```

`LazySeq.numbers(1)` is a sequence of natural numbers (1, 2, 3...). `scan()` creates a new sequence that starts from `0` and for each element of input (natural numbers) adds it to last element of itself. So we get: [`0`, `0+1`, `0+1+2`, `0+1+2+3`, `0+1+2+3+4`, `0+1+2+3+4+5`...]. If you want a sequence of growing strings, just replace few types:

```java
LazySeq.continually("*").
	scan("", (s, c) -> s + c).
	map(s -> "|" + s + "\\").
	take(10).
	forEach(System.out::println);
```

And enjoy this beautiful triangle:

	|\
	|*\
	|**\
	|***\
	|****\
	|*****\
	|******\
	|*******\
	|********\
	|*********\

Alternatively (same output):

```java
LazySeq.iterate("", s -> s + "*").
	map(s -> "|" + s + "\\").
	take(10).
	forEach(System.out::println);
```

## Java collections framework interoperability

`LazySeq` implements `java.util.List` interface, thus can be used in variety of places. Moreover it also implements Java 8 enhancements to collections, namely streams and collectors:

```java
lazySeq.
	stream().
	map(n -> n + 1).
	flatMap(n -> asList(0, n - 1).stream()).
	filter(n -> n != 0).
	skip(4).
	limit(10).
	sorted().
	distinct().
	collect(Collectors.toList());
```

However streams in Java 8 were created to work around feature that is a foundation of `LazySeq` - lazy evaluation. Example above postpones all intermediate steps until `collect()` is called. With `LazySeq` you can safely skip `.stream()` and work directly on sequence:

```java
lazySeq.
	map(n -> n + 1).
	flatMap(n -> asList(0, n - 1)).
	filter(n -> n != 0).
	slice(4, 18).
	limit(10).
	sorted().
	distinct();
```

Moreover `LazySeq` provides special purpose collector (see: `LazySeq.toLazySeq()`) that avoids evaluation even when used with `collect()` - which normally forces full collection computation.

## Implementation details

Each lazy sequence is built around the idea of eagerly computed *head* and lazily evaluated *tail* represented as function. This is very similar to classic single-linked list recursive definition:

```java
class List<T> {
	private final T head;
	private final List<T> tail;
	//...
}
```

However in case of lazy sequence *tail* is given as a function, not a value. Invocation of that function is postponed as long as possible:

```java
class Cons<E> extends LazySeq<E> {
	private final E head;
	private LazySeq<E> tailOrNull;
	private final Supplier<LazySeq<E>> tailFun;

	@Override
	public LazySeq<E> tail() {
		if (tailOrNull == null) {
			tailOrNull = tailFun.get();
		}
		return tailOrNull;
	}
```

For full implementation see [`Cons.java`](https://github.com/nurkiewicz/LazySeq/blob/master/src/main/java/com/nurkiewicz/lazyseq/Cons.java) and [`FixedCons.java`](https://github.com/nurkiewicz/LazySeq/blob/master/src/main/java/com/nurkiewicz/lazyseq/FixedCons.java) used when `tail` is known at creation time (for example `LazySeq.of(1, 2)` as opposed to `LazySeq.cons(1, () -> someTailFun()`).

## Pitfalls and common dangers

Below common issues and misunderstandings are described.

### Evaluating too much

One of the biggest dangers of working with infinite sequences is trying to evaluate them completely, which obviously leads to infinite computation. The idea behind infinite sequence is not to evaluate it in its entirety but to take as much as we need without introducing artificial limits and accidental complexity (see database loading example).

However evaluating whole sequence is way too simple to miss. For example calling `LazySeq.size()` *must* evaluate whole sequence and will run infinitely, eventually filling up stack or heap (implementation detail). There are other methods that require full traversal in order to function properly. E.g. `allMatch()` making sure all elements match given predicate. Some methods are even more dangerous, because whether they will finish or not depends on data in the sequence. For example `anyMatch()` may return immediately if head matches predicate - or never.

Sometimes we can easily avoid costly operations by using more deterministic methods. For example:

```java
seq.size() <= 10		//BAD
```

may not work or be extremely slow if `seq` is infinite. However we can achieve the same with (more) predictable:

```java
seq.drop(10).isEmpty()		//GOOD
```

Remember that lazy sequences are immutable (so we don't really mutate `seq`), `drop(n)` is typically `O(n)` while `isEmpty()` is `O(1)`.

When in doubt, consult source code or JavaDoc to make sure your operation won't too eagerly evaluate your sequence. Also be very cautious when using `LazySeq` where [`java.util.Collection`](http://docs.oracle.com/javase/7/docs/api/java/util/Collection.html) or [`java.util.List`](http://docs.oracle.com/javase/7/docs/api/java/util/List.html) is expected.

### Holding unnecessary reference to head

Lazy sequences be definition remember already computed elements. You have to be aware of that, otherwise your sequence (especially infinite) will quickly fill up available memory. However, because `LazySeq` is just a fancy linked list, if you no longer keep a reference to head (but only to some element in the middle), it becomes eligible for garbage collection. For example:

```java
//LazySeq<Char> first = seq.take(10);
seq = seq.drop(10);
```

First ten elements are dropped and we assume nothing holds a reference to what previously was hept in `seq`. This makes first ten elements eligible for garbage collection. However if we uncomment first line and keep reference to old `head` in `first`, JVM will not release any memory. Let's put that into perspective. The following piece of code will eventually throw `OutOfMemoryError` because `infinite` reference keeps holding the beginning of the sequence, therefore all the elements created so far:

```java
LazySeq<Big> infinite = LazySeq.<Big>continually(Big::new);
for (Big arr : infinite) {
	//
}
```

However by inlining call to `continually()` or extracting it to a method this code works flawlessly (well, still runs forever, but uses almost no memory):

```java
private LazySeq<Big> getContinually() {
	return LazySeq.<Big>continually(Big::new);
}

for (Big arr : getContinually()) {
	//
}
```

What's the difference? For-each loop uses iterators underneath. [`LazySeqIterator`](https://github.com/nurkiewicz/LazySeq/blob/master/src/main/java/com/nurkiewicz/lazyseq/LazySeqIterator.java) underneath doesn't hold a reference to old `head()` when it advances, so if nothing else references that head, it will be eligible for garbage collection, see true `javac` output when for-each is used:

```java
for (Iterator<Big> cur = getContinually().iterator(); cur.hasNext(); ) {
	final Big arr = cur.next();
	//...
}
```

#### TL;DR

Your sequence grows while being traversed. If you keep holding one end while the other grows, it will eventually blow up. Just like your first level cache in Hibernate if you load too much in one transaction. Use only as much as needed.

### Converting to plain Java collections

Converting is simple, but dangerous. This is a consequence of points above. You can convert lazy sequence to `java.util.List` by calling `toList()`:

```java
LazySeq<Integer> even = LazySeq.numbers(0, 2);
even.take(5).toList();  //[0, 2, 4, 6, 8]
```

or using [`Collector` from Java 8](http://download.java.net/lambda/b88/docs/api/java/util/stream/Collector.html) having richer API:

```java
even.
	stream().
	limit(5).
	collect(Collectors.toSet())   //[4, 6, 0, 2, 8]
```

But remember that Java collections are finite from definition so avoid converting lazy sequences to collections explicitly. Note that `LazySeq` is already `List`, thus `Iterable` and `Collection`. It also has efficient `LazySeq.iterator()`. If you can, simply pass `LazySeq` instance directly and may just work.

## Performance, time and space complexity

`head()` of every sequence (except empty) is always computed eagerly, thus accessing it is fast `O(1)`. Computing `tail()` may take everything from `O(1)` (if it was already computed) to infinite time. As an example take this valid stream:

```java
import static com.nurkiewicz.lazyseq.LazySeq.cons;
import static com.nurkiewicz.lazyseq.LazySeq.continually;

LazySeq<Integer> oneAndZeros = cons(
	1,
	() -> continually(0)
).
filter(x -> (x > 0));
```

It represents `1` followed by infinite number of `0`s. By filtering all positive numbers (`x > 0`) we get a sequence with same head, but filtering of tail is delayed (lazy). However if we now carelessly call `oneAndZeros.tail()`, `LazySeq` will keep computing more and more of this infinite sequence, but since there is no positive element after initial `1`, this operation will run forever, eventually throwing `StackOverflowError` or `OutOfMemoryError` (this is an implementation detail).

However if you ever reach this state, it's probably a programming bug or misusing of the library. Typically `tail()` will be close to `O(1)`. On the other hand if you have plenty of operations already "stacked", calling `tail()` will trigger them rapidly one after another, so `tail()` run time is heavily dependant on your data structure.

Most operations on `LazySeq` are `O(1)` since they are lazy. Some operations, like `get(n)` or `drop(n)` are `O(n)` (`n` represents parameter, not sequence length). In general run time will be similar to normal linked list.

Because `LazySeq` remembers all already computed values in a single linked list, memory consumption is always `O(n)`, where `n`n is the number of already computed elements. 

## Troubleshooting

### Error `invalid target release: 1.8` during maven build

If you see this error message during maven build:

	[INFO] BUILD FAILURE
	...
	[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.1:compile (default-compile) on project lazyseq: 
	Fatal error compiling: invalid target release: 1.8 -> [Help 1]

it means you are not compiling using Java 8. [Download JDK 8 with lambda support](https://jdk8.java.net/lambda/) and let maven use it:

	$ export JAVA_HOME=/path/to/jdk8

### I get `StackOverflowError` or program hangs infinitely

When working with `LazySeq` you sometimes get `StackOverflowError` or `OutOfMemoryError`:
 
	java.lang.StackOverflowError
		at sun.misc.Unsafe.allocateInstance(Native Method)
		at java.lang.invoke.DirectMethodHandle.allocateInstance(DirectMethodHandle.java:426)
		at com.nurkiewicz.lazyseq.LazySeq.iterate(LazySeq.java:118)
		at com.nurkiewicz.lazyseq.LazySeq.lambda$0(LazySeq.java:118)
		at com.nurkiewicz.lazyseq.LazySeq$$Lambda$2.get(Unknown Source)
		at com.nurkiewicz.lazyseq.Cons.tail(Cons.java:32)
		at com.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)
		at com.nurkiewicz.lazyseq.LazySeq.size(LazySeq.java:325)

When working with possibly infinite data structures, care must be taken. Avoid calling operations that *must* (`size()`, `allMatch()`, `minBy()`, `forEach()`, `reduce()`, ...) or *can* (`filter()`, `distinct()`, ...) traverse the whole sequence in order to give correct results. See *Pitfalls* for more examples and ways to avoid.

## Maturity

[![Build Status](https://travis-ci.org/nurkiewicz/LazySeq.svg?branch=master)](https://travis-ci.org/nurkiewicz/LazySeq)

### Quality

This project was started as an exercise and is not battle-proven. But a healthy [300+ unit-test suite](https://github.com/nurkiewicz/LazySeq/tree/master/src/test/java/com/nurkiewicz/lazyseq) (3:1 test code/production code ratio) guards quality and functional correctness. I also make sure `LazySeq` is as lazy as possible by mocking tail functions and verifying they are called as rarely as one can get.

Project was tested on [Java 8 build 113](https://jdk8.java.net/download.html) with lambda support.

### Contributions and bug reports

In the event of finding a bug or missing feature, don't hesitate to open a [new ticket](https://github.com/nurkiewicz/LazySeq/issues) or start [pull request](https://github.com/nurkiewicz/LazySeq/pulls). I would also love to see more interesting usages of `LazySeq` in wild.

## License
This project is released under version 2.0 of the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).

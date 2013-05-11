# `LazySeq` - lazy sequence implementation for Java 8

## TODO
* `equals()`/`hashCode()`
* More complex test cases:
** CRON expressions `LazySeq`
** [Collatz conjecture](http://en.wikipedia.org/wiki/Collatz_conjecture)
** Fibonacci numbers using `zip` and `map`
** (In)finite `LazySeq` of database records, fetched page at a time (buffering/caching)

## Possible improvements
* Just like `FixedCons` is used when tail is known up-front, consider `IterableCons` that wraps existing `Iterable` in one node rather than building `FixedCons` hierarchy. This can be used for all `concat` methods.
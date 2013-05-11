package com.blogspot.nurkiewicz.lazyseq;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableList;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/6/13, 9:20 PM
 */
public abstract class LazySeq<E> extends AbstractList<E> {

	private static final LazySeq<?> NIL = new Nil<>();

	public abstract E head();

	public Optional<E> headOption() {
		return Optional.of(head());
	}

	public abstract LazySeq<E> tail();

	public static <E> LazySeq<E> of(E element) {
		return cons(element, empty());
	}

	public static <E> LazySeq<E> of(E element1, E element2, E element3) {
		return cons(element1, of(element2, element3));
	}

	public static <E> LazySeq<E> of(E element1, E element2, E element3, Supplier<LazySeq<E>> tailFun) {
		return cons(element1, of(element2, element3, tailFun));
	}

	public static <E> LazySeq<E> of(E element1, E element2, Supplier<LazySeq<E>> tailFun) {
		return cons(element1, of(element2, tailFun));
	}

	public static <E> LazySeq<E> of(E element, Supplier<LazySeq<E>> tailFun) {
		return cons(element, tailFun);
	}

	public static <E> LazySeq<E> of(E element1, E element2) {
		return cons(element1, of(element2));
	}

	public static <E> LazySeq<E> of(E... elements) {
		return of(Arrays.asList(elements).iterator());
	}

	public static <E> LazySeq<E> of(Iterable<E> elements) {
		return of(elements.iterator());
	}

	public static <E> LazySeq<E> concat(Iterable<E> elements, Supplier<LazySeq<E>> tailFun) {
		return concat(elements.iterator(), tailFun);
	}

	public static <E> LazySeq<E> concat(Iterable<E> elements, LazySeq<E> tail) {
		return concat(elements.iterator(), tail);
	}

	public static <E> LazySeq<E> concat(Iterator<E> iterator, LazySeq<E> tail) {
		if (iterator.hasNext()) {
			return concatNonEmptyIterator(iterator, tail);
		} else {
			return tail;
		}
	}

	private static <E> LazySeq<E> concatNonEmptyIterator(Iterator<E> iterator, LazySeq<E> tail) {
		final E next = iterator.next();
		if (iterator.hasNext()) {
			return cons(next, concatNonEmptyIterator(iterator, tail));
		} else {
			return cons(next, tail);
		}
	}

	public static <E> LazySeq<E> concat(Iterator<E> iterator, Supplier<LazySeq<E>> tailFun) {
		if (iterator.hasNext()) {
			return concatNonEmptyIterator(iterator, tailFun);
		} else {
			return tailFun.get();
		}
	}

	private static <E> LazySeq<E> concatNonEmptyIterator(Iterator<E> iterator, Supplier<LazySeq<E>> tailFun) {
		final E next = iterator.next();
		if (iterator.hasNext()) {
			return cons(next, concatNonEmptyIterator(iterator, tailFun));
		} else {
			return cons(next, tailFun);
		}
	}

	public static <E> LazySeq<E> of(Iterator<E> iterator) {
		if (iterator.hasNext()) {
			return cons(iterator.next(), of(iterator));
		} else {
			return empty();
		}
	}

	public static <E> LazySeq<E> cons(E head, Supplier<LazySeq<E>> tailFun) {
		return new Cons<>(head, tailFun);
	}

	public static <E> LazySeq<E> cons(E head, LazySeq<E> tail) {
		return new FixedCons<>(head, tail);
	}

	@SuppressWarnings("unchecked")
	public static <E> LazySeq<E> empty() {
		return (LazySeq<E>) NIL;
	}

	public static <E> LazySeq<E> iterate(E initial, Function<E, E> fun) {
		return new Cons<>(initial, () -> iterate(fun.apply(initial), fun));
	}

	public static <E> Collector<E, LazySeq<E>> toLazySeq() {
		return DummyLazySeqCollector.getInstance();
	}

	public static <E> LazySeq<E> tabulate(int start, Function<Integer, E> generator) {
		return cons(generator.apply(start), () -> tabulate(start + 1, generator));
	}

	public static <E> LazySeq<E> continually(Supplier<E> generator) {
		return cons(generator.get(), () -> continually(generator));
	}

	protected abstract boolean isTailDefined();

	@Override
	public E get(final int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(Integer.toString(index));
		}
		LazySeq<E> cur = this;
		for (int curIdx = index; curIdx > 0; --curIdx) {
			if (cur.tail().isEmpty()) {
				throw new IndexOutOfBoundsException(Integer.toString(index));
			}
			cur = cur.tail();
		}
		return cur.head();
	}

	public abstract <R> LazySeq<R> map(Function<? super E, ? extends R> mapper);

	@Override
	public Stream<E> stream() {
		return new LazySeqStream<E>(this);
	}

	@Override
	public Spliterator<E> spliterator() {
		return new Spliterator<E>() {

			private LazySeq<E> cur = LazySeq.this;

			@Override
			public boolean tryAdvance(Consumer<? super E> action) {
				if (!cur.isEmpty()) {
					action.accept(cur.head());
					cur = cur.tail();
					return true;
				} else {
					return false;
				}
			}

			@Override
			public Spliterator<E> trySplit() {
				if (cur.isEmpty()) {
					return null;
				}
				final E singleton = cur.head();
				cur = cur.tail();
				return new Spliterator<E>() {
					@Override
					public boolean tryAdvance(Consumer<? super E> action) {
						action.accept(singleton);
						return true;
					}

					@Override
					public Spliterator<E> trySplit() {
						return null;
					}

					@Override
					public long estimateSize() {
						return 1;
					}

					@Override
					public int characteristics() {
						return CONCURRENT | IMMUTABLE | NONNULL | ORDERED;
					}
				};
			}

			@Override
			public long estimateSize() {
				return Long.MAX_VALUE;
			}

			@Override
			public int characteristics() {
				return CONCURRENT | IMMUTABLE | NONNULL | ORDERED;
			}
		};
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder("[");
		LazySeq<E> cur = this;
		while (!cur.isEmpty()) {
			s.append(cur.head());
			if (cur.isTailDefined()) {
				if (!cur.tail().isEmpty()) {
					s.append(", ");
				}
				cur = cur.tail();
			} else {
				s.append(", ?");
				break;
			}
		}
		return s.append("]").toString();
	}

	public abstract LazySeq<E> filter(Predicate<? super E> predicate);

	public abstract <R> LazySeq<R> flatMap(Function<? super E, ? extends Stream<? extends R>> mapper);

	public abstract LazySeq<E> limit(long maxSize);

	public LazySeq<E> take(long maxSize) {
		return limit(maxSize);
	}

	public LazySeq<E> substream(long startInclusive) {
		if (startInclusive > 0) {
			return tail().substream(startInclusive - 1);
		} else {
			return this;
		}
	}

	public LazySeq<E> drop(long startInclusive) {
		return substream(startInclusive);
	}

	public LazySeq<E> substream(long startInclusive, long endExclusive) {
		if (startInclusive > 0) {
			return tail().substream(startInclusive - 1, endExclusive - 1);
		} else {
			return limit(endExclusive);
		}
	}

	public void forEach(Consumer<? super E> action) {
		action.accept(head());
		tail().forEach(action);
	}

	public E reduce(E identity, BinaryOperator<E> accumulator) {
		E result = identity;
		LazySeq<E> cur = this;
		while (!cur.isEmpty()) {
			result = accumulator.apply(result, cur.head());
			cur = cur.tail();
		}
		return result;
	}

	public Optional<E> reduce(BinaryOperator<E> accumulator) {
		if (isEmpty() || tail().isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(tail().reduce(head(), accumulator));
	}

	public <U> U reduce(U identity, BiFunction<U, ? super E, U> accumulator) {
		U result = identity;
		LazySeq<E> cur = this;
		while (!cur.isEmpty()) {
			identity = accumulator.apply(identity, cur.head());
			cur = cur.tail();
		}
		return result;
	}

	public Optional<E> min(Comparator<? super E> comparator) {
		return greatestByComparator(comparator.reverseOrder());
	}

	public Optional<E> max(Comparator<? super E> comparator) {
		return greatestByComparator(comparator);
	}

	private Optional<E> greatestByComparator(Comparator<? super E> comparator) {
		if (tail().isEmpty()) {
			return Optional.of(head());
		}
		E minSoFar = minByComparator(head(), tail().head(), comparator);
		LazySeq<E> cur = this.tail();
		while (!cur.isEmpty()) {
			minSoFar = minByComparator(minSoFar, cur.tail().head(), comparator);
		}
		return Optional.of(minSoFar);
	}

	@Override
	public int size() {
		return 1 + tail().size();
	}

	@Override
	public Iterator<E> iterator() {
		return new LazySeqIterator<E>(this);
	}

	private static <E> E minByComparator(E first, E second, Comparator<? super E> comparator) {
		return comparator.compare(first, second) <= 0? first : second;
	}

	public boolean anyMatch(Predicate<? super E> predicate) {
		return predicate.test(head()) || tail().anyMatch(predicate);
	}

	public boolean allMatch(Predicate<? super E> predicate) {
		return predicate.test(head()) && tail().allMatch(predicate);
	}

	public boolean noneMatch(Predicate<? super E> predicate) {
		return allMatch(predicate.negate());
	}

	public <S, R> LazySeq<R> zip(LazySeq<? extends S> second, BiFunction<? super E, ? super S, ? extends R> zipper) {
		final R headsZipped = zipper.apply(head(), second.head());
		if (second.tail().isEmpty()) {
			return of(headsZipped);
		} else {
			return cons(headsZipped, () -> tail().zip(second.tail(), zipper));
		}
	}

	public LazySeq<E> takeWhile(Predicate<? super E> predicate) {
		if (predicate.test(head())) {
			return cons(head(), tail().takeWhile(predicate));
		} else {
			return empty();
		}
	}

	public LazySeq<E> dropWhile(Predicate<? super E> predicate) {
		if (predicate.test(head())) {
			return tail().dropWhile(predicate);
		} else {
			return empty();
		}
	}

	public LazySeq<List<E>> sliding(int size) {
		final List<E> window = unmodifiableList(new ArrayList<>(take(size)));
		return cons(window, () -> tail().sliding(size));
	}

	public LazySeq<List<E>> grouped(int size) {
		final List<E> window = unmodifiableList(new ArrayList<>(take(size)));
		return cons(window, () -> drop(size).grouped(size));
	}

	public LazySeq<E> scan(E initial, BinaryOperator<E> fun) {
		return cons(initial, () -> tail().scan(fun.apply(initial, head()), fun));
	}

}



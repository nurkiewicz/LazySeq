package com.blogspot.nurkiewicz.lazyseq;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/6/13, 9:20 PM
 */
public abstract class LazySeq<E> extends AbstractList<E> {

	@SuppressWarnings("unchecked")
	public static <E> LazySeq<E> empty() {
		return Nil.instance();
	}

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

	public static <E> LazySeq<E> continually(Iterable<E> cycle) {
		if (!cycle.iterator().hasNext()) {
			return empty();
		}
		return continuallyUnsafe(cycle);
	}

	private static <E> LazySeq<E> continuallyUnsafe(Iterable<E> cycle) {
		return concat(cycle, () -> continually(cycle));
	}

	public static <E> LazySeq<E> continually(E value) {
		return cons(value, () -> continually(value));
	}

	public static LazySeq<Integer> numbers(int start) {
		return numbers(start, 1);
	}

	public static LazySeq<Integer> numbers(int start, int step) {
		return cons(start, () -> numbers(start + step, step));
	}

	public static LazySeq<Double> numbers(double start) {
		return numbers(start, 1.0);
	}

	public static LazySeq<Double> numbers(double start, double step) {
		return cons(start, () -> numbers(start + step, step));
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
		return new LazySeqStream<>(this);
	}

	@Override
	public Stream<E> parallelStream() {
		return stream();
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

	public abstract <R> LazySeq<R> flatMap(Function<? super E, ? extends Iterable<? extends R>> mapper);

	public LazySeq<E> limit(long maxSize) {
		return take(maxSize);
	}

	/**
	 * Converts this {@link LazySeq} to immutable {@link List}.
	 *
	 * Notice that this method will eventually fail at runtime when called on infinite sequence.
	 * @return {@link List} of all elements in this lazy sequence.
	 */
	public List<E> toList() {
		return Collections.unmodifiableList(this.force());
	}

	public LazySeq<E> take(long maxSize) {
		if (maxSize < 0) {
			throw new IllegalArgumentException(Long.toString(maxSize));
		}
		if (maxSize == 0) {
			return empty();
		}
		return takeUnsafe(maxSize);
	}

	protected abstract LazySeq<E> takeUnsafe(long maxSize);

	public LazySeq<E> drop(long startInclusive) {
		if (startInclusive < 0) {
			throw new IllegalArgumentException(Long.toString(startInclusive));
		}
		return dropUnsafe(startInclusive);
	}

	protected LazySeq<E> dropUnsafe(long startInclusive) {
		if (startInclusive > 0) {
			return tail().drop(startInclusive - 1);
		} else {
			return this;
		}
	}

	@Override
	public LazySeq<E> subList(int fromIndex, int toIndex) {
		return slice(fromIndex, toIndex);
	}

	public LazySeq<E> slice(long startInclusive, long endExclusive) {
		if (startInclusive < 0 || startInclusive > endExclusive) {
			throw new IllegalArgumentException("slice(" + startInclusive + ", " + endExclusive + ")");
		}
		return dropUnsafe(startInclusive).takeUnsafe(endExclusive - startInclusive);
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
			result = accumulator.apply(result, cur.head());
			cur = cur.tail();
		}
		return result;
	}

	public <C extends Comparable<? super C>> Optional<E> maxBy(Function<E, C> propertyFun) {
		return max(propertyFunToComparator(propertyFun));
	}

	public Optional<E> max(Comparator<? super E> comparator) {
		return greatestByComparator(comparator);
	}

	public <C extends Comparable<? super C>> Optional<E> minBy(Function<E, C> propertyFun) {
		return min(propertyFunToComparator(propertyFun));
	}

	public Optional<E> min(Comparator<? super E> comparator) {
		return greatestByComparator(comparator.reverseOrder());
	}

	private <C extends Comparable<? super C>> Comparator<? super E> propertyFunToComparator(Function<E, C> propertyFun) {
		return (a, b) -> {
			final C aProperty = propertyFun.apply(a);
			final C bProperty = propertyFun.apply(b);
			return aProperty.compareTo(bProperty);
		};
	}

	private Optional<E> greatestByComparator(Comparator<? super E> comparator) {
		if (tail().isEmpty()) {
			return Optional.of(head());
		}
		E minSoFar = head();
		LazySeq<E> cur = this.tail();
		while (!cur.isEmpty()) {
			minSoFar = maxByComparator(minSoFar, cur.head(), comparator);
			cur = cur.tail();
		}
		return Optional.of(minSoFar);
	}

	private static <E> E maxByComparator(E first, E second, Comparator<? super E> comparator) {
		return comparator.compare(first, second) >= 0? first : second;
	}

	@Override
	public int size() {
		return 1 + tail().size();
	}

	@Override
	public Iterator<E> iterator() {
		return new LazySeqIterator<>(this);
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
		if (second.isEmpty()) {
			return empty();
		} else {
			final R headsZipped = zipper.apply(head(), second.head());
			return cons(headsZipped, () -> tail().zip(second.tail(), zipper));
		}
	}

	public LazySeq<E> takeWhile(Predicate<? super E> predicate) {
		if (predicate.test(head())) {
			return cons(head(), () -> tail().takeWhile(predicate));
		} else {
			return empty();
		}
	}

	public LazySeq<E> dropWhile(Predicate<? super E> predicate) {
		if (predicate.test(head())) {
			return tail().dropWhile(predicate);
		} else {
			return this;
		}
	}

	public LazySeq<List<E>> sliding(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException(Integer.toString(size));
		}
		return slidingUnsafe(size);
	}

	protected LazySeq<List<E>> slidingUnsafe(int size) {
		final List<E> window = take(size).toList();
		return cons(window, () -> tail().slidingFullOnly(size));
	}

	protected LazySeq<List<E>> slidingFullOnly(int size) {
		final List<E> window = take(size).toList();
		if (window.size() < size) {
			return empty();
		} else {
			return cons(window, () -> tail().slidingFullOnly(size));
		}
	}

	public LazySeq<List<E>> grouped(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException(Integer.toString(size));
		}
		return groupedUnsafe(size);
	}

	protected LazySeq<List<E>> groupedUnsafe(int size) {
		final List<E> window = take(size).toList();
		return cons(window, () -> drop(size).groupedUnsafe(size));
	}

	public LazySeq<E> scan(E initial, BinaryOperator<E> fun) {
		return cons(initial, () -> tail().scan(fun.apply(initial, head()), fun));
	}

	public LazySeq<E> distinct() {
		return filterOutSeen(new HashSet<>());
	}

	private LazySeq<E> filterOutSeen(Set<E> exclude) {
		final LazySeq<E> moreDistinct = filter(e -> !exclude.contains(e));
		if (moreDistinct.isEmpty()) {
			return empty();
		}
		final E next = moreDistinct.head();
		exclude.add(next);
		return cons(next, () -> moreDistinct.tail().filterOutSeen(exclude));
	}

	public LazySeq<E> sorted() {
		return sorted((o1, o2) -> ((Comparable<E>) o1).compareTo(o2));
	}

	public LazySeq<E> sorted(Comparator<? super E> comparator) {
		final ArrayList<E> list = new ArrayList<>(this);
		list.sort(comparator);
		return of(list);
	}

	public boolean startsWith(Iterable<E> prefix) {
		return startsWith(prefix.iterator());
	}

	public boolean startsWith(Iterator<E> iterator) {
		return !iterator.hasNext() ||
				head().equals(iterator.next()) && tail().startsWith(iterator);
	}

	public LazySeq<E> force() {
		tail().force();
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LazySeq)) return false;

		LazySeq right = (LazySeq) o;
		return !right.isEmpty() &&
				head().equals(right.head()) &&
				tail().equals(right.tail());
	}

	@Override
	public int hashCode() {
		return head().hashCode() + tail().hashCode() * 31;
	}

}



package com.nurkiewicz.lazyseq;

import java.util.*;
import java.util.function.*;

class Nil<E> extends LazySeq<E> {

	private static final Nil<?> NIL = new Nil<>();

	@SuppressWarnings("unchecked")
	public static <E> Nil<E> instance() {
		return (Nil<E>) NIL;
	}

	@Override
	public E head() {
		throw new NoSuchElementException("head of empty stream");
	}

	@Override
	public Optional<E> headOption() {
		return Optional.empty();
	}

	@Override
	public LazySeq<E> tail() {
		throw new NoSuchElementException("tail of empty stream");
	}

	@Override
	protected boolean isTailDefined() {
		return false;
	}

	@Override
	public E get(int index) {
		throw new IndexOutOfBoundsException(Integer.toString(index));
	}

	@Override
	public <R> LazySeq<R> map(Function<? super E, ? extends R> mapper) {
		return instance();
	}

	@Override
	public LazySeq<E> filter(Predicate<? super E> predicate) {
		return instance();
	}

	@Override
	public <R> LazySeq<R> flatMap(Function<? super E, ? extends Iterable<? extends R>> mapper) {
		return instance();
	}

	@Override
	protected LazySeq<E> takeUnsafe(long maxSize) {
		return instance();
	}

	@Override
	protected LazySeq<E> dropUnsafe(long startInclusive) {
		return instance();
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		//no op
	}

	@Override
	public Optional<E> min(Comparator<? super E> comparator) {
		return Optional.empty();
	}

	@Override
	public Optional<E> max(Comparator<? super E> comparator) {
		return Optional.empty();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean anyMatch(Predicate<? super E> predicate) {
		return false;
	}

	@Override
	public boolean allMatch(Predicate<? super E> predicate) {
		return true;
	}

	@Override
	public <S, R> LazySeq<R> zip(LazySeq<? extends S> second, BiFunction<? super E, ? super S, ? extends R> zipper) {
		return instance();
	}

	@Override
	public LazySeq<E> takeWhile(Predicate<? super E> predicate) {
		return instance();
	}

	@Override
	public LazySeq<E> dropWhile(Predicate<? super E> predicate) {
		return instance();
	}

	@Override
	public LazySeq<List<E>> slidingUnsafe(int size) {
		return instance();
	}

	@Override
	protected LazySeq<List<E>> groupedUnsafe(int size) {
		return instance();
	}

	@Override
	public LazySeq<E> scan(E initial, BinaryOperator<E> fun) {
		return of(initial);
	}

	@Override
	public LazySeq<E> distinct() {
		return instance();
	}

	@Override
	public boolean startsWith(Iterator<E> iterator) {
		return !iterator.hasNext();
	}

	@Override
	public LazySeq<E> force() {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Nil;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
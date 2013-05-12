package com.blogspot.nurkiewicz.lazyseq;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.*;

class Nil<E> extends LazySeq<E> {

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
		return empty();
	}

	@Override
	public LazySeq<E> filter(Predicate<? super E> predicate) {
		return empty();
	}

	@Override
	public <R> LazySeq<R> flatMap(Function<? super E, ? extends Iterable<? extends R>> mapper) {
		return empty();
	}

	@Override
	protected LazySeq<E> takeUnsafe(long maxSize) {
		return empty();
	}

	@Override
	protected LazySeq<E> dropUnsafe(long startInclusive) {
		return empty();
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
		return empty();
	}

	@Override
	public LazySeq<E> takeWhile(Predicate<? super E> predicate) {
		return empty();
	}

	@Override
	public LazySeq<E> dropWhile(Predicate<? super E> predicate) {
		return empty();
	}

	@Override
	public LazySeq<List<E>> slidingUnsafe(int size) {
		return empty();
	}

	@Override
	protected LazySeq<List<E>> groupedUnsafe(int size) {
		return empty();
	}

	@Override
	public LazySeq<E> scan(E initial, BinaryOperator<E> fun) {
		return of(initial);
	}

	@Override
	public boolean isEmpty() {
		return true;
	}
}
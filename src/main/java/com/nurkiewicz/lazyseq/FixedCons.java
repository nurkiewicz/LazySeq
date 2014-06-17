package com.nurkiewicz.lazyseq;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/8/13, 9:09 PM
 */
class FixedCons<E> extends LazySeq<E> {

	private final E head;
	private final LazySeq<E> tail;

	public FixedCons(E head, LazySeq<E> tail) {
		this.head = Objects.requireNonNull(head);
		this.tail = Objects.requireNonNull(tail);
	}

	@Override
	public E head() {
		return head;
	}

	@Override
	public LazySeq<E> tail() {
		return tail;
	}

	@Override
	protected boolean isTailDefined() {
		return true;
	}

	@Override
	public <R> LazySeq<R> map(Function<? super E, ? extends R> mapper) {
		return cons(mapper.apply(head), tail.map(mapper));
	}

	@Override
	public LazySeq<E> filter(Predicate<? super E> predicate) {
		if (predicate.test(head)) {
			return cons(head, tail.filter(predicate));
		} else {
			return tail.filter(predicate);
		}
	}

	@Override
	public <R> LazySeq<R> flatMap(Function<? super E, ? extends Iterable<? extends R>> mapper) {
		final ArrayList<R> result = new ArrayList<>();
		mapper.apply(head).forEach(result::add);
		return concat(result, tail.flatMap(mapper));
	}

	@Override
	protected LazySeq<E> takeUnsafe(long maxSize) {
		if (maxSize > 1) {
			return cons(head, tail.takeUnsafe(maxSize - 1));
		} else {
			return LazySeq.of(head);
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
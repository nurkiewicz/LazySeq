package com.blogspot.nurkiewicz.lazyseq;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/8/13, 9:08 PM
 */
class Cons<E> extends LazySeq<E> {
	private final E head;
	private volatile LazySeq<E> tailOrNull;
	private final Supplier<LazySeq<E>> tailFun;

	Cons(E head, Supplier<LazySeq<E>> tailFun) {
		this.head = head;
		this.tailFun = tailFun;
	}

	@Override
	public E head() {
		return head;
	}

	@Override
	public LazySeq<E> tail() {
		if (!isTailDefined()) {
			synchronized (this) {
				if (!isTailDefined()) {
					tailOrNull = tailFun.get();
				}
			}
		}
		return tailOrNull;
	}

	@Override
	protected boolean isTailDefined() {
		return tailOrNull != null;
	}

	public <R> LazySeq<R> map(Function<? super E, ? extends R> mapper) {
		return cons(mapper.apply(head()), () -> tail().map(mapper));
	}

	@Override
	public LazySeq<E> filter(Predicate<? super E> predicate) {
		if (predicate.test(head)) {
			return cons(head, () -> tail().filter(predicate));
		} else {
			return tail().filter(predicate);
		}
	}

	@Override
	public <R> LazySeq<R> flatMap(Function<? super E, ? extends Stream<? extends R>> mapper) {
		final List<R> headFlattened = mapper.apply(head).collect(Collectors.<R>toList());
		return concat(headFlattened, () -> tail().flatMap(mapper));
	}

	@Override
	public LazySeq<E> limit(long maxSize) {
		if (maxSize > 0) {
			return cons(head, () -> tail().limit(maxSize - 1));
		} else {
			return LazySeq.empty();
		}
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

}
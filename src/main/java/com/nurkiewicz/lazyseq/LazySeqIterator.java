package com.nurkiewicz.lazyseq;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/9/13, 2:15 PM
 */
public class LazySeqIterator<E> implements Iterator<E> {

	private LazySeq<E> underlying;

	public LazySeqIterator(LazySeq<E> lazySeq) {
		this.underlying = lazySeq;
	}

	@Override
	public boolean hasNext() {
		return !underlying.isEmpty();
	}

	@Override
	public E next() {
		final E next = underlying.head();
		underlying = underlying.tail();
		return next;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("remove");
	}

	@Override
	public void forEachRemaining(Consumer<? super E> action) {
		underlying.forEach(action);
	}
}

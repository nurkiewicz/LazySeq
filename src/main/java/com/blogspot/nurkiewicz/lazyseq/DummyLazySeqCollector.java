package com.blogspot.nurkiewicz.lazyseq;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/9/13, 11:09 AM
 */
final class DummyLazySeqCollector<E> implements Collector<E, LazySeq<E>, LazySeq<E>> {

	private static final DummyLazySeqCollector<?> INSTANCE = new DummyLazySeqCollector<>();

	@SuppressWarnings("unchecked")
	public static <E> DummyLazySeqCollector<E> getInstance() {
		return (DummyLazySeqCollector<E>) INSTANCE;
	}

	@Override
	public Supplier<LazySeq<E>> supplier() {
		throw new IllegalStateException("Should never be called");
	}

	@Override
	public BiConsumer<LazySeq<E>, E> accumulator() {
		throw new IllegalStateException("Should never be called");
	}

	@Override
	public BinaryOperator<LazySeq<E>> combiner() {
		throw new IllegalStateException("Should never be called");
	}

	@Override
	public Function<LazySeq<E>, LazySeq<E>> finisher() {
		throw new IllegalStateException("Should never be called");
	}

	@Override
	public Set<Characteristics> characteristics() {
		throw new IllegalStateException("Should never be called");
	}
}

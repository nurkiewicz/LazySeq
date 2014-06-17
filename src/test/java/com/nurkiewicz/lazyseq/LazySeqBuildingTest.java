package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 11:50 AM
 */
public class LazySeqBuildingTest extends AbstractBaseTestCase {

	@Test
	public void shouldCreateEmptySeq() throws Exception {
		assertThat(empty()).isEmpty();
		assertThat(empty().isEmpty()).isTrue();
		assertThat(empty().size()).isZero();
	}

	@Test
	public void shouldCreateEmptySeqFromEmptyList() throws Exception {
		assertThat(of()).isEmpty();
		assertThat(of().isEmpty()).isTrue();
		assertThat(of().size()).isZero();
	}

	@Test
	public void shouldCreateEmptySeqFromEmptyIterable() throws Exception {
		assertThat(of(emptyList())).isEmpty();
		assertThat(of(emptyList()).isEmpty()).isTrue();
		assertThat(of(emptyList()).size()).isZero();
	}

	@Test
	public void shouldCreateEmptySeqFromEmptyIterator() throws Exception {
		assertThat(of(emptyIterator())).isEmpty();
		assertThat(of(emptyIterator()).isEmpty()).isTrue();
		assertThat(of(emptyIterator()).size()).isZero();
	}

	@Test
	public void shouldCreateSeqWithOneElement() throws Exception {
		assertThat(of(1)).hasSize(1);
		assertThat(of(1).isEmpty()).isFalse();
		assertThat(of(1).size()).isEqualTo(1);
	}

	@Test
	public void shouldCreateSeqWithTwoElements() throws Exception {
		assertThat(of(2, 3)).hasSize(2);
		assertThat(of(2, 3).isEmpty()).isFalse();
		assertThat(of(2, 3).size()).isEqualTo(2);
	}

	@Test
	public void shouldCreateSeqWithThreeElements() throws Exception {
		assertThat(of(4, 5, 6)).hasSize(3);
		assertThat(of(4, 5, 6).isEmpty()).isFalse();
		assertThat(of(4, 5, 6).size()).isEqualTo(3);
	}

	@Test
	public void shouldCreateSeqWithSeveralElements() throws Exception {
		assertThat(of(7, 8, 9, 1, 2, 3, 4, 5, 6)).hasSize(9);
		assertThat(of(7, 8, 9, 1, 2, 3, 4, 5, 6).isEmpty()).isFalse();
		assertThat(of(7, 8, 9, 1, 2, 3, 4, 5, 6).size()).isEqualTo(9);
	}

	@Test
	public void shouldCreateSeqWithSeveralElementsFromIterable() throws Exception {
		assertThat(of(asList(7, 8, 9, 1, 2, 3, 4, 5, 6))).hasSize(9);
		assertThat(of(asList(7, 8, 9, 1, 2, 3, 4, 5, 6)).isEmpty()).isFalse();
		assertThat(of(asList(7, 8, 9, 1, 2, 3, 4, 5, 6)).size()).isEqualTo(9);
	}

	@Test
	public void shouldCreateSeqWithSeveralElementsFromIterator() throws Exception {
		assertThat(of(asList(7, 8, 9, 1, 2, 3, 4, 5, 6).iterator())).hasSize(9);
		assertThat(of(asList(7, 8, 9, 1, 2, 3, 4, 5, 6).iterator()).isEmpty()).isFalse();
		assertThat(of(asList(7, 8, 9, 1, 2, 3, 4, 5, 6).iterator()).size()).isEqualTo(9);
	}

	@Test
	public void shouldCreateInfiniteSeqStartingFromFewFixedElements() throws Exception {
		final LazySeq<Integer> infinite = of(1, 2, 3, () -> numbers(4));

		assertThat(infinite.take(10)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@Test
	public void shouldCreateInfiniteSeqStartingFromIterable() throws Exception {
		final LazySeq<Integer> infinite = concat(asList(1, 2, 3), () -> numbers(4));

		assertThat(infinite.take(10)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@Test
	public void shouldCreateInfiniteSeqStartingFromIterableAndConcreteSeq() throws Exception {
		final LazySeq<Integer> infinite = concat(asList(1, 2, 3), numbers(4));

		assertThat(infinite.take(10)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@Test
	public void shouldCreateInfiniteSeqStartingFromIterator() throws Exception {
		final LazySeq<Integer> infinite = concat(asList(1, 2, 3).iterator(), () -> numbers(4));

		assertThat(infinite.take(10)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@Test
	public void shouldCreateInfiniteSeqStartingFromIteratorAndConcreteSeq() throws Exception {
		final LazySeq<Integer> infinite = concat(asList(1, 2, 3).iterator(), numbers(4));

		assertThat(infinite.take(10)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@Test
	public void shouldCreateInfiniteSeqWithConsAndSupplier() throws Exception {
		final LazySeq<Integer> infinite = cons(1, () -> numbers(2));

		assertThat(infinite.take(10)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

	@Test
	public void shouldCreateInfiniteSeqWithConsAndConcreteSeq() throws Exception {
		final LazySeq<Integer> infinite = cons(1, numbers(2));

		assertThat(infinite.take(10)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
	}

}

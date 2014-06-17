package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.numbers;
import static com.nurkiewicz.lazyseq.LazySeq.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 4:15 PM
 */
public class LazySeqFlatMapTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Test
	public void shouldReturnEmptySequenceWhenEmptyAsInput() throws Exception {
		//given
		final LazySeq<Integer> empty = LazySeq.empty();

		//when
		final LazySeq<Integer> flat = empty.flatMap(i -> asList(i, -i));

		//then
		assertThat(flat.isEmpty());
	}

	@Test
	public void shouldFlattenHead() throws Exception {
		//given
		final LazySeq<Integer> raw = of(1, 2);

		//when
		final LazySeq<Integer> flat = raw.flatMap(i -> asList(i, -i));

		//then
		assertThat(flat).isEqualTo(of(1, -1, 2, -2));
	}

	@Test
	public void shouldLazilyFlattenInfiniteStream() throws Exception {
		//given
		final LazySeq<Integer> raw = numbers(1);

		//when
		final LazySeq<Integer> flat = raw.flatMap(i -> asList(i, 0, -i));

		//then
		assertThat(flat.take(10)).isEqualTo(of(1, 0, -1, 2, 0, -2, 3, 0, -3, 4));
	}

	@Test
	public void shouldFlattenHeadOnlyAndNotEvaluateTail() throws Exception {
		//given
		final LazySeq<Integer> raw = LazySeq.of(1, supplierMock);

		//when
		final LazySeq<Integer> flat = raw.flatMap(i -> asList(i, 0, -i));

		//then
		assertThat(flat.get(0)).isEqualTo(1);
		assertThat(flat.get(1)).isEqualTo(0);
		assertThat(flat.get(2)).isEqualTo(-1);
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldKeepEvaluatingIfFirstFlatMapResultIsEmpty() throws Exception {
		//given
		int from = -10;
		final LazySeq<Integer> raw = numbers(from);

		//when
		final LazySeq<Integer> flat = raw.flatMap(LazySeqFlatMapTest::flatMapFun);

		//then
		assertThat(flat.take(10)).isEqualTo(of(2, 3, 4, 5, 6, 7, 0, 0, 0, 0));
	}

	private static Iterable<Integer> flatMapFun(int i) {
		if (i <= 0) {
			return emptyList();
		}
		switch (i) {
			case 1:
				return asList(2);
			case 2:
				return asList(3, 4);
			case 3:
				return asList(5, 6, 7);
			default:
				return asList(0, 0);
		}
	}

}

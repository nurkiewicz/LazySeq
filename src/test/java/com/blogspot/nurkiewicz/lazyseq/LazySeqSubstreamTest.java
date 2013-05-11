package com.blogspot.nurkiewicz.lazyseq;

import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.times;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 6:07 PM
 */
public class LazySeqSubstreamTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Test
	public void shouldThrowWhenNegativeStartIndexOnEmptySeq() throws Exception {
		//given
		final LazySeq<Object> empty = empty();

		try {
			//when
			empty.substream(-1);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenNegativeStartIndexOnNonEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3);

		try {
			//when
			nonEmpty.substream(-1);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenNegativeStartIndexOnInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> infinite = SampleStreams.primes();

		try {
			//when
			infinite.substream(-1);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldReturnEmptySeqWhenSubstreamFromBeginningOfEmptySeq() throws Exception {
		assertThat(empty().substream(0)).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenSubstreamFromFurtherIndexOfEmptySeq() throws Exception {
		assertThat(empty().substream(5)).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenSubstreamPastEnd() throws Exception {
		assertThat(of(1, 2).substream(2)).isEmpty();
	}

	@Test
	public void shouldReturnSubstreamWithoutPrefix() throws Exception {
		assertThat(of(1, 2, 3, 4, 5).substream(2)).isEqualTo(of(3, 4, 5));
	}

	@Test
	public void shouldReturnSubstreamOfInfiniteStream() throws Exception {
		//given
		final LazySeq<Integer> naturals = SampleStreams.naturals(0);

		//when
		final LazySeq<Integer> fromFive = naturals.substream(5);

		//then
		assertThat(fromFive.take(4)).isEqualTo(of(5, 6, 7, 8));
	}

	@Test
	public void shouldNotEvaluateTailExceptFirstElement() throws Exception {
		//given
		final LazySeq<Integer> naturals = LazySeq.of(1, supplierMock);
		BDDMockito.given(supplierMock.get()).willReturn(cons(2, (Supplier<LazySeq<Integer>>) supplierMock::get));

		//when
		naturals.substream(2);

		//then
		Mockito.verify(supplierMock, times(2)).get();
	}

}

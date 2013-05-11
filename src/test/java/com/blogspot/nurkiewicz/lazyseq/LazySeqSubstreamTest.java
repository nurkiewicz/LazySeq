package com.blogspot.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static com.blogspot.nurkiewicz.lazyseq.SampleStreams.naturals;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
		final LazySeq<Integer> naturals = naturals(0);

		//when
		final LazySeq<Integer> fromFive = naturals.substream(5);

		//then
		assertThat(fromFive.take(4)).isEqualTo(of(5, 6, 7, 8));
	}

	@Test
	public void shouldNotEvaluateTailExceptFirstElement() throws Exception {
		//given
		final LazySeq<Integer> naturals = LazySeq.of(1, supplierMock);
		given(supplierMock.get()).willReturn(cons(2, (Supplier<LazySeq<Integer>>) supplierMock::get));

		//when
		naturals.substream(2);

		//then
		verify(supplierMock, times(2)).get();
	}

	@Test
	public void shouldReturnEmptySeqWhenSlicingEmptySeq() throws Exception {
		assertThat(empty().substream(10, 10)).isEmpty();
		assertThat(empty().substream(10, 20)).isEmpty();
		assertThat(empty().substream(0, 0)).isEmpty();
		assertThat(empty().substream(0, 10)).isEmpty();
	}

	@Test
	public void shouldThrowWhenEndIndexIsSmallerThanStartIndexForEmptySeq() throws Exception {
		//when
		final LazySeq<Object> empty = empty();

		try {
			//when
			empty.substream(10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenEndIndexIsSmallerThanStartIndexForNonEmptySeq() throws Exception {
		//when
		final LazySeq<Integer> nonEmpty = of(1, 2);

		try {
			//when
			nonEmpty.substream(10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenEndIndexIsSmallerThanStartIndexForInfiniteSeq() throws Exception {
		final LazySeq<Integer> infinite = naturals(0);

		try {
			//when
			infinite.substream(10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenStartIndexNegative() throws Exception {
		//given
		final LazySeq<Object> empty = empty();

		try {
			//when
			empty.substream(-10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenStartIndexNegativeForNonEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2);

		try {
			//when
			nonEmpty.substream(-10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenStartIndexNegativeForInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> infinite = naturals(0);

		try {
			//when
			infinite.substream(-10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldSliceNonEmptyButFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3, 4, 5, 6);

		//when
		final LazySeq<Integer> subs = nonEmpty.substream(2, 4);

		//then
		assertThat(subs).isEqualTo(of(3, 4));
	}

	@Test
	public void shouldSliceInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = naturals(0);

		//when
		final LazySeq<Integer> subs = naturals.substream(3, 7);

		//then
		assertThat(subs).isEqualTo(of(3, 4, 5, 6));
	}

	@Test
	public void shouldAdjustLengthWhenEndIndexPastEndOfStream() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3, 4, 5, 6, 7);

		//when
		final LazySeq<Integer> subs = nonEmpty.substream(4, 100);

		//then
		assertThat(subs).isEqualTo(of(5, 6, 7));
	}

	@Test
	public void shouldSliceFromBeginningOfSeq() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3, 4, 5, 6, 7);

		//when
		final LazySeq<Integer> subs = nonEmpty.substream(0, 4);

		//then
		assertThat(subs).isEqualTo(of(1, 2, 3, 4));
	}

	@Test
	public void shouldSliceFromBeginningOfSeqAndTrim() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3, 4, 5);

		//when
		final LazySeq<Integer> subs = nonEmpty.substream(0, 100);

		//then
		assertThat(subs).isEqualTo(of(1, 2, 3, 4, 5));
	}

	@Test
	public void shouldReturnEmptySliceWhenSubstreamStartsPastTheEnd() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3);

		//when
		final LazySeq<Integer> subs = nonEmpty.substream(4, 10);

		//then
		assertThat(subs).isEmpty();
	}

}

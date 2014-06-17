package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 7:36 PM
 */
public class LazySeqSliceTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptySeqWhenSlicingEmptySeq() throws Exception {
		assertThat(empty().slice(10, 10)).isEmpty();
		assertThat(empty().slice(10, 20)).isEmpty();
		assertThat(empty().slice(0, 0)).isEmpty();
		assertThat(empty().slice(0, 10)).isEmpty();
	}

	@Test
	public void shouldThrowWhenEndIndexIsSmallerThanStartIndexForEmptySeq() throws Exception {
		//when
		final LazySeq<Object> empty = empty();

		try {
			//when
			empty.slice(10, 9);
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
			nonEmpty.slice(10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenEndIndexIsSmallerThanStartIndexForInfiniteSeq() throws Exception {
		final LazySeq<Integer> infinite = numbers(0);

		try {
			//when
			infinite.slice(10, 9);
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
			empty.slice(-10, 9);
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
			nonEmpty.slice(-10, 9);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenStartIndexNegativeForInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> infinite = numbers(0);

		try {
			//when
			infinite.slice(-10, 9);
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
		final LazySeq<Integer> subs = nonEmpty.slice(2, 4);

		//then
		assertThat(subs).isEqualTo(of(3, 4));
	}

	@Test
	public void shouldSliceInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = numbers(0);

		//when
		final LazySeq<Integer> subs = naturals.slice(3, 7);

		//then
		assertThat(subs).isEqualTo(of(3, 4, 5, 6));
	}

	@Test
	public void shouldAdjustLengthWhenEndIndexPastEndOfStream() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3, 4, 5, 6, 7);

		//when
		final LazySeq<Integer> subs = nonEmpty.slice(4, 100);

		//then
		assertThat(subs).isEqualTo(of(5, 6, 7));
	}

	@Test
	public void shouldSliceFromBeginningOfSeq() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3, 4, 5, 6, 7);

		//when
		final LazySeq<Integer> subs = nonEmpty.slice(0, 4);

		//then
		assertThat(subs).isEqualTo(of(1, 2, 3, 4));
	}

	@Test
	public void shouldSliceFromBeginningOfSeqAndTrim() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3, 4, 5);

		//when
		final LazySeq<Integer> subs = nonEmpty.slice(0, 100);

		//then
		assertThat(subs).isEqualTo(of(1, 2, 3, 4, 5));
	}

	@Test
	public void shouldReturnEmptySliceWhenSubstreamStartsPastTheEnd() throws Exception {
		//given
		final LazySeq<Integer> nonEmpty = of(1, 2, 3);

		//when
		final LazySeq<Integer> subs = nonEmpty.slice(4, 10);

		//then
		assertThat(subs).isEmpty();
	}

}

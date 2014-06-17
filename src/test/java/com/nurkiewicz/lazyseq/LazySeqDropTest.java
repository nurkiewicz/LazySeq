package com.nurkiewicz.lazyseq;

import com.nurkiewicz.lazyseq.samples.Seqs;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 6:07 PM
 */
public class LazySeqDropTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Test
	public void shouldThrowWhenNegativeStartIndexOnEmptySeq() throws Exception {
		//given
		final LazySeq<Object> empty = empty();

		try {
			//when
			long startInclusive = -1;
			empty.drop(startInclusive);
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
			long startInclusive = -1;
			nonEmpty.drop(startInclusive);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenNegativeStartIndexOnInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> infinite = Seqs.primes();

		try {
			//when
			long startInclusive = -1;
			infinite.drop(startInclusive);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldReturnEmptySeqWhenSubstreamFromBeginningOfEmptySeq() throws Exception {
		assertThat(empty().drop((long) 0)).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenSubstreamFromFurtherIndexOfEmptySeq() throws Exception {
		assertThat(empty().drop((long) 5)).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenSubstreamPastEnd() throws Exception {
		assertThat(of(1, 2).drop((long) 2)).isEmpty();
	}

	@Test
	public void shouldReturnSubstreamWithoutPrefix() throws Exception {
		assertThat(of(1, 2, 3, 4, 5).drop((long) 2)).isEqualTo(of(3, 4, 5));
	}

	@Test
	public void shouldReturnSubstreamOfInfiniteStream() throws Exception {
		//given
		final LazySeq<Integer> naturals = numbers(0);

		//when
		final LazySeq<Integer> fromFive = naturals.drop((long) 5);

		//then
		assertThat(fromFive.take(4)).isEqualTo(of(5, 6, 7, 8));
	}

	@Test
	public void shouldNotEvaluateTailExceptFirstElement() throws Exception {
		//given
		final LazySeq<Integer> naturals = LazySeq.of(1, supplierMock);
		given(supplierMock.get()).willReturn(cons(2, (Supplier<LazySeq<Integer>>) supplierMock::get));

		//when
		naturals.drop((long) 2);

		//then
		verify(supplierMock, times(2)).get();
	}

}

package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.numbers;
import static com.nurkiewicz.lazyseq.LazySeq.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 5:40 PM
 */
public class LazySeqTakeTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Test
	public void shouldThrowWhenTakeWithNegativeArguemtn() throws Exception {
		//given
		final LazySeq<Integer> seq = of(1, 2, 3);

		try {
			//when
			seq.limit(-1);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldAllowTakingNElementsFromEmptySeq() throws Exception {
		//given
		final LazySeq<Object> empty = LazySeq.empty();

		//when
		final LazySeq<Object> limited = empty.limit(10);

		//then
		assertThat(limited).isEmpty();
	}

	@Test
	public void shouldLeaveTooShortSeq() throws Exception {
		//given
		final LazySeq<Integer> shortSeq = of(1, 2);

		//when
		final LazySeq<Integer> limited = shortSeq.limit(5);

		//then
		assertThat(limited).isEqualTo(of(1, 2));
	}

	@Test
	public void shouldTrimTooLongSeq() throws Exception {
		//given
		final LazySeq<Integer> shortSeq = of(1, 2, 3, 4, 5);

		//when
		final LazySeq<Integer> limited = shortSeq.limit(3);

		//then
		assertThat(limited).isEqualTo(of(1, 2, 3));
	}

	@Test
	public void shouldTrimInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> shortSeq = numbers(1);

		//when
		final LazySeq<Integer> limited = shortSeq.limit(3);

		//then
		assertThat(limited).isEqualTo(of(1, 2, 3));
	}

	@Test
	public void shouldNotEvaluateTailWhenTakingFirstFewElements() throws Exception {
		//given
		final LazySeq<Integer> infinite = of(1, supplierMock);

		//when
		infinite.take(10);

		//then
		verifyZeroInteractions(supplierMock);
	}

}

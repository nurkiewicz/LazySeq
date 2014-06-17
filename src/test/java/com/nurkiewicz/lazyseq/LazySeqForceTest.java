package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/13/13, 6:26 PM
 */
public class LazySeqForceTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Test
	public void shouldDoNothingWhenForcingEmpty() throws Exception {
		assertThat(empty().force()).isEqualTo(empty());
	}

	@Test
	public void shouldForceWholeFiniteSeq() throws Exception {
		//given
		final LazySeq<Double> nums = LazySeq.numbers(1.0, 0.5).take(5);
		assertThat(nums.toString()).isEqualTo("[1.0, ?]");

		//when
		nums.force();

		//then
		assertThat(nums.toString()).isEqualTo("[1.0, 1.5, 2.0, 2.5, 3.0]");
	}

	@Test
	public void shouldForceOnlyPartOfTheSeq() throws Exception {
		//given
		final LazySeq<Double> nums = LazySeq.numbers(1.0, 0.5);
		assertThat(nums.toString()).isEqualTo("[1.0, ?]");

		//when
		nums.take(5).force();

		//then
		assertThat(nums.toString()).isEqualTo("[1.0, 1.5, 2.0, 2.5, 3.0, ?]");
	}

	@Test
	public void shouldDoNothingWhenForcingTwice() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(1, supplierMock);
		given(supplierMock.get()).willReturn(of(1));

		//when
		lazy.force().force();

		//then
		verify(supplierMock).get();
	}

}

package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.Optional;
import java.util.function.BinaryOperator;

import static com.nurkiewicz.lazyseq.LazySeq.cons;
import static com.nurkiewicz.lazyseq.LazySeq.of;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 7:49 PM
 */
public class LazySeqReduceTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnIdentityWhenEmptySeqStartingFromIdentity() throws Exception {
		//given
		final LazySeq<Integer> empty = LazySeq.empty();

		//when
		final Integer result = empty.reduce(0, sum());

		//then
		assertThat(result).isEqualTo(0);
	}

	@Test
	public void shouldReturnSumOfNumbersInFixedSeqStartingFromIdentity() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 4, 7);

		//when
		final Integer result = fixed.reduce(0, sum());

		//then
		assertThat(result).isEqualTo(1 + 2 + 4 + 7);
	}

	@Test
	public void shouldReturnSumOfLazyButFiniteSeqStartingFromIdentity() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(1, () -> cons(2, () -> cons(4, () -> of(7))));

		//when
		final Integer result = lazy.reduce(0, sum());

		//then
		assertThat(result).isEqualTo(1 + 2 + 4 + 7);
	}

	@Test
	public void shouldReturnAbsentProductWhenEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> empty = LazySeq.empty();

		//when
		final Optional<Integer> absent = empty.reduce(product());

		//then
		assertThat(absent).isEqualTo(Optional.<Integer>empty());
	}

	@Test
	public void shouldReturnProductOfNumbersInFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 4, 7);

		//when
		final Optional<Integer> result = fixed.reduce(product());

		//then
		assertThat(result).isEqualTo(Optional.of(1 * 2 * 4 * 7));
	}

	@Test
	public void shouldReturnProductOfLazyButFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(1, () -> cons(2, () -> cons(4, () -> of(7))));

		//when
		final Optional<Integer> result = lazy.reduce(product());

		//then
		assertThat(result).isEqualTo(Optional.of(1 * 2 * 4 * 7));
	}

	private BinaryOperator<Integer> sum() {
		return (a, b) -> a + b;
	}

	private BinaryOperator<Integer> product() {
		return (a, b) -> a * b;
	}

	@Test
	public void shouldReturnZeroTotalSumOfStringLengthsInEmptySeq() throws Exception {
		//given
		final LazySeq<String> empty = LazySeq.empty();

		//when
		final int totalSum = empty.reduce(0, (acc, i) -> acc + i.length());

		//then
		assertThat(totalSum).isEqualTo(0);
	}

	@Test
	public void shouldReturnTotalSumOfStringLengthsInFixedSeq() throws Exception {
		//given
		final LazySeq<String> fixed = of("1", "22", "4444", "7777777");

		//when
		final int totalSum = fixed.reduce(0, (acc, i) -> acc + i.length());

		//then
		assertThat(totalSum).isEqualTo(1 + 2 + 4 + 7);
	}

	@Test
	public void shouldReturnTotalSumOfStringLengthsOfLazyButFiniteSeq() throws Exception {
		//given
		final LazySeq<String> lazy = cons("1", () -> cons("22", () -> cons("4444", () -> of("7777777"))));

		//when
		final int totalSum = lazy.reduce(0, (acc, i) -> acc + i.length());

		//then
		assertThat(totalSum).isEqualTo(1 + 2 + 4 + 7);
	}

}

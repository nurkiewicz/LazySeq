package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static com.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 10:07 AM
 */
public class LazySeqAllMatchTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<String>> supplierMock;

	@Test
	public void shouldReturnTrueWithAlwaysFalsePredicateButEmptySeq() throws Exception {
		assertThat(empty().allMatch(x -> false)).isTrue();
	}

	@Test
	public void shouldReturnTrueForSingleElementCollectionAndAlwaysTruePredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean all = single.allMatch(x -> true);

		//then
		assertThat(all).isTrue();
	}

	@Test
	public void shouldReturnTrueForSingleElementCollectionAndPassingPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean all = single.allMatch(x -> (x > 0));

		//then
		assertThat(all).isTrue();
	}

	@Test
	public void shouldReturnFalseForSingleElementSeqNotMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean all = single.allMatch(x -> x % 2 == 0);

		//then
		assertThat(all).isFalse();
	}

	@Test
	public void shouldReturnTrueForLongerFiniteSeqWithAllElementsMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = LazySeq.of(5, 10, 15);

		//when
		final boolean all = fixed.allMatch(x -> x % 5 == 0);

		//then
		assertThat(all).isTrue();
	}

	@Test
	public void shouldReturnFalseForLongerFiniteSeqWithOneElementNotMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = LazySeq.of(5, 10, 15);

		//when
		final boolean all = fixed.allMatch(x -> (x <= 10));

		//then
		assertThat(all).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenNoneOfTheElementsOfFiniteSeqMatchesPredicate() throws Exception {
		//given
		final LazySeq<String> fixed = LazySeq.of("a", "bc", "def");

		//when
		final boolean all = fixed.allMatch(String::isEmpty);

		//then
		assertThat(all).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenAllElementsOfLazyFiniteSeqMatchesPredicate() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3, () -> cons(2, () -> of(8)));

		//when
		final boolean all = lazy.allMatch(x -> (x > 0));

		//then
		assertThat(all).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenNoneOfTheElementsOfLayFiniteSeqMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3, () -> cons(-5, () -> of(9)));

		//when
		final boolean all = lazy.allMatch(x -> x % 2 == 0);

		//then
		assertThat(all).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenFirstElementOfInfiniteSeqNotMatchesPredicate() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();

		//when
		final boolean all = primes.allMatch(x -> x % 2 != 0);

		//then
		assertThat(all).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenSomeElementsInInfiniteSeqDoNotMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();

		//when
		final boolean all = primes.allMatch(x -> x < 1000);

		//then
		assertThat(all).isFalse();
	}

	@Test
	public void shouldNotEvaluateTailIfHeadNotMatchesPredicate() throws Exception {
		//given
		final LazySeq<String> lazy = cons("a", supplierMock);

		//when
		lazy.allMatch(String::isEmpty);

		//then
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldEvaluateTailOnlyOnceWhenHeadMatchesButSecondNotMatches() throws Exception {
		//given
		final LazySeq<String> lazy = cons("", supplierMock);
		given(supplierMock.get()).willReturn(of("b"));

		//when
		lazy.allMatch(String::isEmpty);

		//then
		verify(supplierMock).get();
	}

}

package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static com.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 10:18 AM
 */
public class LazySeqNoneMatchTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<String>> supplierMock;

	@Test
	public void shouldReturnTrueWithAlwaysTruePredicateButEmptySeq() throws Exception {
		assertThat(empty().noneMatch(x -> true)).isTrue();
	}

	@Test
	public void shouldReturnTrueForSingleElementCollectionAndAlwaysFalsePredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean none = single.noneMatch(x -> false);

		//then
		assertThat(none).isTrue();
	}

	@Test
	public void shouldReturnTrueForSingleElementCollectionAndFailingPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean none = single.noneMatch(x -> (x < 0));

		//then
		assertThat(none).isTrue();
	}

	@Test
	public void shouldReturnFalseForSingleElementSeqMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean none = single.noneMatch(x -> x % 2 != 0);

		//then
		assertThat(none).isFalse();
	}

	@Test
	public void shouldReturnTrueForLongerFiniteSeqWithNoElementsMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = LazySeq.of(5, 10, 15);

		//when
		final boolean none = fixed.noneMatch(x -> x % 5 != 0);

		//then
		assertThat(none).isTrue();
	}

	@Test
	public void shouldReturnFalseForLongerFiniteSeqWithOneElementMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = LazySeq.of(5, 10, 15);

		//when
		final boolean none = fixed.noneMatch(x -> (x > 10));

		//then
		assertThat(none).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenAllOfTheElementsOfFiniteSeqMatchesPredicate() throws Exception {
		//given
		final LazySeq<String> fixed = LazySeq.of("a", "bc", "def");

		//when
		final boolean none = fixed.noneMatch(s -> !s.isEmpty());

		//then
		assertThat(none).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenAllElementsOfLazyFiniteSeqFailPredicate() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3, () -> cons(2, () -> of(8)));

		//when
		final boolean none = lazy.noneMatch(x -> (x < 0));

		//then
		assertThat(none).isTrue();
	}

	@Test
	public void shouldReturnTrueWhenNoneOfTheElementsOfLayFiniteSeqMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3, () -> cons(-5, () -> of(9)));

		//when
		final boolean none = lazy.noneMatch(x -> x % 2 == 0);

		//then
		assertThat(none).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenFirstElementOfInfiniteSeqMatchesPredicate() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();

		//when
		final boolean none = primes.noneMatch(x -> x % 2 == 0);

		//then
		assertThat(none).isFalse();
	}

	@Test
	public void shouldReturnFalseWhenSomeElementsInInfiniteSeqMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();

		//when
		final boolean none = primes.noneMatch(x -> x > 1000);

		//then
		assertThat(none).isFalse();
	}

	@Test
	public void shouldNotEvaluateTailIfHeadMatchesPredicate() throws Exception {
		//given
		final LazySeq<String> lazy = cons("a", supplierMock);

		//when
		lazy.noneMatch(s -> !s.isEmpty());

		//then
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldEvaluateTailOnlyOnceWhenHeadMatchesButSecondNotMatches() throws Exception {
		//given
		final LazySeq<String> lazy = cons("a", supplierMock);
		given(supplierMock.get()).willReturn(of(""));

		//when
		lazy.noneMatch(String::isEmpty);

		//then
		verify(supplierMock).get();
	}

}

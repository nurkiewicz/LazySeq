package com.blogspot.nurkiewicz.lazyseq;

import com.blogspot.nurkiewicz.lazyseq.samples.Seqs;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 9:53 AM
 */
public class LazySeqAnyMatchTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<String>> supplierMock;

	@Test
	public void shouldReturnFalseWithAlwaysTruePredicateButEmptySeq() throws Exception {
		assertThat(empty().anyMatch(x -> true)).isFalse();
	}

	@Test
	public void shouldReturnTrueForSingleElementCollectionAndAlwaysTruePredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean any = single.anyMatch(x -> true);

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldReturnTrueForSingleElementCollectionAndPassingPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean any = single.anyMatch(x -> (x > 0));

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldReturnFalseForSingleElementSeqNotMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = LazySeq.of(1);

		//when
		final boolean any = single.anyMatch(x -> x % 2 == 0);

		//then
		assertThat(any).isFalse();
	}

	@Test
	public void shouldReturnTrueForLongerFiniteSeqWithAllElementsMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = LazySeq.of(5, 10, 15);

		//when
		final boolean any = fixed.anyMatch(x -> x % 5 == 0);

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldReturnTrueForLongerFiniteSeqWithOneElementMatchingPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = LazySeq.of(5, 10, 15);

		//when
		final boolean any = fixed.anyMatch(x -> (x > 10));

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenNoneOfTheElementsOfFiniteSeqMatchesPredicate() throws Exception {
		//given
		final LazySeq<String> fixed = LazySeq.of("a", "bc", "def");

		//when
		final boolean any = fixed.anyMatch(String::isEmpty);

		//then
		assertThat(any).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenFirstElementOfLazyFiniteSeqMatchesPredicate() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3, () -> cons(-2, () -> of(8)));

		//when
		final boolean any = lazy.anyMatch(x -> x % 3 == 0);

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldReturnTrueWhenOnlyLastElementOfLazyFiniteSeqMatchesPredicate() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3, () -> cons(-2, () -> of(8)));

		//when
		final boolean any = lazy.anyMatch(x -> (x > 5));

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldReturnFalseWhenNoneOfTheElementsOfLayFiniteSeqMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3, () -> cons(-5, () -> of(9)));

		//when
		final boolean any = lazy.anyMatch(x -> x % 2 == 0);

		//then
		assertThat(any).isFalse();
	}

	@Test
	public void shouldReturnTrueWhenFirstElementMatchesForInfiniteStream() throws Exception {
		//given
		final LazySeq<Integer> primes = Seqs.primes();

		//when
		final boolean any = primes.anyMatch(x -> x < 10);

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldReturnTrueWhenSomeElementMatchesForInfiniteStream() throws Exception {
		//given
		final LazySeq<Integer> primes = Seqs.primes();

		//when
		final boolean any = primes.anyMatch(x -> x > 1000);

		//then
		assertThat(any).isTrue();
	}

	@Test
	public void shouldNotEvaluateTailIfHeadMatchesPredicate() throws Exception {
		//given
		final LazySeq<String> lazy = cons("a", supplierMock);

		//when
		lazy.anyMatch(s -> !s.isEmpty());

		//then
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldEvaluateTailOnlyOnceWhenHeadDoesntMatchButSecondMatches() throws Exception {
		//given
		final LazySeq<String> lazy = cons("", supplierMock);
		given(supplierMock.get()).willReturn(of("b"));

		//when
		lazy.anyMatch(s -> !s.isEmpty());

		//then
		verify(supplierMock).get();
	}

}

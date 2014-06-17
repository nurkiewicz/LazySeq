package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static com.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 11:04 AM
 */
public class LazySeqTakeWhileTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<String>> supplierMock;

	@Test
	public void shouldReturnEmptySeqWhenTakingFromEmpty() throws Exception {
		final LazySeq<Object> filtered = empty().takeWhile(x -> true);

		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenSingleElementNotMatching() throws Exception {
		//given
		final LazySeq<Integer> seq = of(-3);

		//when
		final LazySeq<Integer> filtered = seq.takeWhile(x -> (x > 0));

		//then
		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldReturnSingleElementSeqWhenSingleSeqAndElementMatches() throws Exception {
		//given
		final LazySeq<Integer> seq = of(1);

		//when
		final LazySeq<Integer> filtered = seq.takeWhile(x -> (x > 0));

		//then
		assertThat(filtered).isEqualTo(of(1));
	}

	@Test
	public void shouldReturnEmptySeqWhenFirstElementOfSeqFailsPredicate() throws Exception {
		//given
		final LazySeq<Integer> seq = of(-1, 2, 3);

		//when
		final LazySeq<Integer> filtered = seq.takeWhile(x -> (x > 0));

		//then
		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldReturnSubSeqWhenFirstElementMatchesOnly() throws Exception {
		//given
		final LazySeq<Integer> seq = of(-1, 2, 3);

		//when
		final LazySeq<Integer> filtered = seq.takeWhile(x -> (x < 0));

		//then
		assertThat(filtered).isEqualTo(of(-1));
	}

	@Test
	public void shouldReturnSubSeqWithFewElementsAllMatchingPredicate() throws Exception {
		//given
		final LazySeq<String> seq = of("a", "b", "cd", "efg");

		//when
		final LazySeq<String> filtered = seq.takeWhile(s -> s.length() < 2);

		//then
		assertThat(filtered).isEqualTo(of("a", "b"));
	}

	@Test
	public void shouldReturnWholeSeqWhenAllMatching() throws Exception {
		//given
		final LazySeq<String> seq = of("a", "b", "cd", "efg");

		//when
		final LazySeq<String> filtered = seq.takeWhile(s -> !s.isEmpty());

		//then
		assertThat(filtered).isEqualTo(of("a", "b", "cd", "efg"));
	}

	@Test
	public void shouldTakeFirstFewElementsFromInifiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();

		//when
		final LazySeq<Integer> filtered = primes.takeWhile(p -> (p < 10));

		//then
		assertThat(filtered).isEqualTo(of(2, 3, 5, 7));
	}

	@Test
	public void shouldTakeAllElementsFromInfiniteSetWhenAllMatchPrefix() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();

		//when
		final LazySeq<Integer> filtered = primes.takeWhile(p -> (p > 1));

		//then
		assertThat(filtered.take(5)).isEqualTo(of(2, 3, 5, 7, 11));
	}

	@Test
	public void shouldNotEvaluateTailIfFirstElementFailsPredicate() throws Exception {
		//given
		final LazySeq<String> seq = cons("a", supplierMock);

		//when
		seq.takeWhile(String::isEmpty);

		//then
		verifyZeroInteractions(supplierMock);
	}

}

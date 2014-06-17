package com.nurkiewicz.lazyseq;

import com.nurkiewicz.lazyseq.samples.Seqs;
import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.empty;
import static com.nurkiewicz.lazyseq.LazySeq.of;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 11:19 AM
 */
public class LazySeqDropWhileTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptySeqWhenCalledOnEmptySeq() throws Exception {
		//given
		final LazySeq<Object> empty = empty();

		//when
		final LazySeq<Object> filtered = empty.dropWhile(x -> true);

		//then
		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenCalledOnSingleElementSeqWhenFirstElementPassesPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = of(1);

		//when
		final LazySeq<Integer> filtered = single.dropWhile(x -> x > 0);

		//then
		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenCalledOnSingleElementSeqWhenFirstElementFailsPredicate() throws Exception {
		//given
		final LazySeq<Integer> single = of(1);

		//when
		final LazySeq<Integer> filtered = single.dropWhile(x -> x < 0);

		//then
		assertThat(filtered).isEqualTo(of(1));
	}

	@Test
	public void shouldReturnEmptySeqWhenAllElementsMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(2, 4, 6, 8, 10);

		//when
		final LazySeq<Integer> filtered = fixed.dropWhile(x -> x > 0);

		//then
		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldReturnSubSeqWhenFirstFewElementsOfFixedSeqMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(2, 4, 6, 8, 10);

		//when
		final LazySeq<Integer> filtered = fixed.dropWhile(x -> x < 5);

		//then
		assertThat(filtered).isEqualTo(of(6, 8, 10));
	}

	@Test
	public void shouldReturnWholeSeqWhenFirstElementFailsPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(2, 4, 6, 8, 10);

		//when
		final LazySeq<Integer> filtered = fixed.dropWhile(x -> x < 0);

		//then
		assertThat(filtered).isEqualTo(of(2, 4, 6, 8, 10));
	}

	@Test
	public void shouldReturnWholeInfiniteSeqWhenFirstElementFailsPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = Seqs.primes();

		//when
		final LazySeq<Integer> filtered = fixed.dropWhile(x -> x < 0);

		//then
		assertThat(filtered.take(5)).isEqualTo(of(2, 3, 5, 7, 11));
	}

	@Test
	public void shouldReturnSubSeqOfInfiniteSeqWhenFewFirstElementsMatchPredicate() throws Exception {
		//given
		final LazySeq<Integer> fixed = Seqs.primes();

		//when
		final LazySeq<Integer> filtered = fixed.dropWhile(x -> x < 10);

		//then
		assertThat(filtered.take(5)).isEqualTo(of(11, 13, 17, 19, 23));
	}

}

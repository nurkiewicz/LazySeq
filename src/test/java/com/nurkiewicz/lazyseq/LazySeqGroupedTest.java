package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.List;

import static com.nurkiewicz.lazyseq.LazySeq.empty;
import static com.nurkiewicz.lazyseq.LazySeq.of;
import static com.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 12:01 PM
 */
public class LazySeqGroupedTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptySeqWhenRunOnEmpty() throws Exception {
		//given
		final LazySeq<Object> empty = empty();

		//when
		final LazySeq<List<Object>> grouped = empty.grouped(10);

		//then
		assertThat(grouped).isEmpty();
	}

	@Test
	public void shouldThrowWhenArgumentZeroOnEmptySeq() throws Exception {
		//when
		try {
			LazySeq.empty().grouped(0);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenArgumentZero() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 3);

		//when
		try {
			fixed.grouped(0);
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch (IllegalArgumentException e) {
			//then
		}
	}

	@Test
	public void shouldReturnOneElementForSeqShorterThanWindow() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9);

		//when
		final LazySeq<List<Integer>> grouped = fixed.grouped(4);

		//then
		assertThat(grouped.head()).containsExactly(5, 7, 9);
		assertThat(grouped).hasSize(1);
	}

	@Test
	public void shouldReturnOneElementForSeqEqualToWindowSize() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9, 11);

		//when
		final LazySeq<List<Integer>> grouped = fixed.grouped(4);

		//then
		assertThat(grouped.head()).containsExactly(5, 7, 9, 11);
		assertThat(grouped).hasSize(1);
	}

	@Test
	public void shouldReturnMultipleWindowsForFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9, 11);

		//when
		final LazySeq<List<Integer>> grouped = fixed.grouped(3);

		//then
		assertThat(grouped.get(0)).containsExactly(5, 7, 9);
		assertThat(grouped.get(1)).containsExactly(11);
		assertThat(grouped).hasSize(2);
	}

	@Test
	public void shouldReturnMultipleSmallWindowsForFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9, 11);

		//when
		final LazySeq<List<Integer>> grouped = fixed.grouped(2);

		//then
		assertThat(grouped.get(0)).containsExactly(5, 7);
		assertThat(grouped.get(1)).containsExactly(9, 11);
		assertThat(grouped).hasSize(2);
	}

	@Test
	public void shouldReturnWindowOfSizeOne() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9);

		//when
		final LazySeq<List<Integer>> grouped = fixed.grouped(1);

		//then
		assertThat(grouped.get(0)).containsExactly(5);
		assertThat(grouped.get(1)).containsExactly(7);
		assertThat(grouped.get(2)).containsExactly(9);
		assertThat(grouped).hasSize(3);
	}

	@Test
	public void shouldReturnWindowOfSizeOneForSingleElementSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(3);

		//when
		final LazySeq<List<Integer>> grouped = fixed.grouped(1);

		//then
		assertThat(grouped.get(0)).containsExactly(3);
		assertThat(grouped).hasSize(1);
	}

	@Test
	public void shouldReturnWindowOfSizeOneForEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = empty();

		//when
		final LazySeq<List<Integer>> grouped = fixed.grouped(1);

		//then
		assertThat(grouped).isEmpty();
	}

	@Test
	public void shouldCreateInfiniteSeqOfWindowsOverInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();

		//when
		final LazySeq<List<Integer>> grouped = primes.grouped(3);

		//then
		assertThat(grouped.get(0)).containsExactly(2, 3, 5);
		assertThat(grouped.get(1)).containsExactly(7, 11, 13);
		assertThat(grouped.get(2)).containsExactly(17, 19, 23);
	}

}

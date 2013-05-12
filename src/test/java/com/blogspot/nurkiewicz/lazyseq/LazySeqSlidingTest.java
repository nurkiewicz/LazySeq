package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.List;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.empty;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 11:32 AM
 */
public class LazySeqSlidingTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptySeqWhenRunOnEmpty() throws Exception {
		//given
		final LazySeq<Object> empty = empty();

		//when
		final LazySeq<List<Object>> sliding = empty.sliding(10);

		//then
		assertThat(sliding).isEmpty();
	}

	@Test
	public void shouldThrowWhenArgumentZeroOnEmptySeq() throws Exception {
		//when
		try {
			LazySeq.empty().sliding(0);
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
			fixed.sliding(0);
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
		final LazySeq<List<Integer>> sliding = fixed.sliding(4);

		//then
		assertThat(sliding.head()).isEqualTo(asList(5, 7, 9));
		assertThat(sliding).hasSize(1);
	}

	@Test
	public void shouldReturnOneElementForSeqEqualToWindowSize() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9, 11);

		//when
		final LazySeq<List<Integer>> sliding = fixed.sliding(4);

		//then
		assertThat(sliding.head()).isEqualTo(asList(5, 7, 9, 11));
		assertThat(sliding).hasSize(1);
	}

	@Test
	public void shouldReturnMultipleWindowsForFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9, 11);

		//when
		final LazySeq<List<Integer>> sliding = fixed.sliding(3);

		//then
		assertThat(sliding.get(0)).isEqualTo(asList(5, 7, 9));
		assertThat(sliding.get(1)).isEqualTo(asList(7, 9, 11));
		assertThat(sliding).hasSize(2);
	}

	@Test
	public void shouldReturnMultipleSmallWindowsForFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9, 11);

		//when
		final LazySeq<List<Integer>> sliding = fixed.sliding(2);

		//then
		assertThat(sliding.get(0)).isEqualTo(asList(5, 7));
		assertThat(sliding.get(1)).isEqualTo(asList(7, 9));
		assertThat(sliding.get(2)).isEqualTo(asList(9, 11));
		assertThat(sliding).hasSize(3);
	}

	@Test
	public void shouldReturnWindowOfSizeOne() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(5, 7, 9);

		//when
		final LazySeq<List<Integer>> sliding = fixed.sliding(1);

		//then
		assertThat(sliding.get(0)).isEqualTo(asList(5));
		assertThat(sliding.get(1)).isEqualTo(asList(7));
		assertThat(sliding.get(2)).isEqualTo(asList(9));
		assertThat(sliding).hasSize(3);
	}

	@Test
	public void shouldReturnWindowOfSizeOneForSingleElementSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(3);

		//when
		final LazySeq<List<Integer>> sliding = fixed.sliding(1);

		//then
		assertThat(sliding.get(0)).isEqualTo(asList(3));
		assertThat(sliding).hasSize(1);
	}

	@Test
	public void shouldReturnWindowOfSizeOneForEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = empty();

		//when
		final LazySeq<List<Integer>> sliding = fixed.sliding(1);

		//then
		assertThat(sliding).isEmpty();
	}

}

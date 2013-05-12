package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static com.blogspot.nurkiewicz.lazyseq.SampleStreams.primes;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 12:54 PM
 */
public class LazySeqDistinctTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptySeqWhenCalledOnEmptySeq() throws Exception {
		assertThat(empty().distinct()).isEmpty();
	}

	@Test
	public void shouldReturnSeqWithOneElementWhenOnSeqWithOneElement() throws Exception {
		//given
		final LazySeq<Integer> single = of(9);

		//when
		final LazySeq<Integer> dist = single.distinct();

		//then
		assertThat(dist).isEqualTo(of(9));
	}

	@Test
	public void shouldReturnTwoElementsWhenSeqWithTwoDifferentElements() throws Exception {
		//given
		final LazySeq<Integer> twoDistinct = of(9, 7);

		//when
		final LazySeq<Integer> dist = twoDistinct.distinct();

		//then
		assertThat(dist.toList()).containsOnly(7, 9);
	}

	@Test
	public void shouldReturnOneElementWhenSeqWithTwoSameElements() throws Exception {
		//given
		final LazySeq<Integer> twoSame = of(9, 9);

		//when
		final LazySeq<Integer> dist = twoSame.distinct();

		//then
		assertThat(dist).isEqualTo(of(9));
	}

	@Test
	public void shouldReturnFirstDistinctElementFoundOnFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> twoDistinct = of(9, 7);

		//when
		final LazySeq<Integer> dist = twoDistinct.distinct();

		//then
		assertThat(dist.head()).isEqualTo(9);
	}

	@Test
	public void shouldReturnAllElementsOnFixedAlreadyDistinctSeq() throws Exception {
		//given
		final LazySeq<Integer> oneToFive = SampleStreams.naturals(1).take(5);

		//when
		final LazySeq<Integer> dist = oneToFive.distinct();

		//then
		assertThat(dist.toList()).containsOnly(1, 2, 3, 4, 5);
	}

	@Test
	public void shouldReturnAllDistinctElements() throws Exception {
		//given
		final LazySeq<Integer> oneToFiveWithDups = of(1, 4, 2, 5, 3, 4, 3, 3, 2, 1, 5);

		//when
		final LazySeq<Integer> dist = oneToFiveWithDups.distinct();

		//then
		assertThat(dist.toList()).containsOnly(1, 2, 3, 4, 5);
	}

	@Test
	public void shouldReturnFirstFewDistinctElementsFromInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> primesPlus = concat(asList(2, 3, 7), primes());

		//when
		final LazySeq<Integer> dist = primesPlus.distinct();

		//then
		assertThat(dist.take(5)).isEqualTo(of(2, 3, 7, 5, 11));
	}

}

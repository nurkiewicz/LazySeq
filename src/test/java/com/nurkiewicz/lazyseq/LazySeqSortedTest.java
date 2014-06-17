package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 1:17 PM
 */
public class LazySeqSortedTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptySeqWhenTryingToSortEmptySeq() throws Exception {
		assertThat(empty().sorted()).isEmpty();
	}

	@Test
	public void shouldSortFixedSeqWithOneElement() throws Exception {
		//given
		final LazySeq<Integer> single = of(17);

		//when
		final LazySeq<Integer> sorted = single.sorted();

		//when
		assertThat(sorted).isEqualTo(of(17));
	}

	@Test
	public void shouldSortFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(17, 3, 15, 9, 4);

		//when
		final LazySeq<Integer> sorted = fixed.sorted();

		//when
		assertThat(sorted).isEqualTo(of(3, 4, 9, 15, 17));
	}

	@Test
	public void shouldSortLazyButFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3,
				() -> cons(-2,
						() -> cons(8,
								() -> cons(5,
										() -> cons(-4,
												() -> cons(11,
														() -> cons(-2,
																() -> of(1))))))));

		//when
		final LazySeq<Integer> sorted = lazy.sorted();

		//then
		assertThat(sorted).isEqualTo(of(-4, -2, -2, 1, 3, 5, 8, 11));
	}

	@Test
	public void shouldSortStringsByCustomComparator() throws Exception {
		final LazySeq<String> fixed = of("ab", "c", "", "ghjkl", "def");

		//when
		final LazySeq<String> sorted = fixed.sorted((s1, s2) -> s1.length() - s2.length());

		//when
		assertThat(sorted).isEqualTo(of("", "c", "ab", "def", "ghjkl"));
	}

}

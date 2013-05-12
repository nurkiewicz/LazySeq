package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.cons;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 9:24 AM
 */
public class LazySeqSizeTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnZeroForEmptySeq() throws Exception {
		assertThat(LazySeq.empty()).hasSize(0);
		assertThat(LazySeq.empty().size()).isZero();
	}

	@Test
	public void shouldReturnSizeOfFixedLengthCollection() throws Exception {
		assertThat(of(1)).hasSize(1);
		assertThat(of(2, 3)).hasSize(2);
		assertThat(of('a', 'b', 'c')).hasSize(3);
		assertThat(of(3, -2, 8, 5, -4, 11, 2, 1)).hasSize(8);
	}

	@Test
	public void shouldReturnSizeOfLazyButFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(3,
				() -> cons(-2,
						() -> cons(8,
								() -> cons(5,
										() -> cons(-4,
												() -> cons(11,
														() -> cons(2,
																() -> of(1))))))));


		//when
		final int size = lazy.size();

		//then
		assertThat(size).isEqualTo(8);
	}

}

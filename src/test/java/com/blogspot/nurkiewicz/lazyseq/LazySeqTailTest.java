package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.NoSuchElementException;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.empty;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 9:35 AM
 */
public class LazySeqTailTest extends AbstractBaseTestCase {

	@Test
	public void shouldThrowWhenAskingForTailOfEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		try {
			//when
			empty.tail();
		} catch (NoSuchElementException e) {
			//then
		}
	}

	@Test
	public void shouldReturnEmptySeqWhenAskingForTailOfSingleElementSeq() throws Exception {
		assertThat(of(1).tail()).isEmpty();
	}

	@Test
	public void shouldReturnTailOfFixesLengthSeq() throws Exception {
		assertThat(of(1).tail()).isEqualTo(empty());
		assertThat(of(2, 3).tail()).isEqualTo(of(3));
		assertThat(of(4, 5, 6).tail()).isEqualTo(of(5, 6));
		assertThat(of(7, 8, 9, 10).tail()).isEqualTo(of(8, 9, 10));
	}

	@Test
	public void shouldReturnTailOfInfiniteLazySeq() throws Exception {
		assertThat(LazySeq.numbers(1).tail().limit(4)).isEqualTo(of(2, 3, 4, 5));
	}

}

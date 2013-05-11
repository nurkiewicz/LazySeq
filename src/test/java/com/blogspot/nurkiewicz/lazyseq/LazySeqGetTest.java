package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 2:39 PM
 */
public class LazySeqGetTest extends AbstractBaseTestCase {

	@Test
	public void shouldThrowWhenNegativeGet() throws Exception {
		//given
		final LazySeq<Integer> naturals = naturals(1);

		try {
			//when
			naturals.get(-1);
			failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
		} catch (IndexOutOfBoundsException e) {
			//then
		}
	}

	private LazySeq<Integer> naturals(int from) {
		return iterate(from, x -> x + 1);
	}

	@Test
	public void shouldThrowWhenTryingToAccessFirstOfEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		try {
			//when
			empty.get(0);
			failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
		} catch (IndexOutOfBoundsException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenTryingToAccessPastEnd() throws Exception {
		//given
		final LazySeq<Integer> single = of(1);

		try {
			//when
			single.get(1);
			failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
		} catch (IndexOutOfBoundsException e) {
			//then
		}
	}

	@Test
	public void shouldThrowWhenTryingToAccessPastEndOfLongerSeq() throws Exception {
		//given
		final LazySeq<Integer> seq = of(1, 2, 3);

		try {
			//when
			seq.get(3);
			failBecauseExceptionWasNotThrown(IndexOutOfBoundsException.class);
		} catch (IndexOutOfBoundsException e) {
			//then
		}
	}

	@Test
	public void shouldReturnHeadWhenGettingFirstElement() throws Exception {
		assertThat(of("a").get(0)).isEqualTo("a");
		assertThat(of("b", "c", "d").get(0)).isEqualTo("b");
		assertThat(LazySeq.cons("w", LazySeq::<String>empty).head()).isEqualTo("w");
	}

	@Test
	public void shouldReturnLastElementOfFixedSeq() throws Exception {
		assertThat(of('a').get(0)).isEqualTo('a');
		assertThat(of('a', 'b').get(1)).isEqualTo('b');
		assertThat(of('a', 'b', 'c').get(2)).isEqualTo('c');
	}

	@Test
	public void shouldGetElementFarInTheSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = naturals(0);

		//when
		final Integer natural = naturals.get(1_000_000);

		//then
		assertThat(natural).isEqualTo(1_000_000);
	}

}

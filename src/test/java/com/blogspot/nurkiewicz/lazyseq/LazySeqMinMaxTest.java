package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.Optional;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 8:37 AM
 */
public class LazySeqMinMaxTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptyOptionalOnEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		//when
		final Optional<Integer> min = empty.min((a, b) -> a - b);
		final Optional<Integer> max = empty.max((a, b) -> a - b);

		//then
		assertThat(min).isEqualTo(Optional.<Integer>empty());
		assertThat(max).isEqualTo(Optional.<Integer>empty());
	}

	@Test
	public void shouldReturnThisElementWhenSingleElementSeq() throws Exception {
		//given
		final LazySeq<Integer> single = of(1);

		//when
		final Optional<Integer> min = single.min((a, b) -> a - b);
		final Optional<Integer> max = single.max((a, b) -> a - b);

		//then
		assertThat(min).isEqualTo(Optional.of(1));
		assertThat(max).isEqualTo(Optional.of(1));
	}

	@Test
	public void shouldFindBiggestAndSmallestValueInSeqOfIntegers() throws Exception {
		//given
		final LazySeq<Integer> single = of(3, -2, 8, 5, -4, 11, 2);

		//when
		final Optional<Integer> min = single.min((a, b) -> a - b);
		final Optional<Integer> max = single.max((a, b) -> a - b);

		//then
		assertThat(min).isEqualTo(Optional.of(-4));
		assertThat(max).isEqualTo(Optional.of(11));
	}

	@Test
	public void shouldFindBiggestAndSmallestValueInLazyButFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> single = cons(3,
				() -> cons(-2,
						() -> cons(8,
								() -> cons(5,
										() -> cons(-4,
												() -> cons(11, of(2)))))));

		//when
		final Optional<Integer> min = single.min((a, b) -> a - b);
		final Optional<Integer> max = single.max((a, b) -> a - b);

		//then
		assertThat(min).isEqualTo(Optional.of(-4));
		assertThat(max).isEqualTo(Optional.of(11));
	}

	@Test
	public void shouldFindShortestAndLongestStrings() throws Exception {
		//given
		final LazySeq<String> single = of(loremIpsum());

		//when
		final Optional<String> min = single.min((a, b) -> a.length() - b.length());
		final Optional<String> max = single.max((a, b) -> a.length() - b.length());

		//then
		assertThat(min).isEqualTo(Optional.of("id"));
		assertThat(max).isEqualTo(Optional.of("consectetur"));
	}

	@Test
	public void shouldFindFirstAndLastStringAlphabetically() throws Exception {
		//given
		final LazySeq<String> single = of(loremIpsum());

		//when
		final Optional<String> min = single.min(String::compareTo);
		final Optional<String> max = single.max(String::compareTo);

		//then
		assertThat(min).isEqualTo(Optional.of("adipiscing"));
		assertThat(max).isEqualTo(Optional.of("sit"));
	}

	private String[] loremIpsum() {
		return "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi id metus at ligula convallis imperdiet. ".toLowerCase().split("[ \\.,]+");
	}

}

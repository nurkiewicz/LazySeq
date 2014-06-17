package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.Arrays;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stefan Endrullis
 * @since 4/25/14, 1:15 PM
 */
public class LazySeqMkStringTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptyBracketsForEmptySeq() throws Exception {
		assertThat(empty().mkString("[", ", ", "]")).isEqualTo("[]");
	}

	@Test
	public void shouldDisplayWholeSeqWhenNotLazy() throws Exception {
		assertThat(of('x').mkString(", ")).isEqualToIgnoringCase("x");
		assertThat(of('x', 'y').mkString(", ")).isEqualToIgnoringCase("x, y");
		assertThat(of('x', 'y', 'z').mkString(", ")).isEqualToIgnoringCase("x, y, z");
		assertThat(of(Arrays.asList('x', 'y', 'z')).mkString(", ")).isEqualToIgnoringCase("x, y, z");
		assertThat(of(numbers(1).take(4)).mkString(", ")).isEqualToIgnoringCase("1, 2, 3, 4");
	}

	@Test
	public void shouldWorkWithArbitraryStartSepAndEndString() throws Exception {
		assertThat(of('x', 'y').mkString("-")).isEqualToIgnoringCase("x-y");
		assertThat(of('x', 'y').mkString("{", "; ", "}")).isEqualToIgnoringCase("{x; y}");
		assertThat(of('x', 'y').mkString("foo", "", "bar")).isEqualToIgnoringCase("fooxybar");
	}

	@Test
	public void shouldOnlyIncludeEvaluatedElementsIfLazy() throws Exception {
		final LazySeq<Integer> notFull = LazySeq.of(numbers(1));

		// evaluate 3 elements
		notFull.take(3).forEach(x -> {});

		// then
		assertThat(notFull.mkString("[", ", ", "]", true)).isEqualTo("[1, 2, 3, ?]");
	}

}

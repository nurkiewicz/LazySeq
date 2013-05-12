package com.blogspot.nurkiewicz.lazyseq.samples;

import com.blogspot.nurkiewicz.lazyseq.AbstractBaseTestCase;
import com.blogspot.nurkiewicz.lazyseq.LazySeq;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 3:30 PM
 */
public class FibonacciTest extends AbstractBaseTestCase {

	@Test
	public void shouldGenerateFibonacciSequence() throws Exception {
		final LazySeq<Integer> fib = lastTwoFib(0, 1);

		assertThat(fib.startsWith(asList(0, 1, 1, 2, 3, 5, 8, 13, 21)));
	}

	private static LazySeq<Integer> lastTwoFib(int first, int second) {
		return 	LazySeq.<Integer>of(
				first,
				() -> lastTwoFib(second, first + second)
		);

	}

	@Test
	public void shouldReturnGreaterFibonacciNumber() throws Exception {
		final LazySeq<Integer> fib = lastTwoFib(0, 1);
		assertThat(fib.get(5)).isEqualTo(5);
		assertThat(fib.get(45)).isEqualTo(1_134_903_170);
	}

}

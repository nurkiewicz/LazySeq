package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static com.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 11:27 AM
 */
public class LazySeqContainsTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnInfiniteSequenceOfNaturalNumbers() throws Exception {
		final LazySeq<Integer> naturals = numbers(1);
		assertThat(naturals).contains(17);
		assertThat(naturals.take(1000)).doesNotContain(-1, 0);
	}

	@Test
	public void shouldReturnInfiniteSequenceOfPowersOfTwo() throws Exception {
		final LazySeq<Integer> powersOfTwo = iterate(1, x -> x * 2);
		assertThat(powersOfTwo).contains(16, 1024, 65536);
	}

	@Test
	public void shouldReturnInfiniteSequenceOfPowersOfGrowingStrings() throws Exception {
		final LazySeq<String> strings = iterate("", s -> s + s.length());
		assertThat(strings).contains("0123456789101214");
	}

	@Test
	public void shouldReturnStreamOfPrimes() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();
		assertThat(primes.take(10)).isEqualTo(of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29));
		assertThat(primes).contains(997);
		assertThat(primes.take(1000)).doesNotContain(4, 6, 8, 9, 10, 12, 14, 15, 16);

	}

}

package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.function.Function;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.iterate;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static com.blogspot.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 9:49 AM
 */
public class LazySeqIterateTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnInfiniteSequenceOfNaturalNumbers() throws Exception {
		final LazySeq<Integer> naturals = LazySeq.numbers(1);
		assertThat(naturals.head()).isEqualTo(1);
		assertThat(naturals.tail().head()).isEqualTo(2);
		assertThat(naturals.drop(1000).head()).isEqualTo(1001);
	}

	@Test
	public void shouldReturnInfiniteSequenceOfPowersOfTwo() throws Exception {
		final LazySeq<Integer> powersOfTwo = iterate(1, x -> x * 2);
		assertThat(powersOfTwo.get(0)).isEqualTo(1);
		assertThat(powersOfTwo.get(1)).isEqualTo(2);
		assertThat(powersOfTwo.get(2)).isEqualTo(4);
		assertThat(powersOfTwo.drop(10).head()).isEqualTo(1024);
	}

	@Test
	public void shouldReturnInfiniteSequenceOfPowersOfGrowingStrings() throws Exception {
		final LazySeq<String> strings = iterate("", s -> s + s.length());
		assertThat(strings.head()).isEqualTo("");
		assertThat(strings.get(1)).isEqualTo("0");
		assertThat(strings.get(2)).isEqualTo("01");
		assertThat(strings.get(3)).isEqualTo("012");
	}

	@Test
	public void shouldReturnConstantSeqWithIdentity() throws Exception {
		//given
		final LazySeq<Integer> constant = iterate(1, Function.<Integer>identity());
		assertThat(constant.head()).isEqualTo(1);
		assertThat(constant.get(0)).isEqualTo(1);
		assertThat(constant.get(1)).isEqualTo(1);
		assertThat(constant.get(2)).isEqualTo(1);
		assertThat(constant.get(3)).isEqualTo(1);
		//...
	}

	@Test
	public void shouldReturnStreamOfPrimes() throws Exception {
		//given
		final LazySeq<Integer> primes = primes();
		assertThat(primes.take(10)).isEqualTo(of(2, 3, 5, 7, 11, 13, 17, 19, 23, 29));
	}

}

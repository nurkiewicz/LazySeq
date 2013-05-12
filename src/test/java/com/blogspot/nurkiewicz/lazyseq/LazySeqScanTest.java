package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static com.blogspot.nurkiewicz.lazyseq.SampleStreams.primes;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 12:19 PM
 */
public class LazySeqScanTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnInitialElementForEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		//when
		final LazySeq<Integer> scanned = empty.scan(0, (a, c) -> a + c);

		//then
		assertThat(scanned).isEqualTo(of(0));
	}

	@Test
	public void shouldReturnScannedFixedSeqOfIntegers() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 3, 4);

		//when
		final LazySeq<Integer> scanned = fixed.scan(0, (a, c) -> a + c);

		//then
		assertThat(scanned).isEqualTo(of(0, 1, 3, 6, 10));
	}

	@Test
	public void shouldReturnScannedFixedSeqOfStrings() throws Exception {
		//given
		final LazySeq<String> constant = constant("*").take(5);

		//when
		final LazySeq<String> scanned = constant.scan("", (a, c) -> a + c);

		//then
		assertThat(scanned).isEqualTo(of("", "*", "**", "***", "****", "*****"));
	}

	@Test
	public void shouldScanInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> infinite = primes();

		//when
		final LazySeq<Integer> scanned = infinite.scan(1, (a, c) -> a * c);

		//then
		assertThat(scanned.take(4)).isEqualTo(of(1, 2, 1 * 2 * 3, 1 * 2 * 3 * 5));
	}

}

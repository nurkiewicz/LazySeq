package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.empty;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static com.blogspot.nurkiewicz.lazyseq.samples.Seqs.primes;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 3:35 PM
 */
public class LazySeqStartsWithTest extends AbstractBaseTestCase {

	@Test
	public void emptySeqStartsWithEmptySeq() throws Exception {
		assertThat(empty().startsWith(empty())).isTrue();
	}

	@Test
	public void emptySeqDoesNotStartWithNonEmptySeq() throws Exception {
		final LazySeq<Integer> empty = empty();

		assertThat(empty.startsWith(of(1))).isFalse();
		assertThat(empty.startsWith(of(1, 2))).isFalse();
	}

	@Test
	public void everySeqStartsWithEmptySeq() throws Exception {
		assertThat(of(1).startsWith(empty())).isTrue();
		assertThat(of(1, 2).startsWith(empty())).isTrue();
	}

	@Test
	public void seqStartsWithSelf() throws Exception {
		final LazySeq<Character> seq = of('a', 'b', 'c');

		assertThat(seq.startsWith(seq)).isTrue();
	}

	@Test
	public void seqStartsWithPrefix() throws Exception {
		final LazySeq<Character> seq = of('a', 'b', 'c', 'd');

		assertThat(seq.startsWith(asList('a'))).isTrue();
		assertThat(seq.startsWith(asList('a', 'b', 'c'))).isTrue();
	}

	@Test
	public void seqNotStartsWithLongerPrefix() throws Exception {
		final LazySeq<Character> seq = of('a', 'b', 'c');

		assertThat(seq.startsWith(asList('a', 'b', 'c', 'd'))).isFalse();
	}

	@Test
	public void infiniteSeqStartsWithEmpty() throws Exception {
		final LazySeq<Integer> primes = primes();

		assertThat(primes.startsWith(empty())).isTrue();
	}

	@Test
	public void infiniteSeqStartsWithFixedSeq() throws Exception {
		final LazySeq<Integer> primes = primes();

		assertThat(primes.startsWith(asList(2, 3, 5, 7))).isTrue();
	}

	@Test
	public void infiniteSeqNotStartsWithWrongPrefix() throws Exception {
		final LazySeq<Integer> primes = primes();

		assertThat(primes.startsWith(asList(2, 3, 4))).isFalse();
	}

}

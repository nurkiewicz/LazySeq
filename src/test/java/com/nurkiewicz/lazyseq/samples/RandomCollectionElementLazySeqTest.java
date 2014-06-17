package com.nurkiewicz.lazyseq.samples;

import com.nurkiewicz.lazyseq.LazySeq;
import org.testng.annotations.Test;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 4:24 PM
 */
public class RandomCollectionElementLazySeqTest {

	@Test
	public void should() throws Exception {
		//given
		final LazySeq<Character> charStream = LazySeq.<Character>continually(this::randomChar);

		//when
		final LazySeq<Character> uniqueCharStream = charStream.distinct();

		//then
		uniqueCharStream.take(25).forEach(System.out::print);
	}

	private char randomChar() {
		return (char) ('A' + (int) (Math.random() * ('Z' - 'A' + 1)));
	}

}

package com.blogspot.nurkiewicz.lazyseq;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.iterate;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 11:27 AM
 */
public class SampleStreams {

	public static LazySeq<Integer> primes() {
		return iterate(2, SampleStreams::nextPrimeAfter);
	}

	private static int nextPrimeAfter(final int after) {
		int candidate = after + 1;
		while (!isPrime(candidate)) {
			++candidate;
		}
		return candidate;
	}

	private static boolean isPrime(int candidate) {
		final int max = (int) Math.sqrt(candidate);
		for (int div = 2; div <= max; ++div) {
			if (candidate % div == 0) {
				return false;
			}
		}
		return true;
	}


}

package com.blogspot.nurkiewicz.lazyseq;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.iterate;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.tabulate;

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

	/**
	 * @return 1 - 1/3 + 1/5 - 1/7 + 1/9 - ...
	 */
	public static LazySeq<Double> piSeriesEstimation() {
		return tabulate(0, n -> (1 - (n % 2) * 2) / (2 * n + 1.0));
	}

}

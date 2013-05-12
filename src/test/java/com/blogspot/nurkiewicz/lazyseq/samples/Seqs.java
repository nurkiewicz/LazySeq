package com.blogspot.nurkiewicz.lazyseq.samples;

import com.blogspot.nurkiewicz.lazyseq.LazySeq;
import org.quartz.CronExpression;

import java.util.Date;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 11:27 AM
 */
public class Seqs {

	public static LazySeq<Integer> primes() {
		return iterate(2, Seqs::nextPrimeAfter);
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

	public static LazySeq<Date> cronFireTimes(CronExpression expr, Date after) {
		final Date nextFireTime = expr.getNextValidTimeAfter(after);
		if (nextFireTime == null) {
			return empty();
		} else {
			return cons(nextFireTime, cronFireTimes(expr, nextFireTime));
		}
	}


}

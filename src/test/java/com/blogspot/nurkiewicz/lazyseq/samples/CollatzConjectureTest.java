package com.blogspot.nurkiewicz.lazyseq.samples;

import com.blogspot.nurkiewicz.lazyseq.AbstractBaseTestCase;
import com.blogspot.nurkiewicz.lazyseq.LazySeq;
import com.google.common.collect.ImmutableMap;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.cons;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * See: http://en.wikipedia.org/wiki/Collatz_conjecture
 *
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 4:01 PM
 */
public class CollatzConjectureTest extends AbstractBaseTestCase {

	@Test
	public void shouldProduceWholeCollatzConjecture() throws Exception {
		final LazySeq<Long> collatz = collatz(10);
		assertThat(collatz.startsWith(asList(10L, 5L, 16L))).isTrue();
		assertThat(collatz).isEqualTo(of(10L, 5L, 16L, 8L, 4L, 2L, 1L));
	}

	@Test
	public void veryLongProgression() throws Exception {
		final LazySeq<Long> collatz = collatz(63_728_127);
		assertThat(collatz).hasSize(950);
	}

	@Test
	public void veryLongProgressionCached() throws Exception {
		//given
		final LazySeq<Long> collatz = cachedCollatz(63_728_127);
		//when
		assertThat(collatz).hasSize(950);

		//then
		assertThat(collatzCache).
				containsKey(63_728_127L).
				containsKey(16L).
				containsKey(8L);
	}

	private LazySeq<Long> collatz(long from) {
		if (from == 1) {
			return of(1L);
		}
		final long next = from % 2 == 0 ? from / 2 : from * 3 + 1;
		return cons(from, () -> collatz(next));
	}

	private final Map<Long, LazySeq<Long>> collatzCache= new ConcurrentHashMap<>(ImmutableMap.of(1L, of(1L)));

	private LazySeq<Long> cachedCollatz(long from) {
		if (collatzCache.containsKey(from)) {
			return collatzCache.get(from);
		}
		final long next = from % 2 == 0 ? from / 2 : from * 3 + 1;
		final LazySeq<Long> coll = cons(from, () -> cachedCollatz(next));
		collatzCache.put(from, coll);
		return coll;
	}

}

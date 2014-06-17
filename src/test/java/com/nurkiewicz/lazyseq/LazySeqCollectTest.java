package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.List;

import static com.nurkiewicz.lazyseq.LazySeq.empty;
import static com.nurkiewicz.lazyseq.LazySeq.of;
import static java.util.stream.Collectors.toList;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 12:19 PM
 */
public class LazySeqCollectTest extends AbstractBaseTestCase {

	@Test
	public void shouldCollectEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		//when
		final LazySeq<Integer> collected = empty.
				stream().
				collect(LazySeq.<Integer>toLazySeq());

		//then
		assertThat(collected).isEmpty();
	}

	@Test
	public void shouldCollectFixedLengthSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 3);

		//when
		final LazySeq<Integer> collected = fixed.
				stream().
				collect(LazySeq.<Integer>toLazySeq());

		//then
		assertThat(collected).isEqualTo(of(1, 2, 3));
	}

	@Test
	public void shouldCollectInfiniteStream() throws Exception {
		//given
		final LazySeq<Integer> infinite = LazySeq.iterate(1, x -> x * 2);

		//when
		final LazySeq<Integer> collected = infinite.
				stream().
				collect(LazySeq.<Integer>toLazySeq());

		//then
		assertThat(collected.take(5)).isEqualTo(of(1, 2, 4, 8, 16));
	}

	@Test
	public void shouldCollectEmptySeqToList() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		//when
		final List<Integer> collected = empty.
				stream().
				collect(toList());

		//then
		assertThat(collected).isEmpty();
	}

	@Test
	public void shouldCollectFixedLengthSeqToList() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 3);

		//when
		final List<Integer> collected = fixed.
				stream().
				collect(toList());

		//then
		assertThat(collected).containsExactly(1, 2, 3);
	}

	@Test
	public void shouldCollectInfiniteStreamToList() throws Exception {
		//given
		final LazySeq<Integer> infinite = LazySeq.iterate(1, x -> x * 2);

		//when
		final List<Integer> collected = infinite.
				stream().
				limit(5).
				collect(toList());

		//then
		assertThat(collected).containsExactly(1, 2, 4, 8, 16);
	}

	@Test
	public void shouldCollectEmptySeqAfterFewTransformations() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		//when
		final LazySeq<Integer> collected = empty.
				stream().
				filter(x -> x > 1).
				map(x -> x + 10).
				collect(LazySeq.<Integer>toLazySeq());

		//then
		assertThat(collected).isEmpty();
	}

	@Test
	public void shouldCollectFixedLengthSeqAfterFewTransformations() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 3);

		//when
		final LazySeq<Integer> collected = fixed.
				stream().
				filter(x -> x > 1).
				map(x -> x + 10).
				collect(LazySeq.<Integer>toLazySeq());

		//then
		assertThat(collected).isEqualTo(of(12, 13));
	}

	@Test
	public void shouldCollectInfiniteStreamAfterFewTransformations() throws Exception {
		//given
		final LazySeq<Integer> infinite = LazySeq.iterate(1, x -> x * 2);

		//when
		final LazySeq<Integer> collected = infinite.
				stream().
				filter(x -> x > 1).
				map(x -> x + 10).
				collect(LazySeq.<Integer>toLazySeq());

		//then
		assertThat(collected.take(5)).isEqualTo(of(12, 14, 18, 26, 42));
	}

	@Test
	public void shouldCollectEmptySeqAfterFewTransformationsToList() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		//when
		final List<Integer> collected = empty.
				stream().
				filter(x -> x > 1).
				map(x -> x + 10).
				collect(toList());

		//then
		assertThat(collected).isEmpty();
	}

	@Test
	public void shouldCollectFixedLengthSeqAfterFewTransformationsToList() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(1, 2, 3);

		//when
		final List<Integer> collected = fixed.
				stream().
				filter(x -> x > 1).
				map(x -> x + 10).
				collect(toList());

		//then
		assertThat(collected).containsExactly(12, 13);
	}

	@Test
	public void shouldCollectInfiniteStreamAfterFewTransformationsToList() throws Exception {
		//given
		final LazySeq<Integer> infinite = LazySeq.iterate(1, x -> x * 2);

		//when
		final List<Integer> collected = infinite.
				stream().
				filter(x -> x > 1).
				map(x -> x + 10).
				limit(5).
				collect(toList());

		//then
		assertThat(collected).containsExactly(12, 14, 18, 26, 42);
	}

}

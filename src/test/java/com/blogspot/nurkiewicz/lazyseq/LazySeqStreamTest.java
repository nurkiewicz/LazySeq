package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.numbers;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 2:13 PM
 */
public class LazySeqStreamTest extends AbstractBaseTestCase {

	@Test
	public void shouldInvokeMultipleOperationsOnLazySeqAndProduceLazySeq() throws Exception {
		//given
		final LazySeq<Integer> oneToTwenty = numbers(1).take(20);

		//when
		final LazySeq<Integer> collected = stackedStream(oneToTwenty).collect(LazySeq.toLazySeq());

		//then
		assertThat(collected).isEqualTo(LazySeq.of(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
	}

	@Test
	public void shouldInvokeMultipleOperationsOnLazySeq() throws Exception {
		//given
		final LazySeq<Integer> oneToTwenty = numbers(1).take(20);

		//when
		final List<Integer> collected = stackedStream(oneToTwenty).collect(toList());

		//then
		assertThat(collected).isEqualTo(asList(5, 6, 7, 8, 9, 10, 11, 12, 13, 14));
	}

	@Test
	public void shouldInvokeMultipleOperationsAndUseCustomCollector() throws Exception {
		//given
		final LazySeq<Integer> oneToTwenty = numbers(1).take(20);
		final Stream<Integer> integerStream = stackedStream(oneToTwenty);

		//when
		final List<String> collected = integerStream.collect(
				() -> new ArrayList<String>(),
				(list, item) -> list.add(Integer.toString(item)),
				List::addAll
		);

		//then
		assertThat(collected).isEqualTo(asList("5", "6", "7", "8", "9", "10", "11", "12", "13", "14"));
	}

	@Test
	public void shouldInvokeTerminalOperations() throws Exception {
		//given
		final LazySeq<Integer> oneToTwenty = numbers(1).take(20);

		//when
		final Optional<Integer> min = stackedStream(oneToTwenty).min((a, b) -> a - b);

		//then
		assertThat(min).isEqualTo(Optional.of(5));
	}

	private Stream<Integer> stackedStream(LazySeq<Integer> stream) {
		return stream.stream().
				map(n -> n + 1).
				flatMap(n -> asList(0, n - 1).stream()).
				filter(n -> n != 0).
				skip(4).
				limit(10).
				sorted().
				distinct();
	}

}

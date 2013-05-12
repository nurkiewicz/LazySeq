package com.blogspot.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 3:08 PM
 */
public class LazySeqMapTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Test
	public void shouldReturnEmptyWhenMappingEmpty() throws Exception {
		assertThat(LazySeq.empty()).isEmpty();
	}

	@Test
	public void shouldMapFixedSeqWithJustOneElement() throws Exception {
		//given
		final LazySeq<Character> chars = of('a');

		//when
		final LazySeq<Character> toUpper = chars.map(Character::toUpperCase);

		//then
		assertThat(toUpper).isEqualTo(of('A'));
	}

	@Test
	public void shouldMapFixedSeqWithFewElements() throws Exception {
		//given
		final LazySeq<Character> chars = of('a', 'b', 'c');

		//when
		final LazySeq<Character> toUpper = chars.map(Character::toUpperCase);

		//then
		assertThat(toUpper).isEqualTo(of('A', 'B', 'C'));
	}

	@Test
	public void shouldMapInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = LazySeq.numbers(1);

		//when
		final LazySeq<Integer> multiplied = naturals.map(x -> x * 10);

		//then
		assertThat(multiplied.take(4)).isEqualTo(of(10, 20, 30, 40));
	}

	@Test
	public void shouldNotEvaluateTailOnMap() throws Exception {
		//given
		final LazySeq<Integer> seq = LazySeq.cons(17, supplierMock);

		//when
		seq.map(String::valueOf);

		//then
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldMapHead() throws Exception {
		//given
		final LazySeq<Integer> seq = LazySeq.cons(17, supplierMock);

		//when
		final LazySeq<String> strings = seq.map(String::valueOf);

		//then
		assertThat(strings.head()).isEqualToIgnoringCase("17");
	}

}

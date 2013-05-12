package com.blogspot.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.empty;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 3:30 PM
 */
public class LazySeqToStringTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnEmptyBracketsForEmptySeq() throws Exception {
		assertThat(empty().toString()).isEqualTo("[]");
	}

	@Test
	public void shouldDisplayWholeSeqWhenFixedElements() throws Exception {
		assertThat(of('x').toString()).isEqualToIgnoringCase("[x]");
		assertThat(of('x', 'y').toString()).isEqualToIgnoringCase("[x, y]");
		assertThat(of('x', 'y', 'z').toString()).isEqualToIgnoringCase("[x, y, z]");
	}

	@Test
	public void shouldShowOnlyFirstElementOfInfiniteSeqAfterCreation() throws Exception {
		assertThat(LazySeq.numbers(1).toString()).isEqualToIgnoringCase("[1, ?]");
	}

	@Test
	public void shouldIncludeAllFixedElementsProvidedDuringCreationInToString() throws Exception {
		//given
		final LazySeq<Integer> notFull = of(1, 2, (Supplier<LazySeq<Integer>>)() -> of(3));

		//when
		final String s = notFull.toString();

		//then
		assertThat(s).isEqualTo("[1, 2, ?]");
	}

	@Test
	public void shouldIncludeAllEvaluatedElementsInToString() throws Exception {
		//given
		final LazySeq<Integer> naturals = LazySeq.numbers(0);
		naturals.get(4);        //force evaluation

		//when
		final String s = naturals.toString();

		//then
		assertThat(s).isEqualTo("[0, 1, 2, 3, 4, ?]");
	}

	@Test
	public void shouldNotIncludeAllElementsEvenIfOnlyNilLeft() throws Exception {
		//given
		final LazySeq<Integer> notFull = LazySeq.of(1, 2, (Supplier<LazySeq<Integer>>)LazySeq::<Integer>empty);

		//when
		final String s = notFull.toString();

		//then
		assertThat(s).isEqualTo("[1, 2, ?]");
	}

	@Test
	public void shouldIncludeAllEvaluatedElementsWhenFirstEachIterated() throws Exception {
		//given
		final LazySeq<Integer> notFull = LazySeq.of(1, 2, (Supplier<LazySeq<Integer>>)LazySeq::<Integer>empty);
		notFull.forEach(x -> {});

		//when
		final String s = notFull.toString();

		//then
		assertThat(s).isEqualTo("[1, 2]");
	}

}

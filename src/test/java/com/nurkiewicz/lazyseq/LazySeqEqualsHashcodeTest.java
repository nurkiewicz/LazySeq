package com.nurkiewicz.lazyseq;

import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static com.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 3:10 PM
 */
public class LazySeqEqualsHashcodeTest extends AbstractBaseTestCase {

	@Test
	public void twoEmptySequencesAreEqual() throws Exception {
		//given
		final LazySeq<Object> first = empty();
		final LazySeq<Object> second = empty();

		assertThat(first.equals(second)).isTrue();
		assertThat(first.hashCode()).isEqualTo(second.hashCode());
	}

	@Test
	public void twoSequencesAreNotEqualWhenFirstIsEmptyAndSecondIsNot() throws Exception {
		//given
		final LazySeq<Integer> first = empty();
		final LazySeq<Integer> second = of(1);

		assertThat(first.equals(second)).isFalse();
	}

	@Test
	public void twoSequencesAreNotEqualWhenFirstIsNotEmptyButSecondIs() throws Exception {
		final LazySeq<Integer> first = of(1);
		final LazySeq<Integer> second = empty();

		assertThat(first.equals(second)).isFalse();
	}

	@Test
	public void twoShortFixedSequencesAreEqual() throws Exception {
		//given
		final LazySeq<Integer> first = of(1);
		final LazySeq<Integer> second = of(1);

		assertThat(first.equals(second)).isTrue();
		assertThat(first.hashCode()).isEqualTo(second.hashCode());
	}

	@Test
	public void twoLongFixedSequencesAreEqual() throws Exception {
		//given
		final LazySeq<Integer> first = of(1, 2, 3, 4, 5, 6);
		final LazySeq<Integer> second = of(1, 2, 3, 4, 5, 6);

		assertThat(first.equals(second)).isTrue();
		assertThat(first.hashCode()).isEqualTo(second.hashCode());
	}

	@Test
	public void fixedSeqIsEqualToLazyFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> first = of(1, 2, 3, 4);
		final LazySeq<Integer> second = cons(1, () -> cons(2, () -> cons(3, () -> of(4))));

		assertThat(first.equals(second)).isTrue();
		assertThat(first.hashCode()).isEqualTo(second.hashCode());
	}

	@Test
	public void shouldLazyButFiniteSequencesAreEqual() throws Exception {
		//given
		final LazySeq<Integer> first = lazy();
		final LazySeq<Integer> second = lazy();

		assertThat(first.equals(second)).isTrue();
		assertThat(first.hashCode()).isEqualTo(second.hashCode());
	}

	private LazySeq<Integer> lazy() {
		return cons(3,
				() -> cons(-2,
						() -> cons(8,
								() -> cons(5,
										() -> cons(-4,
												() -> cons(11,
														() -> cons(2,
																() -> of(1))))))));
	}

	@Test
	public void finiteSequenceIsNotEqualToInfiniteOne() throws Exception {
		//given
		final LazySeq<Integer> first = of(2, 3, 5, 7);
		final LazySeq<Integer> second = primes();

		assertThat(first.equals(second)).isFalse();
	}

	@Test
	public void infiniteSequenceIsNotEqualToFiniteOne() throws Exception {
		//given
		final LazySeq<Integer> first = primes();
		final LazySeq<Integer> second = of(2, 3, 5, 7);

		assertThat(first.equals(second)).isFalse();
	}

	@Test
	public void emptySeqIsNotEqualToInfinite() throws Exception {
		final LazySeq<Integer> first = empty();
		final LazySeq<Integer> second = primes();

		assertThat(first.equals(second)).isFalse();
	}

	@Test
	public void infiniteSeqIsNotEqualToEmpty() throws Exception {
		final LazySeq<Integer> first = primes();
		final LazySeq<Integer> second = empty();

		assertThat(first.equals(second)).isFalse();
	}

}

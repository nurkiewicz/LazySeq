package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.*;
import static com.nurkiewicz.lazyseq.samples.Seqs.primes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;


/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 10:28 AM
 */
public class LazySeqZipTest extends AbstractBaseTestCase {

	@Mock
	private java.util.function.Supplier<LazySeq<String>> firstSupplier;

	@Mock
	private java.util.function.Supplier<LazySeq<Integer>> secondSupplier;

	@Test
	public void shouldReturnEmptySeqWhenZipEmptyWithEmpty() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();

		//when
		final LazySeq<Integer> zipped = empty.zip(empty, (a, b) -> a + b);

		//then
		assertThat(zipped).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenZipEmptyWithNonEmpty() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();
		final LazySeq<Integer> nonEmpty = of(1);

		//when
		final LazySeq<Integer> zipped = empty.zip(nonEmpty, (a, b) -> a + b);

		//then
		assertThat(zipped).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenZipNonEmptyWithEmpty() throws Exception {
		//given
		final LazySeq<Integer> empty = empty();
		final LazySeq<Integer> nonEmpty = of(1);

		//when
		final LazySeq<Integer> zipped = nonEmpty.zip(empty, (a, b) -> a + b);

		//then
		assertThat(zipped).isEmpty();
	}

	@Test
	public void shouldZipTwoFiniteSequencesOfSameSize() throws Exception {
		//given
		final LazySeq<String> first = of("A", "B", "C");
		final LazySeq<Integer> second = of(1, 2, 3);

		//when
		final LazySeq<String> zipped = first.zip(second, (a, b) -> a + b);

		//then
		assertThat(zipped).isEqualTo(of("A1", "B2", "C3"));
	}

	@Test
	public void shouldZipTwoLazyFiniteSequencesOfSameSize() throws Exception {
		//given
		final LazySeq<String> first = cons("A", () -> cons("B", () -> of("C")));
		final LazySeq<Integer> second = cons(1, () -> cons(2, () -> of(3)));

		//when
		final LazySeq<String> zipped = first.zip(second, (a, b) -> a + b);

		//then
		assertThat(zipped).isEqualTo(of("A1", "B2", "C3"));
	}

	@Test
	public void shouldTrimSecondFixedSeqIfLonger() throws Exception {
		//given
		final LazySeq<String> first = of("A", "B", "C");
		final LazySeq<Integer> second = of(1, 2, 3, 4);

		//when
		final LazySeq<String> zipped = first.zip(second, (a, b) -> a + b);

		//then
		assertThat(zipped).isEqualTo(of("A1", "B2", "C3"));
	}

	@Test
	public void shouldTrimFirstFixedSeqIfLonger() throws Exception {
		//given
		final LazySeq<String> first = of("A", "B", "C", "D");
		final LazySeq<Integer> second = of(1, 2, 3);

		//when
		final LazySeq<String> zipped = first.zip(second, (a, b) -> a + b);

		//then
		assertThat(zipped).isEqualTo(of("A1", "B2", "C3"));
	}

	@Test
	public void shouldTrimSecondLazyButFiniteSeqIfLonger() throws Exception {
		//given
		final LazySeq<String> first = cons("A", () -> cons("B", () -> of("C")));
		final LazySeq<Integer> second = cons(1, () -> cons(2, () -> cons(3, () -> of(4))));

		//when
		final LazySeq<String> zipped = first.zip(second, (a, b) -> a + b);

		//then
		assertThat(zipped).isEqualTo(of("A1", "B2", "C3"));
	}

	@Test
	public void shouldTrimFirstLazyButFiniteSeqIfLonger() throws Exception {
		//given
		final LazySeq<String> first = cons("A", () -> cons("B", () -> cons("C", () -> of("D"))));
		final LazySeq<Integer> second = cons(1, () -> cons(2, () -> of(3)));

		//when
		final LazySeq<String> zipped = first.zip(second, (a, b) -> a + b);

		//then
		assertThat(zipped).isEqualTo(of("A1", "B2", "C3"));
	}

	@Test
	public void shouldZipTwoInfiniteSequences() throws Exception {
		//given
		final LazySeq<Integer> naturals = numbers(1);
		final LazySeq<Integer> primes = primes();

		//when
		final LazySeq<String> zipped = naturals.zip(primes, (n, p) -> n + ": " + p);

		//then
		assertThat(zipped.take(5)).isEqualTo(of("1: 2", "2: 3", "3: 5", "4: 7", "5: 11"));
	}

	@Test
	public void shouldZipFiniteWithInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = numbers(1).take(5);
		final LazySeq<Integer> primes = primes();

		//when
		final LazySeq<String> zipped = naturals.zip(primes, (n, p) -> n + ": " + p);

		//then
		assertThat(zipped).isEqualTo(of("1: 2", "2: 3", "3: 5", "4: 7", "5: 11"));
	}

	@Test
	public void shouldZipInfiniteWithFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = numbers(1);
		final LazySeq<Integer> primes = primes().take(5);

		//when
		final LazySeq<String> zipped = naturals.zip(primes, (n, p) -> n + ": " + p);

		//then
		assertThat(zipped).isEqualTo(of("1: 2", "2: 3", "3: 5", "4: 7", "5: 11"));
	}

	@Test
	public void shouldNotEvaluateTailWhenZippingTwoSequences() throws Exception {
		//given
		final LazySeq<String> first = cons("A", firstSupplier);
		final LazySeq<Integer> second = cons(1, secondSupplier);

		//when
		first.zip(second, (a, b) -> a + b);

		//then
		verifyZeroInteractions(firstSupplier);
		verifyZeroInteractions(secondSupplier);
	}

}

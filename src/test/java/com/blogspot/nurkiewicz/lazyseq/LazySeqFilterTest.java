package com.blogspot.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 3:52 PM
 */
public class LazySeqFilterTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<String>> supplierMock;

	@Test
	public void shouldReturnEmptySeqWhenFilteringEmptySeq() throws Exception {
		assertThat(empty().filter(x -> false)).isEqualTo(empty());
	}

	@Test
	public void shouldReturnEmptySeqWhenNoElementsMatchInFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = of(-1, -2, -3);

		//when
		final LazySeq<Integer> filtered = fixed.filter(x -> x > 0);

		//then
		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldReturnEmptySeqWhenNoElementsMatchInFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> fixed = LazySeq.<Integer>of(-1, -2, () -> of(-3, -4));

		//when
		final LazySeq<Integer> filtered = fixed.filter(x -> x > 0);

		//then
		assertThat(filtered).isEmpty();
	}

	@Test
	public void shouldNotEvaluateTailIfHeadMatchesPredicate() throws Exception {
		//given
		final LazySeq<String> generated = LazySeq.of("A", "BB", supplierMock);

		//when
		generated.filter(s -> !s.isEmpty());

		//then
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldEvaluateTailOnceWhenFirstElementNotMatching() throws Exception {
		//given
		final LazySeq<String> generated = LazySeq.cons("", supplierMock);
		given(supplierMock.get()).willReturn(of("C"));

		//when
		generated.filter(s -> !s.isEmpty());

		//then
		Mockito.verify(supplierMock).get();
	}

	@Test
	public void shouldEvaluateTailMultipleTimesToReturnLastElement() throws Exception {
		//given
		final LazySeq<String> generated = LazySeq.<String>cons("a", () -> cons("bb", () -> cons("ccc", LazySeq::<String>empty)));

		//when
		final LazySeq<String> filtered = generated.filter(s -> s.length() >= 3);

		//then
		assertThat(filtered).isEqualTo(of("ccc"));
	}

	@Test
	public void shouldFilterSeveralItemsFromFiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = numbers(1);

		//when
		final LazySeq<Integer> filtered = naturals.filter(x -> x % 3 == 0);

		//then
		assertThat(filtered.take(4)).containsExactly(3, 6, 9, 12);
	}

}

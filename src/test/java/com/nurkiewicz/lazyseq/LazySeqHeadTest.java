package com.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.cons;
import static com.nurkiewicz.lazyseq.LazySeq.empty;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/10/13, 9:56 PM
 */
public class LazySeqHeadTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Test
	public void shouldFailWhenTryingToAccessHeadOfNil() throws Exception {
		//given
		final LazySeq<String> empty = empty();

		try {
			//when
			empty.head();
			failBecauseExceptionWasNotThrown(NoSuchElementException.class);
		} catch (NoSuchElementException e) {
			//then
		}
	}

	@Test
	public void shouldReturnHeadOfFixedSeqs() throws Exception {
		assertThat(LazySeq.of(1).head()).isEqualTo(1);
		assertThat(LazySeq.of(2, 3).head()).isEqualTo(2);
		assertThat(LazySeq.of(4, 5, 6).head()).isEqualTo(4);
		assertThat(LazySeq.of(Arrays.asList(7, 8, 9, 10, 11, 12)).head()).isEqualTo(7);
	}

	@Test
	public void shouldReturnHeadOfDynamicLazySeq() throws Exception {
		assertThat(LazySeq.of(1, tail()).head()).isEqualTo(1);
		assertThat(LazySeq.of(2, 3, tail()).head()).isEqualTo(2);
		assertThat(LazySeq.of(4, 5, 6, tail()).head()).isEqualTo(4);
		assertThat(LazySeq.concat(Arrays.asList(7, 8, 9, 10, 11, 12), tail()).head()).isEqualTo(7);
	}

	@Test
	public void shouldReturnHeadOfTail() throws Exception {
		assertThat(LazySeq.of(2, 3).tail().head()).isEqualTo(3);
		assertThat(LazySeq.of(2, 3, tail()).tail().head()).isEqualTo(3);
		assertThat(LazySeq.of(4, 5, 6, tail()).tail().head()).isEqualTo(5);
		assertThat(LazySeq.concat(Arrays.asList(7, 8, 9, 10, 11, 12), tail()).tail().head()).isEqualTo(8);
	}

	@Test
	public void shouldNotEvaluateTailIfHeadRequested() throws Exception {
		//given
		LazySeq<Integer> lazy = cons(1, supplierMock);

		//when
		lazy.head();

		//then
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldEvaluateTailOnlyOnceWhenTailsHeadRequested() throws Exception {
		//given
		LazySeq<Integer> lazy = cons(1, supplierMock);
		given(supplierMock.get()).willReturn(cons(2, supplierMock));

		//when
		final Integer tailsHead = lazy.tail().head();

		//then
		verify(supplierMock).get();
		assertThat(tailsHead).isEqualTo(2);
	}

	@Test
	public void shouldEvaluateTailOnlyTwiceOnAccessingThirdElement() throws Exception {
		//given
		LazySeq<Integer> lazy = cons(1, supplierMock);
		given(supplierMock.get()).willReturn(cons(2, supplierMock));

		//when
		final Integer tailsHead = lazy.tail().tail().head();

		//then
		verify(supplierMock, times(2)).get();
		assertThat(tailsHead).isEqualTo(2);
	}

	@Test
	public void shouldThrowWhenTryingToAccessHeadPastTheEndElementOfEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> twoItems = LazySeq.of(1, 2);
		final LazySeq<Integer> tail = twoItems.tail().tail();

		try {
			//when
			tail.head();
			failBecauseExceptionWasNotThrown(NoSuchElementException.class);
		} catch (NoSuchElementException e) {
			//then
		}
	}

	private Supplier<LazySeq<Integer>> tail() {
		return () -> LazySeq.of(42);
	}

}

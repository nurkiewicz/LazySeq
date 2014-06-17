package com.nurkiewicz.lazyseq;

import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.nurkiewicz.lazyseq.LazySeq.continually;
import static com.nurkiewicz.lazyseq.LazySeq.of;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 2:33 PM
 */
public class LazySeqContinuallyTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<Integer> supplierMock;

	@Test
	public void shouldInvokeGeneratorOnlyOnceOnCreation() throws Exception {
		//given
		given(supplierMock.get()).willReturn(42);

		//when
		continually(supplierMock);

		//then
		BDDMockito.verify(supplierMock).get();
	}

	@Test
	public void shouldInvokeGeneratorTwiceWhenAskingForTail() throws Exception {
		//given
		given(supplierMock.get()).willReturn(42);

		//when
		continually(supplierMock).tail();

		//then
		BDDMockito.verify(supplierMock, times(2)).get();
	}

	@Test
	public void shouldNotCallGeneratorForTheSecondTimeWhenAccessingTail() throws Exception {
		//given
		given(supplierMock.get()).willReturn(42, 43);

		//when
		final LazySeq<Integer> cont = continually(supplierMock);
		cont.tail().head();
		cont.tail().head();

		//then
		BDDMockito.verify(supplierMock, times(2)).get();
	}

	@Test
	public void shouldContinuallyProduceSeq() throws Exception {
		//given
		given(supplierMock.get()).willReturn(42, 43);

		//when
		final LazySeq<Integer> cont = continually(supplierMock);

		//then
		assertThat(cont.head()).isEqualTo(42);
		assertThat(cont.tail().head()).isEqualTo(43);
	}

	@Test
	public void shouldCreateCycleFromSingleItemIterable() throws Exception {
		//given
		final LazySeq<Character> posNeg = continually(asList('a'));

		//when
		final LazySeq<Character> subSeq = posNeg.take(5);

		//then
		assertThat(subSeq).isEqualTo(of('a', 'a', 'a', 'a', 'a'));
	}

	@Test
	public void shouldCreateCycleFromIterable() throws Exception {
		//given
		final LazySeq<Integer> posNeg = continually(asList(1, -1));

		//when
		final LazySeq<Integer> subSeq = posNeg.take(5);

		//then
		assertThat(subSeq).isEqualTo(of(1, -1, 1, -1, 1));
	}

	@Test
	public void shouldCreateCycleFromLongIterable() throws Exception {
		//given
		final LazySeq<Integer> posNeg = continually(asList(1, 2, 3, 2));

		//when
		final LazySeq<Integer> subSeq = posNeg.take(7);

		//then
		assertThat(subSeq).isEqualTo(of(1, 2, 3, 2, 1, 2, 3));
	}

	@Test
	public void shouldCreateEmptySeqIfCyclingEmptySeq() throws Exception {
		final LazySeq<Object> contEmpty = continually(emptyList());

		assertThat(contEmpty).isEmpty();
	}

	@Test
	public void shouldCreateCycleOfSingleElement() throws Exception {
		final LazySeq<Character> constant = continually('$');

		assertThat(constant.take(4)).isEqualTo(of('$', '$', '$', '$'));
	}

}

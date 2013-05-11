package com.blogspot.nurkiewicz.lazyseq;

import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.continually;
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
	
}

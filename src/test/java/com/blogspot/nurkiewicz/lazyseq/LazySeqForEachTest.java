package com.blogspot.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.function.Consumer;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.cons;
import static com.blogspot.nurkiewicz.lazyseq.LazySeq.of;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 7:08 PM
 */
public class LazySeqForEachTest extends AbstractBaseTestCase {

	@Mock
	private Consumer<Integer> consumerMock;

	@Test
	public void shouldDoNothingOnEmptySeq() throws Exception {
		//given
		final LazySeq<Integer> empty = LazySeq.empty();

		//when
		empty.forEach(consumerMock);

		//then
		verifyZeroInteractions(consumerMock);
	}

	@Test
	public void shouldCallConsumerForSingleElementInSeq() throws Exception {
		//given
		final LazySeq<Integer> single = of(1);

		//when
		single.forEach(consumerMock);

		//then
		Mockito.verify(consumerMock).accept(1);
		verifyZeroInteractions(consumerMock);
	}

	@Test
	public void shouldCallConsumerForMultipleElementsOfFixedSeq() throws Exception {
		//given
		final LazySeq<Integer> single = of(2, 3, 4);

		//when
		single.forEach(consumerMock);

		//then
		Mockito.verify(consumerMock).accept(2);
		Mockito.verify(consumerMock).accept(3);
		Mockito.verify(consumerMock).accept(4);
		verifyZeroInteractions(consumerMock);
	}

	@Test
	public void shouldCallConsumerForMultipleElementsOfSubstream() throws Exception {
		//given
		final LazySeq<Integer> single = of(2, 3, 4, 5, 6, 7).take(3);

		//when
		single.forEach(consumerMock);

		//then
		Mockito.verify(consumerMock).accept(2);
		Mockito.verify(consumerMock).accept(3);
		Mockito.verify(consumerMock).accept(4);
		verifyZeroInteractions(consumerMock);
	}

	@Test
	public void shouldCallConsumerForEachElementOfLAzilyCreatedButNotInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> single = cons(5, () -> cons(6, () -> of(7)));

		//when
		single.forEach(consumerMock);

		//then
		Mockito.verify(consumerMock).accept(5);
		Mockito.verify(consumerMock).accept(6);
		Mockito.verify(consumerMock).accept(7);
		verifyZeroInteractions(consumerMock);
	}

}

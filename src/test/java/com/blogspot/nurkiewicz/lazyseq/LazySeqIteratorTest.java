package com.blogspot.nurkiewicz.lazyseq;

import org.mockito.Mock;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.blogspot.nurkiewicz.lazyseq.LazySeq.*;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 9:30 AM
 */
public class LazySeqIteratorTest extends AbstractBaseTestCase {

	@Mock
	private Supplier<LazySeq<Integer>> supplierMock;

	@Mock
	private Consumer<Character> consumerMock;

	@Test
	public void shouldReturnEmptyIteratorForEmptySeq() throws Exception {
		final Iterator<Object> iterator = empty().iterator();
		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldAllowCallingHasNextContinuouslyOnEmptyIterator() throws Exception {
		//given
		final Iterator<Object> iterator = empty().iterator();

		//when
		iterator.hasNext();

		//then
		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldThrowWhenTryingToAdvanceIteratorOfEmptySeq() throws Exception {
		//given
		final Iterator<Object> iterator = empty().iterator();

		try {
			//when
			iterator.next();
			failBecauseExceptionWasNotThrown(NoSuchElementException.class);
		} catch (NoSuchElementException e) {
			//then
		}
	}

	@Test
	public void shouldReturnNonEmptyIteratorForSingleElementSeq() throws Exception {
		//given
		final Iterator<String> iterator = of("a").iterator();

		assertThat(iterator.hasNext()).isTrue();
	}

	@Test
	public void shouldAllowAdvancingIteratorForSingleElementSeq() throws Exception {
		//given
		final Iterator<String> iterator = of("a").iterator();

		//when
		final String next = iterator.next();

		//then
		assertThat(next).isEqualTo("a");
	}

	@Test
	public void shouldAllowAdvancingIteratorForSingleElementSeqWhenHasNextWasCalledFirst() throws Exception {
		//given
		final Iterator<String> iterator = of("a").iterator();
		iterator.hasNext();

		//when
		final String next = iterator.next();

		//then
		assertThat(next).isEqualTo("a");
	}

	@Test
	public void shouldNotHaveNextAfterAdvancingButSingleElement() throws Exception {
		//given
		final Iterator<String> iterator = of("a").iterator();
		iterator.next();

		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldThrowWhenTryingToAdvanceIteratorAfterConsumingSingleElement() throws Exception {
		//given
		final Iterator<String> iterator = of("a").iterator();
		iterator.next();

		try {
			//when
			iterator.next();
			failBecauseExceptionWasNotThrown(NoSuchElementException.class);
		} catch (NoSuchElementException e) {
			//then
		}
	}

	@Test
	public void shouldReturnNonEmptyIteratorForFixedSeq() throws Exception {
		//given
		final Iterator<String> iterator = of("a", "b", "c").iterator();

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("a");

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("b");

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("c");

		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldAllowTraversingWithoutCallingHasNext() throws Exception {
		//given
		final Iterator<String> iterator = of("a", "b", "c").iterator();

		assertThat(iterator.next()).isEqualTo("a");
		assertThat(iterator.next()).isEqualTo("b");
		assertThat(iterator.next()).isEqualTo("c");

		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldAllowCallingHasNextMultipleTimes() throws Exception {
		//given
		final Iterator<String> iterator = of("a", "b", "c").iterator();

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("a");
		assertThat(iterator.next()).isEqualTo("b");
		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo("c");

		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldAllowTraversingLazyButFiniteSeq() throws Exception {
		//given
		final Iterator<Integer> iterator = cons(3,
				() -> cons(-2,
						() -> cons(8,
								() -> cons(5,
										() -> cons(-4,
												() -> cons(11,
														() -> cons(2,
																() -> of(1)))))))).iterator();

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(3);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(-2);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(8);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(5);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(-4);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(11);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(2);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(1);

		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldAllowTraversingLazyButFiniteSeqWithoutCallingHasNext() throws Exception {
		//given
		final Iterator<Integer> iterator = cons(3,
				() -> cons(-2,
						() -> cons(8,
								() -> cons(5,
										() -> cons(-4,
												() -> cons(11,
														() -> cons(2,
																() -> of(1)))))))).iterator();

		assertThat(iterator.next()).isEqualTo(3);
		assertThat(iterator.next()).isEqualTo(-2);
		assertThat(iterator.next()).isEqualTo(8);
		assertThat(iterator.next()).isEqualTo(5);
		assertThat(iterator.next()).isEqualTo(-4);
		assertThat(iterator.next()).isEqualTo(11);
		assertThat(iterator.next()).isEqualTo(2);
		assertThat(iterator.next()).isEqualTo(1);
		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	public void shouldNotEvaluateTailWhenCreatingIterator() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(1, supplierMock);

		//when
		lazy.iterator();

		//then
		verifyZeroInteractions(supplierMock);
	}

	@Test
	public void shouldEvaluateTailOnlyOnceWhenAdvancingITerator() throws Exception {
		//given
		final LazySeq<Integer> lazy = cons(1, supplierMock);

		//when
		lazy.iterator().next();

		//then
		verify(supplierMock).get();
	}

	@Test
	public void shouldCreateIteratorForInfiniteSeq() throws Exception {
		//given
		final LazySeq<Integer> naturals = SampleStreams.naturals(1);

		//when
		final Iterator<Integer> iterator = naturals.iterator();

		//then
		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(1);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(2);

		assertThat(iterator.hasNext()).isTrue();
		assertThat(iterator.next()).isEqualTo(3);

		assertThat(iterator.next()).isEqualTo(4);
		assertThat(iterator.next()).isEqualTo(5);
	}

	@Test
	public void shouldCallConsumerForEachElementOfIteratorForFinitSeq() throws Exception {
		//given
		final Iterator<Character> iterator = of('a', 'b', 'c').iterator();

		//when
		iterator.forEachRemaining(consumerMock);

		//then
		verify(consumerMock).accept('a');
		verify(consumerMock).accept('b');
		verify(consumerMock).accept('c');
		verifyNoMoreInteractions(consumerMock);
	}

	@Test
	public void shouldAllowJava5ForEachIteration() throws Exception {
		//given
		final LazySeq<Character> fixed = of('a', 'b', 'c');

		//when
		for (char c : fixed) {
			consumerMock.accept(c);
		}

		//then
		verify(consumerMock).accept('a');
		verify(consumerMock).accept('b');
		verify(consumerMock).accept('c');
	}

}

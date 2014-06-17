package com.nurkiewicz.lazyseq.samples;

import com.nurkiewicz.lazyseq.AbstractBaseTestCase;
import com.nurkiewicz.lazyseq.LazySeq;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static com.nurkiewicz.lazyseq.LazySeq.numbers;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/12/13, 4:36 PM
 */
public class LazyPagingTest extends AbstractBaseTestCase {

	@Mock
	private Consumer<String> pageConsumer;

	private static final int PAGE_SIZE = 5;

	@Test
	public void shouldLoadOnlyFirstPageWhenInitiated() throws Exception {
		records(0);

		verify(pageConsumer).accept("0,5");
	}

	@Test
	public void shouldReturnFirstFewRecords() throws Exception {
		//given
		final LazySeq<Record> records = records(0);

		//when

		//then
		records.startsWith(Arrays.asList(new Record(0), new Record(1), new Record(2), new Record(3)));
		verify(pageConsumer).accept("0,5");
	}

	@Test
	public void shouldNotLoadSubsequentPagesIfOneRecordLeftInCurrentBatch() throws Exception {
		//given
		final LazySeq<Record> records = records(0);

		//when
		records.drop(PAGE_SIZE - 1);

		//then
		verify(pageConsumer).accept("0,5");
	}

	@Test
	public void shouldLoadSecondPageWhenAllElementsFromFirstDropped() throws Exception {
		//given
		final LazySeq<Record> records = records(0);

		//when
		final Record record = records.drop(PAGE_SIZE).head();

		//then
		verify(pageConsumer).accept("0,5");
		verify(pageConsumer).accept("5,5");
		assertThat(record).isEqualTo(new Record(5));
	}

	@Test
	public void shouldLoadAllPreviousPageWhenAccessingArbitraryRecord() throws Exception {
		//given
		final LazySeq<Record> records = records(0);

		//when
		final Record record = records.get(17);

		//then
		final InOrder order = Mockito.inOrder(pageConsumer);
		order.verify(pageConsumer).accept("0,5");
		order.verify(pageConsumer).accept("5,5");
		order.verify(pageConsumer).accept("10,5");
		order.verify(pageConsumer).accept("15,5");
		assertThat(record).isEqualTo(new Record(17));
	}

	private LazySeq<Record> records(int from) {
		return LazySeq.concat(loadPage(from, PAGE_SIZE), () -> records(from + PAGE_SIZE));
	}

	public List<Record> loadPage(int offset, int max) {
		pageConsumer.accept(offset + "," + max);
		return numbers(offset).
				take(max).
				map(Record::new).
				toList();
	}

}

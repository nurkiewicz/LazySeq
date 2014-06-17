package com.nurkiewicz.lazyseq;

import com.nurkiewicz.lazyseq.samples.Seqs;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.Test;

import static com.nurkiewicz.lazyseq.LazySeq.of;
import static com.nurkiewicz.lazyseq.LazySeq.tabulate;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.offset;

/**
 * @author Tomasz Nurkiewicz
 * @since 5/11/13, 12:37 PM
 */
public class LazySeqTabulateTest extends AbstractBaseTestCase {

	@Test
	public void shouldReturnStreamOfGrowingStrings() throws Exception {
		//given
		final LazySeq<String> strings = tabulate(0, len -> StringUtils.repeat("*", len));

		//when
		final LazySeq<String> generated = strings.take(6);

		//then
		assertThat(generated).isEqualTo(of("", "*", "**", "***", "****", "*****"));
	}

	@Test
	public void shouldCreateInfiniteStreamOfPiEstimation() throws Exception {
		//given
		final LazySeq<Double> piSeriesEstimation = Seqs.piSeriesEstimation();

		//when
		final double piEstimation = piSeriesEstimation.
				limit(1000).
				reduce((acc, x) -> acc + x).
				get() * 4;

		//then
		assertThat(piEstimation).isEqualTo(Math.PI, offset(0.01));
	}

	@Test
	public void shouldCreateInfiniteStreamOfPiEstimationWithTransforming() throws Exception {
		//given
		final LazySeq<Double> piSeriesEstimation = Seqs.piSeriesEstimation().map(x -> x * 4);

		//when
		final double piEstimation = piSeriesEstimation.
				limit(1000).
				reduce((acc, x) -> acc + x).
				get();

		//then
		assertThat(piEstimation).isEqualTo(Math.PI, offset(0.01));
	}

}

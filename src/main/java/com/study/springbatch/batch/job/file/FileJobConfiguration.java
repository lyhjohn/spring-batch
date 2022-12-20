package com.study.springbatch.batch.job.file;

import com.study.springbatch.batch.chunk.processor.FIleItemProcessor;
import com.study.springbatch.batch.domain.Product;
import com.study.springbatch.batch.domain.ProductVO;
import javax.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * file 저장 Job
 */
@Configuration
@RequiredArgsConstructor
public class FileJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public Job fileJob() {
		return jobBuilderFactory.get("fileJob")
			.start(fileStep())
			.build();
	}

	@Bean
	public Step fileStep() {
		return stepBuilderFactory.get("fileStep1")
			// ProductVO 객체를 read 하고 Product 객체를 write 한다
			.<ProductVO, Product>chunk(10) // chunk size
			.reader(fileItemReader(null))
			.processor(fileItemProcessor())
			.writer(fileItemWriter())
			.build();
	}

	/**
	 *
	 * @param requestDate = EditConfiguration 에서 requestDate 파라미터 값 읽어옴
	 */
	@Bean
	@StepScope
	public FlatFileItemReader<ProductVO> fileItemReader(@Value("#{jobParameters['requestDate']}") String requestDate) {
		return new FlatFileItemReaderBuilder<ProductVO>()
			.name("flatFile")
			.resource(new ClassPathResource("product_" + requestDate + ".csv")) // 해당 파일에 각 라인을 읽어서 ProductVO에 매핑함
			.fieldSetMapper(new BeanWrapperFieldSetMapper<>())
			.targetType(ProductVO.class)
			.linesToSkip(1) // 파일 read 할 때 첫번째 줄은 스킵
			.delimited().delimiter(",") // 파일 읽을 때 컴마 기준으로 읽음
			.names("id", "name", "price", "type")
			.build();
	}

	@Bean
	public ItemProcessor<ProductVO, Product> fileItemProcessor() {
		return new FIleItemProcessor();
	}

	@Bean
	public JpaItemWriter<Product> fileItemWriter() {
		return new JpaItemWriterBuilder<Product>()
			.entityManagerFactory(entityManagerFactory)
			.usePersist(true)
			.build();
	}
}

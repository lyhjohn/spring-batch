package com.study.springbatch.batch.job.api;

import com.study.springbatch.batch.domain.ProductVO;
import com.study.springbatch.batch.partition.ProductPartitioner;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@Configuration
@RequiredArgsConstructor
public class ApiStepConfiguration {

	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	
	private final int chunkSize = 10;

	@Bean
	public Step apiMasterStep() {
		return stepBuilderFactory.get("apiMasterStep")
			.partitioner(apiSlaveStep().getName(), partitioner())
			.gridSize(3)
			.taskExecutor(taskExecutor())
			.build();
	}

	@Bean
	public Step apiSlaveStep() throws Exception {
		return stepBuilderFactory.get("apiSlaveStep")
			.<ProductVO, ProductVO>chunk(chunkSize)
			.reader(itemReader(null))
			.processor(itemProcessor())
			.writer(itemWriter())
			.build();
	}

	@Bean
	public ProductPartitioner partitioner() {
		ProductPartitioner productPartitioner = new ProductPartitioner();
		productPartitioner.setDataSource(dataSource);
		return productPartitioner;
	}

	@Bean
	@StepScope
	public ItemReader<ProductVO> itemReader(@Value("#{stepExecutionContext['product']}") ProductVO productVO) throws Exception {

		JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(dataSource);
		reader.setPageSize(chunkSize);
		reader.setRowMapper(new BeanPropertyRowMapper(ProductVO.class));

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, name, price, type");
		queryProvider.setFromClause("from product");
		queryProvider.setWhereClause("where type = :type");

		Map<String, Order> sortKeys = new HashMap<>(1);
		sortKeys.put("id", Order.DESCENDING);
		queryProvider.setSortKeys(sortKeys);

		reader.setParameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()));
		reader.setQueryProvider(queryProvider);
		reader.afterPropertiesSet();

		return reader;
	}
}

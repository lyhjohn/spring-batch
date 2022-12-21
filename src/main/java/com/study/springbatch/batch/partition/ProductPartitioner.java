package com.study.springbatch.batch.partition;

import java.util.Map;
import javax.sql.DataSource;
import lombok.Setter;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class ProductPartitioner implements Partitioner {

	private DataSource dataSource;

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Map<String, ExecutionContext> partition(int i) {
		return null;
	}
}

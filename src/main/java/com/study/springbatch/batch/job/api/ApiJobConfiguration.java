package com.study.springbatch.batch.job.api;

import com.study.springbatch.batch.listener.JobListener;
import com.study.springbatch.batch.tasklet.ApiEndTasklet;
import com.study.springbatch.batch.tasklet.ApiStartTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ApiJobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final ApiStartTasklet apiStartTasklet;
	private final ApiEndTasklet apiEndTasklet;
	private final Step jobStep;

	/**
	 * api 통신을 위한 job
	 */
	@Bean
	public Job apiJob() {
		return jobBuilderFactory.get("apiJob")
			.listener(new JobListener())
			.start(apiStep1())
			.next(jobStep) // jobStep에서 chunk 기반 프로세스 담당
			.next(apiStep2())
			.build();
	}

	@Bean
	public Step apiStep1() {
		return stepBuilderFactory.get("step1")
			.tasklet(apiStartTasklet)
			.build();
	}

	@Bean
	public Step apiStep2() {
		return stepBuilderFactory.get("step2")
			.tasklet(apiEndTasklet)
			.build();
	}

}

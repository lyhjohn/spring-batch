package com.study.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * job / step / tasklet 의미 job: 일감 step: 일의 단계 tasklet: 작업 내용
 * 어플 실행 시 배치 테이블이 자동으로 생성되고 job이 자동으로 실행됨
 * 배치 코드 실행 순서 job 실행 -> step 실행 -> tasklet 실행 (job 안에 step 안에 tasklet이 있음)
 */

/**
 * 빈 등록 후 application 설정에서 initialize-schema: never 설정 시 Table 'spring_batch.batch_job_instance'
 * doesn't exist 에러 발생함 스키마 테이블이 생성되지 않아서 못찾기 때문. (mysql 등은 initialize-schema: embedded 는 당연히 안됨) 단,
 * initialize-schema: embedded 설정 후 h2 로 실행 시는 에러 발생 안함
 */
@Configuration
@RequiredArgsConstructor
public class JobConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;


	@Bean
	public Job helloJob() {
		return jobBuilderFactory.get("Job")
			.start(step1())
			.next(step2())
			.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.tasklet(new Tasklet() {
				@Override
				public RepeatStatus execute(StepContribution stepContribution,
					ChunkContext chunkContext) throws Exception {
					Thread.sleep(3000);
					System.out.println("step1 was execute");
					return RepeatStatus.FINISHED;
				}
			}).build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
			.tasklet(new Tasklet() {
				@Override
				public RepeatStatus execute(StepContribution stepContribution,
					ChunkContext chunkContext) throws Exception {
					System.out.println("step2 was execute");
					return RepeatStatus.FINISHED;
				}
			}).build();
	}
}

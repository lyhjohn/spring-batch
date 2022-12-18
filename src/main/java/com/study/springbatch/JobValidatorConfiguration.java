package com.study.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * job / step / tasklet 의미 job: 일감 step: 일의 단계 tasklet: 작업 내용 어플 실행 시 배치 테이블이 자동으로 생성되고 job이 자동으로
 * 실행됨 배치 코드 실행 순서 job 실행 -> step 실행 -> tasklet 실행 (job 안에 step 안에 tasklet이 있음)
 */

/**
 * 빈 등록 후 application 설정에서 initialize-schema: never 설정 시 Table 'spring_batch.batch_job_instance'
 * doesn't exist 에러 발생함 스키마 테이블이 생성되지 않아서 못찾기 때문. (mysql 등은 initialize-schema: embedded 는 당연히 안됨) 단,
 * initialize-schema: embedded 설정 후 h2 로 실행 시는 에러 발생 안함
 */
@Configuration
@RequiredArgsConstructor
public class JobValidatorConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;


	/**
	 * validator: 전달한 JobParameters를 인자로 받아서 검증한다.
	 * DefaultJobParametersValidator 를 사용할수도 있고 Custom으로 구현해서도 사용 가능하다.
	 * DefaultJobParametersValidator 는 requiredKeys(필수키)와 optionalKeys(옵션키)를 인자로 받는다.
	 * 옵션키는 없어도 검증 통과, 필수키는 하나라도 없으면 검증 실패
	 */
	@Bean
	public Job helloJob1() {
		return jobBuilderFactory.get("Job")
			.start(step1()) // SimpleJobBuilder 생성
			.next(step2()) // JobBuilderHelper 가 제공하는 api 를 chain 형식으로 사용 가능
			.next(step3())
//			.validator(new CustomJobParameterValidator())
			.validator(new DefaultJobParametersValidator(new String[]{"name", "date"},
				new String[]{"count"}))
			.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.tasklet(new Tasklet() {
				@Override
				public RepeatStatus execute(StepContribution stepContribution,
					ChunkContext chunkContext) throws Exception {
					System.out.println("step1 was executed");
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
					System.out.println("step2 was executed");
					return RepeatStatus.FINISHED;
				}
			}).build();
	}

	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3")
			.tasklet(new Tasklet() {
				@Override
				public RepeatStatus execute(StepContribution stepContribution,
					ChunkContext chunkContext) throws Exception {
					System.out.println("step3 was executed");
					return RepeatStatus.FINISHED;
				}
			}).build();
	}
}

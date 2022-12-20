package com.study.springbatch;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
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
public class JobApiConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;


	/**
	 * preventRestart: 이미 실패해서 status가 Failed인 Job에 대해 재실행이 안되게 막는다.
	 * incrementer: 동일한 Job 이름이면서 JobParameters 의 동일 key 가 존재하면 key 값을 자동으로 1씩 증가시키며 저장한다.
	 * 				동일 key 가 존재하지 않다면 해당 key 를 생성하고 그 값을 0부터 시작한다. (RunIdIncrementer)
	 * 				혹은 CustomJobParametersIncrementer 를 만들어서 원하는 방식으로 key 값을 자동 생성할 수 있다.
	 * 				JobParameter 가 여러개라 할지라도 incrementer로 다르게 지정한 parameter가 하나라도 섞여있다면 동일한 이름의 Job 생성 가능
	 */
	@Bean
	public Job job() {
		return jobBuilderFactory.get("Job")
			.incrementer(new RunIdIncrementer())
//			.preventRestart()
			.start(step1()) // SimpleJobBuilder 생성
			.next(step2()) // JobBuilderHelper 가 제공하는 api 를 chain 형식으로 사용 가능
			.next(step3())
			.build();
	}

	@Bean
	public Flow flow() {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
		flowBuilder.start(step2()).end();
		return flowBuilder.build();
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
			.<String, String>chunk(3)
			.reader(new ItemReader<String>() {
				@Override
				public String read()
					throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
					return null;
				}
			})
			.processor(new ItemProcessor<String, String>() {
				@Override
				public String process(String s) throws Exception {
					return null;
				}
			})
			.writer(new ItemWriter<String>() {
				@Override
				public void write(List<? extends String> list) throws Exception {

				}
			})
			.build();
	}

	/**
	 * 멀티스레드 관련 작업 시 사용
	 */
	@Bean
	public Step step3() {
		return stepBuilderFactory.get("step3")
			.partitioner(step1())
			.gridSize(2)
			.build();
	}

	/**
	 * step 안에 job
	 */
	@Bean
	public Step step4() {
		return stepBuilderFactory.get("step3")
			.job(job())
			.build();
	}

	/**
	 * step 안에 flow
	 */
	@Bean
	public Step step5() {
		return stepBuilderFactory.get("step5")
			.flow(flow())
			.build();
	}
}

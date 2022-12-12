package com.study.springbatch;

import java.util.Date;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * JobParameter 생성을 테스트 하기 위한 클래스
 * Job 실행방법: 스프링부트에 의한 자동실행 / 수동실행
 * 이 클래스는 Job을 수동으로 실행시킴
 * 스프링부트에 의한 자동실행을 끄기 위해 yml 파일에서 job: enabled: false 설정 必
 * ApplicationRunner 인터페이스 구현을 통해 스프링부트 앱 실행 시 해당 클래스 실행되도록 설정
 */
@Component
public class JobParameterTest implements ApplicationRunner {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Override
	public void run(ApplicationArguments args) throws Exception {

		JobParameters jobParameters = new JobParametersBuilder()
			.addString("name", "user1")
			.addLong("seq", 2L)
			.addDate("date", new Date())
			.addDouble("age", 16.5)
			.toJobParameters();
		
		jobLauncher.run(job, jobParameters);
	}
}

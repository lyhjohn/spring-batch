//package com.study.springbatch;
//
//import java.util.Date;
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.core.JobParametersInvalidException;
//import org.springframework.batch.core.launch.JobLauncher;
//import org.springframework.batch.core.launch.support.SimpleJobLauncher;
//import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
//import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
//import org.springframework.batch.core.repository.JobRestartException;
//import org.springframework.boot.autoconfigure.batch.BasicBatchConfigurer;
//import org.springframework.core.task.SimpleAsyncTaskExecutor;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//public class JobLauncherController {
//
//	private final Job job;
//	private final JobLauncher jobLauncher;
//
//	private final BasicBatchConfigurer basicBatchConfigurer;
//
//	@PostMapping("/batch")
//	public String launch(@RequestBody Member member)
//		throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
//
//		JobParameters jobParameters = new JobParametersBuilder()
//			.addString("id", member.getId())
//			.addDate("date", new Date())
//			.toJobParameters();
//
//		// 비동기적 실행: JobLauncher 실행 후 클라이언트에게 즉시 JobExecution 반환 (실제로는 배치 진행중)
//		SimpleJobLauncher jobLauncher = (SimpleJobLauncher) basicBatchConfigurer.getJobLauncher();
//		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
//
//		// jobLauncher 수동 실행을 위해서는 aplication 설정에서 job 자동실행을 꺼야함
//		jobLauncher.run(job, jobParameters);
//
//		return "batch completed";
//	}
//
//}

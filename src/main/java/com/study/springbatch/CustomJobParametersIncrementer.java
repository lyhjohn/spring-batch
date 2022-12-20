package com.study.springbatch;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

public class CustomJobParametersIncrementer implements JobParametersIncrementer {

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-hhmmss");

	@Override
	public JobParameters getNext(JobParameters jobParameters) {
		String id = format.format(new Date());
		return new JobParametersBuilder().addString("rud.id", id).toJobParameters();
	}
}

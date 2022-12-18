package com.study.springbatch;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class CustomJobParameterValidator implements JobParametersValidator {

	/**
	 * @param jobParameters = 설정 시 저장한 JobParameters 값이 매개변수로 전달됨
	 */
	@Override
	public void validate(JobParameters jobParameters) throws JobParametersInvalidException {
		if (jobParameters.getString("name") == null) {
			throw new JobParametersInvalidException("name parameters is not found");
		}
	}
}

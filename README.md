## 스프링 배치
<hr>

### 핵심 패턴
Read: 데이터를 읽고 (db extract)
Process 데이터를 가공하고 (db transport)
Write: 데이터를 저장한다 (db load)

### 발생할 수 있는 시나리오
- 적은 자원을 가지고 배치 프로세스를 주기적으로 커밋
- 동시 다발적인 Job의 배치 처리, 대용량 멀티스레드의 경우 병렬 처리
- 의존관계로 얽힌 여러 step을 순차적으로 처리
- 조건적 Flow 구성을 통해 체계적이고 유연한 배치 모델 구성
- 반복, 재시도, 예외에 대해 skip 처리

<img src="https://user-images.githubusercontent.com/104713339/205638093-c35a558f-7334-4724-b05b-ea2d99df3854.png">


### job / step / tasklet 의미
job: 일감
step: 일의 단계
tasklet: 작업 내용

### 배치 코드 실행 순서
job 실행 -> step 실행 -> tasklet 실행 (job 안에 step 안에 tasklet이 있음)


### 스키마
외부라이브러리/org.springframework.batch:spring-batch-core/spring-batch-core.jar 경로에서 각각의 DB 스키마와 테이블 DROP 스키마를 볼 수 있다.

<image src="https://user-images.githubusercontent.com/104713339/205916979-e7f909e2-5dee-499d-a9c2-a4fd69a9c08a.png">


### 스키마 생성방법 1. 수동 생성
위 스키마 파일에 있는 DDL을 복사하여 콘솔에서 직접 실행해 테이블을 만들 수 있다.

### 스키마 생성방법 2. 자동 생성
<details>
<summary>application.yml 설정 방법</summary>

```yml
spring:
  profiles:
    active: local
# db 실행 1순위: edit configurations - active profiles 란에 mysql or local 등 작성한 환경으로 실행된다.
# edit configurations 에 명시하지 않을 시 이 곳에서 mysql or local 중 작성한 환경으로 실행된다. (local = h2)

# mysql or h2 등 여러 DB를 설정하고자 하면 구분선을 이용해 여러개의 profile을 설정한다.
# profile 에 따른 db 선택은 위에서 명시한 방법으로 할 수 있다.
---
spring:
  config:
    activate:
      on-profile: local
  datasource:
    hikari:
      jdbc-url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
      username: sa
      password:
      driver-class-name: org.h2.Driver
  batch:
    jdbc:
      initialize-schema: embedded
---
spring:
  config:
    activate:
      on-profile: mysql
  datasource:
    hikari:
      jdbc-url: jdbc:mysql://localhost:3306/spring_batch?useUnicode=true&characterEncoding=utf8
      username: study
      password: yohan
      driver-class-name: com.mysql.jdbc.Driver
  batch:
    jdbc:
      initialize-schema: always # mysql 스키마 항상 생성
```
</details>

### DB 스키마의 테이블 종류
<image src="https://user-images.githubusercontent.com/104713339/206190578-76c3eabf-4966-49e2-af24-b37fe3fff8e2.png">

각 테이블은 "일대다" 관계로 이루어져있다.


### Job 관련 테이블
> BATCH_JOB_INSTANCE
- Job이 실행될 때 JobInstance 정보가 저장되며 job_name과 job_key를 키로 하여 하나의 데이터가 저장된다.
- job_name과 JobParameter가 동시에 중복되는 값으로는 저장되지 않는다.
  - (Job_Key: JobParameter를 직렬화 한 값)

> BATCH_JOB_EXECUTION
- job 실행정보가 저장되며 job 생성/시작/종료시간, 실행상태, 메시지 등을 관리한다.

> BATCH_JOB-EXECUTION-PARAMS
- Job과 함께 실행되는 JobParameter 정보를 저장한다.

> BATCH_JOB_EXECUTION_CONTEXT
- Job의 실행 동안 상태정보, 공유데이터를 직렬화(serialized)와 Json 형식을 사용해 저장한다.
- Step 간 서로 공유 가능하다.

### Step 관련 테이블
> BATCH_STEP_EXECUTION
- Step의 실행정보가 저장되며 생성/시작/종료시간, 실행상태, 메시지 등을 관리한다.

> BATCH_STEP_EXECUTION_CONTEXT
- Job의 실행 동안 상태정보, 공유데이터를 직렬화(serialized)와 Json 형식을 사용해 저장한다.
- Step 별로 저장되며 BATCH_JOB_EXECUTION_CONTEXT와 다르게 Step 간 서로 공유할 수 없다.

### Job
- 하나의 배치작업 자체를 의미. 스프링 배치의 최상위 인터페이스.
- Job Configuration을 통해 생성되는 객체 단위로써, 배치 작업을 어떻게 구성하고 실행할 것인지 명세해 놓은 객체.
- 여러 Step을 포함하고 있는 컨테이너로써 반드시 한개 이상의 Step으로 구성되어야 한다.
- 기본 구현체로 SimpleJob, FlowJob을 제공함.

1. SimpleJob
    - 순자적으로 Step을 실행시키고 보관하는 컨테이너.
2. FlowJob
    - 특정한 조건과 흐름에 따라 Flow 객체를 실행시키는 방식으로 Step을 구성하고 작업을 진행함.


### JobParameter
- JobParameter를 직렬화 한 값이 JobInstance에서 JOB_KEY 이다.
- JobParameter와 JobName이 모두 동일하게는 Job을 생성할 수 없다.
- Job을 실행할 때 함께 포함되어 사용되는 파라미터를 가진 도메인 객체
- JOB_EXECUTION과 BATCH_JOB_EXECUTION_PARAM은 1:N 관계다.

- JobParameter 생성 방법
  - 어플리케이션 실행 시 주입: target 폴더로 이동 후 jar파일이 있는 상태에서 아래 명령어 입력(따옴표 필수)
  
코드로 생성한 클래스가 있다면 yml 파일에 job: enabled: false 설정이 있어서는 안됨. 
코드로 생성한 클래스는 Component 를 지워서 빈 등록 해제해야한다.
```java
java -jar spring-batch-0.0.1-SNAPSHOT.jar 'name=user1' 'seq(long)=2L' 'date(date)=2022/12/11' 'age(double)=16.5'
```
  
혹은 Edit configuration - Program arguments에 아래 명령어 입력
    
```java
name=user1 seq(long)=2L date(date)=2022/12/11 age(double)=16.5
```

  - 코드로 생성
    - ApplicationRunner 를 구현한 클래스 생성 후 JobLauncher와 Job을 주입한다.
    - run 메서드 내부에 JobParameters 생성 후 jobLauncher.run 한다.
  <details>
<summary>코드로 생성 방법 보기</summary>
    
```java
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
```
    
  </details>


### JobExecution
- JobInstance 에 대한 한번의 시도를 의미하는 객체로써 Job 실행 중 발생한 정보들을 저장하고 있음
  - 시작시간, 종료시간, 상태(시작됨, 완료, 실패), 종료상태의 속성을 가짐
- JobExecution 상태가 COMPLETED 면 JobInstance 실행이 완료된 것으로 간주, 재실행 불가
- JobExecution 상태가 FAILED 이면 JobInstance 실행이 실패한 것으로 간주, 재실행 가능
  - 테이블은과 ROW(객체)는 정상적으로 생성 되나 STATUS가 FAILED 임. 계속 실패하면 객체도 계속 생성됨(1:N관계)
  - 이 경우 동일한 JobParameter 값으로 성공할 때까지 JobInstance를 계속 실행할 수 있음
- JobInstance 와 JobExecution 은 1:N 관계.

### STEP
- Batch job을 구성하는 독립적인 하나의 단계.
- 복잡한 비즈니스 로직을 포함하여 모든 설정들을 담고 있음
- 배치작업을 어떻게 구성하고 실행할 것인지 명세해 놓은 객체
- 모든 Job은 하나 이상의 Step으로 구성됨

### STEP 기본 구현체
- TaskletStep
  - 가장 기본이 되는 클래스로서 Tasklet 타입의 구현체들을 제어한다.
- PartitionStep
  - 멀티 스레드 방식으로 Step을 여러개로 분리해서 실행한다.
- JobStep
  - Step 내에서 Job을 실행하도록 한다. (Job-Step-Job-Step... 으로 체인 구성)
- FlowStep
  - Step 내에서 FLow를 실행하도록 한다.

### StepExecution
- Step에 대한 한번의 시도를 의미하는 객체. Step 실행 중 발생한 정보를 저장하고 있음
  - 시작시간, 종료시간, 상태(시작됨, 완료, 실패) 등 속성 가짐
- 매번 시도할 때마다 각 Step이 생성됨 (서로 공유 X)
- 이전 단계 Step이 실패해서 현재 Step을 실행하지 않았다면 이전 단계 Step만 FAILED 상태로 생성되고 현재 StepExecution은 생성되지 않는다.
    - 성공이든 실패든 실행된 Step만 StepExecution을 생성한다.
- Job이 재시작 하더라도 이미 완료된 Step은 재실행 되지 않고 실패했던 Step만 실행됨 (주의할건 Job은 실행)
- StepExecution이 모두 정상적으로 완료되어야 JobExecution이 완료된다.
- StepExecution 중 하나라도 실패하면 JobExecution은 실패한다.
- JobExecution과 StepExecution은 1:N 관계 (여러개의 StepExecution은 하나의 JobExecution을 부모로 갖는다.)
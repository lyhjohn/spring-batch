## 스프링 배치

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

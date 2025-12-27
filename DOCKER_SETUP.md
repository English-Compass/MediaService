# 🐳 Docker 환경 설정 가이드

## 📋 개요
Media Service를 Docker 환경에서 실행하기 위한 설정 가이드입니다.

## 🚀 빠른 시작

### 1. 환경 변수 설정
```bash
# env.example을 복사하여 .env 파일 생성
cp env.example .env

# .env 파일을 편집하여 실제 값으로 변경
nano .env
```

### 2. Docker 컨테이너 실행
```bash
# 모든 서비스 시작 (MySQL, Redis, Kafka, Zookeeper, App)
docker-compose up -d

# 로그 확인
docker-compose logs -f app
```

### 3. 서비스 상태 확인
```bash
# 컨테이너 상태 확인
docker-compose ps

# 헬스 체크
curl http://localhost:8080/actuator/health
```

## 🔧 상세 설정

### 환경 변수 설명
| 변수명 | 설명 | 예시 값 |
|--------|------|---------|
| `DB_NAME` | MySQL 데이터베이스 이름 | `media_service_db` |
| `DB_USERNAME` | MySQL 사용자명 | `media_user` |
| `DB_PASSWORD` | MySQL 패스워드 | `your_secure_password` |
| `DB_ROOT_PASSWORD` | MySQL root 패스워드 | `your_secure_root_password` |
| `GEMINI_API_KEY` | Gemini API 키 | `your_gemini_api_key` |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka 서버 주소 | `kafka:9092` |

### 포트 매핑
| 서비스 | 컨테이너 포트 | 호스트 포트 | 접근 URL |
|--------|---------------|-------------|----------|
| Media Service | 8080 | 8080 | http://localhost:8080 |
| MySQL | 3306 | 3307 | localhost:3307 |
| Redis | 6379 | 6380 | localhost:6380 |
| Kafka | 9092 | 29093 | localhost:29093 |
| Zookeeper | 2181 | 2182 | localhost:2182 |
| Nginx | 80 | 8081 | http://localhost:8081 |

## 🗄️ 데이터베이스 초기화

### 자동 초기화
Docker 컨테이너 시작 시 다음 스크립트가 자동으로 실행됩니다:
- `init-scripts/01-create-database.sql`: 참조 테이블 생성
- `init-scripts/02-create-views.sql`: 분석 뷰 생성
- `init-scripts/03-insert-mock-data.sql`: 샘플 데이터 삽입

### 수동 데이터베이스 접근
```bash
# MySQL 컨테이너에 접속
docker exec -it middle-point-mysql mysql -u root -p

# 또는 호스트에서 직접 접속 (포트 3307 사용)
mysql -h localhost -P 3307 -u root -p

# 데이터베이스 선택
USE media_service_db;

# 테이블 확인
SHOW TABLES;
```

## 🔍 문제 해결

### 일반적인 문제들

#### 1. 포트 충돌
```bash
# 포트 사용 중인 프로세스 확인
lsof -i :8080
lsof -i :3306

# 프로세스 종료
kill -9 <PID>
```

#### 2. 컨테이너 시작 실패
```bash
# 컨테이너 로그 확인
docker-compose logs app
docker-compose logs db

# 컨테이너 재시작
docker-compose restart app
```

#### 3. 데이터베이스 연결 실패
```bash
# MySQL 컨테이너 상태 확인
docker-compose ps db

# MySQL 로그 확인
docker-compose logs db

# 네트워크 연결 테스트
docker exec -it middle-point-app ping db
```

### 로그 확인
```bash
# 특정 서비스 로그
docker-compose logs -f app
docker-compose logs -f db
docker-compose logs -f kafka

# 모든 서비스 로그
docker-compose logs -f
```

## 🛠️ 개발 모드

### 로컬 개발 (H2 데이터베이스)
```bash
# 로컬 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 도커 개발 모드
```bash
# 개발용 환경 변수 설정
export SPRING_PROFILES_ACTIVE=docker
export DB_PASSWORD=dev_password
export GEMINI_API_KEY=mock-api-key

# 컨테이너 실행
docker-compose up -d
```

## 📊 모니터링

### 헬스 체크 엔드포인트
- **애플리케이션**: http://localhost:8080/actuator/health
- **메트릭**: http://localhost:8080/actuator/metrics
- **정보**: http://localhost:8080/actuator/info

### API 테스트
```bash
# 헬스 체크
curl http://localhost:8080/actuator/health

# 사용 가능한 장르 조회
curl http://localhost:8080/api/recommendations/genres

# 사용자 추천 요청 (POST)
curl -X POST http://localhost:8080/api/recommendations/user-requested \
  -H "Content-Type: application/json" \
  -d '{"userId": "user123", "selectedGenres": ["EDUCATION", "ENTERTAINMENT"]}'
```

## 🧹 정리

### 컨테이너 정지 및 제거
```bash
# 모든 컨테이너 정지
docker-compose down

# 볼륨까지 제거 (데이터 삭제)
docker-compose down -v

# 이미지까지 제거
docker-compose down --rmi all
```

### 데이터 백업
```bash
# MySQL 데이터 백업
docker exec middle-point-mysql mysqldump -u root -p media_service_db > backup.sql

# 데이터 복원
docker exec -i middle-point-mysql mysql -u root -p media_service_db < backup.sql
```

## 📚 추가 리소스
- [Docker Compose 공식 문서](https://docs.docker.com/compose/)
- [Spring Boot Docker 가이드](https://spring.io/guides/gs/spring-boot-docker/)
- [MySQL Docker 이미지](https://hub.docker.com/_/mysql)

# Media Recommendation Service API 명세서

## 📋 개요

Media Recommendation Service는 AI 기반 언어 학습 플랫폼 "English Compass"를 위한 미디어 추천 서비스입니다. 사용자의 학습 성과와 선호 장르를 분석하여 맞춤형 미디어 콘텐츠를 추천합니다.

## 🏗️ 아키텍처

- **Framework**: Spring Boot 3.x
- **Database**: H2 (개발), MySQL (운영)
- **Message Queue**: Apache Kafka
- **AI Service**: Gemini API (학습 분석) + Perplexity API (실시간 웹 검색)
- **Port**: 8084 (Docker 환경)

### 🐳 Docker 환경

Media Service는 Docker 컨테이너 환경에서 실행됩니다:

- **Media Service**: `http://localhost:8084`
- **MySQL**: `localhost:3307`
- **Redis**: `localhost:6380`
- **Kafka**: `localhost:29093`
- **Zookeeper**: `localhost:2182`

Docker 환경 설정은 `docker-compose.yml`과 `Dockerfile`을 참조하세요.

## 🔗 Base URL

```
http://localhost:8084
```

---

## 📚 API 엔드포인트

### 1. 사용자 요청 기반 미디어 추천 생성

사용자가 선택한 장르와 학습 성과를 기반으로 맞춤형 미디어 추천을 생성합니다.

#### **POST** `/api/recommendations/user-requested`

**Request Body:**
```json
{
  "userId": "user123",
  "selectedGenres": ["액션", "스릴러", "SF", "판타지"]
}
```

**Request Schema:**
| 필드 | 타입 | 필수 | 설명 | 제약조건 |
|------|------|------|------|----------|
| `userId` | String | ✅ | 사용자 ID | 문자열 형식 |
| `selectedGenres` | String[] | ✅ | 선택된 장르 목록 | 1-5개, 유효한 장르명 |

**Response (Success - 200):**
```json
{
  "status": "SUCCESS",
  "message": "사용자 요청 기반 추천이 성공적으로 생성되었습니다.",
  "totalRecommendations": 5,
  "selectedGenres": ["액션", "코미디"],
  "generatedAt": [2025,9,9,6,12,21,690718384],
  "recommendations": [
    {
      "id": 1,
      "recommendationId": "REC_test-user-007_USER_REQUESTED_99d77846",
      "userId": "test-user-007",
      "title": "Live Friday Quiz #57 - Comedy",
      "description": "BBC Learning English의 라이브 퀴즈 영상으로, 코미디 관련 영어 관용구, 어휘, 문법을 배우며 초중급 학습자에게 적합한 콘텐츠입니다.",
      "url": "https://www.youtube.com/watch?v=0ozbRddLnnQ",
      "thumbnailUrl": null,
      "playUrl": null,
      "mediaType": "YOUTUBE_VIDEO",
      "recommendationType": "USER_REQUESTED",
      "sessionId": null,
      "platform": "YouTube",
      "difficultyLevel": "Beginner to Intermediate",
      "recommendationReason": "코미디 주제로 영어 어휘와 표현을 재미있게 학습할 수 있으며, 50분 이하로 집중 학습에 적합합니다.",
      "estimatedDuration": 30,
      "language": null,
      "category": null,
      "videoId": null,
      "channelName": null,
      "viewCount": null,
      "publishedAt": null,
      "generatedAt": [2025,9,9,6,12,21,524521301],
      "createdAt": [2025,9,9,6,12,21,526109134],
      "updatedAt": [2025,9,9,6,12,21,526109134]
    }
  ]
}
```

**Response (Error - 400):**
```json
{
  "status": "ERROR",
  "message": "최소 1개 이상의 장르를 선택해야 합니다.",
  "totalRecommendations": 0,
  "selectedGenres": null,
  "generatedAt": [2025,9,8,17,4,44,3450505],
  "recommendations": null
}
```

**Response (Error - 500):**
```json
{
  "status": "ERROR",
  "message": "추천 생성 중 오류가 발생했습니다: [오류 상세 내용]",
  "totalRecommendations": 0,
  "selectedGenres": null,
  "generatedAt": [2025,9,8,17,4,44,3450505],
  "recommendations": null
}
```

---

### 2. 사용 가능한 장르 목록 조회

추천에 사용할 수 있는 장르 목록을 조회합니다.

#### **GET** `/api/recommendations/genres`

**Response (200):**
```json
{
  "genres": [
    "액션", "드라마", "코미디", "로맨스", "스릴러",
    "공포", "미스터리", "SF", "판타지", "범죄",
    "전쟁", "음악", "애니메이션", "다큐멘터리"
  ],
  "message": "사용 가능한 장르 목록입니다.",
  "totalCount": 14
}
```

---

### 3. 사용자 추천 히스토리 조회

특정 사용자의 사용자 요청 기반 추천 히스토리를 조회합니다.

#### **GET** `/api/recommendations/user-requested/{userId}`

**Path Parameters:**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `userId` | String | ✅ | 사용자 ID |

**Response (200):**
```json
{
  "status": "SUCCESS",
  "message": "사용자 요청 기반 추천 히스토리 조회 완료",
  "totalRecommendations": 15,
  "selectedGenres": null,
  "generatedAt": [2025,9,8,17,12,30,172967000],
  "recommendations": [
    {
      "id": 16,
      "recommendationId": "REC_user123_USER_REQUESTED_57579d43",
      "userId": "user123",
      "title": "The Big Bang Theory (Season 1, Episode 1)",
      "description": "인기 시트콤으로, 일상적인 대화와 유머를 통해 자연스러운 영어 표현을 배울 수 있습니다.",
      "url": "N/A",
      "thumbnailUrl": null,
      "playUrl": null,
      "mediaType": "DRAMA",
      "recommendationType": "USER_REQUESTED",
      "sessionId": null,
      "platform": "Netflix",
      "difficultyLevel": "Intermediate",
      "recommendationReason": "일상적인 영어 표현과 빠른 대화 속도를 통해 듣기 능력 향상에 도움이 됩니다.",
      "estimatedDuration": 22,
      "language": null,
      "category": null,
      "videoId": null,
      "channelName": null,
      "viewCount": null,
      "publishedAt": null,
      "generatedAt": [2025,9,8,17,12,30,172930000],
      "createdAt": [2025,9,8,17,12,30,173582000],
      "updatedAt": [2025,9,8,17,12,30,173582000]
    }
  ]
}
```

---

### 4. 전체 추천 히스토리 조회

특정 사용자의 모든 추천 히스토리(실시간 추천 + 사용자 요청 추천)를 조회합니다.

#### **GET** `/api/recommendations/history/{userId}`

**Path Parameters:**
| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| `userId` | String | ✅ | 사용자 ID |

**Response (200):**
```json
{
  "status": "SUCCESS",
  "message": "전체 추천 히스토리 조회 완료",
  "totalRecommendations": 10,
  "selectedGenres": null,
  "generatedAt": [2025,9,9,6,30,1,550667541],
  "recommendations": [
    {
      "id": 1,
      "recommendationId": "REC_test-user-015_USER_REQUESTED_fcf5d8fd",
      "userId": "test-user-015",
      "title": "The White Elephant comedy drama episodes 6-10",
      "description": "BBC Learning English의 코미디 드라마 시리즈로, 친절, 늦음, 행운, 비밀, 화남 등 다양한 주제를 다루며 중급 학습자에게 적합한 어휘와 표현을 제공합니다.",
      "url": "https://www.youtube.com/watch?v=aZnISm8oO_g",
      "mediaType": "YOUTUBE_VIDEO",
      "recommendationType": "USER_REQUESTED",
      "platform": "YouTube",
      "difficultyLevel": "Intermediate",
      "recommendationReason": "코미디 드라마 형식으로 재미있게 영어 듣기와 어휘를 학습할 수 있으며, 50분 이하로 구성되어 있습니다.",
      "estimatedDuration": 20
    }
  ]
}
```

---

### 5. 헬스 체크

서비스 상태를 확인합니다.

#### **GET** `/actuator/health`

**Response (200):**
```json
{
  "status": "UP"
}
```

---

## 🎭 장르 목록

| 장르 | 영어명 | 설명 |
|------|--------|------|
| 액션 | Action | 액션 영화/드라마 |
| 드라마 | Drama | 드라마 영화/드라마 |
| 코미디 | Comedy | 코미디 영화/드라마 |
| 로맨스 | Romance | 로맨스 영화/드라마 |
| 스릴러 | Thriller | 스릴러 영화/드라마 |
| 공포 | Horror | 공포 영화/드라마 |
| 미스터리 | Mystery | 미스터리 영화/드라마 |
| SF | Science Fiction | SF 영화/드라마 |
| 판타지 | Fantasy | 판타지 영화/드라마 |
| 범죄 | Crime | 범죄 영화/드라마 |
| 전쟁 | War | 전쟁 영화/드라마 |
| 음악 | Music | 음악 관련 콘텐츠 |
| 애니메이션 | Animation | 애니메이션 영화/드라마 |
| 다큐멘터리 | Documentary | 다큐멘터리 영화/드라마 |

---

## 📊 데이터 모델

### UserRecommendationRequest
```json
{
  "userId": "user123",
  "selectedGenres": ["코미디", "드라마"]
}
```

### UserRecommendationResponse
```json
{
  "status": "SUCCESS|ERROR",
  "message": "응답 메시지",
  "totalRecommendations": 7,
  "selectedGenres": ["코미디", "드라마"],
  "generatedAt": [2025,9,8,17,4,44,3450505],
  "recommendations": [
    {
      "id": 1,
      "recommendationId": "REC_user123_USER_REQUESTED_1b5ab49d",
      "userId": "user123",
      "title": "Live Friday Quiz #57 - Comedy",
      "description": "BBC Learning English의 라이브 퀴즈 영상으로, 코미디 관련 영어 관용구, 어휘, 문법을 배우며 초중급 학습자에게 적합한 콘텐츠입니다.",
      "url": "https://www.youtube.com/watch?v=0ozbRddLnnQ",
      "thumbnailUrl": null,
      "playUrl": null,
      "mediaType": "YOUTUBE_VIDEO",
      "recommendationType": "USER_REQUESTED",
      "sessionId": null,
      "platform": "YouTube",
      "difficultyLevel": "Beginner to Intermediate",
      "recommendationReason": "코미디 주제로 영어 어휘와 표현을 재미있게 학습할 수 있으며, 50분 이하로 집중 학습에 적합합니다.",
      "estimatedDuration": 30,
      "language": null,
      "category": null,
      "videoId": null,
      "channelName": null,
      "viewCount": null,
      "publishedAt": null,
      "generatedAt": [2025,9,8,17,4,43,835925214],
      "createdAt": [2025,9,8,17,4,43,836829172],
      "updatedAt": [2025,9,8,17,4,43,836829172]
    }
  ]
}
```

---

## 🔄 추천 로직

### 1. 실시간 추천 (학습 후 추천)
- **트리거**: 학습 세션 완료 시 Kafka 이벤트 수신
- **AI 분석**: Gemini API가 학습 세션 결과를 분석하여 검색 프롬프트 생성
- **웹 검색**: Perplexity API가 Gemini의 프롬프트로 실제 YouTube 영상 검색
- **추천 타입**: 짧은 유튜브 동영상 위주 (0-3분, 2-3개)
- **목적**: 방금 틀린 문제의 약점 보완
- **난이도 고려**: 학습 세션의 난이도 정보를 활용하여 적절한 난이도의 콘텐츠 추천

### 2. 사용자 요청 추천
- **트리거**: 사용자가 미디어 추천 페이지에서 장르 선택 후 요청
- **AI 분석**: Gemini API가 사용자 성과를 분석하여 검색 프롬프트 생성
- **웹 검색**: Perplexity API가 Gemini의 프롬프트로 다양한 미디어 콘텐츠 검색
- **추천 타입**: 다양한 미디어 (유튜브 2개, 영화 1개, 드라마 1개, 오디오북 1개)
- **목적**: 전반적인 학습 성과 향상
- **난이도 고려**: 난이도 제한 없이 장르와 성능 기반으로만 추천

---

## 🤖 AI 서비스 구조

### Gemini API (학습 분석)
- **역할**: 학습 세션 결과 및 사용자 성과 분석
- **입력**: 
  - 실시간 추천: `LearningCompletedEvent` (세션 문제 상세 정보)
  - 사용자 요청: `UserPerformanceSummary` (성과 데이터) + 선택된 장르
- **출력**: Perplexity API용 검색 프롬프트
- **예시**: "영어 문법 기초 학습 유튜브 영상", "영어 액션 영화 학습 유튜브 영상"

### Perplexity API (실시간 웹 검색)
- **역할**: 실제 존재하는 미디어 콘텐츠 검색 및 추천
- **입력**: Gemini가 생성한 검색 프롬프트
- **출력**: 실제 재생 가능한 미디어 콘텐츠 목록
- **특징**: 
  - 실시간 웹 검색으로 최신 콘텐츠 검색
  - 실제 존재하고 재생 가능한 YouTube 영상만 추천
  - BBC Learning English, EnglishClass101 등 신뢰할 수 있는 채널 우선

---

## 🎯 난이도 필드 사용 정책

### 실시간 추천 (학습 세션 완료 후)
- **난이도 고려**: ✅ **활용**
- **근거**: 학습 세션에서 이미 난이도 정보가 있으므로 이를 토대로 적절한 난이도의 YouTube 영상 추천
- **예시**: 세션에서 중급 난이도 문제를 틀렸다면 중급 수준의 보완 콘텐츠 추천

### 사용자 요청 추천 (미디어 추천 페이지)
- **난이도 고려**: ❌ **미고려**
- **근거**: 사용자가 장르를 선택했을 때는 난이도를 고려하지 않고 장르와 성능 기반으로만 추천
- **예시**: 액션 장르를 선택했다면 난이도에 관계없이 액션 관련 콘텐츠 추천

---

## 📈 성과 분석 데이터

### 카테고리별 성과
- **데이터 소스**: `category_performance_view`
- **계산 방식**: `(정답 수 / 총 문제 수) * 100`
- **그룹핑**: `major_category + "-" + minor_category`
- **예시**: `{"여행-가족": 80.0, "비즈니스-회의": 65.0}`

### 난이도별 성과
- **데이터 소스**: `difficulty_achievement_view`
- **계산 방식**: `(정답 수 / 총 문제 수) * 100`
- **그룹핑**: `difficulty_level` (1: 초급, 2: 중급, 3: 고급)
- **예시**: `{1: 90.0, 2: 75.0, 3: 45.0}`

---

## ⚠️ 오류 코드

| HTTP Status | 오류 유형 | 설명 |
|-------------|-----------|------|
| 400 | Bad Request | 잘못된 요청 데이터 (유효성 검증 실패) |
| 500 | Internal Server Error | 서버 내부 오류 (DB 연결 실패, AI API 오류 등) |

---



## 📝 사용 예시

### 1. 장르 목록 조회
```bash
curl -X GET http://localhost:8084/api/recommendations/genres
```

**응답 예시:**
```json
{
  "genres": ["액션", "드라마", "코미디", "로맨스", "스릴러", "공포", "미스터리", "SF", "판타지", "범죄", "전쟁", "음악", "애니메이션", "다큐멘터리"],
  "message": "사용 가능한 장르 목록입니다.",
  "totalCount": 14
}
```

### 2. 사용자 요청 기반 추천 생성
```bash
curl -X POST http://localhost:8084/api/recommendations/user-requested \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "test-user-007",
    "selectedGenres": ["액션", "코미디"]
  }'
```

**응답 예시:**
```json
{
  "status": "SUCCESS",
  "message": "사용자 요청 기반 추천이 성공적으로 생성되었습니다.",
  "totalRecommendations": 5,
  "selectedGenres": ["액션", "코미디"],
  "generatedAt": [2025,9,9,6,12,21,690718384],
  "recommendations": [
    {
      "id": 1,
      "recommendationId": "REC_test-user-007_USER_REQUESTED_99d77846",
      "userId": "test-user-007",
      "title": "Live Friday Quiz #57 - Comedy",
      "description": "BBC Learning English의 라이브 퀴즈 영상으로, 코미디 관련 영어 관용구, 어휘, 문법을 배우며 초중급 학습자에게 적합한 콘텐츠입니다.",
      "url": "https://www.youtube.com/watch?v=0ozbRddLnnQ",
      "thumbnailUrl": null,
      "playUrl": null,
      "mediaType": "YOUTUBE_VIDEO",
      "recommendationType": "USER_REQUESTED",
      "sessionId": null,
      "platform": "YouTube",
      "difficultyLevel": "Beginner to Intermediate",
      "recommendationReason": "코미디 주제로 영어 어휘와 표현을 재미있게 학습할 수 있으며, 50분 이하로 집중 학습에 적합합니다.",
      "estimatedDuration": 30,
      "language": null,
      "category": null,
      "videoId": null,
      "channelName": null,
      "viewCount": null,
      "publishedAt": null,
      "generatedAt": [2025,9,9,6,12,21,524521301],
      "createdAt": [2025,9,9,6,12,21,526109134],
      "updatedAt": [2025,9,9,6,12,21,526109134]
    }
  ]
}
```

### 3. 사용자 요청 추천 히스토리 조회
```bash
curl -X GET http://localhost:8084/api/recommendations/user-requested/user123
```

### 4. 전체 추천 히스토리 조회
```bash
curl -X GET http://localhost:8084/api/recommendations/history/user123
```

**응답 예시:**
```json
{
  "status": "SUCCESS",
  "message": "사용자 요청 기반 추천 히스토리 조회 완료",
  "totalRecommendations": 15,
  "selectedGenres": null,
  "generatedAt": [2025,9,8,17,12,30,172967000],
  "recommendations": [
    {
      "id": 16,
      "recommendationId": "REC_user123_USER_REQUESTED_57579d43",
      "userId": "user123",
      "title": "The Big Bang Theory (Season 1, Episode 1)",
      "description": "인기 시트콤으로, 일상적인 대화와 유머를 통해 자연스러운 영어 표현을 배울 수 있습니다.",
      "url": "N/A",
      "thumbnailUrl": null,
      "playUrl": null,
      "mediaType": "DRAMA",
      "recommendationType": "USER_REQUESTED",
      "sessionId": null,
      "platform": "Netflix",
      "difficultyLevel": "Intermediate",
      "recommendationReason": "일상적인 영어 표현과 빠른 대화 속도를 통해 듣기 능력 향상에 도움이 됩니다.",
      "estimatedDuration": 22,
      "language": null,
      "category": null,
      "videoId": null,
      "channelName": null,
      "viewCount": null,
      "publishedAt": null,
      "generatedAt": [2025,9,8,17,12,30,172930000],
      "createdAt": [2025,9,8,17,12,30,173582000],
      "updatedAt": [2025,9,8,17,12,30,173582000]
    }
  ]
}
```

### 5. 실시간 추천 테스트 (학습 세션 완료 후)
```bash
# 1. Kafka 이벤트 발행 (학습 세션 완료 시뮬레이션)
echo '{"userId":"test-user-016","sessionId":"session-001","totalQuestions":5,"correctAnswers":3,"accuracyRate":60.0,"totalLearningTimeMinutes":15,"sessionQuestions":[{"questionText":"What is your name?","userAnswer":"My name is John","correctAnswer":"My name is John","majorCategory":"일상생활","minorCategory":"인사","difficultyLevel":1}]}' | docker exec -i middle-point-kafka kafka-console-producer --bootstrap-server localhost:9092 --topic learning-events

# 2. 추천 결과 확인
curl -X GET http://localhost:8084/api/recommendations/history/test-user-016
```

**응답 예시:**
```json
{
  "status": "SUCCESS",
  "message": "전체 추천 히스토리 조회 완료",
  "totalRecommendations": 3,
  "selectedGenres": null,
  "generatedAt": [2025,9,9,8,11,22,87269847],
  "recommendations": [
    {
      "id": 28,
      "recommendationId": "REC_test-user-016_REAL_TIME_SESSION_d926f947",
      "userId": "test-user-016",
      "title": "English in 3 minutes (Intermediate / B1/B2) - Grammar - YouTube",
      "description": "중급 학습자를 위한 3분 내외 문법 강의로, 영어 시제 이름과 사용법을 예시와 함께 연습할 수 있습니다.",
      "url": "https://www.youtube.com/watch?v=fxyiSJFjef0",
      "mediaType": "YOUTUBE_VIDEO",
      "recommendationType": "REAL_TIME_SESSION",
      "sessionId": "session-001",
      "platform": "YouTube",
      "difficultyLevel": "Intermediate",
      "recommendationReason": "시제 이해와 정확한 사용을 돕는 짧고 핵심적인 문법 영상입니다.",
      "estimatedDuration": 3
    }
  ]
}
```

### 6. 헬스 체크
```bash
curl -X GET http://localhost:8084/actuator/health
```

**응답 예시:**
```json
{
  "status": "UP"
}
```

---



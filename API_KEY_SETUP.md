# 🔐 API 키 설정 가이드

## 📋 **설정 방법**

### **방법 1: 환경 변수 설정 (권장)**

#### **macOS/Linux**
```bash
# 터미널에서 설정 (재시작 시 사라짐)
export GEMINI_API_KEY="your-actual-gemini-api-key-here"

# 영구 설정 (zsh 사용 시)
echo 'export GEMINI_API_KEY="your-actual-gemini-api-key-here"' >> ~/.zshrc
source ~/.zshrc

# bash 사용 시
echo 'export GEMINI_API_KEY="your-actual-gemini-api-key-here"' >> ~/.bashrc
source ~/.bashrc
```

#### **Windows**
```cmd
# 명령 프롬프트에서 설정 (재시작 시 사라짐)
set GEMINI_API_KEY=your-actual-gemini-api-key-here

# PowerShell에서 설정
$env:GEMINI_API_KEY="your-actual-gemini-api-key-here"
```

### **방법 2: application-local.yml 파일 사용**

1. `src/main/resources/application-local.yml` 파일을 열기
2. `your-actual-gemini-api-key-here` 부분을 실제 API 키로 교체
3. 애플리케이션 실행 시 `--spring.profiles.active=local` 옵션 추가

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### **방법 3: IntelliJ IDEA에서 환경 변수 설정**

1. Run/Debug Configurations 열기
2. Environment variables 섹션에서 추가:
   - `GEMINI_API_KEY` = `your-actual-gemini-api-key-here`

## 🚨 **보안 주의사항**

### **❌ 절대 하지 말아야 할 것들**
- API 키를 `application.yml`에 직접 입력
- API 키를 Git에 커밋
- API 키를 코드에 하드코딩
- API 키를 공개 저장소에 업로드

### **✅ 안전한 방법들**
- 환경 변수 사용
- `.gitignore`에 포함된 설정 파일 사용
- CI/CD 환경에서만 사용하는 시크릿 관리
- API 키를 정기적으로 로테이션

## 🔍 **API 키 확인 방법**

### **Gemini API 키 발급**
1. [Google AI Studio](https://makersuite.google.com/app/apikey) 접속
2. Google 계정으로 로그인
3. "Create API Key" 클릭
4. 생성된 API 키 복사

### **API 키 테스트**
```bash
# 환경 변수 확인
echo $GEMINI_API_KEY

# 애플리케이션 실행 시 로그에서 확인
./gradlew bootRun
```

## 📁 **파일 구조**
```
MediaService/
├── .gitignore                    # Git에서 제외할 파일들
├── src/main/resources/
│   ├── application.yml           # 기본 설정 (API 키 없음)
│   └── application-local.yml     # 로컬 설정 (Git에 커밋 안됨)
└── API_KEY_SETUP.md             # 이 가이드 문서
```

## 🚀 **애플리케이션 실행**

### **기본 실행 (Mock 모드)**
```bash
./gradlew bootRun
```

### **로컬 설정으로 실행**
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

### **환경 변수로 실행**
```bash
GEMINI_API_KEY="your-key" ./gradlew bootRun
```




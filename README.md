# 여행을 기록하다, 행북

행북은 여행을 기록하기 위해 만들어진 앱입니다. 여행을 다녀올 때, 사진들은 정리되지 않고 체계적이지 못한 계획으로 예산을 넘어선 지출을 하기도 하였습니다. 사진과 지출은 나의 여행이 어땠는지를 돌아볼 수 있는 소중한 것이라 생각하여 여행의 일정별로 기록할 수 있는 앱을 만들게 되었습니다.


## Functions

### 여행 등록

1. 여행지 선택
>Google Location API 를 통하여 여행지 검색 및 선택
1. 시작일 
1. 종료일 
1. 예산 지정
> 실시간 환율 정보를 가져와 한화를 해당 국가에서 사용하는 돈으로 계산 (선택 가능)
1. 예산 계획
> 식비/교통비/쇼핑/선물/문화체험/기타 등으로 나누어 돈을 얼마를 쓸지 지정


### 사진 관리
> 사진 업로드 / 다운로드

여행 중 사진을 업로드 할 수 있는 기능을 제공합니다.

해당 날짜에 찍은 사진을 더 빠르게 찾을 수 있도록 업로드 할 수 있는 이미지는 해당 날짜에 찍은 이미지만 가능하도록 하였습니다.


> 위젯

여행 중 찰나의 순간을 바로 촬영하고 기록할 수 있도록 플로팅 위젯을 제공합니다.


### 지출 관리

>  지출 저장

해당 날짜의 지출을 지출 분야를 나누어 기록할 수 있도록 하였습니다.


> 지출 및 예산 안내

저장된 지출과 여행을 등록할 때 사용한 예산을 이용하여 계획에 없던 과소비를 막도록 알림을 해줍니다.


### 기록 알림

여행이 끝날 시간에 맞추어 그 날 저장하지 않은 사진과 지출을 기록할 수 있도록 알림을 줍니다.



### 여행 공유

함께하는 동행자에게 여행 코드를 공유하여 함께 수정하며 여행 기록을 관리할 수 있습니다. 

### 책으로 남기는 여행

여행이 끝난 후, 최고의 사진을 정하여 책의 표지를 만들도록 합니다.

이 여행의 기록은 시간이 지나서도 되돌아 보며 지난 지출을 확인하고 예산을 정하는데 도움을 받을 수 있고, 사진을 다시 저장할 수 있습니다.


## Screenshots

> 스플래시

<img src="https://bit.ly/2Aknrxm" alt="스플래시" width="20%"/>


> 메인화면

<img src="https://bit.ly/2CTNLRr" alt="메인" width="20%"/><img src="https://bit.ly/2S3sTvt" alt="여행등록" width="20%"/><img src="https://bit.ly/2OyK9uD" alt="코드공유" width="20%"/><img src="https://bit.ly/2OxYOpC" alt="여행 현황" width="20%"/>


> 여행등록

<img src="https://bit.ly/2OxwWCd" alt="여행지등록" width="20%"/><img src="https://bit.ly/2PdtY5f" alt="시작일" width="20%"/><img src="https://bit.ly/2AjzV8B" alt="종료일" width="20%"/><img src="https://bit.ly/2yOWVdz" alt="예산" width="20%"/><img src="https://bit.ly/2EIRNh8" alt="예산" width="20%"/>


> 사진 등록

<img src="https://bit.ly/2Ale40q" alt="사진리스트" width="20%"/><img src="https://bit.ly/2PgeNYW" alt="사진등록" width="20%"/><img src="https://bit.ly/2q2WdWk" alt="위젯" width="20%"/>


> 지출 등록

<img src="https://bit.ly/2q2Yr87" alt="지출리스트" width="20%"/><img src="https://bit.ly/2CutPU9" alt="지출등록" width="20%"/>




## Server Side

행북은 Firebase를 통해 사용자의 데이터를 저장하고 관리합니다.

- Firebase Realtime Database
- Firebase Storage



## Extras
- author [zoripong](https://github.com/zoripong/)
- CONTACT ME : <mailto:devuri404@gmail.com>
# TOAA (The One Above All)

<img src="https://user-images.githubusercontent.com/58676453/178432481-5ec70202-4dfb-402c-ba85-e80ceaf1d70d.png" width="500"/>


## APK Link



## Contributor

카이스트 산업및시스템공학과 [정진규](https://github.com/jeongjingyu)

숙명여자대학교 소프트웨어학부 [송유진](https://github.com/kryptonite43)




## Concept

"Google, Naver, Youtube, Wikipedia, 검색 엔진을 하나로"

[TOAA](https://namu.wiki/w/%EC%9B%90%20%EC%96%B4%EB%B3%B4%EB%B8%8C%20%EC%98%AC): 마블 코믹스의 창조주이자 모든 것의 위에 존재하는 자

4개의 검색 엔진을 하나의 창에서 동시 검색할 수 있는 TOAA는 가장 검색 엔진다운 검색 엔진입니다.



## Brief Overview

- 카카오 로그인을 통한 본인 확인
- 본인의 최근 검색기록 및 검색 text change에 따른 검색기록 확인
- 실시간으로 많이 검색된 키워드 제시 (실시간 검색어 기능)



## Detailed Description

**Start Screen**

<img src="https://user-images.githubusercontent.com/58676453/178439764-a6d46762-7804-417f-96d9-bffd9ca8d422.jpg" width="200"/>


- LottieFiles를 통해 lottie animation 다운로드 후 배치
- 화면 중간에서 3초 동안 반복해서 animation 진행되도록 display


**Kakao Login Page**

<img src="https://user-images.githubusercontent.com/58676453/178438942-b4d6b803-7c90-4800-9ab2-7179769909d2.jpg" width="200"/>


- 앱 시작 시 홈 탭으로 시작
- RecyclerView 위에 CardView 구현
- 각 CardView에 식당 사진 리스트, 식당 이름, 식당 한줄평, 식당 별점, 메뉴 보기 텍스트 배치
- 식당 사진 리스트는 CardView 안에 ScrollView로 각 4개의 사진이 horizontal scroll을 통해 확인할 수 있도록 구현
- '메뉴 보기'에 onClickListener로 각 식당의 대표 메뉴판 이미지가 Dialog로 팝업 가능하도록 구현, 다시 이미지 클릭 시 Dialog dismiss


**Home Page (Main Search)**

<img src="https://user-images.githubusercontent.com/58676453/178438666-ffe0a62e-64d2-450d-b1a6-a4996c833a42.png" width="500"/>


- 앱 시작 시 홈 탭으로 시작
- RecyclerView 위에 CardView 구현
- 각 CardView에 식당 사진 리스트, 식당 이름, 식당 한줄평, 식당 별점, 메뉴 보기 텍스트 배치
- 식당 사진 리스트는 CardView 안에 ScrollView로 각 4개의 사진이 horizontal scroll을 통해 확인할 수 있도록 구현
- '메뉴 보기'에 onClickListener로 각 식당의 대표 메뉴판 이미지가 Dialog로 팝업 가능하도록 구현, 다시 이미지 클릭 시 Dialog dismiss



**Search Result Page**

<img src="https://user-images.githubusercontent.com/58676453/178439323-56da0154-aa0d-478e-a2c2-930a0432288d.png" width="700"/>


- ExpandableListView 활용
- json 형식으로 저장한 카이스트 주변 맛집 13곳의 정보(연락처, 평균 가격, 별점, 운영시간 등)를 파싱
- RatingBar를 활용하여 평점을 별로 표현함
- ListView에서 각 Group 클릭 시 해당 가게에 대한 정보를 확인할 수 있도록 확장됨
- Intent를 활용하여 'CALL' 버튼 클릭 시 해당 가게로 전화 걸기 가능
- 'MAP' 버튼 클릭시 해당 가게의 위치를 확인할 수 있도록 지도 탭으로 이동
- 검색 시 검색어에 해당하는 가게만 ListView에 뜰 수 있도록 함



## User Scenario

1. 


## Extra
- Font: Noto Sans Medium
- Android Studio Chipmunk
- nodeJS, mongoDB

# TOAA (The One Above All)

<img src="https://user-images.githubusercontent.com/58676453/178432481-5ec70202-4dfb-402c-ba85-e80ceaf1d70d.png" width="500"/>


## APK Link

https://drive.google.com/file/d/1tCRrMSmpFg8s2EttuHZdqGesk-Pu9E9w/view?usp=sharing

## Contributor

카이스트 산업및시스템공학과 [정진규](https://github.com/jeongjingyu)

숙명여자대학교 소프트웨어학부 [손유진](https://github.com/kryptonite43)




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


- 카카오 로그인 버튼을 누르면 기기의 카카오톡 앱 존재 여부에 따라 카카오톡 간편 로그인 또는 웹페이지를 통한 로그인으로 연결
- 로그인 성공 시 사용자의 이름, 프로필 이미지, 이메일 정보 받아옴
- UserApiClient에서 사용자 토큰 정보를 확인하여 로그인 이력이 있다면 앱 종료 후 재실행 시에 자동 로그인 구현




**Home Page (Main Search)**

<img src="https://user-images.githubusercontent.com/58676453/178446028-bd7923a1-5c6f-4eae-aae8-69b6032f3b11.png" width="800"/>


- 중앙 search 부분 클릭 시 본인 최근 검색어 listview 형태로 확인, keyboard 팝업, fullscreen touch 시 전체 dismiss
- 오른쪽 검색 버튼 또는 enter 클릭 시 해당 text에 대한 search result page로 이동

***카카오 로그인 정보***
- 상단 오른쪽 버튼 누르면 로그인 정보(카카오톡 프로필 이미지, 이름, 이메일)와 로그아웃 버튼 팝업, fullscreen touch 시 dismiss

***최근 검색어***
- 오른쪽 검색 버튼 또는 enter 클릭 시 mongoDB에 이메일, text, get 여부(이후 최근 검색어 삭제에 활용, default=true), 검색 시점 저장
- 최근 검색어는 검색한 시점 기준 최근 순 (get=true인 data만)
- 최근 검색어 listview에서 오른쪽 x버튼 클릭 시 최근 검색어 listview에서 삭제, 해당 text에 대한 모든 이전의 본인 검색 DB의 get=false로 update

***실시간 검색어***
- 화면 하단에 실시간 검색어 display, home 화면 켜진 시점으로부터 이전 1시간 동안의 검색 기록 대상, 새로고침 버튼 클릭 시 버튼 클릭 시 그 시점으로부터 이전 1시간 동안의 검색 기록 대상
- mongoDB에서 text에 대한 group 이후 count 기준으로 순위 display


**Search Result Page**

<img src="https://user-images.githubusercontent.com/58676453/178439323-56da0154-aa0d-478e-a2c2-930a0432288d.png" width="700"/>


- google, youtube, naver, wikipedia 아이콘을 누르면 각각 해당하는 페이지로 하단의 WebView에 검색 결과를 보여줌
- 검색창을 눌렀을 때 검색 기록을 볼 수 있으며, 해당 페이지에서 재검색 가능. fullscreen 터치 시 검색 기록 창 dismiss
- 검색창 좌측의 아이콘을 누르면 다시 Home Page로 이동
- 뒤로가기 버튼을 눌렀을 시에도 Home Page에 검색 기록 업데이트


## Extra
- Font: Noto Sans Medium
- Android Studio Chipmunk
- nodeJS, mongoDB

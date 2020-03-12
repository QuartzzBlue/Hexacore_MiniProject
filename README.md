# TCP/IP Communitation with IoT Client and Android Application
> 멀티캠퍼스 Hexacore 팀 : 강동욱 김연주 백대현 이슬 전국현 최여진



- Client : Pad 에서 Start 버튼 누르면 Receiver Thread 에서 1 수신해 Sender Thread 로 난수 생성해 msg 객체에 담아 전송
- Pad : Client 에서 수신한 난수를 display 하고, TCP/IP 서버(Server.java)에 접속. TCP/IP 서버로부터 Web 에서 받아온 Msg 객체 Receive. tid  null 이면 Sender1 Thread 생성되어 접속한 모든 Client 를 제어합니다. null 이 아니면 tid 의 값에 따라 특정 Client 를 제어합니다.
- TCP/IP Server : TCP/IP Client 로부터 수신받은 msg 객체를 Pad 로 전송
- main.jsp : target IP, 제어자 (0/1) 을 입력받는 form.
- WebAppServlet: main.jsp 로부터 target ip, 제어자가 담긴 msg 객체를 Sender Thread 로 Tcp/ip 서버로 전송하는 Tcp/ip Client 역할
- TCP/IP Client ->TCP/IP Server : msg 객체 전송, Tcp/ip Server 는 받은 msg 객체를 Pad 로 Send
- TCP/IP Server -> Pad : Pad 는 받은 msg 객체의 txt 를 추출해 Client의 on/off 제어할 수 있다.





## 1. 프로젝트 개요

## 2. 프로젝트 환경

## 3. 시스템 구성도

![image-20200310113534302](https://github.com/yeojini/Hexacore/blob/master/IMG/structure.png)  

## 제작자

- 강동욱 
- 김연주
- 백대현 
- 이슬 
- 전국현 
- 최여진
  - Mail : who3637@naver.com
  - Github : https://github.com/yeojini

---
<주의>
- \Hexacore\tabserver\app\src\main\AndroidManifest.xml 파일에
```
android:usesCleartextTraffic="true"
```

추가해야 안드로이드 9.0 이상에서 작동합니다.

---

- **git을 활용한 프로젝트 관리** (git 에 `push` 할 때 주의할 점)​

✔️ git bash 접속해서 `git pull`을 먼저하기

✔️ `master` 권한으로 올리지 않기 → `branch` 생성해서 `push` 하기

✔️ `branch` 이름은 자신이 맡은 역할을 나타낼 수 있도록 만듦

> 오늘 calendar 작업을 했으면, branch 이름은 calendar

> commit message에 날짜와 함께 작업 내용 상세하게 기록해주세요 😄

    git branch 브랜치명 → branch 생성
    git branch → 현재 접속 된 branch 확인
    git checkout 브랜치명 → 해당하는 branch로 접속
    
    git push origin 브랜치명
    → 접속된 `branch`로 push 하는 법 (저장소 이름인 'origin' 뒤에 branch 이름 써서 push 하기)

# JavaGame_MapleStory: 자바 기반 멀티플레이어 게임 서버 구현 프로젝트

# 소개
네트워크 프로그래밍 수업에서 만든 자바 기반의 멀티플레이 메이플스토리 구현 프로젝트입니다. 자바 소켓 프로그래밍을 활용하여 클라이언트, 서버 코드를 직접 구현했습니다.

---

JavaObjServer를 실행하면 서버 GUI가 실행됩니다.<br>
이후 Launcher를 실행하면 클라이언트가 실행이 되고 IP주소로 서버에 들어갈 수 있습니다.

### 주요 목표
클라이언트-–서버 구조에서 발생하는 상태 동기화 문제를 이해하기 위해 멀티스레딩 구조를 설계하고 소켓 프로그래밍으로 구현했습니다.


### 핵심 기능
- 실시간 캐릭터 동기화 및 이동
- 멀티 플레이어 환경의 데이터 처리
- 멀티 스레딩

### 기술스택
언어 : 자바<br>
에디터 : 이클립스<br>
버전 관리 : git, github<br><br>

### 기간
2026년 11월 17일 ~ 12월 8일 (4주)

### 인원
1인 개발 (클라이언트 + 서버)

---
# 코드
## 메인 코드
```
class AcceptServer extends Thread 
{
  //컴파일 오류 체크 X
  @SuppressWarnings("unchecked")
  public void run() 
  {
    while (true) 
    { // 사용자 접속을 계속해서 받기 위해 while문
      try {
        
        AppendText("Waiting new clients ...");
        client_socket = socket.accept(); // accept가 일어나기 전까지는 무한 대기중
        AppendText("새로운 참가자 from " + client_socket);
        // User 당 하나씩 Thread 생성
        UserService new_user = new UserService(client_socket);
        
        UserVec.add(new_user); // 새로운 참가자 배열에 추가
        new_user.start(); // 만든 객체의 스레드 실행
        AppendText("현재 참가자 수 " + UserVec.size());
        
      } catch (IOException e) {
        AppendText("accept() error");
        // System.exit(0);
      }
    }
  }
}
```

## 전송 함수
```
public void Logout() {
  String msg;
  if(user.getName() == null) {
    msg = "누군가 퇴장했지만 이름이 null입니다\n";
  }
  else
    msg = "[" + user.getName() + "]님이 퇴장 하였습니다.\n";
  MapleStoryMsg msg1 = new MapleStoryMsg("400");
  msg1.setName(user.getName());
  
  UserVec.removeElement(this); // Logout한 현재 객체를 벡터에서 지운다
  WriteAllObject(msg1); // 나를 제외한 다른 User들에게 전송
  AppendText("사용자 " + "[" + user.getName() + "] 퇴장. 현재 참가자 수 " + UserVec.size());
}

// 모든 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
public void WriteAll(String str) {
  for (int i = 0; i < user_vc.size(); i++) {
    UserService user = (UserService) user_vc.elementAt(i);
  }
}

// 모든 User들에게 Object를 방송. 채팅 message와 image object를 보낼 수 있다
public void WriteAllObject(Object ob) {
  for (int i = 0; i < user_vc.size(); i++) {
    UserService user = (UserService) user_vc.elementAt(i);
    user.WriteOneObject(ob);
  }
}

// 나를 제외한 User들에게 방송. 각각의 UserService Thread의 WriteONe() 을 호출한다.
public void WriteOthers(String str) {
  for (int i = 0; i < user_vc.size(); i++) {
    UserService user = (UserService) user_vc.elementAt(i);
    user.WriteOne(str);
  }
}
public void WriteOhtersObject(Object ob) {
  for (int i = 0; i < user_vc.size(); i++) {
    
    UserService user = (UserService) user_vc.elementAt(i);
    if(!user.getUser().getName().equals(this.user.getName()))
      user.WriteOneObject(ob);
  }
}

// Windows 처럼 message 제외한 나머지 부분은 NULL 로 만들기 위한 함수
public byte[] MakePacket(String msg) {
  byte[] packet = new byte[BUF_LEN];
  byte[] bb = null;
  int i;
  for (i = 0; i < BUF_LEN; i++)
    packet[i] = 0;
  try {
    bb = msg.getBytes("euc-kr");
  } catch (UnsupportedEncodingException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
  for (i = 0; i < bb.length; i++)
    packet[i] = bb[i];
  return packet;
}

// UserService Thread가 담당하는 Client 에게 1:1 전송
public void WriteOne(String msg) {
  try {
    MapleStoryMsg obcm = new MapleStoryMsg("200");
    oos.writeObject(obcm);
  } catch (IOException e) {
    AppendText("dos.writeObject() error");
    try {
      ois.close();
      oos.close();
      client_socket.close();
      client_socket = null;
      ois = null;
      oos = null;
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    Logout(); // 에러가난 현재 객체를 벡터에서 지운다
  }
}

// 귓속말 전송
public void WritePrivate(String msg) {
  try {
    MapleStoryMsg obcm = new MapleStoryMsg("200");
    oos.writeObject(obcm);
  } catch (IOException e) {
    AppendText("dos.writeObject() error");
    try {
      oos.close();
      client_socket.close();
      client_socket = null;
      ois = null;
      oos = null;
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    Logout(); // 에러가난 현재 객체를 벡터에서 지운다
  }
}
public void WriteOneObject(Object ob) {
  try {
      oos.writeObject(ob);
  } 
  catch (IOException e) {
    AppendText("oos.writeObject(ob) error");		
    try {
      ois.close();
      oos.close();
      client_socket.close();
      client_socket = null;
      ois = null;
      oos = null;				
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    Logout();
  }
}
```

### 데이터 코드 분석
```
public void run() 
{
  while (true) 
  { 
    // 사용자 접속을 계속해서 받기 위해 while문
    try {
      Object obcm = null;
      String msg = null;
      MapleStoryMsg cm = null;
      
      if (socket == null)
        break;

      
      try {
        obcm = ois.readObject();
      } catch (ClassNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return;
      }

      
      if (obcm == null)
        break;
      
      if (obcm instanceof MapleStoryMsg) {
        cm = (MapleStoryMsg) obcm;
        AppendObject(cm);
      } else
        continue;
      
      //받기
      //login
      switch(cm.getCode()) {
      case "100":
        user = new User(cm.getName());
        user.setX(cm.getX());
        user.setY(cm.getY());
        //user.setImg(cm.getImg());
        user.setKeybuff(cm.getKeybuff());
        Login();
        break;
      case "101": //x만
        user.setX(cm.getX());
        break;
      case "102": //y만
        user.setY(cm.getY());
        break;
      case "103": //위치
        //WriteAllObject(cm);
        user.setX(cm.getX());
        user.setY(cm.getY());
        AppendText(user.getX() + " " + user.getY());
        WriteOhtersObject(cm);
        break;
      case "104": //키 입력
        //이동 처리 함수
        user.setKeybuff(cm.getKeybuff());
        //WriteAllObject(cm);
        break;
      case "105":
      case "106":
      case "107":
      case "108":
      case "109":
        WriteOhtersObject(cm);
        break;
      case "110":
        WriteOhtersObject(cm);
        break;
      }
      
      if (cm.getCode().matches("200")) {
      } else if (cm.getCode().matches("300")) {
        WriteAllObject(cm);
      } else if (cm.getCode().matches("400")) { // logout message 처리 로그아웃
        Logout();
        break;
      }  else if (cm.getCode().matches("500")) {
      }  else if (cm.getCode().matches("600")) {
      }  else if (cm.getCode().matches("700")) {
      } 
    } catch (IOException e) {
      AppendText("ois.readObject() error");
      try {
        ois.close();
        oos.close();
        client_socket.close();
        Logout(); // 에러가난 현재 객체를 벡터에서 지운다
        break;
      } catch (Exception ee) {
        break;
      } // catch문 끝
    } // 바깥 catch문끝
  } // while
} // run
```


---
### 게임 플레이
<img width="1187" height="889" alt="image" src="https://github.com/user-attachments/assets/47c3f4d2-bd31-4dfd-a906-80107ad8af62" />
<img width="1082" height="591" alt="image" src="https://github.com/user-attachments/assets/dcdb90c4-19be-4d94-9d4e-34c12899a781" />

# 아이템3. private 생성자나 열거 타입으로 싱글턴임을 보증하라  

> 싱글턴(singleton) ?  

- 인스턴스를 오직 하나만 생성할 수 있는 클래스
  - 함수(<a href="item24.md">아이템 24</a>)와 같은 무상태(stateless) 객체
  - 설계상 유일해야 하는 시스템 컴포넌트
- 이를 사용하는 클라이언트를 어려워질 수 있음

### public static final 필드 방식의 싱글턴

```
public class Elvis {
  public static final Elvis INSTANCE = new Elvis();

  private Elvis() {
    ...
  }

  public void leaveTheBuilding() {
    ...
  }
}
```

=> 리플렉션을 사용하지 않는 이상 생성자는 최초 한번만 호출  
=> 그것도 막으려면 생성자 2번 호출 될 때 예외 전가

**장점**  

- 해당 클래스가 싱글턴임을 API에 명백히 드러남  
- 간결함

---  

### 정적 팩터리 방식의 싱글턴  

```
public class Elvis {
  private static final Elvis INSTANCE = new Elvis();

  private Elvis() {
    ...
  }

  public static Elvis getInstance() {
    return INSTANCE;
  }

  public void leaveTheBuilding() {
    ...
  }
}
```  

**장점**  

- 마음이 바뀌면 API를 바꾸지 않고 싱글턴이 아니게 변경 가능
- 원한다면 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있음(<a href="item30.md">아이템 30</a>)  
- 참조를 공급자(supplier)로 사용할 수 있음  

---  

### Enum을 활용한 싱글턴  

```
public enum Elvis {
    INSTANCE;

    public void doSomething() {
      ...
    }
}
```  

**장점**  

- 직렬화/역직렬화 할 때 코딩으로 문제 해결 할 필요X  
- 리플렉션으로 호출되는 문제X

**단점**  

- Enum 말고 다른 상위 클래스를 상속할 수 없음

---

### Lazy holder를 이용한 싱글턴  

```
public class Elvis {
  pubilc static Elvis getInstace() {
    return LazyHolder.INSTANCE;
  }

  private Elvis() {
    ...
  }

  private static class LazyHolder {
    private static final Singleton INSTANCE = new Singleton();
  }
}
```  

=> Elvis.getInstace()를 호출하는 순간 LazyHolder 클래스가 로딩되며
Elvis 클래스가 초기화 진행

**장점**  

- thread safe

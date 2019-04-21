# 아이템5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라  
=> 많은 클래스는 하나 이상의 자원에 의존  

> 사전(dictionary)에 의존하는 맞춤법 검사기 ?  

### 정적 유틸리티를 잘못 사용 한 예  

```
public class SpellChecker {
  private static final Lexicon dictionary = ...;

  private SpellChecker() {    
  }

  public static boolean isValid(String word) {
    ...
  }

  public static List<String> suggestions(String typo) {
    ...
  }    
}
```  

### 싱글턴(<a href="./item03.md">아이템3</a>을 잘못 사용한 예)  

```
public class SpellChecker {
  public static SpellChecker INSTANCE = new SpellChecker(...);
  private final Lexicon dictionary = ...;  

  private SpellChecker() {    
  }

  public boolean isValid(String word) {
    ...
  }

  public List<String> suggestions(String typo) {
    ...
  }    
}
```  

- 유연하지 않고 테스트하기 어려움  

> final 한정자를 제거하고 다른 사전으로 교체하는 메소드를 추가하면?  
e.g) setDictionary(...)

- 어색하고 오류를 내기 쉬우며 멀티스레드 환경에서 사용할 수 없음  

### 의존 객체 주입은 유연성과 테스트 용이성을 높여줌  

```
public class SpellChecker {
  private final Lexicon dictionary;

  public SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
  }

  public boolean isValid(String word) {
    ...
  }

  public List<String> suggestions(String typo) {
    ...
  }      
}
```  

- 자원이 몇 개든 의존 관계가 어떻든 상관없이 잘 작동
- 또한 불변(<a href="./item17.md">아이템17</a>)을 보장하여  
(같은 자원을 사용하려는) 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있기도 함  
- 의존 객체 주입은 생성자, 정적 팩터리(<a href="./item01.md">아이템1</a>), 빌더(<a href="./item02.md">아이템2</a>) 모두에 똑같이 응용할 수 있음  

=> 이 패턴의 쓸만한 변형으로 생성자에 자원 팩터리를 넘겨주는 방식  
(팩터리란 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체)  

### 클라이언트가 제공한 팩터리가 생성한 타일(Tile)들로 구성된 모자이크(Mosaic)를 만드는 메소드  

```
Mosic create(Supplier<? extends Tile> tileFactory) { ... }
```

- 의존성이 수천 개나 되는 큰 프로젝트에서는 코드를 어지럽게 만들기도 함  
- 대거(Dagger), 주스(Guice), 스프링(Spring) 같은 의존 객체 주입 프레임워크를 사용하면  
이런 어질러짐을 해소할 수 있음  

### 핵심정리

- 클래스가 내부적으로 하나 이상의 자원에 의존하고 그 자원이 클래스 동작에 영향을 준다면  
=> 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋음  
- 이 자원들을 클래스가 직접 만들게 해서도 안됨  
- 필요한 자원을 (혹은 그 자원을 만들어주는 팩터리를) 생성자에 (혹은 정적 팩터리나 빌더에) 넘겨주자  
- 의존 객체 주입이라 하는 이 기법은 클래스의 유연성, 재사용성, 테스트 용이성을 기막히게 개선해줌

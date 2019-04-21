# 아이템1. 생성자 대신 정적 팩토리 메소드를 고려하라  

```
// java.lang.Boolean class
public static Boolean valueOf(boolean b) {
    return (b ? TRUE : FALSE);
}
```

=> public 생성자 이외의 static factory 메소드 방식으로 인스턴스를  
생성할 수 있음  

## 장점1. 이름을 가질 수 있다.

- ```new BigInteger(int, Random)```과 ```BigInteger.probablePrime(int bitLength, Random rnd)```  이 존재할 때 어느것이 값이 소수인 BigInteger를 반환하는지 명확함  
- 하나의 시그니처는 생성자를 하나만 만들 수 있음. 매개변수 순서를 다르게 한 생성자를 추가하는 것은 좋지 않은 발상  

## 장점2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.

- 불변 클래스(immutable class; item17)는 인스턴스를 미리 만들어 놓거나 새로 생성하면서 캐싱할 수 있음(Boolean.valueOf(boolean b)는 객체를 생성하지 않음)  
=> (특히 생성 비용이 큰) 같은 객체가 자주 요청되는 상황이라면 성능을 끌어줌  
- 플라이웨이트 패턴(Flyweight pattern)도 이와 비슷한 기법  

## 장점3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

- 이 능력은 반환할 객체의 클래스를 자유롭게 선택할 수 있게 하는 '엄청난 유연성'을 제공한다.  
- API를 만들 때 이 유연성을 응용하면 구현 클래스를 공개하지 않아도 그 객체를 반환할 수 있어서 API를 작게 유지할 수 있다.  
=> 대표적으로 java.util.Collections
=> API가 작아진다는 것은 개념적인 무게, 즉 프로그래머가 익혀야 할 개념의 수와 난이도를 낮춘다는 것  
- java8부터 인터페이스가 정적 메소드를 가질 수 있으므로 인스턴스화 불가 동반 클래스를 둘 필요가 없음  
=> java9에서는 private 정적 메소드까지 허락하지만, 정적 멤버 필드와 클래스는 여전히 public  

## 장점4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

- 반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관X  
- EnumSet은 아래와 같이 원소수에 따라 RegularEnumSet과 JumboEnumSet을 제공

```
// java.util.EnumSet
public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
    Enum<?>[] universe = getUniverse(elementType);
    if (universe == null)
        throw new ClassCastException(elementType + " not an enum");

    if (universe.length <= 64)
        return new RegularEnumSet<>(elementType, universe);
    else
        return new JumboEnumSet<>(elementType, universe);
}
```  

=> 클라이언트는 이 두 클래스의 존재를 몰라도 되며 다음 릴리즈일 때 RegularEnumSet이 효과가 적어  
이를 삭제해도 아무런 문제가 발생X  
=> 비슷하게 성능을 개선한 세번째 클래스가 추가되어도 됨  

## 장점5. 정적 팩토리 메소드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

- 이러한 유연함은 서비스 제공자 프레임워크(service provider framework)를 만드는 근간이 된다.  
  - 서비스 인터페이스(Service interface) : 구현체의 동작을 정의  
    => e.g) JDBC::Connection
  - 제공자 등록 API(provider registration API) : 제공자가 구현체를 등록할 때 사용하는 제공자 등록  
    => e.g) JDBC::DriverManager.registerDriver()
  - 서비스 접근 API(Service access API) : 클라이언트가 서비스의 인스턴스를 얻을 때 사용  
    => e.g) JDBC::DriverManager.getConnection()

## 단점1. 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩토리 메소드만 제공하면 하위 클래스를 만들 수 없다.

- 위에서 애기한 컬렉션 프레임워크의 유틸리티 구현 클래스들은 상속할 수 없다는 이야기  
=> 상속보다 컴포지션을 사용(<a href="./item18.md">아이템18</a>)하도록 유도하고 불변 타입(<a href="./item17.md">아이템17</a>)으로 만들려면 이 제약을 지켜야 한다는 점에서 오히려 장점이 될 수 있음  

## 단점2. 정적 팩터리 메소드는 프로그래머가 찾기 어렵다.

- 생성자처럼 API 설명에 명확히 드러나지 않음  
=> javadoc에서 지원하기 전까지 API 문서를 잘 써놓고 메소드 이름도 널리 알려진 규약을 따라  
짓는 식으로 문제를 완화해야 함.  

---  

## 널리 알려진 메소드 네이밍 룰  

- **from** : 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형번환 메소드  

=> ```Date d = Date.from(instant);```  

- **of** : 여러 매개변수를 받아 적절한 타입의 인스턴스를 반환하는 집계 메소드
=> ```Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);```  

- **valueOf** : from과 of의 더 자세한 버전
=> ```BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);```  

- **instance** or **getInstance** : (매개변수를 받는다면) 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스임을 보장하지는 않음  
=> ```StackWalker luke = StackWalker.getInstance(options);```  

- **create** or **newInstance** : instance or getInstance와 같지만 매번 새로운 인스턴스 생성을 반환함을 보장한다.  
=> ```Object newArray = Array.newInstance(classObject, arrayLen);```  

- **getType** : getInstance와 같으나 생성할 클래스가 아닌 다른 클래스에 팩터리 메소드를 정의할 때 쓴다. "Type"은 팩토리 메서드가 반환할 객체의 타입  
=> ```FileStore fs = Files.getFileStore(path);```  

- **newType** : newInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메소드를 정의할 때 쓴다. "Type"은 팩터리 메서드가 반환할 객체의 타입
=> ```BufferedReader br = Files.newBufferedReader(path)```  

- **type** : getType과 newType의 간결한 버전
=> ```List<Complaint> litany = Collections.list(legacyLitany);```  

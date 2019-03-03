# 아이템2. 생성자에 매개변수가 많다면 빌더를 고려하라  

정적 팩터리와 생성자에는 똑같은 제약이 존재  
=> 선택적 매개변수가 많을때 적절히 대응하기 어려움  
=> 점층적 생성자 패턴(telescoping constructor pattern)을 주로 사용함  
=> 매개변수가 많아지면 클라이언트 코드를 작성하거나 읽기 어려움  

> 점층적 생성자 패턴 예제  

```
public class NutritionFacts {
  private final int servingSize;    // (ml, 1회 제공량)    필수
  private final int servings;       // (회, 총 n회 제공량) 필수
  private final int calories;       // (1회 제공량당)     선택
  private final int fat;            // (g/1회 제공량)     선택
  private final int sodium;         // (mg/1회 제공량)    선택
  private final int carbohydrate;   // (g/1회 제공량)     선택

  public NutritionFacts(it servingSize, int servings) {
    this(servingSize, servings, 0);    
  }

  this(servingSize, servings, calories, 0);    
  public NutritionFacts(it servingSize, int servings, int calories) {
  }
  ...
}

// client side
NutritionFacts cocacola = new NutritionFacts(240,8,100,0,35,27);
```  

> 자바빈즈 패턴  

```
public class NutritionFacts {
  private final int servingSize = -1;
  private final int servings = -1;
  private final int calories = 0;
  private final int fat = 0;
  private final int sodium = 0;
  private final int carbohydrate = 0;

  // setters
  public void setServingSize(int servingSize) {
    this.servingSize = servingSize;
  }
  ...
}

// client side  
NutritionFacts cocacola = new NutritionFacts();
cocacola.setServingSize(240);
...
```   

=> 객체 하나를 만들려면 메소드를 여러 개 호출해야 하고 객체가 완전히 생성되기 전까지  
일관성(consistency)이 무너진 상태에 놓이게 됨  
=> 클래스를 불면(<a href="./item17.md">아이템17</a>)으로 만들 수도 없고 스레드 안정성  
을 위해 프로그래머가 추가적으로 작업을 해줘야 함  

=> 이를 해결하기 위해 객체를 수동으로 freezing 해주고 프리징 하기 전에는 사용할 수 없도록  
할 수 있지만 다루기 어렵고 런타임 오류에 취약함  

=> 빌더패턴을 대안으로 삼을 수 있음  

```
public class NutritionFacts {
  private final int servingSize;
  private final int servings;
  private final int calories;
  private final int fat;
  private final int sodium;
  private final int carbohydrate;

  public static class Builder {
    // 필수 매개변수
    private final int servingSize;
    private final int servings;

    // 선택 매개변수 - 기본값으로 초기화
    private int calories = 0;
    private int fat = 0;
    private int sodium = 0;
    private int carbohydrate = 0;

    public Builder(int servingSize, int servings) {
      this.servingSize = servingSize;
      this.servings = servings;
    }

    public Builder calories(int calories) {
      this.calories = calories;
      return this;
    }

    public NutritionFacts build() {
      return new NutritionFacts(this);
    }    
  }

  private NutritionFacts(Builder builder) {
    this.servingSize = builer.servingSize;
    this.servings = builer.servings;
    this.calories = builer.calories;
    this.fat = builer.fat;
    this.sodium = builer.sodium;
    this.carbohydrate = builer.carbohydrate;
  }
}

// client side
NutritionFacts cocacola = new NutritionFacts.Builder(240,8)
    .calories(100).sodium(35).build();
```  

=> 빌더 패턴은 명명된 선택적 매개변수(named optional parameters)를 흉내 낸 것  
=> 빌더의 생성자와 메소드에서 입력 매개변수를 검사  

> 계층적으로 설계된 클래스와 잘 어울리는 빌더 패턴  

```
public abstract class Pizza {

    public enum Topping {HAM, MUSHROOM, ONION, PEPPER, SAUSAGE}

    final Set<Topping> toppings;

    abstract static class Builder<T extends Builder<T>> {

        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);

        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));

            return self();
        }

        abstract Pizza build();

        // simulated self-type 관용구
        protected abstract T self();
    }

    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone();
    }
}
```  

```
public class NyPizza extends Pizza {

    public enum Size {SMALL, MEDIUM, LARGE}

    private final Size size;

    public static class Builder extends Pizza.Builder<Builder> {

        private final Size size;

        public Builder(Size size) {
            this.size = Objects.requireNonNull(size);
        }

        @Override
        NyPizza build() {
            return new NyPizza(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private NyPizza(Builder builder) {
        super(builder);
        this.size = builder.size;
    }
}
```  

```
public class Calzone extends Pizza {

    private final boolean sauceInside;

    public static class Builder extends Pizza.Builder<Builder> {

        private boolean sauceInside = false;

        public Builder sauceInside() {
            this.sauceInside = true;
            return this;
        }

        @Override
        public Calzone build() {
            return new Calzone(this);
        }

        @Override
        protected Builder self() {
            return this;
        }
    }

    private Calzone(Builder builder) {
        super(builder);
        sauceInside = builder.sauceInside;
    }
}
```  

```
NyPizza nyPizza = new NyPizza.Builder(SMALL)
    .addTopping(SAUSAGE).addTopping(ONION).build();

Calzone calzone = new Calzone.Builder()
    .addTopping(HAM).sauceInside().build();
```  

**생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 낫다**  
=> 빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가 훨씬 간결하고 자바빈즈보다 안전하다.

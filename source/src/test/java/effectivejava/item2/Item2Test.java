package effectivejava.item2;

import static effectivejava.item2.NyPizza.Size.SMALL;
import static effectivejava.item2.Pizza.Topping.HAM;
import static effectivejava.item2.Pizza.Topping.ONION;
import static effectivejava.item2.Pizza.Topping.SAUSAGE;

import org.junit.Test;

/**
 * @GitHub : https://github.com/zacscoding
 */
public class Item2Test {

    @Test
    public void createPizza() {
        NyPizza nyPizza = new NyPizza.Builder(SMALL)
            .addTopping(SAUSAGE).addTopping(ONION).build();

        Calzone calzone = new Calzone.Builder()
            .addTopping(HAM).sauceInside().build();
    }
}

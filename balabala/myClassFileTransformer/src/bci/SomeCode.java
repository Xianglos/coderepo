package bci;

import java.math.BigDecimal;

public class SomeCode {

    public boolean runme() {
        Numm num = new Numm(10);

        num.setNum(20);

        System.out.println(num.add(30));

        BigDecimal bigd1 = new BigDecimal("123");
        BigDecimal bigd2 = new BigDecimal("124");

        num.setNum(60);

        if (bigd1.equals(bigd2)) {
            System.out.println("equals");

        }

        return true;

    }

}

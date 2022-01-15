package nz.bradley.neil.cpu.testing.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
@Repeatable(Shoulds.class)
public @interface Should {
    String value();
}

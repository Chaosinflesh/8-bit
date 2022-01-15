package nz.bradley.neil.cpu.testing.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Shoulds {
    Should[] value();
}

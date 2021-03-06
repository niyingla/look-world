package coding.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Aspects.class)
public @interface Aspect {
    Class[] types();
}

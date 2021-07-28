package coding.annotation;

import java.lang.annotation.*;

/**
 * 使用此注解会转化@Aspect合并变成@Aspects
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Aspects {
    Aspect[] value();
}

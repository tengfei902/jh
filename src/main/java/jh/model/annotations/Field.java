package jh.model.annotations;

import org.springframework.web.bind.annotation.Mapping;

import java.lang.annotation.*;

/**
 * Created by tengfei on 2017/11/4.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface Field {
    boolean required() default false;
    String defaults() default "";
    String alias() default "";
    Type type() default Field.Type.varchar;

    enum Type {
        varchar,number,date;
    }
}



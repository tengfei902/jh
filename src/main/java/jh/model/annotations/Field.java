package jh.model.annotations;

/**
 * Created by tengfei on 2017/11/4.
 */
public @interface Field {
    boolean required() default false;
    String defaults() default "";
    Type type() default Field.Type.varchar;

    enum Type {
        varchar,number,date;
    }
}



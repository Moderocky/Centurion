package mx.kenzie.centurion.annotation;

import mx.kenzie.centurion.KnownArguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For a method that denotes some argument pattern.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Argument {

    String description() default "";

    String pattern() default "";

    KnownArguments[] value() default {};

}

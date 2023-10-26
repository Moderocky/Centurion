package mx.kenzie.centurion.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The root data for a command, typically placed on a class that contains all the command's patterns.
 * <pre>{@code
 * @Command("test")
 * public class TestCommand {
 *
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CommandDetails {

    /**
     * The command label (what you write to execute the command) followed by any aliases.
     * If nothing is provided this will infer a name from the member it was placed on.
     */
    String[] value() default {};

    String description() default "";

}

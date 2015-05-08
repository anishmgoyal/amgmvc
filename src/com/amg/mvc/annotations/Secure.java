package com.amg.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This is placed on a controller whose methods cannot be accessed by
 * unauthorized users, or a method which cannot be called from the
 * action caller in the Context Manager.
 * @author Anish Goyal
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {
	String[] roles() default "all";
	String redirectAction() default "";
	boolean requireAll() default false;
}

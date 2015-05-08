package com.amg.mvc.annotations;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation is used for setting the column name
 * for a field in a model connected to a database table.
 * @author Anish Goyal
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Map {
	public String value();
	public boolean id() default false;
}
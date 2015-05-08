package com.amg.mvc.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This defines a class in which all required beans are
 * auto-wired, or a field which needs to be wired.
 * @param value <br /> This should be specified IF AND ONLY IF:
 * <ul>
 * 		<li>The class of the field being wired is not the same as
 * 			the class of the object depended upon.</li>
 * 		<li>The classname of the field being wired is not the same
 * 			as the key which the object is stored under as defined
 * 			by the WireAs annotation on the class in question.</li>
 * 		<li>The object required was added manually (not through the
 * 			loadObjects() method), and was mapped with a keyword not
 * 			equal to the classname of the field being wired, or its
 * 			fully qualified name.</li>
 * </ul>
 * @see com.amg.mvc.annotations.WireAs
 * @author Anish Goyal
 *
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
//TODO: Implement value handler.
public @interface Wire {
	String value() default "";
}
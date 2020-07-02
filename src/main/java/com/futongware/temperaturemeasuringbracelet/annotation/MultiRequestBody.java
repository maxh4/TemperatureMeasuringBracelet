package com.futongware.temperaturemeasuringbracelet.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiRequestBody {

    boolean required() default true;

    /**
     * When value or param name not match, allows parse outer json fields
     */
    boolean parseAllFields() default true;

    /**
     * Json node key
     */
    String value() default "";
}
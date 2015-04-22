package com.unigames.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by wenfeng on 14/12/16.
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)

public @interface RequestAttribute {
    String value();
}

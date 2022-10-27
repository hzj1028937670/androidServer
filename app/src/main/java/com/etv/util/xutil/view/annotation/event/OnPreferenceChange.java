package com.etv.util.xutil.view.annotation.event;

import android.preference.Preference;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: wyouflf
 * Date: 13-8-16
 * Time: 下午2:37
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(
        listenerType = Preference.OnPreferenceChangeListener.class,
        listenerSetter = "setOnPreferenceChangeListener",
        methodName = "onPreferenceChange")
public @interface OnPreferenceChange {
    String[] value();
}

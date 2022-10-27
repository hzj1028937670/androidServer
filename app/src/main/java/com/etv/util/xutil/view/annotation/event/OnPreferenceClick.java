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
        listenerType = Preference.OnPreferenceClickListener.class,
        listenerSetter = "setOnPreferenceClickListener",
        methodName = "onPreferenceClick")
public @interface OnPreferenceClick {
    String[] value();
}

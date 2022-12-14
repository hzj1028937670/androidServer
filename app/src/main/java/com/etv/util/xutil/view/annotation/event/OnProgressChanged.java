package com.etv.util.xutil.view.annotation.event;

import android.widget.SeekBar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: wyouflf
 * Date: 13-8-16
 * Time: 下午2:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@EventBase(
        listenerType = SeekBar.OnSeekBarChangeListener.class,
        listenerSetter = "setOnSeekBarChangeListener",
        methodName = "onProgressChanged")
public @interface OnProgressChanged {
    int[] value();

    int[] parentId() default 0;
}

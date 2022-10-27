package org.litepal;

import org.litepal.exceptions.GlobalException;

import android.app.Application;
import android.content.Context;

public class LitePalApplication extends Application {

    /**
     * Global application context.
     */
    static Context sContext;

    /**
     * Construct of LitePalApplication. Initialize application context.
     */
    public LitePalApplication() {
        sContext = this;
    }

    /**
     * Deprecated. Use {@link LitePal#initialize(Context)} instead.
     *
     * @param context Application context.
     */
    @Deprecated
    public static void initialize(Context context) {
        sContext = context;
    }

    /**
     * Get the global application context.
     *
     * @return Application context.
     * @throws org.litepal.exceptions.GlobalException
     */
    public static Context getContext() {
        if (sContext == null) {
            throw new GlobalException(GlobalException.APPLICATION_CONTEXT_IS_NULL);
        }
        return sContext;
    }

}

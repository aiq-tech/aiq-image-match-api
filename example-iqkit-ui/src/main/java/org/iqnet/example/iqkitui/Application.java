package org.iqnet.example.iqkitui;

import android.support.multidex.MultiDexApplication;

import com.nextfaze.logging.Logging;

import org.iqnect.iqkit.IQKit;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Accessors(prefix = "m")
public class Application extends MultiDexApplication {

    // TODO: insert your app id and secret here
    //region VARIABLES
    private static final String APP_ID = "APP-ID";
    private static final String APP_SECRET = "APP-SECRET";
    //endregion

    @Getter
    private IQKit mIQKit;

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();
        log.debug("application started");

        mIQKit = IQKit.init(this, APP_ID, APP_SECRET);
    }

    private void initLogging() {
        Logging.configure(this, "log4j.properties");
    }
}

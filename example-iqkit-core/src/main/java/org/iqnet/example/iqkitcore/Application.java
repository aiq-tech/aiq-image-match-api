package org.iqnet.example.iqkitcore;

import com.nextfaze.logging.Logging;

import org.iqnect.iqkit.IQKit;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Accessors(prefix = "m")
@Slf4j
public class Application extends android.app.Application {

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

        mIQKit = IQKit.init(this, APP_ID, APP_SECRET);
        log.debug("instantiated iqkit: {}", mIQKit);
    }

    private void initLogging() {
        Logging.configure(this, "log4j.properties");
    }

}

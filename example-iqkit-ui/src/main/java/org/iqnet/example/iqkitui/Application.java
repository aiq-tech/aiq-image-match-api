package org.iqnet.example.iqkitui;

import android.support.multidex.MultiDexApplication;

import org.iqnect.iqkit.IQKit;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Accessors(prefix = "m")
public class Application extends MultiDexApplication {

    @Getter
    private IQKit mIQKit;

    @Override
    public void onCreate() {
        super.onCreate();

        log.debug("application started");

        mIQKit = IQKit.init(this, BuildConfig.IQNECT_APP_ID, BuildConfig.IQNECT_APP_SECRET);
    }
}

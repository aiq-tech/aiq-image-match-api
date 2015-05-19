package org.iqnect.example.iqkitcore;

import org.iqnect.iqkit.IQKit;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Accessors(prefix = "m")
@Slf4j
public class Application extends android.app.Application {

    @Getter
    private IQKit mIQKit;

    @Override
    public void onCreate() {
        super.onCreate();

        mIQKit = IQKit.init(this, BuildConfig.IQNECT_APP_ID, BuildConfig.IQNECT_APP_SECRET);
        log.debug("instantiated iqkit: {}", mIQKit);
    }

}

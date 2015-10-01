package org.iqnect.example.iqkitcore;

import android.net.Uri;
import android.text.TextUtils;

import org.iqnect.iqkit.IQKit;
import org.iqnect.iqkit.IQKitConfiguration;

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

        // TODO: remove when all servers are setup
        String serverUrl = TextUtils.isEmpty(BuildConfig.IQNECT_APP_SERVER) ?
                "http://cloud-capi.iqnect.org" : BuildConfig.IQNECT_APP_SERVER;

        if (TextUtils.isEmpty(serverUrl)) {
            log.debug("using default server");
            mIQKit = IQKit.init(this, BuildConfig.IQNECT_APP_ID, BuildConfig.IQNECT_APP_SECRET);
        } else {
            log.debug("using custom server: {}", serverUrl);
            IQKitConfiguration config = new IQKitConfiguration(this);

            config.setAppId(BuildConfig.IQNECT_APP_ID);
            config.setAppSecret(BuildConfig.IQNECT_APP_SECRET);
            config.setBaseUri(Uri.parse(serverUrl));

            mIQKit = IQKit.init(config);
        }
        log.debug("instantiated iqkit: {}", mIQKit);
    }

}

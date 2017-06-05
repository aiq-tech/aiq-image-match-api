package org.iqnect.example.iqkitui;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import org.iqnect.iqkit.IQKit;
import org.iqnect.iqkit.IQKitConfiguration;

public class Application extends MultiDexApplication {

    private static final String TAG = Application.class.getSimpleName();

    private IQKit mIQKit;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "application started");

        // TODO: remove when all servers are setup
        String serverUrl = TextUtils.isEmpty(BuildConfig.IQNECT_APP_SERVER) ?
                "http://cloud-capi.iqnect.org" : BuildConfig.IQNECT_APP_SERVER;

        if (TextUtils.isEmpty(serverUrl)) {
            Log.d(TAG, "using default server");
            mIQKit = IQKit.init(this, BuildConfig.IQNECT_APP_ID, BuildConfig.IQNECT_APP_SECRET);
        } else {
            Log.d(TAG, "using custom server: " + serverUrl);
            IQKitConfiguration config = new IQKitConfiguration(this);

            config.setAppId(BuildConfig.IQNECT_APP_ID);
            config.setAppSecret(BuildConfig.IQNECT_APP_SECRET);
            config.setBaseUri(Uri.parse(serverUrl));

            mIQKit = IQKit.init(config);
        }
        Log.d(TAG, "instantiated iqkit: " + mIQKit);
    }

    @NonNull
    public IQKit getIQKit() {
        return mIQKit;
    }
}

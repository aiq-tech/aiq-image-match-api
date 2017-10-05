package tech.aiq.imagematch.example;

import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.util.Log;

import tech.aiq.imagematch.api.ImageMatchService;

public class Application extends MultiDexApplication {

    private static final String TAG = Application.class.getSimpleName();


    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "application started");

        // TODO: remove when all servers are setup
        String serverUrl = BuildConfig.AIQ_APP_SERVER;

        if (TextUtils.isEmpty(serverUrl)) {
            serverUrl = ImageMatchService.getServiceEndPoint(ImageMatchService.ServiceType.PRODUCTION);
            Log.d(TAG, "using default server");
        }
        ImageMatchService.init(this, BuildConfig.AIQ_APP_ID, BuildConfig.AIQ_APP_SECRET, serverUrl);
        Log.d(TAG, "instantiated image match sdk ");
    }

}

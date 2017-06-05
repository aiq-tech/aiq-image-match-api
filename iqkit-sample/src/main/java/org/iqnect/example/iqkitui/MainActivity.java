package org.iqnect.example.iqkitui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.iqnect.iqkit.api.NotFoundResponseException;
import org.iqnect.iqkit.model.SearchResult;
import org.iqnect.iqkit.search.SearchService;
import org.iqnect.iqkit.ui.app.SearchResultActivity;
import org.iqnect.iqkit.ui.camera.IqKitScannerActivity;
import org.iqnect.iqkit.util.ServiceUtils;

import java.io.IOException;

import retrofit.mime.TypedFile;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static org.iqnect.iqkit.util.BitmapUtils.getSizeRestrictedBitmap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_CODE_CHOOSE_IMAGE = 100;
    private static final int LONGEST_SIDE_DESIRED_PIXELS = 800;

    @NonNull
    private View mProgressBarContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBarContainer = findViewById(R.id.progress_bar_container);
        Log.d(TAG, "activity created");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_CHOOSE_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        Log.e(TAG,"No uri returned, cannot search");
                        break;
                    }
                    searchBitmap(uri);
                }
                break;
        }

        Log.d(TAG, "activity result: " + data);
    }

    @NonNull
    private SearchService getSearchService() {
        return ((Application) getApplication()).getIQKit().getSearchService();
    }

    @SuppressWarnings("unused")
    public void onButtonPressed(@NonNull View view) {
        int viewId = view.getId();

        if (viewId == R.id.button_open_scanner) {
            IqKitScannerActivity.start(this);
        } else if(viewId == R.id.button_select_image) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_CODE_CHOOSE_IMAGE);
        }
    }

    private void searchBitmap(@NonNull Uri uri) {
        Bitmap result = null;
        try {
            result = getSizeRestrictedBitmap(this, uri, LONGEST_SIDE_DESIRED_PIXELS, false, false);
            TypedFile typedFile = ServiceUtils.typedFile(this, result);
            performSearch(getSearchService().searchByImage(typedFile));
        } catch (IOException e) {
            Log.e(TAG, "Error searching for image", e);
            showToast("Error: " + e.getMessage());
        } finally {
            if (result != null) {
                result.recycle();
            }
        }
    }

    private void performSearch(@NonNull Observable<SearchResult> observable) {
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressBarContainer.setVisibility(VISIBLE);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgressBarContainer.setVisibility(GONE);
                    }
                })
                .subscribe(new Action1<SearchResult>() {
                    @Override
                    public void call(@Nullable SearchResult searchResult) {
                        if (searchResult != null) {
                            Intent searchResultIntent = SearchResultActivity.getSearchResultIntent(MainActivity.this, searchResult);
                            startActivity(searchResultIntent);
                        } else {
                            showToast("No match found");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (throwable instanceof NotFoundResponseException) {
                            showToast("No match found");
                        } else {
                            showToast("Error: " + throwable.getMessage());
                        }
                    }
                });
    }

    private void showToast(@NonNull String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}

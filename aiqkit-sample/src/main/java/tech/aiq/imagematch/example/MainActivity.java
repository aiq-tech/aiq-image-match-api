package tech.aiq.imagematch.example;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import tech.aiq.imagematch.api.ImageMatchService;
import tech.aiq.imagematch.ui.ImageMatchUI;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CAPTURE_IMAGE = 101;
    private static final int REQUEST_CODE_CHOOSE_IMAGE = 100;
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
            case REQUEST_CAPTURE_IMAGE:

                if(resultCode == RESULT_OK) {

                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    //scale up the bitmap large enough for testing
                    imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth() * 3, imageBitmap.getHeight()* 3, true);
                    searchBitmap(imageBitmap);
                }
                break;
            default:
                break;
        }
    }

    @SuppressWarnings("unused")
    public void onButtonPressed(@NonNull View view) {
        int viewId = view.getId();

        if (viewId == R.id.button_open_scanner) {
            // start the image match ui
            ImageMatchUI.start(this);
        }
        else if(viewId == R.id.button_take_image) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
        }
        else if(viewId == R.id.button_select_image) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.select_image)), REQUEST_CODE_CHOOSE_IMAGE);
        }
    }

    private void searchBitmap(@NonNull Uri uri) {
        // match image by image file
        performSearch(ImageMatchService.matchImage(uri));
    }
    private void searchBitmap(@NonNull Bitmap bitmap) {
        // match image by a image in memory
        performSearch(ImageMatchService.matchImage(bitmap));
    }
    private void performSearch( Observable<String> observable) {
        if(observable == null)
            return;
        // handle the search result
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
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(@Nullable String searchResult) {
                        // take the returned payload as url and show it in browser window
                        if (searchResult != null) {
                            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchResult));
                            startActivity(myIntent);
                        } else {
                            showToast("No match found");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        // check if the result is "no match found'
                        if(ImageMatchService.isImageNotFoundError(throwable)) {
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

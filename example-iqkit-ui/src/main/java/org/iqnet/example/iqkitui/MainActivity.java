package org.iqnet.example.iqkitui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import org.iqnect.iqkit.model.SearchResult;
import org.iqnect.iqkit.ui.ScannerActivity;
import org.iqnect.iqkit.ui.web.WebActivity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        log.debug("activity created");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        log.debug("activity result: {}", data);
        ScannerActivity.handleScannerResult(requestCode, resultCode, data, new ScannerActivity.ScannerResultHandler() {
            @Override
            public void onSearchResult(@Nullable SearchResult searchResult) {
                log.debug("search result: {}", searchResult);

                if (searchResult != null) {
                    WebActivity.start(MainActivity.this, searchResult.getSearchbarTitle(), searchResult.getPayloadUri());
                } else {
                    showToast("no result from image search");
                }
            }

            @Override
            public void onError(Throwable throwable) {
                log.debug("error: {}", throwable);
            }
        });
    }

    public void onButtonPressed(View view) {
        int viewId = view.getId();

        if(viewId == R.id.button_start_iqkit) {
            org.iqnect.iqkit.ui.MainActivity.start(this);
        } else if(viewId == R.id.button_start_scanner) {
            ScannerActivity.start(this);
        }
    }

    private void showToast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}

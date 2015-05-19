package org.iqnect.example.iqkitcore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.iqnect.iqkit.model.SearchResult;
import org.iqnect.iqkit.search.SearchService;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchText;
    private TextView mResponseView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mSearchText = (EditText) findViewById(R.id.search_text);
        mResponseView = (TextView) findViewById(R.id.response);
    }

    public void onButtonPressed(View button) {
        if(button.getId() == R.id.button_search) {
            sendSearchRequest(mSearchText.getText().toString());
        }
    }

    private SearchService getSearchService() {
        return ((Application) getApplication()).getIQKit().getSearchService();
    }

    private void sendSearchRequest(String query) {
        if(query == null || query.isEmpty())
            return;

        getSearchService().searchByKeyword(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<SearchResult>() {
                    @Override
                    public void call(@Nullable SearchResult searchResult) {
                        if(searchResult == null) {
                            mResponseView.setText("not found response");
                        } else {
                            mResponseView.setText("response:\n" + searchResult.getPayloadUri().toString());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mResponseView.setText("error:\n" + throwable.getLocalizedMessage());
                    }
                });
    }
}

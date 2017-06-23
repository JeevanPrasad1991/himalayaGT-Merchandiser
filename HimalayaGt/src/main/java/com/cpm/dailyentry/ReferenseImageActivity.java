package com.cpm.dailyentry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.xmlGetterSetter.WindowListGetterSetter;

/**
 * Created by jeevanp on 30-03-2017.
 */

public class ReferenseImageActivity extends AppCompatActivity {
    FloatingActionButton fab;
    WebView webView;
    Bitmap b;
    String str;
    ProgressDialog pd;
    GSKDatabase db;
   WindowListGetterSetter current;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refrence_layout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        pd= ProgressDialog.show(this, "", "Please wait......", true);
        webView = (WebView) findViewById(R.id.imageView4);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new GSKDatabase(getApplicationContext());
        db.open();
        current = (WindowListGetterSetter) getIntent().getSerializableExtra("OBJECT");
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        if(current.getPlanogram_image()!=null || !current.getPlanogram_image().equals("")){
            webView.loadUrl(current.getPlanogram_image());
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent im=new Intent(ReferenseImageActivity.this,ChecklistActivity.class);
                im.putExtra("OBJECT", current);
                startActivity(im);
                finish();
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }
        });
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            pd.dismiss();
            super.onPageFinished(view, url);
            view.clearCache(true);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            pd.show();
            super.onPageStarted(view, url, favicon);
        }


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
        startActivity(new Intent(ReferenseImageActivity.this,SecondaryWindowActivity.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
       startActivity(new Intent(ReferenseImageActivity.this,SecondaryWindowActivity.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
}



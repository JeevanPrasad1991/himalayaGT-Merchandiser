package com.cpm.dailyentry;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.cpm.Constants.CommonString;
import com.cpm.capitalfoods.R;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ashishc on 31-08-2016.
 */
public class PlanogramActivity extends AppCompatActivity {
    FloatingActionButton fab;
    WebView webView;
    Bitmap b;
    String str;
    ProgressDialog pd;
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planogramlayout);
        str = getIntent().getExtras().getString("Plalogram");
        fab = (FloatingActionButton) findViewById(R.id.fab);
        pd= ProgressDialog.show(PlanogramActivity.this, "", "Please wait......", true);
        webView = (WebView) findViewById(R.id.imageView3);
        webView.setWebViewClient(new MyWebViewClient());
        //String url = CommonString1.URL_Notice_Board;
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);

        if(str!=null || !str.equals("")){
            webView.loadUrl(str);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

   /* public class information extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(PlanogramActivity.this);
            dialog.setMessage("Please wait....");
            dialog.show();
            dialog.setCancelable(true);
        }

        @Override
        protected String doInBackground(String... arg0) {
            dialog.dismiss();
            try {
                URL url = new URL(str);
                InputStream is = new BufferedInputStream(url.openStream());
                b = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
           // imageview.setImageBitmap(b);
        }
    }
*/

}

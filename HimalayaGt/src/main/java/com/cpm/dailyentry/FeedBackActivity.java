package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.xmlGetterSetter.FeedBackGetterSetter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jeevanp on 03-04-2017.
 */

public class FeedBackActivity extends AppCompatActivity implements View.OnClickListener{
    private SharedPreferences preferences;
    String store_cd, visit_date, intime,str="",username;
    GSKDatabase db;
    EditText edit_feedback;
    Button btn_feedback;
    FeedBackGetterSetter feedBackGetterSetter=new FeedBackGetterSetter();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        edit_feedback= (EditText) findViewById(R.id.edit_feedback);
        btn_feedback= (Button) findViewById(R.id.btn_feedback);
        btn_feedback.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username = preferences.getString(CommonString1.KEY_USERNAME, null);
        str = CommonString1.FILE_PATH;
        db = new GSKDatabase(this);
        db.open();
        feedBackGetterSetter= db.getfeedbackData(store_cd);
        if (!feedBackGetterSetter.getFeedBack().equalsIgnoreCase("")){
            edit_feedback.setText(feedBackGetterSetter.getFeedBack());
            btn_feedback.setText("Update");
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(FeedBackActivity.this,StoreEntry.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FeedBackActivity.this,StoreEntry.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();


    }


    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":" + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);
        return intime;

    }
    boolean validate(){
        boolean feedback=true;
        if (edit_feedback.getText().toString().equalsIgnoreCase("")){
            feedback=false;
            Snackbar.make(btn_feedback,"Please enter feedback",Snackbar.LENGTH_LONG).show();
        }else {
            feedback=true;
        }

        return feedback;
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if (id==R.id.btn_feedback){
            if (validate()){
                final AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage("Do you want to save feedback data");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.insertfeedbackData(store_cd,username,visit_date,edit_feedback.getText().toString().trim().replaceAll("[&^<>{}'$]", " "));
                        Snackbar.make(btn_feedback,"Data successfully saved",Snackbar.LENGTH_LONG).show();
                        startActivity(new Intent(FeedBackActivity.this,StoreEntry.class));
                        finish();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }

        }

    }
}

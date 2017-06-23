package com.cpm.dailyentry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.xmlGetterSetter.WindowListGetterSetter;

import java.util.ArrayList;

/**
 * Created by jeevanp on 21-01-2017.
 */

public class WindowListActivity extends AppCompatActivity {
    GSKDatabase database;
    ArrayList<WindowListGetterSetter> windowList;
    RecyclerView recyclerView;
    WindowAdapter windowAdapter;
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    String value = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_window_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Window Planogram");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        database = new GSKDatabase(this);
        database.open();
        windowList = database.getWindowListData();
        recyclerView = (RecyclerView) findViewById(R.id.rec_window);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        //value = preferences.getString(CommonString.KEY_BACK_PRESS, null);

        if (windowList.size() > 0) {

            windowAdapter = new WindowAdapter();
            recyclerView.setAdapter(windowAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        }
    }

    class WindowAdapter extends RecyclerView.Adapter<WindowAdapter.ViewHolder> {

        private LayoutInflater inflator;

        WindowAdapter() {
            inflator = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.window_list_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final WindowListGetterSetter current = windowList.get(position);
            holder.tv_window.setText(current.getWindow());
            holder.parent_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(), WindowPlanogramActivity.class);
                    in.putExtra(CommonString.KEY_WINDOW_NAME, current.getWindow());
                    in.putExtra(CommonString.KEY_WINDOW_IMAGE, current.getPlanogram_image());
                    startActivity(in);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                }
            });

        }

        @Override
        public int getItemCount() {
            return windowList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_window;
            RelativeLayout parent_layout;
            public ViewHolder(View itemView) {
                super(itemView);
                tv_window = (TextView) itemView.findViewById(R.id.tv_window);
                parent_layout = (RelativeLayout) itemView.findViewById(R.id.parent_layout);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.empty_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}

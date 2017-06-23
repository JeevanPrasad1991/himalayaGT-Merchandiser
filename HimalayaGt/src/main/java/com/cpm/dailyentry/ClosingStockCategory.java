package com.cpm.dailyentry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cpm.Constants.CommonString;
import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.geotag.LocationActivity;
import com.cpm.xmlGetterSetter.CategoryMasterGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by jeevanp on 27-01-2017.
 */

public class ClosingStockCategory extends AppCompatActivity implements AdapterView.OnItemClickListener {
    Toolbar toolbar;
    ListView stockcategory_list;
    GSKDatabase db;
    private SharedPreferences preferences;
    String username, store_cd, visit_date, intime;
    ArrayList<CategoryMasterGetterSetter> categorymaster_list = new ArrayList<>();
    String STATE_CD, STORE_TYPE_CD;
    boolean stock_falg = false;
    StockNewGetterSetter stock_list=new StockNewGetterSetter();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutstock_category);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        stockcategory_list = (ListView) findViewById(R.id.stockcategory_list);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username = preferences.getString(CommonString1.KEY_USERNAME, null);
        intime = preferences.getString(CommonString1.KEY_STORE_IN_TIME, "");
        STORE_TYPE_CD = preferences.getString(CommonString1.KEY_STORE_TYPE_CD, "");
        STATE_CD = preferences.getString(CommonString1.KEY_STATE_CD, "");
        db = new GSKDatabase(this);
        db.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        categorymaster_list = db.getCategoryData(STATE_CD, STORE_TYPE_CD);
        if (categorymaster_list.size() > 0) {
            stockcategory_list.setAdapter(new MyAdapter());
            stockcategory_list.setOnItemClickListener(this);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(ClosingStockCategory.this, StockAvailability.class);
        intent.putExtra(CommonString1.KEY_CATEGORY_ID, categorymaster_list.get(position).getCategory_cd().get(0));
        startActivity(intent);
        finish();
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return categorymaster_list.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layoutcategory_item, null);
                holder.category = (TextView) convertView.findViewById(R.id.tvstorename1);
                holder.closebtn = (ImageView) convertView.findViewById(R.id.closechkin1);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            stock_list=db.getStockData(store_cd, categorymaster_list.get(position).getCategory_cd().get(0));
            if (stock_list.getCATEGORY_CD() !=null){
                holder.closebtn.setVisibility(View.VISIBLE);
            }
            else {
                holder.closebtn.setVisibility(View.GONE);
            }
            holder.category.setText(categorymaster_list.get(position).getCategory().get(0));
            holder.category.setId(position);
        return convertView;
    }

    private class ViewHolder {
        TextView category;
        ImageView closebtn;
    }

}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(ClosingStockCategory.this,StoreEntry.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ClosingStockCategory.this);
        builder.setMessage("Do you want to exits")
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                startActivity(new Intent(ClosingStockCategory.this, StoreEntry.class));
                                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
                                finish();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

}





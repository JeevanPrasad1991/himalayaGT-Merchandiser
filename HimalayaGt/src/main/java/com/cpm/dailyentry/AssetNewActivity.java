package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.xmlGetterSetter.AssetInsertdataGetterSetter;
import com.cpm.xmlGetterSetter.AssetMappingGetterSetter;
import com.cpm.xmlGetterSetter.AssetMasterNewGetterSetter;
import com.cpm.xmlGetterSetter.WindowListGetterSetter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AssetNewActivity extends AppCompatActivity {
    GSKDatabase db;
    private SharedPreferences preferences;
    String store_cd, visit_date, username, intime, state_cd;
    String _pathforcheck, _path, str, img1 = "";
    RecyclerView rec_recyclerV;
    Button btnSave;
    ImageView img;
    ArrayList<AssetMappingGetterSetter> listDataHeader = new ArrayList<>();
    ValueAdapter adapter;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asset_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rec_recyclerV = (RecyclerView) findViewById(R.id.rec_recyclerV);
        btnSave = (Button) findViewById(R.id.save_btn);
        img = (ImageView) findViewById(R.id.imgnodata);
        db = new GSKDatabase(getApplicationContext());
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username = preferences.getString(CommonString1.KEY_USERNAME, null);
        // preparing list data
        // prepareListData();
        listDataHeader = db.getassetCategoryData(store_cd);
        if (listDataHeader.size() > 0) {
            adapter = new ValueAdapter(getApplicationContext(), listDataHeader);
            rec_recyclerV.setAdapter(adapter);
            rec_recyclerV.setLayoutManager(new LinearLayoutManager(this));
           // rec_recyclerV.setAdapter(new MyAdapterAsset());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            startActivity(new Intent(AssetNewActivity.this, StoreEntry.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AssetNewActivity.this, StoreEntry.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }

    public class ValueAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private LayoutInflater inflator;
        List<AssetMappingGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, ArrayList<AssetMappingGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.item_asset, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
            final AssetMappingGetterSetter current = data.get(position);
            viewHolder.asset_name.setText(current.getAsset().get(0));
            viewHolder.asset_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(getApplicationContext(), SubAssetCategoryActivity.class);
                    in.putExtra("OBJECT",  current);
                    startActivity(in);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    finish();
                }
            });

            if (!img1.equalsIgnoreCase("")) {
               /* if (position == child_position) {
                    //childText.get(childPosition).setCamera("YES");
                    current.setWindow_image(img1);
                    //childText.setImg(img1);
                    img1 = "";

                }*/
            }


        }

        @Override
        public int getItemCount() {
            return data.size();
        }

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView asset_name;
        CardView crd_v;
        public MyViewHolder(View itemView) {
            super(itemView);
            asset_name = (TextView) itemView.findViewById(R.id.asset_name);
            crd_v = (CardView) itemView.findViewById(R.id.crd_v);
        }
    }


/*
    private class MyAdapterAsset extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listDataHeader.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(final int position, View convertView, final ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_asset, null);
                holder.asset_name = (TextView) convertView.findViewById(R.id.asset_name);
                holder.crd_v = (CardView) convertView.findViewById(R.id.crd_v);
               */
/* holder.remark_edtt = (EditText) convertView.findViewById(R.id.remark_edtt);
                holder.rl_cam = (LinearLayout) convertView.findViewById(R.id.rl_cam);
                holder.checkboxlist1 = (CheckBox) convertView.findViewById(R.id.checkboxlist1);
                holder.image_asset = (ImageButton) convertView.findViewById(R.id.image_asset);
                holder.refimage1 = (ImageButton) convertView.findViewById(R.id.refimage1);*//*

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.crd_v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AssetNewActivity.this, SubAssetCategoryActivity.class);

                }
            });
           */
/* holder.remark_edtt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {

                }
            });
            holder.image_asset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    _path = CommonString1.FILE_PATH + _pathforcheck;
                    startCameraActivity();
                }
            });
            holder.refimage1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(AssetNewActivity.this,AssetReferenceActivity.class);
                    intent.putExtra("KEY",listDataHeader.get(position).getAdditional_image());
                    startActivity(intent);

                }
            });
            final ViewHolder finalHolder = holder;
            holder.checkboxlist1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                   if (isChecked){
                       finalHolder.rl_cam.setVisibility(View.INVISIBLE);
                       listDataHeader.get(position).setIslisted(true);
                       //finalHolder.rl_cam.
                   }else {
                       finalHolder.rl_cam.setVisibility(View.VISIBLE);
                       listDataHeader.get(position).setIslisted(false);
                   }

               }
           });*//*

            holder.asset_name.setText(listDataHeader.get(position).getAsset().get(0));
            //holder.remark_edtt.setText(listDataHeader.get(position).getRemark());
            holder.asset_name.setId(position);
            //  holder.remark_edtt.setId(position);
            return convertView;
        }

        private class ViewHolder {
            TextView asset_name;
            EditText remark_edtt;
            LinearLayout rl_cam;
            CheckBox checkboxlist1;
            ImageButton image_asset, refimage1;
            CardView crd_v;

        }


    }
*/


}

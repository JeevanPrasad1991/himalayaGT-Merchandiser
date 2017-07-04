package com.cpm.dailyentry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cpm.Constants.CommonString1;
import com.cpm.GetterSetter.NavMenuItemGetterSetter;
import com.cpm.database.GSKDatabase;
import com.cpm.capitalfoods.R;
import com.cpm.geotag.LocationActivity;
import com.cpm.xmlGetterSetter.AssetMappingGetterSetter;
import com.cpm.xmlGetterSetter.AssetMasterNewGetterSetter;
import com.cpm.xmlGetterSetter.CategoryMasterGetterSetter;
import com.cpm.xmlGetterSetter.StockGetterSetter;
import com.cpm.xmlGetterSetter.WindowListGetterSetter;
import com.cpm.xmlGetterSetter.WindowSKUEntryGetterSetter;

public class StoreEntry extends AppCompatActivity implements OnClickListener {
    GSKDatabase db;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor = null;
    String store_cd, state_cd, store_type_cd;
    boolean food_flag;
    String user_type = "", GEO_TAG = "", date = "";
    private ArrayList<StockGetterSetter> stockData = new ArrayList<StockGetterSetter>();
    ValueAdapter adapter;
    RecyclerView recyclerView;
    private ArrayList<WindowListGetterSetter> windowdata = new ArrayList<WindowListGetterSetter>();
    List<WindowListGetterSetter> WINDOWSIZE = new ArrayList<>();
    ArrayList<AssetMappingGetterSetter> listassetMasterData = new ArrayList<>();
    ArrayList<CategoryMasterGetterSetter> categorymaster_list = new ArrayList<>();
    boolean stock_flag = false, kpi_flag = false, geoTag_flag = false, poasm_flage = false, asset_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_item_recycle_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new GSKDatabase(getApplicationContext());
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        store_type_cd = preferences.getString(CommonString1.KEY_STORE_TYPE_CD, null);
        state_cd = preferences.getString(CommonString1.KEY_STATE_CD, null);
        food_flag = preferences.getBoolean(CommonString1.KEY_FOOD_STORE, false);
        GEO_TAG = preferences.getString(CommonString1.KEY_GEO_TAG, null);
        date = preferences.getString(CommonString1.KEY_DATE, null);
        user_type = preferences.getString(CommonString1.KEY_USER_TYPE, null);
        if (user_type != null) {
        }
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        windowdata = db.getWindowListData(state_cd, store_type_cd);
        WINDOWSIZE = db.getwindowdat(store_cd);
        listassetMasterData = db.getassetCategoryData(store_cd);
        recyclerView = (RecyclerView) findViewById(R.id.drawer_layout_recycle);
        adapter = new ValueAdapter(getApplicationContext(), getdata());
        recyclerView.setAdapter(adapter);
       /* if (stock_flag && kpi_flag && geoTag_flag && poasm_flage) {
            db.updateStoreStatusOnCheckout(store_cd, date, CommonString1.KEY_VALID);
        }*/

        recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        if (checkallData()) {
            db.updateCoverageCheckoutStatus(store_cd, date, CommonString1.KEY_VALID);
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, DailyEntryScreen.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        int id = view.getId();
        switch (id) {
            case R.id.openingstock:
                if (!db.isClosingDataFilled(store_cd)) {
                    Intent in1 = new Intent(getApplicationContext(), StockAvailability.class);
                    startActivity(in1);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                } else {
                    Snackbar.make(recyclerView, "Data cannot be changed", Snackbar.LENGTH_SHORT).show();
                }

                break;
            case R.id.closingstock:
                stockData = db.getOpeningStock(store_cd);
                if ((stockData.size() <= 0) || (stockData.get(0).getOpen_stock_cold_room() == null) || (stockData.get(0).getOpen_stock_cold_room().equals(""))) {
                    Snackbar.make(recyclerView,
                            "First Fill Opening Stock and Midday Stock Data", Snackbar.LENGTH_SHORT).show();

                } else {
                    stockData = db.getMiddayStock(store_cd);
                    if ((stockData.size() <= 0) || (stockData.get(0).getMidday_stock() == null) || (stockData.get(0).getMidday_stock().equals(""))) {
                        Snackbar.make(recyclerView,
                                "First Fill Opening Stock and Midday Stock Data", Snackbar.LENGTH_SHORT).show();

                    } else {
                        Intent in2 = new Intent(getApplicationContext(), ClosingStock.class);
                        startActivity(in2);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                    }
                }


                break;


            case R.id.midstock:
                if (!db.isClosingDataFilled(store_cd)) {
                    Intent in3 = new Intent(getApplicationContext(), MidDayStock.class);
                    startActivity(in3);
                    overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                } else {
                    Snackbar.make(recyclerView, "Data cannot be changed", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case R.id.prommotion:
                Intent in4 = new Intent(getApplicationContext(), WindowsActivity.class);
                startActivity(in4);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                break;

          /*  case R.id.assets:

                Intent in5 = new Intent(getApplicationContext(), AssetNewActivity.class);

                startActivity(in5);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                break;*/

            case R.id.foodstore:

                Intent in6 = new Intent(getApplicationContext(), FoodStore.class);

                startActivity(in6);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                break;

            case R.id.facingcompetitor:

                Intent in7 = new Intent(getApplicationContext(), FacingCompetitor.class);

                startActivity(in7);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                break;

            case R.id.calls:

                Intent in8 = new Intent(getApplicationContext(), CallsActivity.class);

                startActivity(in8);

                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);

                break;


            case R.id.performance:

                Intent startPerformance = new Intent(StoreEntry.this, Performance.class);
                startActivity(startPerformance);
                overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_planogram_item) {
            startActivity(new Intent(StoreEntry.this, WindowListActivity.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        } else if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            startActivity(new Intent(this, DailyEntryScreen.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();


        }

        return super.onOptionsItemSelected(item);
    }


    public class ValueAdapter extends RecyclerView.Adapter<ValueAdapter.MyViewHolder> {

        private LayoutInflater inflator;

        List<NavMenuItemGetterSetter> data = Collections.emptyList();

        public ValueAdapter(Context context, List<NavMenuItemGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;

        }

        @Override
        public ValueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
            View view = inflator.inflate(R.layout.custom_row, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final ValueAdapter.MyViewHolder viewHolder, final int position) {
            final NavMenuItemGetterSetter current = data.get(position);
            //viewHolder.txt.setText(current.txt);
            viewHolder.icon.setImageResource(current.getIconImg());
            viewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //category list activity
                    if (current.getIconImg() == R.drawable.stock1 || current.getIconImg() == R.drawable.stock_done1) {
                        if (!db.isClosingDataFilled(store_cd)) {
                            Intent in1 = new Intent(getApplicationContext(), ClosingStockCategory.class);
                            startActivity(in1);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            finish();
                        } else {
                            Snackbar.make(recyclerView, "Data cannot be changed", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                   /* if (current.getIconImg() == R.drawable.midday_stock || current.getIconImg() == R.drawable.midday_stock_done) {
                        if (!db.isClosingDataFilled(store_cd)) {
                            Intent in3 = new Intent(getApplicationContext(), MidDayStock.class);
                            startActivity(in3);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        } else {
                            Snackbar.make(recyclerView, "Data cannot be changed", Snackbar.LENGTH_SHORT).show();
                        }


                    }*/
                    //secondry window activity
                    if (current.getIconImg() == R.drawable.windows || current.getIconImg() == R.drawable.window_done) {
                        Intent in4 = new Intent(getApplicationContext(), SecondaryWindowActivity.class);
                        startActivity(in4);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }

                    //Asset activity
                    if (current.getIconImg() == R.drawable.asset_new || current.getIconImg() == R.drawable.asset_new_done) {
                        Intent in4 = new Intent(getApplicationContext(), AssetNewActivity.class);
                        startActivity(in4);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }

                    //feedback
                    if (current.getIconImg() == R.drawable.feedback || current.getIconImg() == R.drawable.feedback_done) {
                        Intent in5 = new Intent(getApplicationContext(), FeedBackActivity.class);
                        startActivity(in5);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }
                    //posm activity
                    if (current.getIconImg() == R.drawable.posm1 || current.getIconImg() == R.drawable.posm_done1) {
                        Intent in5 = new Intent(getApplicationContext(), POSMActivity.class);
                        startActivity(in5);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }

                    //AdditionalVisibility  activity
                    if (current.getIconImg() == R.drawable.additional_visibility || current.getIconImg() == R.drawable.additional_visibility_done) {
                        Intent in5 = new Intent(getApplicationContext(), AdditionalVisibilityActivity.class);
                        startActivity(in5);
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                        finish();
                    }


                    if (current.getIconImg() == R.drawable.geotag1 || current.getIconImg() == R.drawable.geotag_done1) {
                        if (GEO_TAG.equalsIgnoreCase(CommonString1.KEY_GEO_Y)) {
                            Snackbar.make(recyclerView, "GoeTag Already Done", Snackbar.LENGTH_SHORT).show();
                        } else if (db.getGeotaggingData(store_cd).size() > 0) {

                            if (db.getGeotaggingData(store_cd).get(0).getGEO_TAG().equalsIgnoreCase(CommonString1.KEY_GEO_Y)) {
                                Snackbar.make(recyclerView, "GoeTag Already Done", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Intent in4 = new Intent(getApplicationContext(), LocationActivity.class);
                            startActivity(in4);
                            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                            finish();
                        }


                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return data.size();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {

            //TextView txt;
            ImageView icon;

            public MyViewHolder(View itemView) {
                super(itemView);
                icon = (ImageView) itemView.findViewById(R.id.list_icon);
            }
        }
    }

    public boolean checkallData() {
        boolean alldata_flag = true;
        if (windowdata.size() > 0) {
            for (int k = 0; k < windowdata.size(); k++) {
                kpi_flag = db.isOpeningDataFilledKpi(store_cd, windowdata.get(k).getWindow_cd().get(0));
                if (!kpi_flag) {
                    alldata_flag = false;
                    break;
                }
            }
        }
        categorymaster_list = db.getCategoryData(state_cd, store_type_cd);
        for (int i = 0; i < categorymaster_list.size(); i++) {
            stock_flag = db.isOpeningDataFilled(store_cd, state_cd, categorymaster_list.get(i).getCategory_cd().get(0));
            if (!stock_flag) {
                alldata_flag = false;
                break;
            }
        }

//Asset cagegory
        if (listassetMasterData.size() > 0) {
            for (int k = 0; k < listassetMasterData.size(); k++) {
                asset_flag = db.isOpeningDataFilledAsset(store_cd, listassetMasterData.get(k).getAsset_cd().get(0));
                if (!asset_flag) {
                    alldata_flag = false;
                    break;
                }
            }
        }


        if (!GEO_TAG.equalsIgnoreCase(CommonString1.KEY_GEO_Y)) {
            if (db.getGeotaggingData(store_cd).size() == 0) {
                alldata_flag = false;
            }
        }
        //  posm data image
        poasm_flage = db.isPOSMDataFilled(store_cd);
        if (!poasm_flage) {
            alldata_flag = false;
        }
        return alldata_flag;
    }

    public List<NavMenuItemGetterSetter> getdata() {
        List<NavMenuItemGetterSetter> data = new ArrayList<>();
        int windows = 0, posmImg, Stock = 0, additionalvisibility, assetImg = 0, geotag = 0, feedback = 0;
        if (windowdata.size() > 0) {
            for (int k = 0; k < windowdata.size(); k++) {
                kpi_flag = db.isOpeningDataFilledKpi(store_cd, windowdata.get(k).getWindow_cd().get(0));
                if (kpi_flag == false) {
                    windows = R.drawable.windows;
                    break;
                } else {
                    kpi_flag = true;
                    windows = R.drawable.window_done;
                }
            }
        }

//Asset cagegory
        if (listassetMasterData.size() > 0) {
            for (int k = 0; k < listassetMasterData.size(); k++) {
                asset_flag = db.isOpeningDataFilledAsset(store_cd, listassetMasterData.get(k).getAsset_cd().get(0));
                if (!asset_flag) {
                    assetImg = R.drawable.asset_new;
                    break;
                } else {
                    asset_flag = true;
                    assetImg = R.drawable.asset_new_done;
                }
            }
        }


        // Store additional visibility
        if (db.isAdditionalVisibilityDataFilled(store_cd)) {
            additionalvisibility = R.drawable.additional_visibility_done;
        } else {
            additionalvisibility = R.drawable.additional_visibility;
        }

        //  posm data image
        if (db.isPOSMDataFilled(store_cd)) {
            poasm_flage = true;
            posmImg = R.drawable.posm_done1;
        } else {
            posmImg = R.drawable.posm1;
        }

        categorymaster_list = db.getCategoryData(state_cd, store_type_cd);
        for (int i = 0; i < categorymaster_list.size(); i++) {
            stock_flag = db.isOpeningDataFilled(store_cd, state_cd, categorymaster_list.get(i).getCategory_cd().get(0));
            if (stock_flag == false) {
                Stock = R.drawable.stock1;
                break;
            } else {
                stock_flag = true;
                Stock = R.drawable.stock_done1;
            }
        }

        if (db.getGeotaggingData(store_cd).size() > 0) {
            if (db.getGeotaggingData(store_cd).get(0).getGEO_TAG().equalsIgnoreCase(CommonString1.KEY_GEO_Y)) {
                geotag = R.drawable.geotag_done1;
                geoTag_flag = true;
            } else {
                geotag = R.drawable.geotag1;
            }
        } else {
            if (GEO_TAG.equalsIgnoreCase(CommonString1.KEY_GEO_Y)) {
                geoTag_flag = true;
                geotag = R.drawable.geotag_done1;
            } else {
                geotag = R.drawable.geotag1;
            }
        }


       /* if (db.isClosingDataFilled(store_cd)) {
            closingImg = R.drawable.closing_stock_done;
        } else {
            closingImg = R.drawable.closing_stock;
        }
        if (db.isMiddayDataFilled(store_cd)) {
            middayImg = R.drawable.midday_stock_done;
        } else {
            middayImg = R.drawable.midday_stock;
        }*/

        if (db.isFeedbackDataFilled(store_cd)) {
            feedback = R.drawable.feedback_done;
        } else {
            feedback = R.drawable.feedback;
        }


        if (user_type.equals("Promoter")) {
            // int img[] = {Stock, middayImg, windows, closingImg,additionalImg};
            int img[] = {Stock, windows, posmImg, feedback};
            for (int i = 0; i < img.length; i++) {
                NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
                recData.setIconImg(img[i]);
                data.add(recData);
            }
        } else if (user_type.equals("Merchandiser")) {
            int img[] = {windows, posmImg, Stock, geotag, feedback, additionalvisibility, assetImg};
            for (int i = 0; i < img.length; i++) {
                NavMenuItemGetterSetter recData = new NavMenuItemGetterSetter();
                recData.setIconImg(img[i]);
                data.add(recData);
            }
        }

        return data;
    }


}

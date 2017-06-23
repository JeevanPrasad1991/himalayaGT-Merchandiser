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
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.xmlGetterSetter.AssetMappingGetterSetter;
import com.cpm.xmlGetterSetter.BrandGetterSetter;
import com.cpm.xmlGetterSetter.POSMDATAGetterSetter;
import com.cpm.xmlGetterSetter.SkuMasterGetterSetter;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class SubAssetCategoryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    FloatingActionMenu fab_mainMenu;
    String _pathforcheck = "", _path = "", str, state_cd, img1 = "", updat = "", a1, a2;
    Boolean existOrnot = true;
    String value1 = "";
    String store_cd, visit_date, username, store_type_cd, category_cd, planogram, asset_cd, validatebrandYesNo, validateSkuYesNo, Error_Message;
    private SharedPreferences preferences;
    GSKDatabase db;
    AssetMappingGetterSetter current;
    EditText remark_edtt;
    LinearLayout rl_cam;
    CheckBox checkboxlist1;
    ImageButton image_asset, refimage1;
    Spinner brand_spinner, sku_spinner;
    EditText edt_stock;
    ListView stock_list;
    AssetMappingGetterSetter assetMappingGetterSetter = new AssetMappingGetterSetter();
    ArrayList<BrandGetterSetter> brandList = new ArrayList<>();
    ArrayList<SkuMasterGetterSetter> skuMasterGetterSetterArrayList = new ArrayList<>();
    List<String> spinnerBranddatalist = new ArrayList<String>();
    List<String> spinnerSkudatalist = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adaptersku;
    ArrayList<BrandGetterSetter> AddedAlldata = new ArrayList<>();
    ArrayList<BrandGetterSetter> insertedStockListData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_asset_category);
        fab_mainMenu = (FloatingActionMenu) findViewById(R.id.fab_menu_orderDelivery);
        final com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab);
        final com.github.clans.fab.FloatingActionButton fab_sku = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.sku_save);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        remark_edtt = (EditText) findViewById(R.id.remark_edtt);
        rl_cam = (LinearLayout) findViewById(R.id.rl_cam);
        checkboxlist1 = (CheckBox) findViewById(R.id.checkboxlist1);
        image_asset = (ImageButton) findViewById(R.id.image_asset);
        refimage1 = (ImageButton) findViewById(R.id.refimage1);
        brand_spinner = (Spinner) findViewById(R.id.brand_spinner);
        sku_spinner = (Spinner) findViewById(R.id.sku_spinner);
        edt_stock = (EditText) findViewById(R.id.edt_stock);
        stock_list = (ListView) findViewById(R.id.stock_list);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username = preferences.getString(CommonString1.KEY_USERNAME, null);
        state_cd = preferences.getString(CommonString1.KEY_STATE_CD, null);
        store_type_cd = preferences.getString(CommonString1.KEY_STORE_TYPE_CD, null);
        str = CommonString1.FILE_PATH;
        db = new GSKDatabase(getApplicationContext());
        db.open();
        current = (AssetMappingGetterSetter) getIntent().getSerializableExtra("OBJECT");
        category_cd = current.getCategory_id().get(0);
        planogram = current.getAdditional_image().get(0);
        asset_cd = current.getAsset_cd().get(0);
        getSupportActionBar().setTitle(current.getAsset().get(0));
        _pathforcheck = store_cd + "ASSETIMG" + current.getAsset_cd().get(0) + "_" + visit_date.replace("/", "") +
                "_" + getCurrentTime().replace(":", "") + ".jpg";
        brandList = db.getBrandDataViaCategory_cd(state_cd, store_type_cd, current.getCategory_id().get(0));
        skuMasterGetterSetterArrayList = db.getAssetSkuMasterData(state_cd, store_type_cd, current.getCategory_id().get(0), brandList.get(0).getBrand_cd().get(0));
        a1 = "-Select Brand-";
        spinnerBranddatalist.add(a1);
        for (int i = 0; i < brandList.size(); i++) {
            spinnerBranddatalist.add(brandList.get(i).getBrand().get(0));
        }
        adapter = new ArrayAdapter<String>(this, R.layout.spinner_custom_item, spinnerBranddatalist);
        adapter.setDropDownViewResource(R.layout.spinner_custom_item1);
        brand_spinner.setAdapter(adapter);
        brand_spinner.setOnItemSelectedListener(this);
        a2 = "-Select Sku-";
        spinnerSkudatalist.add(a2);
        for (int i = 0; i < skuMasterGetterSetterArrayList.size(); i++) {
            spinnerSkudatalist.add(skuMasterGetterSetterArrayList.get(i).getSku().get(0));
        }
        adaptersku = new ArrayAdapter<String>(this, R.layout.spinner_custom_item, spinnerSkudatalist);
        adaptersku.setDropDownViewResource(R.layout.spinner_custom_item1);
        sku_spinner.setAdapter(adaptersku);
        sku_spinner.setOnItemSelectedListener(this);
        fab.setOnClickListener(this);
        image_asset.setOnClickListener(this);
        refimage1.setOnClickListener(this);
        fab_sku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stock_list.clearFocus();
                stock_list.invalidateViews();
                if (validateskuandBrand()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SubAssetCategoryActivity.this);
                    builder.setMessage("Are you sure you want to save")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            db.open();
                                            BrandGetterSetter brandGetterSetter = new BrandGetterSetter();
                                            String brandspinValue = brand_spinner.getSelectedItem().toString();
                                            brandGetterSetter.setBrand(brandspinValue);
                                            String skuSpinValue = sku_spinner.getSelectedItem().toString();
                                            brandGetterSetter.setSku(skuSpinValue);
                                            long str = brand_spinner.getSelectedItemId();
                                            String strLongBrandid = Long.toString(str);
                                            long str1 = sku_spinner.getSelectedItemId();
                                            String strLongSkuId = Long.toString(str1);
                                            brandGetterSetter.setBrand_cd(strLongBrandid);
                                            brandGetterSetter.setSku_cd(strLongSkuId);
                                            brandGetterSetter.setSkuQuantity(edt_stock.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            AddedAlldata.add(brandGetterSetter);
                                            db.insertAssetStockData(store_cd, state_cd, category_cd, AddedAlldata);
                                            Snackbar.make(stock_list, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                            img1 = "";
                                            brand_spinner.setSelection(0);
                                            sku_spinner.setSelection(0);
                                            AddedAlldata.clear();
                                            brandGetterSetter.setSkuQuantity("");
                                            setDataToListView();
                                            insertedStockListData.clear();

                                        }
                                    })
                            .setNegativeButton("No",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();

                    alert.show();

                } else {
                    Snackbar.make(stock_list, Error_Message, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        setDataToListView();
        checkboxlist1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    assetMappingGetterSetter.setIslisted(true);
                    rl_cam.setVisibility(View.VISIBLE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SubAssetCategoryActivity.this);
                    builder.setMessage("Are you sure you want to close the window")
                            .setTitle("Parinaam")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    checkboxlist1.setChecked(false);
                                    rl_cam.setVisibility(View.INVISIBLE);
                                    assetMappingGetterSetter.setIslisted(false);
                                }

                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            checkboxlist1.setChecked(true);
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
        remark_edtt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    EditText Caption = (EditText) v;
                    value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                    if (!value1.equals("")) {
                    }
                }
            }
        });


    }
    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    public Boolean validateimagedata() {
        boolean isgood = true;
        if (assetMappingGetterSetter.getAsset_image().equalsIgnoreCase("")) {
            isgood = false;
            Snackbar.make(stock_list, "Please click image", Snackbar.LENGTH_LONG).show();
        } else if (assetMappingGetterSetter.islisted() == false) {
            if (remark_edtt.getText().toString().toString().equalsIgnoreCase("")) {
                isgood = false;
                Snackbar.make(stock_list, "Please enter remark", Snackbar.LENGTH_LONG).show();
            }
        } else {
            isgood = true;
        }
        return isgood;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refimage1:
                String str = current.getAdditional_image().get(0);
                Intent in = new Intent(SubAssetCategoryActivity.this, PlanogramActivity.class);
                in.putExtra("Plalogram", str);
                startActivity(in);
                break;
            case R.id.image_asset:
                _path = CommonString1.FILE_PATH + _pathforcheck;
                startCameraActivity();
                break;
            case R.id.fab:
                if (validateimagedata()) {
                    if (insertedStockListData.size()>0){
                        AlertDialog.Builder builder = new AlertDialog.Builder(SubAssetCategoryActivity.this);
                        builder.setMessage("Do you want to save checklist data");
                        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                    }else {
                        Snackbar.make(stock_list, "Please fill atleast one sku quantity ", Snackbar.LENGTH_LONG).show();
                    }

                }
        }

    }

    public void setDataToListView() {
        try {
            insertedStockListData = db.getAssetStockSavedData(store_cd, category_cd);
            if (insertedStockListData.size() > 0) {
                for (int i = 0; i < insertedStockListData.size(); i++) {
                    BrandGetterSetter secdata = new BrandGetterSetter();
                    secdata.setBrand(insertedStockListData.get(i).getBrand().get(0));
                    secdata.setSku(insertedStockListData.get(i).getSku().get(0));
                    secdata.setSkuQuantity(insertedStockListData.get(i).getSkuQuantity());
                    AddedAlldata.add(secdata);
                }
                Collections.reverse(AddedAlldata);
                stock_list.setAdapter(new MyAdapter());
                stock_list.invalidateViews();

            }


        } catch (Exception e) {
            Log.d("Exception when fetching", e.toString());
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
        switch (arg0.getId()) {
            case R.id.brand_spinner:
                validatebrandYesNo = brand_spinner.getSelectedItem().toString();
                break;
            case R.id.sku_spinner:
                validateSkuYesNo = sku_spinner.getSelectedItem().toString();
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), AssetNewActivity.class));
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(getApplicationContext(), AssetNewActivity.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();

        }
        return super.onOptionsItemSelected(item);
    }

    protected void startCameraActivity() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {
                        image_asset.setBackgroundResource(R.drawable.cam_icon_done);
                        img1 = _pathforcheck;
                        _pathforcheck = "";
                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    boolean validateskuandBrand() {
        boolean flag = true;
        String name = brand_spinner.getSelectedItem().toString();
        String sku_name = sku_spinner.getSelectedItem().toString();
        if (name.equalsIgnoreCase("-Select Brand-")) {
            Error_Message = "Please select Brand from dropdown list";
            flag = false;
        } else if (sku_name.equalsIgnoreCase("-Select Sku-")) {
            Error_Message = "Please select Sku from dropdown list";
            flag = false;
        } else if (edt_stock.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", "").equalsIgnoreCase("")) {
            Error_Message = "Please fill stock value";
            flag = false;
        } else {
            flag = true;
        }


        return flag;
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return AddedAlldata.size();
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
                convertView = inflater.inflate(R.layout.asset_stock_item, null);
                holder.asset_quantity = (TextView) convertView.findViewById(R.id.asset_quantity);
                holder.asset_sku = (TextView) convertView.findViewById(R.id.asset_sku);
                holder.asset_brand = (TextView) convertView.findViewById(R.id.asset_brand);
                holder.delRow = (ImageView) convertView.findViewById(R.id.imgDelRow1);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.asset_quantity.setText(AddedAlldata.get(position).getSkuQuantity());
            holder.asset_sku.setText(AddedAlldata.get(position).getSku().get(0));
            holder.asset_brand.setText(AddedAlldata.get(position).getBrand().get(0));


            holder.delRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SubAssetCategoryActivity.this);
                    builder.setMessage("Are you sure you want to Delete")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String listid = AddedAlldata.get(position).getKey_id().get(0);
                                            db.remove(listid);
                                            notifyDataSetChanged();
                                            AddedAlldata.remove(position);
                                            notifyDataSetChanged();
                                            if (AddedAlldata.size() == 0) {
                                                sku_spinner.setEnabled(true);
                                                brand_spinner.setEnabled(true);
                                                // addbtn.setEnabled(true);
                                            }
                                            stock_list.setAdapter(new MyAdapter());
                                            stock_list.invalidateViews();


                                        }
                                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });


            holder.asset_quantity.setId(position);
            holder.asset_sku.setId(position);
            holder.delRow.setId(position);
            holder.asset_brand.setId(position);
            return convertView;
        }

        private class ViewHolder {
            TextView asset_quantity, asset_sku, asset_brand;
            ImageView delRow;

        }


    }

}

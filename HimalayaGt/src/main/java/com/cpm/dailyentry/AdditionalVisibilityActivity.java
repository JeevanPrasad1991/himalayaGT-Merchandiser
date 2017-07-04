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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.xmlGetterSetter.AssetMappingGetterSetter;
import com.cpm.xmlGetterSetter.CategoryMasterGetterSetter;
import com.cpm.xmlGetterSetter.POSMDATAGetterSetter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class AdditionalVisibilityActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    GSKDatabase db;
    String store_cd, visit_date, username, intime, categoryspinervalue, _pathforcheck, _path, str, a1, Error_Message, validateYesNo;
    private SharedPreferences preferences;
    boolean ischangedflag = false;
    Spinner spinner;
    private String image1 = "";
    String reason_id;
    String img1 = "";
    static int grp_position = -1;
    static int child_position = -1;
    ArrayAdapter<String> adapter;
    List<String> spinnerdatalist = new ArrayList<String>();
    ListView list2;
    ImageView imagebutton, img_addit;
    private Button savebtn, addbtn, cancel_btn;
    ToggleButton category_toggle;
    LinearLayout rl_camera;
    int toggle_value = 1;
    ArrayList<CategoryMasterGetterSetter> datalist = new ArrayList<CategoryMasterGetterSetter>();
    private ArrayList<AssetMappingGetterSetter> list = new ArrayList<AssetMappingGetterSetter>();
    ArrayList<AssetMappingGetterSetter> secPlaceData = new ArrayList<AssetMappingGetterSetter>();
    EditText edt_remark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_visibility);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = new GSKDatabase(getApplicationContext());
        db.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username = preferences.getString(CommonString1.KEY_USERNAME, null);
        str = CommonString1.FILE_PATH;
        //prepareListData();
        spinner = (Spinner) findViewById(R.id.spin_category1);
        imagebutton = (ImageView) findViewById(R.id.imageButton1);
        addbtn = (Button) findViewById(R.id.btn_Add1);
        category_toggle = (ToggleButton) findViewById(R.id.stock_toggle);
        list2 = (ListView) findViewById(R.id.listView3);
        rl_camera = (LinearLayout) findViewById(R.id.rl_camera);
        edt_remark = (EditText) findViewById(R.id.edt_remark);
        datalist = db.getCategoryDataByStoreCd();
        a1 = "-Select Category-";
        spinnerdatalist.add(a1);
        for (int i = 0; i < datalist.size(); i++) {
            spinnerdatalist.add(datalist.get(i).getCategory().get(0));
        }
        adapter = new ArrayAdapter<String>(AdditionalVisibilityActivity.this, R.layout.spinner_custom_item, spinnerdatalist);
        adapter.setDropDownViewResource(R.layout.spinner_custom_item1);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        addbtn.setOnClickListener(this);
        imagebutton.setOnClickListener(this);
        setDataToListView();
        category_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggle_value = 1;
                } else {
                    toggle_value = 0;

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == R.id.btn_Add1) {
            list2.clearFocus();
            list2.invalidateViews();
            if (validateDuplicate()) {
                if (validation()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to save")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            db.open();
                                            AssetMappingGetterSetter secdata = new AssetMappingGetterSetter();
                                            categoryspinervalue = spinner.getSelectedItem().toString();
                                            secdata.setCategory(spinner.getSelectedItem().toString());
                                            long str = spinner.getSelectedItemId();
                                            String strLong = Long.toString(str);
                                            secdata.setCategory_id(strLong);
                                            secdata.setAdditional_image(img1);
                                            secdata.setToglvale(String.valueOf(toggle_value));
                                            secdata.setRemark(edt_remark.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));
                                            secPlaceData.add(secdata);
                                            db.insertAdditionalVisibilityData(store_cd, secPlaceData);
                                            Snackbar.make(addbtn, "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                            img1 = "";
                                            imagebutton.setBackgroundResource(R.drawable.cam_icon);
                                            spinner.setSelection(0);
                                            secPlaceData.clear();
                                            secdata.setRemark("");
                                            edt_remark.setText("");
                                            list.clear();
                                            setDataToListView();


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
                    Snackbar.make(addbtn, Error_Message, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(addbtn, "This category already added!", Snackbar.LENGTH_SHORT).show();
            }
        }

        if (id == R.id.imageButton1) {
            _pathforcheck = store_cd + "_ADDITIONALIMG_" + visit_date.replace("/", "") + "_" + getCurrentTime().replace(":", "") + ".jpg";
            _path = CommonString1.FILE_PATH + _pathforcheck;
            startCameraActivity();
        }

    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;

    }

    @Override
    public void onBackPressed() {
        if (ischangedflag) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(CommonString1.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    startActivity(new Intent(getApplicationContext(), StoreEntry.class));
                                    finish();
                                    overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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

        } else {
            startActivity(new Intent(this, StoreEntry.class));
            finish();
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        }


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
            startActivity(new Intent(this, StoreEntry.class));
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
                        imagebutton.setBackgroundResource(R.drawable.cam_icon_done);
                        image1 = _pathforcheck;
                        img1 = _pathforcheck;
                        _pathforcheck = "";
                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


    public void onItemSelected(AdapterView<?> arg0, View v, int position, long arg3) {
        switch (arg0.getId()) {
            case R.id.spin_category:
                validateYesNo = spinner.getSelectedItem().toString();
                if ((position != 0)) {
                    POSMDATAGetterSetter secdata = new POSMDATAGetterSetter();
                }
                break;


        }


    }


    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }


    public void setDataToListView() {
        try {
            list = db.getAdditionalinsertedData(store_cd);
            if (list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    AssetMappingGetterSetter secdata = new AssetMappingGetterSetter();
                    secdata.setCategory_id(list.get(i).getCategory_id().get(0));
                    secdata.setCategory(list.get(i).getCategory().get(0));
                    secdata.setAdditional_image(list.get(i).getAdditional_image().get(0));
                    secdata.setKey_id(list.get(i).getKey_id());
                    secdata.setToglvale(list.get(i).getToglvale().get(0));
                    secdata.setRemark(list.get(i).getRemark());
                    secPlaceData.add(secdata);
                }
                Collections.reverse(secPlaceData);
                list2.setAdapter(new MyAdapter());
                list2.invalidateViews();

            }


        } catch (Exception e) {
            Log.d("Exception when fetching", e.toString());
        }

    }

    boolean validation() {
        boolean flag = true;
        String name = spinner.getSelectedItem().toString();
        if (name.equalsIgnoreCase("-Select Category-")) {
            Error_Message = "Please select Category from dropdown list";
            flag = false;
        } else if (img1.equals("")) {
            imagebutton.setBackgroundResource(R.drawable.cam_icon);
            validateYesNo = spinner.getSelectedItem().toString();
            Error_Message = "Please take " + validateYesNo + " image";
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
            return secPlaceData.size();
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
                convertView = inflater.inflate(R.layout.secondaryplac_mt_adapter_new, null);
                holder.category = (TextView) convertView.findViewById(R.id.txt_category_);
                holder.ImageURL = (ImageView) convertView.findViewById(R.id.txt_imageurl);
                holder.toggle_vslue = (TextView) convertView.findViewById(R.id.txt_toggle);
                holder.delRow = (ImageView) convertView.findViewById(R.id.imgDelRow1);
                // holder.txt_remark_row = (TextView) convertView.findViewById(R.id.txt_remark_row);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.category.setText(secPlaceData.get(position).getCategory().get(0));
            final Bitmap bmp = BitmapFactory.decodeFile(str + secPlaceData.get(position).getAdditional_image().get(0));
            holder.ImageURL.setImageBitmap(bmp);
            String value = secPlaceData.get(position).getToglvale().get(0);
            if (value.equals("1")) {
                value = "Yes";
            } else {
                value = "No";
            }
            holder.toggle_vslue.setText(value);
            // holder.txt_remark_row.setText(secPlaceData.get(position).getRemark());
            holder.delRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AdditionalVisibilityActivity.this);
                    builder.setMessage("Are you sure you want to Delete")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String listid = String.valueOf(secPlaceData.get(position).getKey_id());
                                            db.remove(listid);
                                            notifyDataSetChanged();
                                            secPlaceData.remove(position);
                                            notifyDataSetChanged();
                                            if (secPlaceData.size() == 0) {
                                                spinner.setEnabled(true);
                                                addbtn.setEnabled(true);
                                            }
                                            list2.setAdapter(new MyAdapter());
                                            list2.invalidateViews();
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
            holder.ImageURL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(AdditionalVisibilityActivity.this);
                    //setting custom layout to dialog
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.custom_dialog_new);
                    dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    cancel_btn = (Button) dialog.findViewById(R.id.cancel_btn);
                    img_addit = (ImageView) dialog.findViewById(R.id.img_addit);
                    img_addit.setImageBitmap(bmp);
                    db = new GSKDatabase(AdditionalVisibilityActivity.this);
                    db.open();
                    cancel_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            });
           /* holder.txt_remark_row.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        int pos = Caption.getText().length();
                        Caption.setSelection(pos);
                        String value1 = Caption.getText().toString().replaceAll("^#@&0+(?!$)", "");
                        if (value1.equals("")) {
                            secPlaceData.get(position).setRemark("");
                        } else {
                            secPlaceData.get(position).setRemark(value1);
                        }

                    }
                }
            });*/

            holder.category.setId(position);
            holder.ImageURL.setId(position);
            holder.toggle_vslue.setId(position);
            holder.delRow.setId(position);
            return convertView;
        }

        private class ViewHolder {
            TextView category, toggle_vslue, txt_remark_row;
            ImageView delRow, ImageURL;

        }


    }

    boolean validateDuplicate() {
        boolean flag = true;
        db.open();
        list = db.getAdditionalinsertedData(store_cd);
        String value = String.valueOf(spinner.getSelectedItemId());
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getCategory_id().get(0).equalsIgnoreCase(value)) {
                    flag = false;
                    break;
                }
            }
        }
        return flag;
    }


}

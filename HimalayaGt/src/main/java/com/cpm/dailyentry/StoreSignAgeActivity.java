package com.cpm.dailyentry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.keyboard.BasicOnKeyboardActionListener;
import com.cpm.keyboard.CustomKeyboardView;
import com.cpm.xmlGetterSetter.AssetInsertdataGetterSetter;
import com.cpm.xmlGetterSetter.FacingCompetitorGetterSetter;
import com.cpm.xmlGetterSetter.StoreSignAgeGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class StoreSignAgeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner ExistSpinner, WorkingSpinner;
    // EditText Edname,EdContact,EdAddress;
    Button btnsave;
    CustomKeyboardView mKeyboardView;
    //  Keyboard mKeyboard;
    private ArrayAdapter<CharSequence> ExistTypeAdapter, WorkingAdapter;
    //ListView lv;
    //LinearLayout heading;
    ImageView img_cam;
    String _pathforcheck, _path, str;
    boolean flag = true;
    GSKDatabase db;
    // private ArrayAdapter<CharSequence> categoryAdapter,assetAdapter,brandAdapter;
    ArrayList<FacingCompetitorGetterSetter> categorylist = new ArrayList<FacingCompetitorGetterSetter>();
    ArrayList<StockNewGetterSetter> brandlist = new ArrayList<StockNewGetterSetter>();
    ArrayList<AssetInsertdataGetterSetter> assetlist;
    ArrayList<StoreSignAgeGetterSetter> SFTLIST = new ArrayList<StoreSignAgeGetterSetter>();
    //ArrayList<StoreSignAgeGetterSetter> SFTLIST=new ArrayList<StoreSignAgeGetterSetter>();

    String category_cd, asset_cd, category, asset, brand_cd, brand;
    static int currentapiVersion = 1;
    StoreSignAgeGetterSetter StoreSignAgeGetterSetter = new StoreSignAgeGetterSetter();

    private SharedPreferences preferences;
    String store_cd, visit_date, intime, img_str = null;
    LinearLayout workinglayoyt, cameralayoyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_store_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ExistSpinner = (Spinner) findViewById(R.id.spinner3);
        WorkingSpinner = (Spinner) findViewById(R.id.spinner4);
        btnsave = (Button) findViewById(R.id.btn_poi_save);
        img_cam = (ImageView) findViewById(R.id.imageView2);
        workinglayoyt = (LinearLayout) findViewById(R.id.layoutworking);
        cameralayoyt = (LinearLayout) findViewById(R.id.layoutcarema);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        str = CommonString1.FILE_PATH;
        db = new GSKDatabase(getApplicationContext());
        db.open();


        ExistTypeAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_custom_item);

        ExistTypeAdapter.add("Select Signage");
        ExistTypeAdapter.add("Yes");
        ExistTypeAdapter.add("No");

        WorkingAdapter = new ArrayAdapter<CharSequence>(this, R.layout.spinner_custom_item);
        WorkingAdapter.add("Select Working Condition");
        WorkingAdapter.add("Yes");
        WorkingAdapter.add("No");
        ExistSpinner.setAdapter(ExistTypeAdapter);
        ExistSpinner.setOnItemSelectedListener(this);

        WorkingSpinner.setAdapter(WorkingAdapter);
        WorkingSpinner.setOnItemSelectedListener(this);

        setSFTdata();

        img_cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                _pathforcheck = store_cd + "Signage"
                        + "Image" + visit_date.replace("/", "") + getCurrentTime().replace(":", "") + ".jpg";

                _path = CommonString1.FILE_PATH + _pathforcheck;

                intime = getCurrentTime();

                startCameraActivity();


            }
        });

        btnsave.setOnClickListener(new View.OnClickListener()

                                   {
                                       @Override
                                       public void onClick(View v) {
                                           if ((ExistSpinner.getSelectedItem().toString().equalsIgnoreCase("Select Signage"))) {

                                               Snackbar.make(v, "Please Select Signage ", Snackbar.LENGTH_LONG)
                                                       .setAction("Action", null).show();

                                               flag = false;

                                           } else if (ExistSpinner.getSelectedItem().toString().equalsIgnoreCase("Yes")) {


                                               if (WorkingSpinner.getSelectedItem().toString().equalsIgnoreCase("Select Working Condition")) {
                                                   Snackbar.make(v, "Please Select Working Condition", Snackbar.LENGTH_LONG)
                                                           .setAction("Action", null).show();

                                                   flag = false;
                                               } else if ((img_str == null) || img_str.equals("")) {
                                                   Snackbar.make(v, "Please Take a Image", Snackbar.LENGTH_LONG)
                                                           .setAction("Action", null).show();


                                                   flag = false;
                                               } else {

                                                   flag = true;
                                               }

                                           } else if (ExistSpinner.getSelectedItem().toString().equalsIgnoreCase("No")) {


                                               flag = true;
                                           } else {
                                               //flag=true;
                                           }


// flag=true;


                                           if (flag) {
                                               AlertDialog.Builder builder = new AlertDialog.Builder(StoreSignAgeActivity.this);
                                               builder.setMessage("Are you sure you want to save")
                                                       .setCancelable(false)
                                                       .setPositiveButton("Yes",
                                                               new DialogInterface.OnClickListener() {
                                                                   public void onClick(DialogInterface dialog,
                                                                                       int id) {


                                                                       //  StoreSignAgeGetterSetter.setName(Edname.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$]", ""));
                                                                       //   StoreSignAgeGetterSetter.setContact(EdContact.getText().toString().replaceAll("[@&^<>{}'$]", ""));
                                                                       //  StoreSignAgeGetterSetter.setAddress(EdAddress.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$]", ""));

                                                                       StoreSignAgeGetterSetter.setExistSpinner(ExistSpinner.getSelectedItem().toString());
                                                                       StoreSignAgeGetterSetter.setWorkingsppiner(WorkingSpinner.getSelectedItem().toString());
                                                                       StoreSignAgeGetterSetter.setImage(img_str);

                                                                       long str = ExistSpinner.getSelectedItemId();

                                                                       String strLong = Long.toString(str);

                                                                       if (strLong.equals("2")) {
                                                                           strLong = "0";
                                                                       } else {

                                                                       }


                                                                       long str1 = WorkingSpinner.getSelectedItemId();

                                                                       String strLong1 = Long.toString(str1);

                                                                       if (strLong1.equals("2")) {
                                                                           strLong1 = "0";
                                                                       } else {

                                                                       }


                                                                       StoreSignAgeGetterSetter.setExistSpinner_CD(strLong);
                                                                       StoreSignAgeGetterSetter.setWorkingsppiner_CD(strLong1);


                                                                       db.insertStoreSignAgeData(StoreSignAgeGetterSetter, store_cd);


                                                                       img_cam.setBackgroundResource(R.drawable.cam_icon);

                                                                       Snackbar.make(btnsave,
                                                                               "Data has been saved", Snackbar.LENGTH_SHORT).show();
                                                                       finish();

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

                                           }

                                       }
                                   }

        );


    }


    public void setSFTdata() {

        SFTLIST = db.getSFTData(store_cd);


        if (SFTLIST.size() > 0) {

            String str = SFTLIST.get(0).getExistSpinner_CD();

            int exit = Integer.parseInt(str);


            if (exit == 0) {
                exit = 2;
            } else {

            }


            ExistSpinner.setSelection(exit);

            String str1 = SFTLIST.get(0).getWorkingsppiner_CD();
            int work = Integer.parseInt(str1);

            if (work == 0) {
                work = 2;
            } else {

            }

            WorkingSpinner.setSelection(work);

            img_str = SFTLIST.get(0).getImage();


            if (img_str != null) {
                img_cam.setBackgroundResource(R.drawable.cam_icon_done);
            } else {

                img_cam.setBackgroundResource(R.drawable.cam_icon);
            }


            btnsave.setText("UPDATE");


            // EdContact.setText(SFTLIST.get(0).getContact());

            //  EdAddress.setText(SFTLIST.get(0).getAddress());


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {

            // NavUtils.navigateUpFromSameTask(this);
            finish();

            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {


        finish();


        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);


    }


    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

        return intime;

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getId() == R.id.spinner3) {

            String singage = ExistSpinner.getSelectedItem().toString();

            if (position != 0) {
                if (singage.equalsIgnoreCase("No")) {

                    cameralayoyt.setVisibility(View.GONE);
                    workinglayoyt.setVisibility(View.GONE);
                    img_cam.setBackgroundResource(R.drawable.cam_icon);
                    img_str = "";
                    WorkingSpinner.setSelection(2);

                } else {

                    cameralayoyt.setVisibility(View.VISIBLE);
                    workinglayoyt.setVisibility(View.VISIBLE);


                }
            }


        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    protected void startCameraActivity() {

        try {
            Log.i("MakeMachine", "startCameraActivity()");
            File file = new File(_path);
            Uri outputFileUri = Uri.fromFile(file);

            Intent intent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
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

                        //img_cam.setBackgroundResource(R.drawable.camera_list_tick);

                        // Decode the filepath with BitmapFactory followed by the position
                        // Bitmap bmp = BitmapFactory.decodeFile(str + _pathforcheck);


                        // img_cam.setImageBitmap(bmp);
                        img_cam.setBackgroundResource(R.drawable.cam_icon_done);


                        // img_clicked.setVisibility(View.GONE);
                        // img_cam.setVisibility(View.VISIBLE);

                        //Set Clicked image to Imageview

                        //_pathforcheck = "";

                        img_str = _pathforcheck;
                        _pathforcheck = "";
                        //Toast.makeText(getApplicationContext(), ""+image1, Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}

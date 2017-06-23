package com.cpm.dailyentry;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.keyboard.CustomKeyboardView;
import com.cpm.xmlGetterSetter.COMPETITORGetterSetter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class COMPETITORActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemSelectedListener{

    boolean checkflag=true;
    String Error_Message;
    static int currentapiVersion = 1;
    String _pathforcheck,_path,str;
    Button btnsave;
    //ListView lv;
    ListView ListView;
    EditText Editpro;
    ListView  list2;
    ImageView imagebutton;
    private String image1 = "";
    String reason_id;
    String img1 = "";
    //Spinner spin;

    private ArrayList<COMPETITORGetterSetter> list = new ArrayList<COMPETITORGetterSetter>();
    private ArrayAdapter<CharSequence> dfAdapter;
   // ArrayList<COMPETITORGetterSetter> facingcomplist=new ArrayList<COMPETITORGetterSetter>();
    ArrayList<COMPETITORGetterSetter> categorylist=new ArrayList<COMPETITORGetterSetter>();
    //ArrayList<String> dflist=new ArrayList<String>();
    ArrayList<String> statuslist=new ArrayList<String>();
    String existsvalue;
    //EditText etremarkone;
    //LinearLayout lv_layout;
    private Button savebtn,addbtn;
    GSKDatabase db;
    Spinner spinner,spinner_exists;
    int listsize=0;
    List<String> spinnerdatalist=new ArrayList<String>();
    private SharedPreferences preferences;
    String store_cd;
    String a1,STATE_CD,STORE_TYPE_CD,categoryspinervalue;
    String visit_date,username,intime;
    private ArrayAdapter<CharSequence> ExistTypeAdapter,ExistAdapter;
    CustomKeyboardView mKeyboardView;
  //  Keyboard mKeyboard;
  ArrayList<COMPETITORGetterSetter>secPlaceData = new ArrayList<COMPETITORGetterSetter>();
   MyAdaptor myadapter;
    ArrayAdapter<String> adapter;
    boolean ischangedflag=false;

   // List<COMPETITORGetterSetter> listDataHeader;
   // HashMap<COMPETITORGetterSetter, List<COMPETITORGetterSetter>> listDataChild;

   // ExpandableListAdapter listAdapter;
   LinearLayout  cameralayout;
    List<Integer> checkHeaderArray = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentapiVersion = android.os.Build.VERSION.SDK_INT;

        //lv=(ListView) findViewById(R.id.lvfacing);
        // get the list view


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        spinner_exists = (Spinner) findViewById(R.id.spin_exists);
        spinner = (Spinner) findViewById(R.id.spin_category);


        Editpro = (EditText) findViewById(R.id.et_quantity);

        imagebutton = (ImageView) findViewById(R.id.imageButton);

         cameralayout = (LinearLayout) findViewById(R.id.cameralayout);



        //savebtn = (Button) findViewById(R.id.btn_Save);
        addbtn = (Button) findViewById(R.id.btn_Add);
        list2 = (ListView) findViewById(R.id.listView1);

        str = CommonString1.FILE_PATH;

        db=new GSKDatabase(getApplicationContext());
        db.open();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);

        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username= preferences.getString(CommonString1.KEY_USERNAME, null);
        intime= preferences.getString(CommonString1.KEY_STORE_IN_TIME, "");




        a1 = "Select COMPETITOR";
        spinnerdatalist.add(a1);

        categorylist = db.getCOMPETITORData();



        for(int i=0;i<categorylist.size();i++)
        {
            spinnerdatalist.add(categorylist.get(i).getCOMPANY());

        }

        adapter = new ArrayAdapter<String>(COMPETITORActivity.this,R.layout.spinner_custom_item,spinnerdatalist);


        adapter.setDropDownViewResource(R.layout.spinner_custom_item);


        ExistAdapter = new ArrayAdapter<CharSequence>(this,
                R.layout.spinner_custom_item);

        ExistAdapter.add("Select Exists");
        ExistAdapter.add("YES");
        ExistAdapter.add("NO");




        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(COMPETITORActivity.this);

        spinner_exists.setAdapter(ExistAdapter);
        spinner_exists.setOnItemSelectedListener(this);


        addbtn.setOnClickListener(this);

        imagebutton.setOnClickListener(this);


        setDataToListView();



    }

    public void setDataToListView(){
        try{


            list = db.getCOMPETITORData(store_cd);

            if(list.size()>0){

                for(int i =0;i<list.size();i++)
                {
                    COMPETITORGetterSetter secdata = new COMPETITORGetterSetter();


                    secdata.setEdText(list.get(i).getEdText());

                    secdata.setCOMPANY_CD(list.get(i).getCOMPANY_CD());

                    secdata.setCOMPANY(list.get(i).getCOMPANY());

                    secdata.setImage(list.get(i).getImage());
                    secdata.setSpinnerExists_CD(list.get(i).getSpinnerExists_CD());

                    secdata.setID(list.get(i).getID());

                    secPlaceData.add(secdata);


                }


                Collections.reverse(secPlaceData);
                list2.setAdapter(new MyAdaptor());
                list2.invalidateViews();


            }


        }
        catch(Exception e){
            Log.d("Exception when fetching",
                    e.toString());
        }


    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if(id==R.id.btn_Add){

            list2.clearFocus();

            list2.invalidateViews();

            if(validate()){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to save")
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {

                                        db.open();

                                        COMPETITORGetterSetter secdata = new COMPETITORGetterSetter();


                                        categoryspinervalue =spinner.getSelectedItem().toString();

                                        secdata.setCOMPANY(spinner.getSelectedItem().toString());


                                        existsvalue=spinner_exists.getSelectedItem().toString();

                                        secdata.setSpinnerExists(spinner_exists.getSelectedItem().toString());



                                        long str=spinner.getSelectedItemId();

                                        String strLong = Long.toString(str);

                                        secdata.setCOMPANY_CD(strLong);


                                        long str1=spinner_exists.getSelectedItemId();

                                        String strLong1 = Long.toString(str1);

                                        if(strLong1.equals("2"))
                                        {
                                            strLong1="0";
                                        }
                                        else
                                        {

                                        }

                                        secdata.setSpinnerExists_CD(strLong1);


                                        secdata.setImage(img1);

                                        secdata.setEdText(Editpro.getText().toString().replaceAll("[-@.?/|=+_#%:;^*()!&^<>{},'$0]", ""));


                                        secPlaceData.add(secdata);


                                        getMid();

                                        db.insertCOMPETITORDATA(store_cd, secPlaceData, list, img1);

                                        Snackbar.make(spinner_exists,
                                                "Data has been saved", Snackbar.LENGTH_SHORT).show();


                                        Editpro.setText("");

                                        img1="";

                                        imagebutton.setBackgroundResource(R.drawable.camera);
                                        spinner.setSelection(0);
                                        spinner_exists.setSelection(0);

                                        cameralayout.setVisibility(View.VISIBLE);

                                        secPlaceData.clear();
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

            }
            else{

                Snackbar.make(spinner_exists, Error_Message, Snackbar.LENGTH_SHORT).show();
            }

        }

        if(id== R.id.imageButton) {



            _pathforcheck = store_cd + "competitorImage" + visit_date.replace("/","")+getCurrentTime().replace(":","")+".jpg";

            _path = CommonString1.FILE_PATH
                    + _pathforcheck;


            startCameraActivity();




        }






    }

    private class MyAdaptor extends BaseAdapter {

        @Override
        public int getCount() {

            return secPlaceData.size();
        }

        @Override
        public Object getItem(int position) {

            return position;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();



                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.competition_item, null);

               // holder.cardView=(CardView) convertView.findViewById(R.id.card_view);

                holder.tvCOMPANY= (TextView) convertView.findViewById(R.id.txt_cAMPANY);

                holder.TViMAGE= (TextView) convertView.findViewById(R.id.txt_iMAGE);

                holder.TVremark= (TextView) convertView.findViewById(R.id.txt_remark);
                holder.delRow = (ImageView)convertView.findViewById(R.id.imgDelRow);

              //  holder.checkbox= (CheckBox) convertView.findViewById(R.id.checkBox);
               // holder.camraImage= (ImageView) convertView.findViewById(R.id.camraimage);



               // holder.etfacing=(EditText) convertView.findViewById(R.id.etfaceup);
                //holder.etstoredf=(EditText) convertView.findViewById(R.id.etstoredf);




                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();

            }





           holder.tvCOMPANY.setText(secPlaceData.get(position).getCOMPANY());
           holder.TViMAGE.setText(secPlaceData.get(position).getImage());
           holder.TVremark.setText(secPlaceData.get(position).getEdText());


            holder.delRow.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {


                    AlertDialog.Builder builder = new AlertDialog.Builder(COMPETITORActivity.this);
                    builder.setMessage("Are you sure you want to Delete")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {

                                            String listid = secPlaceData.get(position).getID();


							/*final CrownOtherPromotioanlGetterSetter user=secPlaceData.get(position);

							final String	user_id=user.getID();*/


                                            db.RemoveCOM(listid);
                                            notifyDataSetChanged();
                                            secPlaceData.remove(position);
                                            notifyDataSetChanged();


                                            if (secPlaceData.size() == 0) {

                                                spinner.setEnabled(true);
                                                spinner_exists.setEnabled(true);
                                                Editpro.setEnabled(true);
                                                addbtn.setEnabled(true);

                                            }

                                            list2.setAdapter(new MyAdaptor());
                                            list2.invalidateViews();


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
            });



            holder.tvCOMPANY.setId(position);
             holder.TViMAGE.setId(position);
              holder.TVremark.setId(position);
            holder.delRow.setId(position);

            if(!checkflag){

                boolean tempflag=false;

                /*if(holder.etstoredf.getText().toString().equals("")){
                    //holder.dfavail.setTextColor(Color.RED);
                    //convertView.setBackgroundColor(getResources().getColor(R.color.red));
                    holder.etstoredf.setHintTextColor(getResources().getColor(R.color.red));
                    holder.etstoredf.setHint("Empty");
                    tempflag=true;
                }*/


                /*if(holder.etfacing.getText().toString().equals("")){
                    //holder.dfavail.setTextColor(Color.RED);

                    holder.etfacing.setHintTextColor(getResources().getColor(R.color.red));
                    holder.etfacing.setHint("Empty");
                    tempflag=true;
                }*/


                if(tempflag){
                    //holder.dfavail.setTextColor(Color.RED);
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                }
                else{
                    //holder.dfavail.setTextColor(Color.BLACK);
                    holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                }

            }



            return convertView;
        }

    }


    private class ViewHolder {
        TextView tvCOMPANY, TViMAGE,TVremark;
        ImageView delRow;
       // CheckBox checkbox;
        ImageView camraImage;
        //EditText etfacing;
        //EditText etstoredf;
        CardView cardView;
    }




    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());

       /* String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);*/

        return cdate;

    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub



            if(ischangedflag){

                AlertDialog.Builder builder = new AlertDialog.Builder(
                        COMPETITORActivity.this);
                builder.setMessage(CommonString1.ONBACK_ALERT_MESSAGE)
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {

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

            }
            else{
                finish();

                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            }




    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

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

        if(id==android.R.id.home){

            // NavUtils.navigateUpFromSameTask(this);
            finish();

            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);

        }

        return super.onOptionsItemSelected(item);
    }



    public long checkMid() {
        return db.CheckMid(visit_date, store_cd);
    }

    public long getMid() {

        long mid = 0;

        mid = checkMid();

        if (mid == 0) {
            CoverageBean cdata = new CoverageBean();
            cdata.setStoreId(store_cd);
            cdata.setVisitDate(visit_date);
            cdata.setUserId(username);
            cdata.setInTime(intime);
            cdata.setOutTime(getCurrentTime());
            cdata.setReason("");
            cdata.setReasonid("0");
            cdata.setLatitude("0.0");
            cdata.setLongitude("0.0");
            mid = db.InsertCoverageData(cdata);

        }

        return mid;
    }

    public boolean validate(){

        boolean flag=true;
        if(((spinner.getSelectedItem().toString().equalsIgnoreCase("Select COMPETITOR"))))
        {
            //Toast.makeText(getApplicationContext(), "Please select ImageType from dropdown list", Toast.LENGTH_SHORT).show();

            Error_Message="Please select Competitor Name from dropdown list";

            flag=false;

        }

        else if(((spinner_exists.getSelectedItem().toString().equalsIgnoreCase("Select Exists"))))
        {
            //Toast.makeText(getApplicationContext(), "Please select ImageType from dropdown list", Toast.LENGTH_SHORT).show();

            Error_Message="Please select Competitor Exists from dropdown list";

            flag=false;

        }




        else if(((spinner_exists.getSelectedItem().toString().equalsIgnoreCase("YES")))) {


            if (img1.equals("")) {

                imagebutton.setBackgroundResource(R.drawable.camera_not_done);
                String validateYesNo = spinner.getSelectedItem().toString();
                Error_Message = "Please take " + validateYesNo + " image";
                flag = false;

            }
        }
        else if(Editpro.getText().toString().equals(""))
        {
            Editpro.setHint("EMPTY");
            Editpro.setHintTextColor(getResources().getColor(R.color.red));
            Error_Message="Please fill Remarks";
            flag=false;

        }

        else
        {
            flag=true;
        }






        return flag;
    }






    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


        if(parent.getId()==  R.id.spin_exists){

            String window = spinner_exists.getSelectedItem().toString();

            if (position !=0) {
                if (window.equalsIgnoreCase("NO")) {

                    cameralayout.setVisibility(View.GONE);

                    img1="";

                    imagebutton.setBackgroundResource(R.drawable.camera);

                }
                else {

                    cameralayout.setVisibility(View.VISIBLE);



                }
            }




        }


    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

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

                        imagebutton.setBackgroundResource(R.drawable.camera_done);
                        image1 = _pathforcheck;
                        img1 = _pathforcheck;

                        _pathforcheck = "";
                        //	Toast.makeText(getApplicationContext(), ""+image1, Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }

}

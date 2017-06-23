package com.cpm.dailyentry;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString1;

import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.keyboard.BasicOnKeyboardActionListener;
import com.cpm.keyboard.CustomKeyboardView;
import com.cpm.capitalfoods.R;
import com.cpm.xmlGetterSetter.DeepFreezerTypeGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;

import static android.R.attr.scrollX;

@SuppressLint("ClickableViewAccessibility")
public class StockAvailability extends AppCompatActivity implements OnClickListener {
    boolean validate = true;
    boolean flagcoldroom = false;
    boolean flagmccain = false;
    boolean flagstoredf = false;
    List<Integer> checkValidHeaderArray = new ArrayList<Integer>();
    List<Integer> checkValidChildArray = new ArrayList<Integer>();
    boolean checkflag = true;
    static int currentapiVersion = 1;
    List<Integer> checkHeaderArray = new ArrayList<Integer>();
    StockAvailebilityAdapter listAdapter;
    RecyclerView recyclView;
    Button btnSave;
    TextView tvheader;
    ArrayList<StockNewGetterSetter> listDataHeader;
    private SharedPreferences preferences;
    String store_cd,img_str="",_pathforcheck, _path, str,visit_date, username, intime,Error_Message;
    ArrayList<StockNewGetterSetter> brandDatawithskuList;
    GSKDatabase db;
    CustomKeyboardView mKeyboardView;
    Keyboard mKeyboard;
    boolean openmccaindfFlag = false;
    boolean ischangedflag = false;
    Snackbar snackbar;
    String category_cd, STATE_CD, STORE_TYPE_CD;
    boolean update_flag = false;
    ImageView category_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opening_stock);
        currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // get the list view
        recyclView = (RecyclerView) findViewById(R.id.lvExp);
        btnSave = (Button) findViewById(R.id.save_btn);
        tvheader = (TextView) findViewById(R.id.txt_idealFor);
        mKeyboard = new Keyboard(this, R.xml.keyboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        category_img= (ImageView) findViewById(R.id.category_img);
        category_img.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        str = CommonString1.FILE_PATH;
        mKeyboardView = (CustomKeyboardView) findViewById(R.id.keyboard_view);
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setOnKeyboardActionListener(new BasicOnKeyboardActionListener(this));
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username = preferences.getString(CommonString1.KEY_USERNAME, null);
        intime = preferences.getString(CommonString1.KEY_STORE_IN_TIME, "");
        STORE_TYPE_CD = preferences.getString(CommonString1.KEY_STORE_TYPE_CD, "");
        STATE_CD = preferences.getString(CommonString1.KEY_STATE_CD, "");
        category_cd = (String) getIntent().getSerializableExtra(CommonString1.KEY_CATEGORY_ID);
        db = new GSKDatabase(getApplicationContext());
        db.open();
        // preparing list data
        prepareListData1();
        openmccaindfFlag = preferences.getBoolean("opnestkmccaindf", false);
        if (brandDatawithskuList.size()>0){
            if (!brandDatawithskuList.get(0).getCategory_image().equalsIgnoreCase("")){
                category_img.setBackgroundResource(R.drawable.cam_icon_done);
                img_str=brandDatawithskuList.get(0).getCategory_image();
            }else {
                category_img.setBackgroundResource(R.drawable.cam_icon);
            }
            listAdapter = new StockAvailebilityAdapter(this, brandDatawithskuList);
            // setting list adapter
            recyclView.setAdapter(listAdapter);
            recyclView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        }

        btnSave.setOnClickListener(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            recyclView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        getCurrentFocus().clearFocus();

                    }
                }
            });
        }
    }


    //prepare list
    private void prepareListData1() {
        //get brand
        brandDatawithskuList = db.getInsertedSkuListData(STATE_CD, store_cd, category_cd);
        if (brandDatawithskuList.size() > 0) {
            for (int i = 0; i < brandDatawithskuList.size(); i++) {
                //if (!brandDatawithskuList.get(i).getOpenning_total_stock().equalsIgnoreCase("")) {
                    update_flag = true;
                    btnSave.setText("Update");
               // }
            }
        } else {
            brandDatawithskuList = db.getHeaderStockSkuData(STATE_CD, STORE_TYPE_CD, category_cd);
        }


    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();
        if (id == R.id.save_btn) {
            recyclView.clearFocus();
            recyclView.invalidate();
            listAdapter.notifyDataSetChanged();
            if (snackbar == null || !snackbar.isShown()) {
                if (validateimage()){
                if (validateData(brandDatawithskuList)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Are you sure you want to save")
                            .setCancelable(false)
                            .setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            db.open();
                                            getMid();
                                            db.InsertOpeningStockData(category_cd,
                                                    store_cd, STATE_CD, STORE_TYPE_CD, brandDatawithskuList,img_str);
                                            db.InsertOpeningStockCategoryData(category_cd,store_cd,STATE_CD,STORE_TYPE_CD,img_str);
                                            startActivity(new Intent(StockAvailability.this, ClosingStockCategory.class));
                                            Snackbar.make(recyclView, "Data has been saved", Snackbar.LENGTH_LONG).show();
                                            finish();
                                            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
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
                } else {
                    listAdapter.notifyDataSetChanged();
                Snackbar.make(recyclView, Error_Message, Snackbar.LENGTH_LONG).show();
                }
            }
        }
        else if (id==R.id.category_img){
            _pathforcheck = store_cd +"_"+ "_CATEGORYIMG_" +category_cd+"_"+
                    visit_date.replace("/", "") + "_"+getCurrentTime().replace(":", "") + ".jpg";

            _path = CommonString1.FILE_PATH + _pathforcheck;
            intime = getCurrentTime();
            startCameraActivity();

        }
    }

    boolean validateData(ArrayList<StockNewGetterSetter> listDatachild) {
        boolean flag = true;
        checkHeaderArray.clear();
        for (int j = 0; j < listDatachild.size(); j++) {
            String openstocktotal = listDatachild.get(j).getOpenning_total_stock();
            String openfacing = listDatachild.get(j).getOpening_facing();
            String expriyd6 = listDatachild.get(j).getStock_under45days();
            if (openstocktotal.equalsIgnoreCase("") || (openfacing.equalsIgnoreCase("")) || (expriyd6.equalsIgnoreCase(""))) {
                checkflag = false;
                flag = false;
                Error_Message = "Please fill all the data";
                break;
            } else {
                checkflag = true;
            }
            if (flag == false) {
                checkflag = false;
                break;
            }
        }
        recyclView.clearFocus();
        return checkflag;
    }
     boolean validateimage(){
         boolean flag = true;
         if (img_str==null || img_str.equalsIgnoreCase("")) {
          flag=false;
             Error_Message="Please capture category image";
         }else {
             flag=true;
         }
         return flag;
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

    public boolean validateImg() {
        int count = 0;
        for (int i = 0; i < listDataHeader.size(); i++) {
            if (!(listDataHeader.get(i).getImg_cam() == null) && !listDataHeader.get(i).getImg_cam().equals("")) {
                count++;
            }
        }

        int target_count = 0;

        if (listDataHeader.size() > 5) {
            target_count = 5;
        } else {
            target_count = listDataHeader.size();
        }

        if (count >= target_count) {
            return true;
        } else {

            Error_Message = "Please click at least " + target_count + " images";
            return false;
        }

        //return true;

    }



    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        if (mKeyboardView.getVisibility() == View.VISIBLE) {
            mKeyboardView.setVisibility(View.INVISIBLE);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(StockAvailability.this);
            builder.setMessage(CommonString1.ONBACK_ALERT_MESSAGE)
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(
                                        DialogInterface dialog, int id) {
                                    startActivity(new Intent(StockAvailability.this, ClosingStockCategory.class));
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

        }


    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mKeyboardView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mKeyboardView.setKeyboard(mKeyboard);
        mKeyboardView.setVisibility(View.INVISIBLE);
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
            cdata.setStatus(CommonString1.KEY_CHECK_IN);
            mid = db.InsertCoverageData(cdata);
        }

        return mid;
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    class MutableWatcher implements TextWatcher {
        private int mPosition;
        private boolean mActive;

        void setPosition(int position) {
            mPosition = position;
        }

        void setActive(boolean active) {
            mActive = active;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mActive) {
                //  mUserDetails.set(mPosition, s.toString());
            }
        }
    }

    private class StockAvailebilityAdapter extends RecyclerView.Adapter<StockAvailebilityAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        Context context;
        ArrayList<StockNewGetterSetter> skulist;

        public StockAvailebilityAdapter(Context context, ArrayList<StockNewGetterSetter> list) {
            inflator = LayoutInflater.from(context);
            this.context = context;
            this.skulist = list;
        }


        @Override
        public int getItemCount() {
            return skulist.size();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.list_item_openingstk, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            final StockNewGetterSetter childText = skulist.get(position);
            holder.txtListChild.setText(childText.getSku());
            if (currentapiVersion >= 31) {
                holder.ettotalstck.setTextIsSelectable(true);
                holder.etfacing.setTextIsSelectable(true);
                holder.etstckunder.setTextIsSelectable(true);
                holder.ettotalstck.setRawInputType(InputType.TYPE_CLASS_TEXT);
                holder.etfacing.setRawInputType(InputType.TYPE_CLASS_TEXT);
                holder.etstckunder.setRawInputType(InputType.TYPE_CLASS_TEXT);

            } else {
                if (skulist.size() <= 7) {
                    holder.ettotalstck.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String value1 = s.toString().replaceFirst("^0+(?!$)", "");
                            final String facing = skulist.get(position).getOpening_facing();
                            final String stk_under = skulist.get(position).getStock_under45days();
                            if (value1.equals("")) {
                                skulist.get(position).setOpenning_total_stock("");
                            } else if (!facing.equals("") && !stk_under.equals("")) {
                                ischangedflag = true;
                                skulist.get(position).setOpenning_total_stock(value1);

                            } else {
                                if (!facing.equals("")) {
                                    int totalstk = 0;
                                    totalstk = Integer.parseInt(value1);
                                    int fac = Integer.parseInt(facing);
                                    if (fac > totalstk) {
                                        skulist.get(position).setOpening_facing("");
                                        skulist.get(position).setStock_under45days("");
                                        skulist.get(position).setStockunder12("");
                                        skulist.get(position).setStockgreater13("");
                                    }
                                }

                                if (!stk_under.equals("")) {
                                    int totalstk = 0;
                                    totalstk = Integer.parseInt(value1);
                                    int fac = Integer.parseInt(facing);
                                    int stk_und = Integer.parseInt(stk_under);
                                    if (stk_und > totalstk) {
                                        skulist.get(position).setStock_under45days("");
                                        skulist.get(position).setStockunder12("");
                                        skulist.get(position).setStockgreater13("");
                                    }
                                }
                                if (stk_under.equals("0")) {
                                    skulist.get(position).setStock_under45days("");
                                    skulist.get(position).setStockunder12("");
                                    skulist.get(position).setStockgreater13("");
                                    skulist.get(position).setOpening_facing("");
                                }

                                if (value1.equals("0")) {
                                    skulist.get(position).setOpenning_total_stock("0");
                                    skulist.get(position).setStockunder6("0");
                                    skulist.get(position).setOpening_facing("0");
                                    skulist.get(position).setStock_under45days("0");
                                    skulist.get(position).setStockgreater13("0");
                                    skulist.get(position).setStockunder12("0");
                                    holder.etfacing.setText("0");
                                    holder.ettotalstck.setText("0");
                                    holder.etstckunder.setText("0");
                                    holder.etfacing.setEnabled(false);
                                    holder.etstckunder.setEnabled(false);
                                    holder.ettotalstck.setEnabled(true);
                                } else {
                                    holder.etfacing.setEnabled(true);
                                    holder.etstckunder.setEnabled(true);
                                    holder.ettotalstck.setEnabled(true);
                                }

                                ischangedflag = true;
                                skulist.get(position).setOpenning_total_stock(value1);
                            }

                            if (skulist.get(position).getOpenning_total_stock().equals("0")) {
                                skulist.get(position).setOpenning_total_stock("0");
                                skulist.get(position).setOpening_facing("0");
                                skulist.get(position).setStock_under45days("0");
                                skulist.get(position).setStockunder12("0");
                                skulist.get(position).setStockgreater13("0");
                                skulist.get(position).setStockunder6("0");
                                holder.etfacing.setEnabled(false);
                                holder.etstckunder.setEnabled(false);
                                holder.ettotalstck.setEnabled(true);

                            } else {
                                holder.etstckunder.setEnabled(true);
                                holder.etfacing.setEnabled(true);
                                holder.ettotalstck.setEnabled(true);
                            }
                        }

                    });


                } else {
                    holder.ettotalstck.setOnFocusChangeListener(new OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (hasFocus) {
                                // showKeyboardWithAnimation();
                            }
                            //hideSoftKeyboard();
                            if (!hasFocus) {
                                //hide();
                                final int position = v.getId();
                                final EditText Caption = (EditText) v;
                                int pos = Caption.getText().length();
                                Caption.setSelection(pos);
                                String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                                final String facing = skulist.get(position).getOpening_facing();
                                final String stk_under = skulist.get(position).getStock_under45days();
                                final String stk_under12 = skulist.get(position).getStockunder12();
                                // final String stk_under13 = _listDataChild.get(listDataHeader.get(groupPosition)).get(position).getStockgreater13();
                                if (value1.equals("")) {
                                    skulist.get(position).setOpenning_total_stock("");

                                } else if (!facing.equals("") && !stk_under.equals("")) {
                                    ischangedflag = true;
                                    skulist.get(position).setOpenning_total_stock(value1);

                                } else {
                                    if (!facing.equals("")) {
                                        int totalstk = 0;
                                        totalstk = Integer.parseInt(value1);
                                        int fac = Integer.parseInt(facing);
                                        if (fac > totalstk) {
                                            skulist.get(position).setOpening_facing("");
                                            skulist.get(position).setStock_under45days("");
                                            skulist.get(position).setStockunder12("");
                                            skulist.get(position).setStockgreater13("");
                                        }
                                    }

                                    if (!stk_under.equals("")) {
                                        int totalstk = 0;
                                        totalstk = Integer.parseInt(value1);
                                        int fac = Integer.parseInt(facing);
                                        int stk_und = Integer.parseInt(stk_under);
                                        if (stk_und > totalstk) {
                                            skulist.get(position).setStock_under45days("");
                                            skulist.get(position).setStockunder12("");
                                            skulist.get(position).setStockgreater13("");
                                        }
                                    }
                                    if (stk_under.equals("0")) {
                                        skulist.get(position).setStock_under45days("");
                                        skulist.get(position).setStockunder12("");
                                        skulist.get(position).setStockgreater13("");
                                        skulist.get(position).setOpening_facing("");
                                    }

                                    if (value1.equals("0")) {
                                        skulist.get(position).setOpenning_total_stock("0");
                                        skulist.get(position).setStockunder6("0");
                                        skulist.get(position).setOpening_facing("0");
                                        skulist.get(position).setStock_under45days("0");
                                        skulist.get(position).setStockgreater13("0");
                                        skulist.get(position).setStockunder12("0");
                                        holder.etfacing.setText("0");
                                        holder.ettotalstck.setText("0");
                                        holder.etstckunder.setText("0");
                                        holder.etfacing.setEnabled(false);
                                        holder.etstckunder.setEnabled(false);
                                        holder.ettotalstck.setEnabled(true);
                                    }
                                    ischangedflag = true;
                                    skulist.get(position).setOpenning_total_stock(value1);
                                }


                                if (skulist.get(position).getOpenning_total_stock().equals("0")) {
                                    skulist.get(position).setOpening_facing("0");
                                    skulist.get(position).setStock_under45days("0");
                                    skulist.get(position).setStockunder12("0");
                                    skulist.get(position).setStockgreater13("0");
                                    skulist.get(position).setStockunder6("0");
                                    holder.etfacing.setEnabled(false);
                                    holder.etstckunder.setEnabled(false);
                                    holder.ettotalstck.setEnabled(true);

                                } else {
                                    if (!skulist.get(position).getOpenning_total_stock().equals("")) {
                                        holder.etstckunder.setEnabled(true);
                                        holder.etfacing.setEnabled(true);
                                        holder.ettotalstck.setEnabled(true);
                                    }
                                }


                            }
                        }
                    });

                }
                holder.etfacing.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            //  showKeyboardWithAnimation();
                        }
                        if (!hasFocus) {
                            // hide();
                            final int position = v.getId();
                            final EditText Caption = (EditText) v;
                            String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                            final String stock = skulist.get(position).getOpenning_total_stock();
                            if (value1.equals("")) {
                                skulist.get(position).setOpening_facing("");
                            } else if (stock.equals("")) {
                                Snackbar.make(recyclView, "First fill Total Stock data", Snackbar.LENGTH_LONG).show();
                                skulist.get(position).setOpening_facing("");
                            } else {

                                int totalstk = 0;
                                totalstk = Integer.parseInt(stock);
                                int facing = Integer.parseInt(value1);
                                if (facing > totalstk) {
                                    snackbar = Snackbar.make(recyclView, "Facing cannot be greater than Total Stock", Snackbar.LENGTH_LONG);
                                    snackbar.show();
                                    holder.etfacing.setText("");
                                    holder.etstckunder.setText("");
                                } else {
                                    ischangedflag = true;
                                    skulist.get(position).setOpening_facing(value1);
                                    recyclView.invalidate();
                                }


                            }

                        }

                    }
                });

                holder.etstckunder.setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        if (hasFocus) {
                            //  showKeyboardWithAnimation();
                        }
                        if (!hasFocus) {
                            // hide();
                            final int position = v.getId();
                            final EditText Caption = (EditText) v;
                            String value1 = Caption.getText().toString().replaceFirst("^0+(?!$)", "");
                            final String stock = skulist.get(position).getOpenning_total_stock();
                            if (value1.equals("")) {
                                skulist.get(position).setStock_under45days("");

                            } else if (stock.equals("")) {

                                Snackbar.make(recyclView, "First fill Total Stock data", Snackbar.LENGTH_LONG).show();
                                skulist.get(position).setStock_under45days("");

                            } else {
                                int totalstk = 0;
                                totalstk = Integer.parseInt(stock);
                                int stkunder = Integer.parseInt(value1);
                                if (stkunder > totalstk) {
                                    Snackbar.make(recyclView, "Stock Under Expiry cannot be greater than Total Stock", Snackbar.LENGTH_LONG).show();
                                    holder.etstckunder.setText("");
                                } else {
                                    ischangedflag = true;
                                    skulist.get(position).setStock_under45days(value1);
                                    recyclView.invalidate();
                                }

                            }

                        }

                    }
                });


                if (skulist.get(position).getOpenning_total_stock().equals("0")) {

                    skulist.get(position).setStock_under45days("0");
                    skulist.get(position).setStockunder12("0");
                    skulist.get(position).setStockgreater13("0");
                    skulist.get(position).setStockunder6("0");
                    holder.ettotalstck.setEnabled(true);
                    holder.etfacing.setEnabled(false);
                    holder.etstckunder.setEnabled(false);
                    recyclView.invalidate();
                    recyclView.clearFocus();


                } else {
                    //if (!skulist.get(position).getOpenning_total_stock().equals("")) {
                     /* holder.etfacing.setText("");
                        holder.etstckunder.setText("");
                    holder.ettotalstck.setText("");*/
                    holder.etstckunder.setEnabled(true);
                    holder.etfacing.setEnabled(true);
                    holder.ettotalstck.setEnabled(true);
                    recyclView.invalidate();
                    recyclView.clearFocus();
                    // }

                }

                holder.ettotalstck.setText(childText.getOpenning_total_stock());
                holder.etfacing.setText(childText.getOpening_facing());
                holder.etstckunder.setText(childText.getStock_under45days());
                holder.ettotalstck.setId(position);
                holder.etfacing.setId(position);
                holder.etstckunder.setId(position);


                if (!checkflag) {
                    boolean tempflag = false;
                    if (holder.ettotalstck.getText().toString().equals("")) {
                        holder.ettotalstck.setHintTextColor(getResources().getColor(R.color.red));
                        holder.ettotalstck.setHint("Empty");
                        tempflag = true;
                    } else {
                        //holder.ettotalstck.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                    if (holder.etfacing.getText().toString().equals("")) {
                        holder.etfacing.setHintTextColor(getResources().getColor(R.color.red));
                        holder.etfacing.setHint("Empty");
                        tempflag = true;
                    } else {
                        //holder.etfacing.setBackgroundColor(getResources().getColor(R.color.white));
                    }

                    if (holder.etstckunder.getText().toString().equals("")) {
                        holder.etstckunder.setHintTextColor(getResources().getColor(R.color.red));
                        holder.etstckunder.setHint("Empty");
                        tempflag = true;
                    } else {

                    }

                    if (tempflag) {
                        recyclView.invalidate();
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                    } else {
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                    }
                }

                if (!validate) {
                    recyclView.invalidate();
                    if (checkValidHeaderArray.contains(position)) {
                        if (checkValidChildArray.contains(position)) {
                            boolean tempflag = false;

                            if (flagcoldroom) {
                                holder.ettotalstck.setTextColor(getResources().getColor(R.color.red));
                                tempflag = true;
                            } else {
                                holder.ettotalstck.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }
                            if (flagmccain) {
                                holder.etfacing.setTextColor(getResources().getColor(R.color.red));
                                tempflag = true;
                            } else {
                                holder.etfacing.setTextColor(getResources().getColor(R.color.colorPrimary));
                            }


                            if (!flagcoldroom && !flagmccain && !flagstoredf) {
                                holder.ettotalstck.setTextColor(getResources().getColor(R.color.colorPrimary));
                                holder.etfacing.setTextColor(getResources().getColor(R.color.colorPrimary));
                                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                                tempflag = false;
                            } else {

                                holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                                tempflag = true;
                            }


                        } else {
                            holder.ettotalstck.setTextColor(getResources().getColor(R.color.colorPrimary));
                            holder.etfacing.setTextColor(getResources().getColor(R.color.colorPrimary));
                            holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                        }

                    } else {
                        holder.ettotalstck.setTextColor(getResources().getColor(R.color.colorPrimary));
                        holder.etfacing.setTextColor(getResources().getColor(R.color.colorPrimary));
                        holder.cardView.setCardBackgroundColor(getResources().getColor(R.color.white));
                    }

                }
            }

        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            EditText ettotalstck, etfacing, etstckunder/*,etstckunder12,etstckgreater12*/;
            LinearLayout openmccaindf_layout;
            CardView cardView;
            //Button pPickDate;
            EditText edittext4;
            TextView txtListChild;
            public MutableWatcher mWatcher;
            public int mYear, mMonth, mDay, mHour, mMinute;

            public MyViewHolder(View convertView) {
                super(convertView);
                cardView = (CardView) convertView.findViewById(R.id.card_view);
                ettotalstck = (EditText) convertView.findViewById(R.id.etOpening_Stock_total_stock);
                etfacing = (EditText) convertView.findViewById(R.id.etOpening_Stock_Facing);
                etstckunder = (EditText) convertView.findViewById(R.id.etStock_Under_6);
                openmccaindf_layout = (LinearLayout) convertView.findViewById(R.id.openmccaindf_layaout);
                txtListChild = (TextView) convertView.findViewById(R.id.lblListItem);
            }
        }
    }


    public static void setListViewHeightBasedOnChildren(ListView listView, int extraheight) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + extraheight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(StockAvailability.this,ClosingStockCategory.class));
            // NavUtils.navigateUpFromSameTask(this);
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }

        return super.onOptionsItemSelected(item);
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
                        category_img.setBackgroundResource(R.drawable.cam_icon_done);
                        img_str = _pathforcheck;
                        _pathforcheck = "";
                    }
                }

                break;
        }


        super.onActivityResult(requestCode, resultCode, data);
    }


}

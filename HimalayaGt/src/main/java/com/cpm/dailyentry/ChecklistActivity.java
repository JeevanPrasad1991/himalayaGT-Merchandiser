package com.cpm.dailyentry;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;
import com.cpm.database.GSKDatabase;
import com.cpm.xmlGetterSetter.AnswerChecklistGetterSetter;
import com.cpm.xmlGetterSetter.ChecklistGetterSetter;
import com.cpm.xmlGetterSetter.SkuQwantityGetterSetter;
import com.cpm.xmlGetterSetter.WindowListGetterSetter;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static android.widget.NumberPicker.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;
public class ChecklistActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rec_checklist;
    String window_cd, window_name, sku_hold, brand_cd;
    ChecklistAdapter adapter;
    GSKDatabase db;
    ArrayList<ChecklistGetterSetter> data;
    HashMap<ChecklistGetterSetter, ArrayList<AnswerChecklistGetterSetter>> listDataChild;
    boolean isSkuFilled = false;
    String store_cd, visit_date, username;
    private SharedPreferences preferences;
    ImageButton image_window, refimage;
    WindowListGetterSetter current;
    ArrayList<ChecklistGetterSetter> answered_list = new ArrayList<>();
    String _pathforcheck = "", _path = "", str, state_cd, img1 = "", window_img = "", updat = "", a1;
    CheckBox checkBox;
    Boolean existOrnot = true;
    LinearLayout lay_refimageCamera;
    RelativeLayout lay_remarks;
    EditText edt_remarks;
    String value1 = "";
    WindowListGetterSetter windowListGetterSetter = new WindowListGetterSetter();
    AnswerChecklistGetterSetter answered_data;
    boolean update_flag = false;
    boolean islisted, sku_flag = false, preparesku_flag = false;
    boolean check;
    ArrayList<SkuQwantityGetterSetter> skuListData = new ArrayList<SkuQwantityGetterSetter>();
    ArrayList<SkuQwantityGetterSetter> insertedskuQuantityData = new ArrayList<SkuQwantityGetterSetter>();
    private boolean isPromptAdapter;
    ListView sku_list;
    Button save_sku, cancel_btn;
    FloatingActionMenu fab_mainMenu;
    int key_id;
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist);
        rec_checklist = (RecyclerView) findViewById(R.id.rec_checklist);
        fab_mainMenu = (FloatingActionMenu) findViewById(R.id.fab_menu_orderDelivery);
        final com.github.clans.fab.FloatingActionButton fab = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab);
        final com.github.clans.fab.FloatingActionButton fab_sku = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.sku_save);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        image_window = (ImageButton) findViewById(R.id.image_window);
        refimage = (ImageButton) findViewById(R.id.refimage);
        checkBox = (CheckBox) findViewById(R.id.checkboxlist);
        lay_refimageCamera = (LinearLayout) findViewById(R.id.lay_refimageCamera);
        lay_remarks = (RelativeLayout) findViewById(R.id.lay_remarks);
        edt_remarks = (EditText) findViewById(R.id.remark_edt);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, null);
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        username = preferences.getString(CommonString1.KEY_USERNAME, null);
        state_cd = preferences.getString(CommonString1.KEY_STATE_CD, null);
        db = new GSKDatabase(getApplicationContext());
        db.open();
        current = (WindowListGetterSetter) getIntent().getSerializableExtra("OBJECT");
        window_cd = current.getWindow_cd().get(0);
        window_name = current.getWindow();
        sku_hold = current.getSku_hold();
        brand_cd = current.getBrand_cd();
        islisted = current.islisted();
        getSupportActionBar().setTitle(window_name);
        image_window.setOnClickListener(this);
        refimage.setOnClickListener(this);
        //prepareSkuquantityData();
        windowListGetterSetter = db.getUpdatedWindowListData(window_cd, store_cd);
        if (windowListGetterSetter.getKey_id() != 0) {
            update_flag = true;
            if (!windowListGetterSetter.getWindow_image().equalsIgnoreCase("")) {
                image_window.setBackgroundResource(R.drawable.cam_icon_done);
            } else {
                image_window.setBackgroundResource(R.drawable.cam_icon);
            }


            if (!windowListGetterSetter.getRemark().equalsIgnoreCase("")) {
                edt_remarks.setText(windowListGetterSetter.getRemark());
            }
            //key_id = windowListGetterSetter.getKey_id();

        }

        if (update_flag) {
            check = windowListGetterSetter.islisted();
            if (check == false) {
                checkBox.setChecked(check);
                rec_checklist.setVisibility(View.GONE);
            } else {
                // lay_refimageCamera.setVisibility(View.VISIBLE);
                // lay_remarks.setVisibility(View.VISIBLE);
                rec_checklist.setVisibility(View.VISIBLE);
                // lay_remarks.setEnabled(true);
                //lay_refimageCamera.setEnabled(true);
            }

            skuListData = db.getWindowSkuQuantityInsertedDataByCommonid(windowListGetterSetter.getKey_id());
            windowListGetterSetter.setSkuQuantityList(skuListData);
        }
        data = db.getChecklistData(window_cd);
        listDataChild = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            ChecklistGetterSetter answered_temp = new ChecklistGetterSetter();
            answered_temp.setCHECKLIST_CD(data.get(i).getChecklist_cd());
            ArrayList<AnswerChecklistGetterSetter> ans_data;
            String str = "-Select checklist-";
            ans_data = db.getChecklistAnswerData(data.get(i).getChecklist_cd());
            AnswerChecklistGetterSetter ch = new AnswerChecklistGetterSetter();
            ch.setAnswer_cd("0");
            ch.setAnswer(str);
            ans_data.add(0, ch);
            if (update_flag && check) {
                answered_data = db.getInsertedChecklistAnswerData(data.get(i).getChecklist_cd(), window_cd, store_cd);
                // answered_data = db.getInsertedChecklistAnswerDataByCommonId(windowListGetterSetter.getKey_id());
                answered_temp.setANSWER_CD(answered_data.getAnswer_cd());
            } else {
                //  for (int j = 0; j < ans_data.size(); j++) {
                   /* if (ans_data.get(0).getAnswer().equalsIgnoreCase("-Select checklist-")) {
                        answered_temp.setANSWER_CD(ans_data.get(0).getAnswer_cd());
                        break;
                    }*/
                answered_temp.setANSWER_CD("0");
                // }
            }

            answered_list.add(answered_temp);
            listDataChild.put(data.get(i), ans_data);
        }

        adapter = new ChecklistAdapter(getApplicationContext(), data);
        rec_checklist.setAdapter(adapter);
        rec_checklist.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        fab_sku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ChecklistActivity.this);
                //setting custom layout to dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                sku_list = (ListView) dialog.findViewById(R.id.sku_list);
                save_sku = (Button) dialog.findViewById(R.id.save_sku);
                cancel_btn = (Button) dialog.findViewById(R.id.no_sku);
                if (windowListGetterSetter.getSkuQuantityList().size() > 0) {
                    save_sku.setText("Update");
                    skuListData = windowListGetterSetter.getSkuQuantityList();
                } else {
                    skuListData = db.getSkuWindowData(brand_cd);
                }

                sku_list.setAdapter(new skuDataadapter());
                cancel_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                save_sku.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sku_list.clearFocus();
                        sku_list.invalidateViews();
                        if (validatequantitydata()) {
                            dialog.dismiss();
                            windowListGetterSetter.setSkuQuantityList(skuListData);
                        } else {
                            Toast.makeText(ChecklistActivity.this, "Please fill at least one sku quantity ", Toast.LENGTH_LONG).show();
                            Snackbar.make(sku_list, "Please fill at least one sku quantity ", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();


            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatedata()) {
                    if (validate_checklist_data()) {
                        if (windowListGetterSetter.getSkuQuantityList().size() > 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChecklistActivity.this);
                            builder.setMessage("Do you want to save checklist data");
                            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (windowListGetterSetter.islisted() == false) {
                                        long key_id = db.InsertWindowsData(store_cd, visit_date, username, window_cd, false,
                                                windowListGetterSetter.getWindow_image(), edt_remarks.getText().toString()
                                                        .trim().replaceAll("^&0@+(?!$)", ""));
                                        db.deleteCheckListInsertedData(window_cd, store_cd);
                                        db.insertWindowSkuQwantity(store_cd, brand_cd, skuListData, key_id);
                                        startActivity(new Intent(ChecklistActivity.this, SecondaryWindowActivity.class));
                                        finish();
                                        Snackbar.make(rec_checklist, "Window data save successfully", Snackbar.LENGTH_LONG).show();
                                    } else {
                                        long key_id = db.InsertWindowsData(store_cd, visit_date, username, window_cd, existOrnot,
                                                windowListGetterSetter.getWindow_image(), edt_remarks.getText().toString().trim()
                                                        .replaceAll("^&0@+(?!$)", ""));
                                        db.InsertCheckListData(store_cd, username, window_cd, answered_list, key_id);
                                        db.insertWindowSkuQwantity(store_cd, brand_cd, skuListData, key_id);
                                        Snackbar.make(rec_checklist, "Window data save successfully", Snackbar.LENGTH_LONG).show();
                                        startActivity(new Intent(ChecklistActivity.this, SecondaryWindowActivity.class));
                                        finish();
                                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                                    }
                                }
                            });

                            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                        } else {
                            Snackbar.make(rec_checklist, "Please fill atleast one sku quantity ", Snackbar.LENGTH_LONG).show();
                        }
                    }

                }

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rec_checklist.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (SCROLL_STATE_TOUCH_SCROLL == oldScrollY) {
                        View currentFocus = getCurrentFocus();
                        if (currentFocus != null) {
                            currentFocus.clearFocus();
                        }
                    }
                }
            });
        }


        edt_remarks.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {

                    windowListGetterSetter.setIslisted(true);
                    lay_refimageCamera.setVisibility(View.VISIBLE);
                    lay_remarks.setVisibility(View.VISIBLE);
                    rec_checklist.setVisibility(View.VISIBLE);
                    image_window.setEnabled(isChecked);
                    refimage.setEnabled(isChecked);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChecklistActivity.this);
                    builder.setMessage("Are you sure you want to close the window")
                            .setTitle("Parinaam")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    checkBox.setChecked(false);
                                    rec_checklist.setVisibility(View.GONE);
                                    lay_refimageCamera.setVisibility(View.VISIBLE);
                                    lay_remarks.setVisibility(View.VISIBLE);
                                    windowListGetterSetter.setIslisted(false);
                                   /* if (windowListGetterSetter.getWindow_image() == null) {
                                        Snackbar.make(rec_checklist, "Please click image", Snackbar.LENGTH_LONG).show();
                                    }*/
                                }

                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            checkBox.setChecked(true);


                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refimage:
                String str = current.getPlanogram_image();
                Intent in = new Intent(ChecklistActivity.this, PlanogramActivity.class);
                in.putExtra("Plalogram", str);
                startActivity(in);
                break;
            case R.id.image_window:
                _path = CommonString1.FILE_PATH + _pathforcheck;
                startCameraActivity();
                break;
        }
    }

    class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.MyViewHolder> {
        private LayoutInflater inflator;
        List<ChecklistGetterSetter> data = Collections.emptyList();

        public ChecklistAdapter(Context context, List<ChecklistGetterSetter> data) {
            inflator = LayoutInflater.from(context);
            this.data = data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflator.inflate(R.layout.item_checklist, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.mItem = data.get(position);
            holder.tv_checklist.setText(holder.mItem.getChecklist());
            holder.ans = listDataChild.get(holder.mItem);
            holder.customAdapter = new CustomSpinnerAdapter(getApplicationContext(), holder.ans);
            holder.spinner.setAdapter(holder.customAdapter);
            holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    answered_list.get(position).setANSWER_CD(holder.ans.get(pos).getAnswer_cd());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            for (int i = 0; i < holder.ans.size(); i++) {
                if (answered_list.get(position).getANSWER_CD().equals(holder.ans.get(i).getAnswer_cd())) {
                    holder.spinner.setSelection(i);
                }
            }

        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_checklist;
            Spinner spinner;
            ChecklistGetterSetter mItem;
            ArrayList<AnswerChecklistGetterSetter> ans;
            CustomSpinnerAdapter customAdapter;

            public MyViewHolder(View itemView) {
                super(itemView);
                tv_checklist = (TextView) itemView.findViewById(R.id.tv_checklist1);
                spinner = (Spinner) itemView.findViewById(R.id.spin_checklist_ans);
            }
        }
    }

    public class CustomSpinnerAdapter extends BaseAdapter {
        Context context;
        ArrayList<AnswerChecklistGetterSetter> ans;
        LayoutInflater inflter;

        public CustomSpinnerAdapter(Context applicationContext, ArrayList<AnswerChecklistGetterSetter> ans) {
            this.context = applicationContext;
            this.ans = ans;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return ans.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.custom_spinner_item, null);
            TextView names = (TextView) view.findViewById(R.id.tv_ans);
            names.setText(ans.get(i).getAnswer());
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            isPromptAdapter = false;
            return (super.getDropDownView(position, convertView, parent));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            startActivity(new Intent(ChecklistActivity.this, SecondaryWindowActivity.class));
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChecklistActivity.this, SecondaryWindowActivity.class));
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
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

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    @Override
    protected void onResume() {
        super.onResume();
        _pathforcheck = store_cd + "_WINDOWIMG_" + current.getWindow_cd().get(0) + "_" + visit_date.replace("/", "") +
                "_" + getCurrentTime().replace(":", "") + ".jpg";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:
                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    img1 = _pathforcheck;
                    window_img = _pathforcheck;
                    windowListGetterSetter.setWindow_image(window_img);
                    _pathforcheck = "";
                    image_window.setBackgroundResource(R.drawable.cam_icon_done);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public Boolean validatedata() {
        boolean isgood = true;
        if (windowListGetterSetter.getWindow_image() == null || windowListGetterSetter.getWindow_image().equalsIgnoreCase("")) {
            isgood = false;
            Snackbar.make(rec_checklist, "Please click image", Snackbar.LENGTH_LONG).show();
        }
        return isgood;
    }


    public boolean validatequantitydata() {
        boolean isgood = false;
        if (skuListData.size() > 0) {
            for (int i = 0; i < skuListData.size(); i++) {
                if (!skuListData.get(i).getQwantity().equals("")) {
                    isgood = true;
                    break;
                }
            }
        }
        return isgood;
    }

    public Boolean validate_checklist_data() {
        boolean isgood = true;
        if (windowListGetterSetter.islisted() == true) {
            if (answered_list.size() > 0) {
                for (int i = 0; i < answered_list.size(); i++) {
                    if (answered_list.get(i).getANSWER_CD().equals("0")) {
                        isgood = false;
                        Snackbar.make(rec_checklist, "Please select checklist", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
        return isgood;
    }

    private class skuDataadapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return skuListData.size();
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

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.row_sku_list, null);
                holder.window_sku_txt = (TextView) convertView.findViewById(R.id.window_sku_txt);
                holder.qwantity_edt = (EditText) convertView.findViewById(R.id.qwantity_edt);
                holder.qwantity_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (!hasFocus) {
                            //hide();
                            final int position = v.getId();
                            final EditText Caption = (EditText) v;
                            int pos = Caption.getText().length();
                            Caption.setSelection(pos);
                            String value1 = Caption.getText().toString().replaceAll("^#@&0+(?!$)", "");
                            if (value1.equals("")) {
                                skuListData.get(position).setQwantity("");
                            } else {
                                skuListData.get(position).setQwantity(value1);
                            }
                        }
                    }
                });
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.qwantity_edt.setText(skuListData.get(position).getQwantity());
            holder.window_sku_txt.setText(skuListData.get(position).getSku().get(0));
            holder.qwantity_edt.setId(position);
            return convertView;
        }

        private class ViewHolder {
            TextView window_sku_txt;
            EditText qwantity_edt;
        }
    }

    public void prepareSkuquantityData() {
        db.open();
        // skuListData = db.getWindowSkuQuantityInsertedData(store_cd);
        skuListData = db.getWindowSkuQuantityInsertedDataByCommonid(windowListGetterSetter.getKey_id());
        if (skuListData.size() > 0) {
            preparesku_flag = true;
        } else {
            skuListData = db.getSkuWindowData(brand_cd);
        }
    }
}

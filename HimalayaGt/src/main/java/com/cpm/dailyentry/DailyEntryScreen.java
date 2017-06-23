package com.cpm.dailyentry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.cpm.Constants.CommonString1;

import com.cpm.database.GSKDatabase;
import com.cpm.capitalfoods.R;
import com.cpm.delegates.CoverageBean;
import com.cpm.download.CompleteDownloadActivity;
import com.cpm.upload.CheckoutNUpload;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.MappingPromotionGetterSetter;
import com.cpm.xmlGetterSetter.WindowListGetterSetter;
import com.cpm.xmlGetterSetter.WindowSKUEntryGetterSetter;

public class DailyEntryScreen extends AppCompatActivity implements OnItemClickListener, LocationListener {

    GSKDatabase database;
    ArrayList<JourneyPlanGetterSetter> jcplist;
    ArrayList<CoverageBean> coverage;
    private SharedPreferences preferences;
    private String date, store_intime;
    ListView lv;
    //String store_cd;
    String store_cd;
    private SharedPreferences.Editor editor = null;
    private Dialog dialog;
    public static String currLatitude = "0.0";
    public static String currLongitude = "0.0";
    String user_type;
    LinearLayout parent_linear, nodata_linear;
    private ArrayList<WindowListGetterSetter> windowdata = new ArrayList<WindowListGetterSetter>();
    List<WindowSKUEntryGetterSetter> WINDOWSIZE = new ArrayList<WindowSKUEntryGetterSetter>();
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.storelistlayout);
        lv = (ListView) findViewById(R.id.list);
        nodata_linear = (LinearLayout) findViewById(R.id.no_data_lay);
        // parent_linear = (LinearLayout) findViewById(R.id.parent_linear);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        database = new GSKDatabase(this);
        database.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        date = preferences.getString(CommonString1.KEY_DATE, null);
        store_intime = preferences.getString(CommonString1.KEY_STORE_IN_TIME, "");
        store_cd = preferences.getString(CommonString1.KEY_STORE_CD, "");
        editor = preferences.edit();
        user_type = preferences.getString(CommonString1.KEY_USER_TYPE, null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Download data
                if (checkNetIsAvailable()) {
                    if (database.isCoverageDataFilled(date)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DailyEntryScreen.this);
                        builder.setTitle("Parinaam");
                        builder.setMessage("Please Upload Previous Data First")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent startUpload = new Intent(DailyEntryScreen.this, CheckoutNUpload.class);
                                        startActivity(startUpload);
                                        finish();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Intent startDownload = new Intent(DailyEntryScreen.this, CompleteDownloadActivity.class);
                        startActivity(startDownload);
                        finish();
                    }
                } else {
                    Snackbar.make(lv, "No Network Available", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                }
            }

        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        jcplist = database.getJCPData(date);
        coverage = database.getCoverageData(date);
        if (jcplist.size() > 0) {
            // setCheckOutData();
            lv.setAdapter(new MyAdapter());
            lv.setOnItemClickListener(this);
            lv.setVisibility(View.VISIBLE);
            nodata_linear.setVisibility(View.GONE);
            fab.setVisibility(View.GONE);
        }
    }


    public boolean checkNetIsAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }



    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jcplist.size();
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
                convertView = inflater.inflate(R.layout.storelistrow, null);
                holder.storename = (TextView) convertView.findViewById(R.id.tvstorename);
                holder.city = (TextView) convertView.findViewById(R.id.tvcity);
                holder.keyaccount = (TextView) convertView.findViewById(R.id.tvkeyaccount);
                holder.img = (ImageView) convertView.findViewById(R.id.img);
                holder.checkout = (Button) convertView.findViewById(R.id.chkout);
                holder.checkinclose = (ImageView) convertView.findViewById(R.id.closechkin);
                holder.imgtag = (ImageView) convertView.findViewById(R.id.imgtag);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imgtag.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (jcplist.get(position).getGEO_TAG().get(0).equalsIgnoreCase(CommonString1.KEY_GEO_Y)) {
                        Snackbar.make(lv, "Store already GEOTAG", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                    }
                }
            });
            holder.checkout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AlertDialog.Builder builder = new AlertDialog.Builder(DailyEntryScreen.this);
                    builder.setMessage("Are you sure you want to Checkout")
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog, int id) {
                                            if (CheckNetAvailability()) {
                                                Intent i = new Intent(DailyEntryScreen.this, CheckOutStoreActivity.class);
                                                i.putExtra(CommonString1.KEY_STORE_CD, jcplist.get(position).getStore_cd().get(0));
                                                startActivity(i);
                                                //finish();
                                            } else {
                                                Snackbar.make(lv, "No Network", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                            }
                                        }
                                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(
                                DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
            String storecd = jcplist.get(position).getStore_cd().get(0);
            ArrayList<CoverageBean> coverage_data = database.getCoverageSpecificData(storecd);
            if (jcplist.get(position).getGEO_TAG().get(0).equalsIgnoreCase(CommonString1.KEY_GEO_Y)) {
                holder.imgtag.setVisibility(View.VISIBLE);
                holder.img.setVisibility(View.GONE);
                holder.img.setBackgroundResource(R.drawable.store);
            } else {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.store);
                holder.imgtag.setVisibility(View.GONE);
            }
            if (jcplist.get(position).getUploadStatus().get(0).equals(CommonString1.KEY_U)) {
                holder.img.setVisibility(View.VISIBLE);
                holder.img.setBackgroundResource(R.drawable.tick_u);
                holder.checkout.setVisibility(View.INVISIBLE);
                holder.checkinclose.setVisibility(View.INVISIBLE);
            } else if ((jcplist.get(position).getUploadStatus().get(0).equals(CommonString1.KEY_D))) {
                holder.img.setVisibility(View.INVISIBLE);
                holder.checkinclose.setBackgroundResource(R.drawable.tick_d);
                holder.checkinclose.setVisibility(View.VISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if ((jcplist.get(position).getCheckOutStatus().get(0).equals(CommonString1.KEY_C))) {
                holder.img.setVisibility(View.INVISIBLE);
                holder.checkinclose.setBackgroundResource(R.drawable.tick_c);
                holder.checkinclose.setVisibility(View.VISIBLE);
                holder.checkout.setVisibility(View.INVISIBLE);
            } else if (coverage_data.size() > 0) {
                if (coverage_data.get(0).getStatus().equals(CommonString1.STORE_STATUS_LEAVE)) {
                    holder.img.setBackgroundResource(R.drawable.leave_tick);
                    holder.img.setVisibility(View.VISIBLE);
                    holder.checkout.setVisibility(View.INVISIBLE);
                } else if (coverage_data.get(0).getStatus().equals(CommonString1.KEY_VALID)) {
                    holder.checkout.setBackgroundResource(R.drawable.checkout);
                    holder.checkout.setVisibility(View.VISIBLE);
                    holder.checkout.setEnabled(true);
                    holder.checkinclose.setVisibility(View.INVISIBLE);
                    holder.img.setVisibility(View.INVISIBLE);
                } else if (coverage_data.get(0).getStatus().equals(CommonString1.KEY_INVALID)) {
                    holder.img.setVisibility(View.INVISIBLE);
                    holder.checkout.setEnabled(false);
                    holder.checkout.setVisibility(View.INVISIBLE);
                    holder.checkinclose.setBackgroundResource(R.drawable.checkin_ico);
                    holder.checkinclose.setVisibility(View.VISIBLE);
                }

            } else {
                holder.checkout.setEnabled(false);
                holder.checkout.setVisibility(View.INVISIBLE);
               /* holder.img.setVisibility(View.INVISIBLE);
                holder.img.setBackgroundResource(R.drawable.store);*/
                holder.checkinclose.setEnabled(false);
                holder.checkinclose.setVisibility(View.INVISIBLE);
            }

            holder.storename.setText(jcplist.get(position).getStore_name().get(0));
            holder.city.setText(jcplist.get(position).getCity().get(0));
            holder.keyaccount.setText(jcplist.get(position).getKey_account().get(0));
            return convertView;
        }

        private class ViewHolder {
            TextView storename, city, keyaccount;
            ImageView img, checkinclose, imgtag;
            Button checkout;
        }


    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        // TODO Auto-generated method stub
        store_cd = jcplist.get(position).getStore_cd().get(0);
        final String upload_status = jcplist.get(position).getUploadStatus().get(0);
        final String checkoutstatus = jcplist.get(position).getCheckOutStatus().get(0);
        final String STORETYPE_CD = jcplist.get(position).getSTORETYPE_CD().get(0);
        final String STATE_CD = jcplist.get(position).getSTATE_CD().get(0);
        final String GeoTag = jcplist.get(position).getGEO_TAG().get(0);
        editor = preferences.edit();
        editor.putString(CommonString1.KEY_STORE_TYPE_CD, STORETYPE_CD);
        editor.putString(CommonString1.KEY_STATE_CD, STATE_CD);
        editor.commit();
        ArrayList<CoverageBean> coverage_data = database.getCoverageData(date);
        if (upload_status.equals(CommonString1.KEY_U)) {
            Snackbar.make(lv, "All Data Uploaded", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else if (upload_status.equals(CommonString1.KEY_D)) {
            Snackbar.make(lv, "Data Uploaded", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else if (((checkoutstatus.equals(CommonString1.KEY_C)))) {
            Snackbar.make(lv, "Store already checked out", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        } else {
            boolean enteryflag = true;
            if (coverage_data.size() > 0) {
                for (int i2 = 0; i2 < coverage_data.size(); i2++) {
                    if (coverage_data.get(i2).getStoreId().equalsIgnoreCase(store_cd)) {
                        if (coverage_data.get(i2).getStatus().equals(CommonString1.STORE_STATUS_LEAVE)) {
                            Snackbar.make(lv, "Store Already Closed", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                            enteryflag = false;
                            break;
                        }
                    }

                }
                if (enteryflag) {
                    for (int i = 0; i < coverage_data.size(); i++) {
                        if (coverage_data.get(i).getStatus().equalsIgnoreCase(CommonString1.KEY_VALID) ||
                                coverage_data.get(i).getStatus().equalsIgnoreCase(CommonString1.KEY_INVALID)) {
                            if (!coverage_data.get(i).getStoreId().equalsIgnoreCase(store_cd)) {
                                Snackbar.make(lv, "Please checkout from current store", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
                                enteryflag = false;
                                break;
                            }
                        }
                    }
                }
            }
            if (enteryflag) {
                showMyDialog(store_cd, jcplist.get(position).getStore_name().get(0), "Yes",
                        jcplist.get(position).getVISIT_DATE().get(0), jcplist.get(position).getCheckOutStatus().get(0),
                        jcplist.get(position).getGEO_TAG().get(0));
            }

        }
    }

    public String getCurrentTime() {
        Calendar m_cal = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String cdate = formatter.format(m_cal.getTime());
        return cdate;
    }

    public boolean CheckNetAvailability() {

        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .getState() == NetworkInfo.State.CONNECTED
                || connectivityManager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            // we are connected to a network
            connected = true;
        }
        return connected;
    }

    void showMyDialog(final String storeCd, final String storeName, final String status, final String visitDate, final String checkout_status, final String geo_tag) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogbox);
        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrpvisit);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.yes) {
                    editor = preferences.edit();
                    editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "Yes");
                    editor.putString(CommonString1.KEY_LATITUDE, currLatitude);
                    editor.putString(CommonString1.KEY_LONGITUDE, currLongitude);
                    editor.putString(CommonString1.KEY_STORE_NAME, storeName);
                    editor.putString(CommonString1.KEY_STORE_CD, storeCd);
                    editor.putString(CommonString1.KEY_GEO_TAG, geo_tag);
                    if (!visitDate.equals("")) {
                        editor.putString(CommonString1.KEY_VISIT_DATE, visitDate);
                    }
                    if (status.equals("Yes")) {
                        editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "Yes");
                    }
                    database.updateStoreStatusOnCheckout(storeCd, date, CommonString1.KEY_INVALID);
                    editor.commit();
                    if (store_intime.equalsIgnoreCase("")) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString1.KEY_STORE_IN_TIME, getCurrentTime());
                        editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "Yes");
                        editor.commit();

                    }

                    dialog.cancel();
                    boolean flag = true;

                    if (coverage.size() > 0) {
                        for (int i = 0; i < coverage.size(); i++) {
                            if (store_cd.equals(coverage.get(i).getStoreId())) {
                                flag = false;
                                break;
                            }
                        }
                    }

                    if (flag == true) {
                        Intent in = new Intent(DailyEntryScreen.this, StoreimageActivity.class);
                        startActivity(in);
                        finish();
                        overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
                    } else {
                        Intent in = new Intent(DailyEntryScreen.this, StoreEntry.class);
                        startActivity(in);
                        finish();

                    }


                    //finish();

                } else if (checkedId == R.id.no) {
                    dialog.cancel();
                    if (checkout_status.equals(CommonString1.KEY_INVALID) || checkout_status.equals(CommonString1.KEY_VALID)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(DailyEntryScreen.this);
                        builder.setMessage(CommonString1.DATA_DELETE_ALERT_MESSAGE)
                                .setCancelable(false)
                                .setPositiveButton("Yes",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog,
                                                                int id) {
                                                UpdateData(storeCd);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(CommonString1.KEY_STORE_CD, storeCd);
                                                editor.putString(CommonString1.KEY_STORE_IN_TIME, getCurrentTime());
                                                editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "");
                                                editor.commit();
                                                Intent in = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
                                                startActivity(in);
                                               // finish();

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
                        UpdateData(storeCd);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(CommonString1.KEY_STORE_CD, storeCd);
                        editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "");
                        editor.commit();
                        Intent in = new Intent(DailyEntryScreen.this, NonWorkingReason.class);
                        startActivity(in);
                       // finish();
                    }
                }
            }

        });


        dialog.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        currLatitude = Double.toString(location.getLatitude());
        currLongitude = Double.toString(location.getLongitude());
    }


    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }

    public void UpdateData(String storeCd) {
        database.open();
        database.deleteSpecificStoreData(storeCd);
        database.updateStoreStatusOnCheckout(storeCd, jcplist.get(0).getVISIT_DATE().get(0), "N");

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
            overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
            finish();


        }
        return super.onOptionsItemSelected(item);
    }


}
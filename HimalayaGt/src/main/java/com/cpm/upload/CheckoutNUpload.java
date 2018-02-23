package com.cpm.upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cpm.Constants.CommonString1;
import com.cpm.capitalfoods.R;

import com.cpm.database.GSKDatabase;
import com.cpm.delegates.CoverageBean;
import com.cpm.message.AlertMessage;

import com.cpm.retrofit.RetrofitClass;
import com.cpm.xmlGetterSetter.AssetMappingGetterSetter;
import com.cpm.xmlGetterSetter.BrandGetterSetter;
import com.cpm.xmlGetterSetter.ChecklistGetterSetter;
import com.cpm.xmlGetterSetter.FailureGetterSetter;
import com.cpm.xmlGetterSetter.FeedBackGetterSetter;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.POSMDATAGetterSetter;
import com.cpm.xmlGetterSetter.SkuQwantityGetterSetter;
import com.cpm.xmlGetterSetter.StoreSignAgeGetterSetter;
import com.cpm.xmlGetterSetter.StockNewGetterSetter;
import com.cpm.xmlGetterSetter.WindowListGetterSetter;
import com.cpm.xmlHandler.FailureXMLHandler;

public class CheckoutNUpload extends Activity {

    ArrayList<JourneyPlanGetterSetter> jcplist;
    GSKDatabase database;
    private SharedPreferences preferences;
    private String username, visit_date, store_id, store_intime, store_out_time, date, prev_date, result;
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message, tv_title;
    private Data data;
    ArrayList<CoverageBean> coverageBean;
    private ArrayList<CoverageBean> coverageBeanlist = new ArrayList<CoverageBean>();
    String app_ver;
    String datacheck = "";
    String[] words;
    String validity;
    int mid;
    String errormsg = "";
    String Path;
    private FailureGetterSetter failureGetterSetter = null;
    private ArrayList<StoreSignAgeGetterSetter> SignAgeData = new ArrayList<StoreSignAgeGetterSetter>();
    private ArrayList<POSMDATAGetterSetter> POSMdata = new ArrayList<POSMDATAGetterSetter>();
    private ArrayList<WindowListGetterSetter> window1 = new ArrayList<WindowListGetterSetter>();
    private ArrayList<ChecklistGetterSetter> check1 = new ArrayList<ChecklistGetterSetter>();
    FeedBackGetterSetter feedBackGetterSetter;
    JourneyPlanGetterSetter journeyPlanGetterSetter;
    private ArrayList<StockNewGetterSetter> stockData = new ArrayList<StockNewGetterSetter>();
    private ArrayList<StockNewGetterSetter> stockCategoryImageData = new ArrayList<StockNewGetterSetter>();
    private ArrayList<SkuQwantityGetterSetter> windowskuQuantityList = new ArrayList<SkuQwantityGetterSetter>();
    private ArrayList<AssetMappingGetterSetter> assetData = new ArrayList<>();
    ArrayList<BrandGetterSetter> assetStockData = new ArrayList<>();
    private ArrayList<AssetMappingGetterSetter> additional_visibility = new ArrayList<AssetMappingGetterSetter>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout_n_upload);
        database = new GSKDatabase(this);
        database.open();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        username = preferences.getString(CommonString1.KEY_USERNAME, "");
        app_ver = preferences.getString(CommonString1.KEY_VERSION, "");
        visit_date = preferences.getString(CommonString1.KEY_DATE, null);
        Path = CommonString1.FILE_PATH;
        if (!isCheckoutDataExist()) {
            new UploadTask(this).execute();
        }
    }

    public boolean isCheckoutDataExist() {
        boolean flag = false;
        jcplist = database.getAllJCPData();
        for (int i = 0; i < jcplist.size(); i++) {
            if (!jcplist.get(i).getVISIT_DATE().get(0).equals(visit_date)) {
                prev_date = jcplist.get(i).getVISIT_DATE().get(0);
                store_id = jcplist.get(i).getStore_cd().get(0);
                coverageBean = database.getCoverageSpecificData(store_id);
                if (coverageBean.size() > 0) {
                    for (int i1 = 0; i1 < coverageBean.size(); i1++) {
                        if (coverageBean.get(i1).getStatus().equals(CommonString1.KEY_VALID)) {
                            flag = true;
                            username = coverageBean.get(i1).getUserId();
                            store_intime = coverageBean.get(i1).getInTime();
                            store_out_time = coverageBean.get(i1).getOutTime();
                            if (store_out_time == null || store_out_time.equals("")) {
                                store_out_time = getCurrentTime();
                            }
                            date = coverageBean.get(i1).getVisitDate();
                            new BackgroundTask(this).execute();
                        }
                    }

                }

            }
        }

        return flag;
    }

    private class BackgroundTask extends AsyncTask<Void, Data, String> {
        private Context context;

        BackgroundTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom);
            dialog.setTitle("Uploading Checkout Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                String myTime = coverageBean.get(0).getInTime();
                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss");
                Date d = null;
                try {
                    d = df1.parse(myTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(d);
                cal.get(Calendar.HOUR_OF_DAY);
                cal.add(Calendar.MINUTE, 30);
                String out_time = df1.format(cal.getTime());
                data = new Data();
                data.value = 20;
                data.name = "Checked out Data Uploading";
                publishProgress(data);
                String onXML = "[STORE_CHECK_OUT_STATUS][USER_ID]"
                        + username
                        + "[/USER_ID]" + "[STORE_ID]"
                        + store_id
                        + "[/STORE_ID][LATITUDE]"
                        + coverageBean.get(0).getLatitude()
                        + "[/LATITUDE][LOGITUDE]"
                        + coverageBean.get(0).getLongitude()
                        + "[/LOGITUDE][CHECKOUT_DATE]"
                        + coverageBean.get(0).getVisitDate()
                        + "[/CHECKOUT_DATE][CHECK_OUTTIME]"
                        + out_time
                        + "[/CHECK_OUTTIME][CHECK_INTIME]"
                        + coverageBean.get(0).getInTime()
                        + "[/CHECK_INTIME][CREATED_BY]"
                        + username
                        + "[/CREATED_BY][/STORE_CHECK_OUT_STATUS]";

                final String sos_xml = "[DATA]" + onXML + "[/DATA]";

                SoapObject request = new SoapObject(CommonString1.NAMESPACE, "Upload_Store_ChecOut_Status");
                request.addProperty("onXML", sos_xml);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                androidHttpTransport.call(CommonString1.SOAP_ACTION + "Upload_Store_ChecOut_Status", envelope);
                Object result = (Object) envelope.getResponse();

                if (result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS_chkout)) {
                    database.updateCoverageStoreOutTime(store_id, coverageBean.get(0).getVisitDate(), store_out_time, CommonString1.KEY_C);
                    database.updateStoreStatusOnCheckout(store_id, coverageBean.get(0).getVisitDate(), CommonString1.KEY_C);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonString1.KEY_STORE_CD, "");
                    editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "");
                    editor.commit();
                    data.value = 100;
                    data.name = "Checkout Done";
                    publishProgress(data);
                    return CommonString1.KEY_SUCCESS;
                } else {
                    if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS_chkout)) {
                        return "Upload_Store_ChecOut_Status";
                    }
                    if (result.toString().equalsIgnoreCase(CommonString1.KEY_FAILURE)) {
                        return "Upload_Store_ChecOut_Status";
                    }
                }

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(CheckoutNUpload.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_upload", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();

                    }
                });
            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        CheckoutNUpload.this,
                        AlertMessage.MESSAGE_EXCEPTION, "uploaded", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub

            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            dialog.dismiss();

            if (result.equals(CommonString1.KEY_SUCCESS)) {
                new UploadTask(CheckoutNUpload.this).execute();
            } else if (!result.equals("")) {
                Toast.makeText(getApplicationContext(), "Network Error Try Again", Toast.LENGTH_SHORT).show();
                finish();

            }

        }

    }

    class Data {
        int value;
        String name;
    }

    public String getCurrentTime() {

        Calendar m_cal = Calendar.getInstance();

        String intime = m_cal.get(Calendar.HOUR_OF_DAY) + ":"
                + m_cal.get(Calendar.MINUTE) + ":" + m_cal.get(Calendar.SECOND);

        return intime;

    }


    private class UploadTask extends AsyncTask<Void, Data, String> {
        private Context context;

        UploadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dialog = new Dialog(context);
            dialog.setContentView(R.layout.custom_upload);
            dialog.setTitle("Uploading Data");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);
            tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {

                data = new Data();
                coverageBeanlist = database.getCoverageData(prev_date);
                for (int i = 0; i < coverageBeanlist.size(); i++) {
                    journeyPlanGetterSetter = database.getStoreStatus(coverageBeanlist.get(i).getStoreId());
                    if (!journeyPlanGetterSetter.getUploadStatus().get(0).equalsIgnoreCase(CommonString1.KEY_D)) {
                        String onXML = "[DATA][USER_DATA][STORE_CD]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_CD]" + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE][LATITUDE]"
                                + coverageBeanlist.get(i).getLatitude()
                                + "[/LATITUDE][APP_VERSION]"
                                + app_ver
                                + "[/APP_VERSION][LONGITUDE]"
                                + coverageBeanlist.get(i).getLongitude()
                                + "[/LONGITUDE][IN_TIME]"
                                + coverageBeanlist.get(i).getInTime()
                                + "[/IN_TIME][OUT_TIME]"
                                + coverageBeanlist.get(i).getOutTime()
                                + "[/OUT_TIME][UPLOAD_STATUS]"
                                + "N"
                                + "[/UPLOAD_STATUS][USER_ID]" + username
                                + "[/USER_ID][IMAGE_URL]" + coverageBeanlist.get(i).getImage()
                                + "[/IMAGE_URL][REASON_ID]"
                                + coverageBeanlist.get(i).getReasonid()
                                + "[/REASON_ID][REASON_REMARK]"
                                + coverageBeanlist.get(i).getRemark()
                                + "[/REASON_REMARK][/USER_DATA][/DATA]";

                        SoapObject request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE);
                        request.addProperty("onXML", onXML);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                        androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE, envelope);
                        Object result = (Object) envelope.getResponse();
                        datacheck = result.toString();
                        datacheck = datacheck.replace("\"", "");
                        words = datacheck.split("\\;");
                        validity = (words[0]);

                        if (validity.equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                            database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString1.KEY_P);
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(),
                                    coverageBeanlist.get(i).getVisitDate(), CommonString1.KEY_P);
                            final int finalI = i;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    String value = "Upload Store Data " + (finalI + 1) + " of " + coverageBeanlist.size();
                                    tv_title.setText(value);
                                }
                            });
                        } else {
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                continue;
                               // return CommonString1.METHOD_UPLOAD_DR_STORE_COVERAGE;
                            }

                        }

                        mid = Integer.parseInt((words[1]));
                        data.value = 10;
                        data.name = "Uploading";
                        publishProgress(data);
                        String final_xml = "";

                        ///////window1 DATA ,checklist,stock window
                        final_xml = "";
                        onXML = "";
                        window1 = database.getwindowdat(coverageBeanlist.get(i).getStoreId());
                        if (window1.size() > 0) {
                            for (int j = 0; j < window1.size(); j++) {
                                int key_id = window1.get(j).getKey_id();

                                check1 = database.getchecklistByCommonid(key_id);
                                String onXMLCHECKLIST = "", checklistfinal_xml = "";

                                for (int j1 = 0; j1 < check1.size(); j1++) {
                                    onXMLCHECKLIST = "[CHECK_LIST][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[WINDOW_CD]"
                                            + check1.get(j1).getWINDOW_CD()
                                            + "[/WINDOW_CD]"
                                            + "[CHECKLIST_CD]"
                                            + check1.get(j1).getCHECKLIST_CD()
                                            + "[/CHECKLIST_CD]"
                                            + "[ANSWER_CD]"
                                            + check1.get(j1).getANSWER_CD()
                                            + "[/ANSWER_CD]"
                                            + "[COMMON_ID]"
                                            + key_id
                                            + "[/COMMON_ID]"
                                            + "[/CHECK_LIST]";

                                    checklistfinal_xml = checklistfinal_xml + onXMLCHECKLIST;
                                }
                                final String checklist_xml = checklistfinal_xml;

                                windowskuQuantityList = database.getWindowSkuQuantityInsertedDataByCommonid(key_id);
                                String windowQuantityOnXML = "", windowQuantity_final_xml="";
                                for (int j2 = 0; j2 < windowskuQuantityList.size(); j2++) {
                                    if (windowskuQuantityList.get(j2).getQwantity().equalsIgnoreCase("")) {
                                        continue;
                                    }
                                    windowQuantityOnXML = "[WINDOW_STOCK][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[BRAND_CD]"
                                            + windowskuQuantityList.get(j2).getBrand_cd().get(0)
                                            + "[/BRAND_CD]"
                                            + "[SKU_CD]"
                                            + windowskuQuantityList.get(j2).getSku_cd().get(0)
                                            + "[/SKU_CD]"
                                            + "[QUANTITY]"
                                            + windowskuQuantityList.get(j2).getQwantity()
                                            + "[/QUANTITY]"
                                            + "[COMMON_ID]"
                                            + key_id
                                            + "[/COMMON_ID]"
                                            + "[/WINDOW_STOCK]";

                                    windowQuantity_final_xml = windowQuantity_final_xml + windowQuantityOnXML;

                                }
                                final String windowStock_xml = windowQuantity_final_xml;


                                onXML = "[WINDOW_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[WINDOW_CD]"
                                        + window1.get(j).getWindow_cd().get(0)
                                        + "[/WINDOW_CD]"
                                        + "[REMARK]"
                                        + window1.get(j).getRemark()
                                        + "[/REMARK]"
                                        + "[WINDOW_IMAGE]"
                                        + window1.get(j).getWindow_image()
                                        + "[/WINDOW_IMAGE]"
                                        + "[EXISTORNOT]"
                                        + window1.get(j).getExitOrNot().get(0)
                                        + "[/EXISTORNOT]"
                                        + "[CHECKLIST]"
                                        + checklist_xml
                                        + "[/CHECKLIST]"
                                        + "[WINDOW_STOCK]"
                                        + windowStock_xml
                                        + "[/WINDOW_STOCK]"
                                        + "[/WINDOW_DATA]";


                                final_xml = final_xml + onXML;

                            }


                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "GT_WINDOW_DATA_WITH_CHECKLIST_AND_STOCK");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                            androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();

                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                continue;
                               // return "GT_WINDOW_DATA_WITH_CHECKLIST_AND_STOCK";
                            }
                            data.value = 20;
                            data.name = "GT_WINDOW_DATA_WITH_CHECKLIST_AND_STOCK";
                            publishProgress(data);
                        }


                        // asset  data

                        final_xml = "";
                        onXML = "";
                        assetData = database.getassetdata(coverageBeanlist.get(i).getStoreId());
                        if (assetData.size() > 0) {

                            for (int j = 0; j < assetData.size(); j++) {

                                int key_id = assetData.get(j).getKey_id();
                                assetStockData=   database.getAssetStockSavedDataByCommonid(key_id);
                                String onXMLASSETSTOCK = "", assetfinal_xml = "";

                                for (int j1 = 0; j1 < assetStockData.size(); j1++) {
                                    onXMLASSETSTOCK = "[ASSET_STOCK][MID]"
                                            + mid
                                            + "[/MID]"
                                            + "[CREATED_BY]"
                                            + username
                                            + "[/CREATED_BY]"
                                            + "[SKU_CD]"
                                            + assetStockData.get(j1).getSku_cd().get(0)
                                            + "[/SKU_CD]"
                                            + "[STOCK_QUANTITY]"
                                            + assetStockData.get(j1).getSkuQuantity()
                                            + "[/STOCK_QUANTITY]"
                                            + "[BRAND_CD]"
                                            + assetStockData.get(j1).getBrand_cd().get(0)
                                            + "[/BRAND_CD]"
                                            + "[COMMON_ID]"
                                            + key_id
                                            + "[/COMMON_ID]"
                                            + "[/ASSET_STOCK]";

                                    assetfinal_xml = assetfinal_xml + onXMLASSETSTOCK;
                                }
                                final String assetStock_xml = assetfinal_xml;


                                onXML = "[ASSET_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[ASSET_IMG]"
                                        + assetData.get(j).getAsset_image()
                                        + "[/ASSET_IMG]"
                                        + "[REMARK]"
                                        + assetData.get(j).getRemark()
                                        + "[/REMARK]"
                                        + "[EXITORNOT]"
                                        + assetData.get(j).getExitOrNot()
                                        + "[/EXITORNOT]"
                                        + "[ASSET_STOCK_DATA]"
                                        + assetStock_xml
                                        + "[/ASSET_STOCK_DATA]"
                                        + "[/ASSET_DATA]";

                                final_xml = final_xml + onXML;

                            }
                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "GT_ASSET_DATA_WITH_STOCK");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                            androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                continue;
                                //return "GT_ASSET_DATA_WITH_STOCK";
                            }
                            data.value = 38;
                            data.name = "GT_ASSET_DATA_WITH_STOCK";
                            publishProgress(data);

                        }



                        // Adddititional data

                        final_xml = "";
                        onXML = "";
                        additional_visibility  = database.getAdditionalinsertedData(coverageBeanlist.get(i).getStoreId());
                        if (additional_visibility.size() > 0) {

                            for (int j = 0; j < additional_visibility.size(); j++) {
                                onXML = "[ADDITIONAL_VISIBILITY][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[CATEGORY_CD]"
                                        + additional_visibility.get(j).getCategory_id().get(0)
                                        + "[/CATEGORY_CD]"
                                        + "[ADDITIONALV_IMG]"
                                        + additional_visibility.get(j).getAdditional_image().get(0)
                                        + "[/ADDITIONALV_IMG]"
                                        + "[TOGGLE_VALUE]"
                                        + additional_visibility.get(j).getToglvale().get(0)
                                        + "[/TOGGLE_VALUE]"
                                        + "[REMARK]"
                                        + additional_visibility.get(j).getRemark()
                                        + "[/REMARK]"
                                        + "[/ADDITIONAL_VISIBILITY]";

                                final_xml = final_xml + onXML;

                            }
                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "GT_ADDITIONAL_VISIBILITY");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                            androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                continue;
                              //  return "GT_ADDITIONAL_VISIBILITY";
                            }
                            data.value = 38;
                            data.name = "GT_ADDITIONAL_VISIBILITY";
                            publishProgress(data);

                        }


                        // stock category image with data

                        final_xml = "";
                        onXML = "";
                        stockCategoryImageData = database.getstockcategoryDataImages(coverageBeanlist.get(i).getStoreId());
                        if (stockCategoryImageData.size() > 0) {

                            for (int j = 0; j < stockCategoryImageData.size(); j++) {
                                onXML = "[STOCK_CATEGORY_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[CATEGORY_CD]"
                                        + stockCategoryImageData.get(j).getCATEGORY_CD()
                                        + "[/CATEGORY_CD]"
                                        + "[CATEGORY_IMAGE]"
                                        + stockCategoryImageData.get(j).getCategory_image()
                                        + "[/CATEGORY_IMAGE]"
                                        + "[/STOCK_CATEGORY_DATA]";

                                final_xml = final_xml + onXML;

                            }
                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "GT_STOCK_CATEGORY_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                            androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                continue;
                                //return "STOCK_CATEGORY_DATA";
                            }
                            data.value = 38;
                            data.name = "STOCK_CATEGORY_DATA";
                            publishProgress(data);

                        }

                        // stock data
                        final_xml = "";
                        onXML = "";
                        stockData = database.getStockUpload(coverageBeanlist.get(i).getStoreId());
                        if (stockData.size() > 0) {
                            for (int j = 0; j < stockData.size(); j++) {
                                onXML = "[STOCK_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[OPENING_TOTAL_STOCK]"
                                        + stockData.get(j).getOpenning_total_stock()
                                        + "[/OPENING_TOTAL_STOCK]"
                                        + "[FACING]"
                                        + stockData.get(j).getOpening_facing()
                                        + "[/FACING]"
                                        + "[EXPIRE_STOCK]"
                                        + stockData.get(j).getStock_under45days()
                                        + "[/EXPIRE_STOCK]"
                                        + "[SKU_CD]"
                                        + stockData.get(j).getSku_cd()
                                        + "[/SKU_CD]"
                                        + "[BRAND_CD]"
                                        + stockData.get(j).getBrand_cd()
                                        + "[/BRAND_CD]"
                                        + "[/STOCK_DATA]";
                                final_xml = final_xml + onXML;

                            }
                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                            request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "GT_STOCK_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                            androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                continue;
                               // return "STOCK_DATA";
                            }
                            data.value = 45;
                            data.name = "STOCK_DATA";
                            publishProgress(data);

                        }



//							uploading POSM_DATA data
                        final_xml = "";
                        onXML = "";
                        POSMdata = database.getPOSMData(coverageBeanlist.get(i).getStoreId());
                        if (POSMdata.size() > 0) {
                            for (int j = 0; j < POSMdata.size(); j++) {

                                onXML = "[POSM_DATA][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[QUANTITY]"
                                        + POSMdata.get(j).getEdText()
                                        + "[/QUANTITY]"
                                        + "[POSM_CD]"
                                        + POSMdata.get(j).getPOSM_CD().get(0)
                                        + "[/POSM_CD]"
                                        + "[Image_URL]"
                                        + POSMdata.get(j).getImage_Url()
                                        + "[/Image_URL]"
                                        + "[/POSM_DATA]";

                                final_xml = final_xml + onXML;

                            }

                            final String sos_xml = "[DATA]" + final_xml + "[/DATA]";

                            request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_UPLOAD_XML);
                            request.addProperty("XMLDATA", sos_xml);
                            request.addProperty("KEYS", "GT_POSM_DATA");
                            request.addProperty("USERNAME", username);
                            request.addProperty("MID", mid);
                            envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                            androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_XML, envelope);
                            result = (Object) envelope.getResponse();

                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                continue;
                                //return "POSM_DATA";
                            }

                            data.value = 60;
                            data.name = "POSM_DATA";
                            publishProgress(data);

                        }


///////FEEDBACK_DATA DATA
                        final_xml = "";
                        onXML = "";
                        feedBackGetterSetter = database.getfeedbackData(coverageBeanlist.get(i).getStoreId());
                        if (feedBackGetterSetter.getFeedBack() != null) {
                            if (!feedBackGetterSetter.getFeedBack().equalsIgnoreCase("")) {
                                onXML = "[FEEDBACK][MID]"
                                        + mid
                                        + "[/MID]"
                                        + "[CREATED_BY]"
                                        + username
                                        + "[/CREATED_BY]"
                                        + "[FEEDBACK]"
                                        + feedBackGetterSetter.getFeedBack()
                                        + "[/FEEDBACK]"
                                        + "[/FEEDBACK]";

                                final_xml = final_xml + onXML;


                                final String sos_xml = "[DATA]" + final_xml
                                        + "[/DATA]";

                                request = new SoapObject(
                                        CommonString1.NAMESPACE,
                                        CommonString1.METHOD_UPLOAD_XML);
                                request.addProperty("XMLDATA", sos_xml);
                                request.addProperty("KEYS", "GT_FEEDBACK_DATA");
                                request.addProperty("USERNAME", username);
                                request.addProperty("MID", mid);

                                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                                envelope.dotNet = true;
                                envelope.setOutputSoapObject(request);
                                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                                androidHttpTransport.call(CommonString1.SOAP_ACTION + CommonString1.METHOD_UPLOAD_XML, envelope);
                                result = (Object) envelope.getResponse();
                                if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                    continue;
                                  //  return "FEEDBACK_DATA";
                                }

                                data.value = 70;
                                data.name = "FEEDBACK_DATA";
                                publishProgress(data);

                            }
                        }


//////////////////////////////////////

                    /*    if (coverageBeanlist.get(i).getImage() != null && !coverageBeanlist.get(i).getImage().equals("")) {
                            if (new File(CommonString1.FILE_PATH + coverageBeanlist.get(i).getImage()).exists()) {
                                result = UploadImage(coverageBeanlist.get(i).getImage(), "GTStoreImages");
                                if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                    return "GTStoreImages";
                                } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FALSE)) {
                                    return "GTStoreImages";
                                } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FAILURE)) {
                                    return "GTStoreImages" + "," + errormsg;
                                }
                                runOnUiThread(new Runnable() {

                                    public void run() {
                                        message.setText("GT Store Image Uploaded");
                                    }
                                });

                            }
                            data.value = 60;
                            data.name = "GTStoreImages";
                            publishProgress(data);
                        }


//////////////////////////
                        if (POSMdata.size() > 0) {

                            for (int j = 0; j < POSMdata.size(); j++) {

                                if (POSMdata.get(j).getImage_Url() != null && !POSMdata.get(j).getImage_Url().equals("")) {

                                    if (new File(CommonString1.FILE_PATH + POSMdata.get(j).getImage_Url()).exists()) {

                                        result = UploadImage(POSMdata.get(j).getImage_Url(), "GTPosmImages");


                                        if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                            return "GTPosmImages";
                                        } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FALSE)) {
                                            return "GTPosmImages";
                                        } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FAILURE)) {
                                            return "GTPosmImages" + "," + errormsg;
                                        }
                                        runOnUiThread(new Runnable() {

                                            public void run() {
                                                message.setText("GTPosmImages Uploaded");
                                            }
                                        });

                                    }
                                }
                            }
                            data.value = 80;
                            data.name = "GTPosmImages";
                            publishProgress(data);
                        }


                        ///////////////
                        if (window1.size() > 0) {
                            for (int j = 0; j < window1.size(); j++) {
                                if (window1.get(j).getWindow_image() != null && !window1.get(j).getWindow_image().equals("")) {

                                    if (new File(CommonString1.FILE_PATH + window1.get(j).getWindow_image()).exists()) {

                                        result = UploadImage(window1.get(j).getWindow_image(), "GTWindowImages");


                                        if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                            return "GTWindowImages";
                                        } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FALSE)) {
                                            return "GTWindowImages";
                                        } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

                                            return "GTWindowImages" + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {

                                                message.setText("GTWindowImages Uploaded");
                                            }
                                        });

                                    }
                                }
                            }
                            data.value = 95;
                            data.name = "GTWindowImages";
                            publishProgress(data);
                        }


                        if (stockCategoryImageData.size() > 0) {
                            for (int j = 0; j < stockCategoryImageData.size(); j++) {
                                if (stockCategoryImageData.get(j).getCategory_image() != null && !stockCategoryImageData.get(j).getCategory_image().equals("")) {
                                    if (new File(CommonString1.FILE_PATH + stockCategoryImageData.get(j).getCategory_image()).exists()) {
                                        result = UploadImage(stockCategoryImageData.get(j).getCategory_image(), "GTStockImages");
                                        if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                            return "GTStockImages";
                                        } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FALSE)) {
                                            return "GTStockImages";
                                        } else if (result.toString().equalsIgnoreCase(CommonString1.KEY_FAILURE)) {

                                            return "GTStockImages" + "," + errormsg;
                                        }

                                        runOnUiThread(new Runnable() {

                                            public void run() {

                                                message.setText("GTStockImages Uploaded");
                                            }
                                        });

                                    }
                                }
                            }
                            data.value = 98;
                            data.name = "GTStockImages";
                            publishProgress(data);
                        }*/


                        // SET COVERAGE STATUS

                        final_xml = "";
                        onXML = "";

                        onXML = "[COVERAGE_STATUS][STORE_ID]"
                                + coverageBeanlist.get(i).getStoreId()
                                + "[/STORE_ID]"
                                + "[VISIT_DATE]"
                                + coverageBeanlist.get(i).getVisitDate()
                                + "[/VISIT_DATE]"
                                + "[USER_ID]"
                                + coverageBeanlist.get(i).getUserId()
                                + "[/USER_ID]"
                                + "[STATUS]"
                                + CommonString1.KEY_D
                                + "[/STATUS]"
                                + "[/COVERAGE_STATUS]";

                        final_xml = final_xml + onXML;
                        final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                        SoapObject request1 = new SoapObject(CommonString1.NAMESPACE, CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS);
                        request1.addProperty("onXML", sos_xml);
                        SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope1.dotNet = true;
                        envelope1.setOutputSoapObject(request1);
                        HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString1.URL);
                        androidHttpTransport1.call(CommonString1.SOAP_ACTION + CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope1);
                        Object result1 = (Object) envelope1.getResponse();
                        if (result1.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                            database.open();
                            database.updateCoverageStatus(coverageBeanlist.get(i).getMID(), CommonString1.KEY_D);
                            database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(), coverageBeanlist.get(i).getVisitDate(), CommonString1.KEY_D);
                        }
                        if (!result1.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                            continue;
                           // return "COVERAGE_STATUS";
                        }
                        data.value = 75;
                        data.name = "COVERAGE_STATUS";
                        publishProgress(data);

                    }

                }
                File dir = new File(CommonString1.FILE_PATH);
                ArrayList<String> list = new ArrayList();
                list = getFileNames(dir.listFiles());
                if (list.size() > 0) {
                    for (int i1 = 0; i1 < list.size(); i1++) {
                        if (list.get(i1).contains("_STOREIMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
                                    originalFile.getName(), "GTStoreImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 80;
                            data.name = "StoreImages";
                            publishProgress(data);
                        }
                        if (list.get(i1).contains("_NONWORKING_")) {
                            File originalFile = new File(Path + list.get(i1));
                            //Retrofit
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this, originalFile.getName(), "GTStoreImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 85;
                            data.name = "GTStoreImages";
                            publishProgress(data);
                        }

                        if (list.get(i1).contains("_POASMIMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this, originalFile.getName(), "GTPosmImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 90;
                            data.name = "GTPosmImages";
                            publishProgress(data);
                        }


                        if (list.get(i1).contains("_WINDOWIMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this, originalFile.getName(), "GTWindowImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 93;
                            data.name = "GTWindowImages";
                            publishProgress(data);
                        }

                        if (list.get(i1).contains("_CATEGORYIMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            //Retrofit
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this, originalFile.getName(), "GTStockImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 95;
                            data.name = "GTStockImages";
                            publishProgress(data);
                        }

                        if (list.get(i1).contains("front")) {
                            File originalFile = new File(Path + list.get(i1));
                            //Retrofit
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this, originalFile.getName(), "GTGeoTagImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 95;
                            data.name = "GTGeoTagImages";
                            publishProgress(data);
                        }

                        if (list.get(i1).contains("_ADDITIONALIMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
                                    originalFile.getName(), "GTAdditionalVisibilityImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 80;
                            data.name = "_ADDITIONALIMG_";
                            publishProgress(data);
                        }

                        if (list.get(i1).contains("_ASSETIMG_")) {
                            File originalFile = new File(Path + list.get(i1));
                            result = RetrofitClass.UploadImageByRetrofit(CheckoutNUpload.this,
                                    originalFile.getName(), "GTAssetImages");
                            if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                                return result.toString();
                            }
                            data.value = 80;
                            data.name = "_ASSETIMG_";
                            publishProgress(data);
                        }

                    }
                    String response = updateStatus();
                    if (!response.equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                        return CommonString1.KEY_FAILURE;
                    } else {
                        return CommonString1.KEY_SUCCESS;
                    }

                } else {
                    String response = updateStatus();
                    if (response.equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                        return CommonString1.KEY_SUCCESS;
                    } else {
                        return AlertMessage.MESSAGE_SOCKETEXCEPTION;
                    }
                }
            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(CheckoutNUpload.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket_upload", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        message.showMessage();
                    }
                });
            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(
                        CheckoutNUpload.this,
                        AlertMessage.MESSAGE_EXCEPTION, "uploaded", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Data... values) {
            // TODO Auto-generated method stub
            pb.setProgress(values[0].value);
            percentage.setText(values[0].value + "%");
            message.setText(values[0].name);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.contains(CommonString1.KEY_SUCCESS)) {
                AlertMessage message = new AlertMessage(CheckoutNUpload.this, AlertMessage.MESSAGE_UPLOAD_DATA, "success", null);
                message.showMessage();
               // database.deleteAllTables();
            } else if (!result.equals("")) {
                AlertMessage message = new AlertMessage(CheckoutNUpload.this, "Error in uploading :" + result, "success", null);
                message.showMessage();
            }
        }
    }

    private String updateStatus() throws IOException, XmlPullParserException {
        String final_xml = "";
        String onXML = "";
        Object result1 = "";
        try {
            coverageBeanlist = database.getCoverageData(prev_date);
            for (int i = 0; i < coverageBeanlist.size(); i++) {
                journeyPlanGetterSetter = database.getStoreStatus(coverageBeanlist.get(i).getStoreId());
                if (journeyPlanGetterSetter.getUploadStatus().get(0).equalsIgnoreCase(CommonString1.KEY_D)) {
                    onXML = "[COVERAGE_STATUS][STORE_ID]"
                            + coverageBeanlist.get(i).getStoreId()
                            + "[/STORE_ID]"
                            + "[VISIT_DATE]"
                            + coverageBeanlist.get(i).getVisitDate()
                            + "[/VISIT_DATE]"
                            + "[USER_ID]"
                            + coverageBeanlist.get(i).getUserId()
                            + "[/USER_ID]"
                            + "[STATUS]"
                            + CommonString1.KEY_U
                            + "[/STATUS]"
                            + "[/COVERAGE_STATUS]";
                    final_xml = final_xml + onXML;

                    final String sos_xml = "[DATA]" + final_xml + "[/DATA]";
                    SoapObject request1 = new SoapObject(CommonString1.NAMESPACE, CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS);
                    request1.addProperty("onXML", sos_xml);
                    SoapSerializationEnvelope envelope1 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope1.dotNet = true;
                    envelope1.setOutputSoapObject(request1);
                    HttpTransportSE androidHttpTransport1 = new HttpTransportSE(CommonString1.URL);
                    androidHttpTransport1.call(CommonString1.SOAP_ACTION + CommonString1.MEHTOD_UPLOAD_COVERAGE_STATUS, envelope1);
                    result1 = (Object) envelope1.getResponse();
                    if (result1.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                        database.open();
                        database.deleteSpecificStoreData(coverageBeanlist.get(i).getStoreId());
                        database.updateStoreStatusOnLeave(coverageBeanlist.get(i).getStoreId(),
                                coverageBeanlist.get(i).getVisitDate(), CommonString1.KEY_U);
                        return CommonString1.KEY_SUCCESS;
                    }
                    if (!result1.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {
                        return "COVERAGE_STATUS";
                    }
                    data.value = 100;
                    data.name = "COVERAGE_STATUS";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result1.toString();

    }

    public ArrayList<String> getFileNames(File[] file) {
        ArrayList<String> arrayFiles = new ArrayList<String>();
        if (file.length > 0) {
            for (int i = 0; i < file.length; i++)
                arrayFiles.add(file[i].getName());
        }
        return arrayFiles;
    }


    public String UploadImage(String path, String folder_path) throws Exception {
        errormsg = "";
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(Path + path, o);
        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        Bitmap bitmap = BitmapFactory.decodeFile(
                Path + path, o2);

        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        byte[] ba = bao.toByteArray();
        String ba1 = Base64.encodeBytes(ba);

        SoapObject request = new SoapObject(CommonString1.NAMESPACE,
                CommonString1.METHOD_UPLOAD_IMAGE);

        String[] split = path.split("/");
        String path1 = split[split.length - 1];

        request.addProperty("img", ba1);
        request.addProperty("name", path1);
        request.addProperty("FolderName", folder_path);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                CommonString1.URL);

        androidHttpTransport
                .call(CommonString1.SOAP_ACTION_UPLOAD_IMAGE,
                        envelope);
        Object result = (Object) envelope.getResponse();

        if (!result.toString().equalsIgnoreCase(CommonString1.KEY_SUCCESS)) {

            if (result.toString().equalsIgnoreCase(CommonString1.KEY_FALSE)) {
                return CommonString1.KEY_FALSE;
            }

            SAXParserFactory saxPF = SAXParserFactory.newInstance();
            SAXParser saxP = saxPF.newSAXParser();
            XMLReader xmlR = saxP.getXMLReader();

            // for failure
            FailureXMLHandler failureXMLHandler = new FailureXMLHandler();
            xmlR.setContentHandler(failureXMLHandler);

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(result.toString()));
            xmlR.parse(is);

            failureGetterSetter = failureXMLHandler
                    .getFailureGetterSetter();

            if (failureGetterSetter.getStatus().equalsIgnoreCase(
                    CommonString1.KEY_FAILURE)) {
                errormsg = failureGetterSetter.getErrorMsg();
                return CommonString1.KEY_FAILURE;
            }
        } else {
            new File(Path + path).delete();
            SharedPreferences.Editor editor = preferences
                    .edit();
            editor.putString(CommonString1.KEY_STOREVISITED_STATUS, "");
            editor.commit();
        }

        return result.toString();
    }


}

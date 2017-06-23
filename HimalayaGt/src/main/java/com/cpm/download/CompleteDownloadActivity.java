package com.cpm.download;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cpm.Constants.CommonString1;
import com.cpm.database.GSKDatabase;
import com.cpm.delegates.TableBean;
import com.cpm.capitalfoods.R;
import com.cpm.fragment.MainFragment;
import com.cpm.message.AlertMessage;
import com.cpm.xmlGetterSetter.AssetChecklistGetterSetter;
import com.cpm.xmlGetterSetter.AssetMappingGetterSetter;
import com.cpm.xmlGetterSetter.AssetMasterGetterSetter;
import com.cpm.xmlGetterSetter.AssetMasterNewGetterSetter;
import com.cpm.xmlGetterSetter.BrandGetterSetter;
import com.cpm.xmlGetterSetter.COMPANY_MASTERGetterSetter;
import com.cpm.xmlGetterSetter.CategoryMasterGetterSetter;
import com.cpm.xmlGetterSetter.ColdroomClosingGetterSetter;
import com.cpm.xmlGetterSetter.CompanyGetterSetter;
import com.cpm.xmlGetterSetter.DeepFreezerGetterSetter;
import com.cpm.xmlGetterSetter.DesignationGetterSetter;
import com.cpm.xmlGetterSetter.Deviation_Reason;
import com.cpm.xmlGetterSetter.JourneyPlanGetterSetter;
import com.cpm.xmlGetterSetter.MappingAssetChecklistGetterSetter;
import com.cpm.xmlGetterSetter.MappingAssetGetterSetter;
import com.cpm.xmlGetterSetter.MappingAvailabilityGetterSetter;
import com.cpm.xmlGetterSetter.MappingPromotionGetterSetter;
import com.cpm.xmlGetterSetter.MappingStatusWindows;
import com.cpm.xmlGetterSetter.MappingWindowChecklistGetterSetter;
import com.cpm.xmlGetterSetter.NonWorkingReasonGetterSetter;
import com.cpm.xmlGetterSetter.POSMDATAGetterSetter;
import com.cpm.xmlGetterSetter.PerformanceGetterSetter;
import com.cpm.xmlGetterSetter.SkuMasterGetterSetter;

import com.cpm.xmlGetterSetter.Sup_Merchandiser;
import com.cpm.xmlGetterSetter.Sup_Performance;
import com.cpm.xmlGetterSetter.Sup_Window;
import com.cpm.xmlGetterSetter.SupervisorGetterSetter;
import com.cpm.xmlGetterSetter.WindowChecklistAnswerGetterSetter;
import com.cpm.xmlHandler.XMLHandlers;

public class CompleteDownloadActivity extends AppCompatActivity {
    private Dialog dialog;
    private ProgressBar pb;
    private TextView percentage, message;
    private Data data;
    int eventType;
    JourneyPlanGetterSetter jcpgettersetter;
    SkuMasterGetterSetter skumastergettersetter;
    MappingAvailabilityGetterSetter mappingavailgettersetter;
    WindowChecklistAnswerGetterSetter windowChecklistAnswerGetterSetter;
    MappingStatusWindows mapplingstatuswindows;
    POSMDATAGetterSetter posmgettersetter;
    MappingAssetGetterSetter mappingassetgettersetter;
    AssetMasterGetterSetter assetmastergettersetter;
    COMPANY_MASTERGetterSetter Companygettersetter;
    MappingWindowChecklistGetterSetter mappingWindowChecklistGetterSetter;
    BrandGetterSetter brandGetterSetter;
    NonWorkingReasonGetterSetter nonworkinggettersetter;
    CategoryMasterGetterSetter categorygettersetter;
    AssetMappingGetterSetter assetMappingGetterSetter;
    AssetMasterNewGetterSetter assetMasterNewGetterSetter;
    GSKDatabase db;
    TableBean tb;
    String _UserId;
    private SharedPreferences preferences;
    boolean flag_cold_stock = true;
    boolean promotion_flag = true;
    boolean asset_flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mainpage);
        setContentView(R.layout.activity_main_menu);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        _UserId = preferences.getString(CommonString1.KEY_USERNAME, "");
        tb = new TableBean();
        db = new GSKDatabase(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        new BackgroundTask(this).execute();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainFragment cartfrag = new MainFragment();
        fragmentManager.beginTransaction().replace(R.id.frame_layout, cartfrag).commit();

    }

    class Data {
        int value;
        String name;
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
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom);
            //dialog.setTitle("Download Files");
            dialog.setCancelable(false);
            dialog.show();
            pb = (ProgressBar) dialog.findViewById(R.id.progressBar1);
            percentage = (TextView) dialog.findViewById(R.id.percentage);
            message = (TextView) dialog.findViewById(R.id.message);

        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String resultHttp = "";
            try {

                data = new Data();
                // JCP
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                SoapObject request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "JOURNEY_PLAN_GT");
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                androidHttpTransport.call(CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object result = (Object) envelope.getResponse();
                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    jcpgettersetter = XMLHandlers.JCPXMLHandler(xpp, eventType);
                    if (jcpgettersetter.getTable_journey_plan() != null) {
                        String jcpTable = jcpgettersetter.getTable_journey_plan();
                        TableBean.setjcptable(jcpTable);
                    }

                    if (jcpgettersetter.getStore_cd().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;
                    } else {
                        return "JOURNEY PLAN";
                    }
                    data.value = 10;
                    data.name = "JCP Data Downloading";
                    publishProgress(data);

                }


                // Store List Master
                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "SKU_MASTER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                result = (Object) envelope.getResponse();

                if (result.toString() != null) {
                    xpp.setInput(new StringReader(result.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    skumastergettersetter = XMLHandlers.storeListXML(xpp, eventType);
                    String skutable = skumastergettersetter.getSku_master_table();
                    if (skutable != null) {
                        TableBean.setSkumastertable(skutable);
                    }
                    if (skumastergettersetter.getSku_cd().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;
                    } else {
                        return "SKU MASTER";
                    }
                    data.value = 20;
                    data.name = "SKU_MASTER Download";
                    publishProgress(data);
                }


                // Brand Master data

                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "BRAND_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultbrand = (Object) envelope.getResponse();

                if (resultbrand.toString() != null) {

                    xpp.setInput(new StringReader(resultbrand.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    brandGetterSetter = XMLHandlers.brandMasterXML(xpp, eventType);

                    if (brandGetterSetter.getBrand_master_table() != null) {
                        String brandtable = brandGetterSetter.getBrand_master_table();
                        TableBean.setBrandtable(brandtable);
                    }
                    if (brandGetterSetter.getBrand_cd().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;

                    } else {
                        return "BRAND MASTER";
                    }

                    data.value = 30;
                    data.name = "Brand Master Data Downloading";
                    publishProgress(data);

                }


                // MAPPING_CORE_SKU data
                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "MAPPING_AVAILABILITY_GT");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultwork = (Object) envelope.getResponse();

                if (resultwork.toString() != null) {

                    xpp.setInput(new StringReader(resultwork.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    mappingavailgettersetter = XMLHandlers.mappingAvailabiltylXML(xpp, eventType);

                    if (mappingavailgettersetter.getMapping_availability_table() != null) {
                        String mappingtable = mappingavailgettersetter.getMapping_availability_table();
                        TableBean.setMappingavailablitytable(mappingtable);
                    }

                    if (mappingavailgettersetter.getSTATE_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;
                        data.value = 40;
                        data.name = "MAPPING_AVAILABILITY Downloading";
                        publishProgress(data);

                    } else {
                        return "MAPPING AVAILABILITY";
                    }

                }


// POSM_MASTER data
                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "POSM_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultmapping = (Object) envelope.getResponse();

                if (resultmapping.toString() != null) {

                    xpp.setInput(new StringReader(resultmapping.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    posmgettersetter = XMLHandlers.mappingpromotXML(xpp, eventType);

                    if (posmgettersetter.getMapping_POSM_table() != null) {
                        String mappingtable = posmgettersetter.getMapping_POSM_table();
                        TableBean.setMappingposmtable(mappingtable);
                    }

                    if (posmgettersetter.getPOSM_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;

                        data.value = 55;
                        data.name = "POSM_MASTER Data Downloading";
                        publishProgress(data);
                    } else {
                        return "POSM MASTER";
                    }

                }


                // WINDOW_MASTER data

                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "WINDOW_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultnonWorking = (Object) envelope.getResponse();

                if (resultnonWorking.toString() != null) {

                    xpp.setInput(new StringReader(resultnonWorking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    mapplingstatuswindows = XMLHandlers.mappingavailXML(xpp, eventType);

                    if (mapplingstatuswindows.getMapping_status_windows() != null) {
                        String mappingtable = mapplingstatuswindows.getMapping_status_windows();
                        TableBean.setMappingavailtable(mappingtable);
                    }

                    if (mapplingstatuswindows.getWINDOW_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;
                        data.value = 65;
                        data.name = "WINDOW_MASTER Downloading";
                        publishProgress(data);

                    } else {
                        return "WINDOW MASTER";
                    }

                }


                // WINDOW_CHECKLIST data

                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "WINDOW_CHECKLIST");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultmappingasst = (Object) envelope.getResponse();

                if (resultmappingasst.toString() != null) {

                    xpp.setInput(new StringReader(resultmappingasst.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    mappingassetgettersetter = XMLHandlers.mappingassetXML(xpp, eventType);

                    if (mappingassetgettersetter.getMapping_asset_table() != null) {
                        String mappingtable = mappingassetgettersetter.getMapping_asset_table();
                        TableBean.setMappingassettable(mappingtable);
                    }

                    if (mappingassetgettersetter.getCHECKLIST_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;
                        data.value = 75;
                        data.name = "WINDOW_CHECKLIST Data Downloading";
                        publishProgress(data);

                    } else {
                        //  return "WINDOW CHECKLIST";
                        // asset_flag = false;
                    }

                }


                // WINDOW_MAPPING data

                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "WINDOW_MAPPING");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultasset = (Object) envelope.getResponse();

                if (resultasset.toString() != null) {

                    xpp.setInput(new StringReader(resultasset.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    assetmastergettersetter = XMLHandlers.assetMasterXML(xpp, eventType);

                    if (assetmastergettersetter.getAsset_master_table() != null) {
                        String assettable = assetmastergettersetter.getAsset_master_table();
                        TableBean.setAssetmastertable(assettable);
                    }
                    if (assetmastergettersetter.getSTATE_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;


                    } else {
                        return "WINDOW MAPPING";
                    }

                    data.value = 80;
                    data.name = "WINDOW_MAPPING Data Downloading";
                    publishProgress(data);
                }


                // MAPPING_WINDOW_CHECKLIST data

                request = new SoapObject(CommonString1.NAMESPACE,
                        CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "MAPPING_WINDOW_CHECKLIST");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object result_window_checklist = (Object) envelope.getResponse();

                if (result_window_checklist.toString() != null) {

                    xpp.setInput(new StringReader(result_window_checklist.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    mappingWindowChecklistGetterSetter = XMLHandlers.mappingWindowChecklistXML(xpp, eventType);
                    if (mappingWindowChecklistGetterSetter.getTable_mapping_window_checklist() != null) {
                        String mappingwindowChecklisttable = mappingWindowChecklistGetterSetter.getTable_mapping_window_checklist();
                        TableBean.setTable_mappingwindowchecklist(mappingwindowChecklisttable);
                    }
                    if (mappingWindowChecklistGetterSetter.getWINDOW_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;
                        data.value = 80;
                        data.name = "MAPPING WINDOW CHECKLIST Downloading";
                        publishProgress(data);
                    } else {
                        return "MAPPING WINDOW CHECKLIST";
                    }


                }


                // WINDOW_CHECKLIST_ANSWER data

                request = new SoapObject(CommonString1.NAMESPACE,
                        CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "WINDOW_CHECKLIST_ANSWER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object result_window_checklist_ans = (Object) envelope.getResponse();

                if (result_window_checklist_ans.toString() != null) {

                    xpp.setInput(new StringReader(result_window_checklist_ans.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    windowChecklistAnswerGetterSetter = XMLHandlers.WindowChecklistAnswerXML(xpp, eventType);
                    if (windowChecklistAnswerGetterSetter.getTable_window_checklist_answer() != null) {
                        String window_checklist_ans_table = windowChecklistAnswerGetterSetter.getTable_window_checklist_answer();
                        TableBean.setTable_windowchecklistanswer(window_checklist_ans_table);
                    }

                    if (windowChecklistAnswerGetterSetter.getANSWER_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;
                        data.value = 80;
                        data.name = "WINDOW CHECKLIST ANSWER Data Downloading";
                        publishProgress(data);
                    }

                }


                //Category master data

                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "CATEGORY_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                androidHttpTransport.call(CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultcategorymaster = (Object) envelope.getResponse();

                if (resultcategorymaster.toString() != null) {

                    xpp.setInput(new StringReader(resultcategorymaster.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();
                    categorygettersetter = XMLHandlers.categoryMasterXML(xpp, eventType);
                    if (categorygettersetter.getCategory_master_table() != null) {
                        String categorytable = categorygettersetter.getCategory_master_table();
                        TableBean.setCategorymastertable(categorytable);
                    }
                    if (categorygettersetter.getCategory_cd().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;

                    } else {
                        return "CATEGORY MASTER";
                    }

                    data.value = 83;
                    data.name = "Category Master Downloading";
                    publishProgress(data);
                }


                // COMPANY_MASTER data

                request = new SoapObject(CommonString1.NAMESPACE,
                        CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "COMPANY_MASTER");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultCOMPANY_MASTER = (Object) envelope.getResponse();

                if (resultCOMPANY_MASTER.toString() != null) {

                    xpp.setInput(new StringReader(resultCOMPANY_MASTER.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    Companygettersetter = XMLHandlers.comapnyMasterXML(xpp, eventType);
                    if (Companygettersetter.getCompany_master_table() != null) {
                        String table = Companygettersetter.getCompany_master_table();
                        TableBean.setCompanymastertable(table);
                    }
                    if (Companygettersetter.getCOMPANY_CD().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;

                    } else {
                        return "COMPANY_MASTER";
                    }
                    data.value = 85;
                    data.name = "COMPANY MASTER Data Downloading";
                    publishProgress(data);
                }

                // MAPPING_ASSET_GT data

                request = new SoapObject(CommonString1.NAMESPACE,
                        CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "MAPPING_ASSET_GT");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                androidHttpTransport.call(CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object MAPPING_ASSET_GT = (Object) envelope.getResponse();

                if (MAPPING_ASSET_GT.toString() != null) {

                    xpp.setInput(new StringReader(MAPPING_ASSET_GT.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    assetMappingGetterSetter = XMLHandlers.AssetMappingXML(xpp, eventType);
                    if (assetMappingGetterSetter.getTableassetmapping() != null) {
                        String table = assetMappingGetterSetter.getTableassetmapping();
                        TableBean.setAssetmappindtable(table);
                    }
                    if (assetMappingGetterSetter.getAsset_cd().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;

                    } else {
                        return "Mapping asset";
                    }
                    data.value = 88;
                    data.name = "Mapping asset Data Downloading";
                    publishProgress(data);
                }


                // aSSET mASTER data

                request = new SoapObject(CommonString1.NAMESPACE, CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);
                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "ASSET_MASTER");
                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);
                androidHttpTransport = new HttpTransportSE(CommonString1.URL);
                androidHttpTransport.call(CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object ASSET_MASTER = (Object) envelope.getResponse();
                if (ASSET_MASTER.toString() != null) {
                    xpp.setInput(new StringReader(ASSET_MASTER.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    assetMasterNewGetterSetter = XMLHandlers.AssetMasterNewXML(xpp, eventType);
                    if (assetMasterNewGetterSetter.getAssetMasternewTable() != null) {
                        String table = assetMasterNewGetterSetter.getAssetMasternewTable();
                        TableBean.setAssetmasterNewtable(table);
                    }
                    if (assetMasterNewGetterSetter.getAsset_cd().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;

                    } else {
                        return "Asset Master";
                    }
                    data.value = 89;
                    data.name = "Asset Master Data Downloading";
                    publishProgress(data);
                }


                //Non Working Reason data

                request = new SoapObject(CommonString1.NAMESPACE,
                        CommonString1.METHOD_NAME_UNIVERSAL_DOWNLOAD);

                request.addProperty("UserName", _UserId);
                request.addProperty("Type", "NON_WORKING_REASON_NEW");

                envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                androidHttpTransport = new HttpTransportSE(CommonString1.URL);

                androidHttpTransport.call(
                        CommonString1.SOAP_ACTION_UNIVERSAL, envelope);
                Object resultnonworking = (Object) envelope.getResponse();

                if (resultnonworking.toString() != null) {

                    xpp.setInput(new StringReader(resultnonworking.toString()));
                    xpp.next();
                    eventType = xpp.getEventType();

                    nonworkinggettersetter = XMLHandlers.nonWorkinReasonXML(xpp, eventType);

                    if (nonworkinggettersetter.getNonworking_table() != null) {
                        String nonworkingtable = nonworkinggettersetter.getNonworking_table();
                        TableBean.setNonworkingtable(nonworkingtable);
                    }
                    if (nonworkinggettersetter.getReason_cd().size() > 0) {
                        resultHttp = CommonString1.KEY_SUCCESS;

                    } else {
                        return "NON WORKING REASON";
                    }

                    data.value = 90;
                    data.name = "Non Working Reason Downloading";
                    publishProgress(data);

                }


                db.open();
                if (jcpgettersetter.getStore_cd().size() > 0) {
                    db.insertJCPData(jcpgettersetter);
                }
                // if (skumastergettersetter.getSku_cd().size() > 0) {
                db.insertSkuMasterData(skumastergettersetter);
                //  }
                //  if (brandGetterSetter.getBrand_cd().size() > 0) {
                db.insertBrandMasterData(brandGetterSetter);
                //   }
                //  if (mappingavailgettersetter.getSTATE_CD().size() > 0) {
                db.insertMAPPING_AvailibilityData(mappingavailgettersetter);
                //   }
                // if (posmgettersetter.getPOSM_CD().size() > 0) {
                db.insertPOSMata(posmgettersetter);
                //  }
                // if (Companygettersetter.getCOMPANY_CD().size() > 0) {
                db.insertCOMPANY_MASTERData(Companygettersetter);
                db.insertAssetMappingData(assetMappingGetterSetter);
                // }

                data.value = 95;
                data.name = "Created display";
                //if (mapplingstatuswindows.getWINDOW_CD().size() > 0) {
                db.insertWINDOW_MASTER(mapplingstatuswindows);
                // }
                // if (mappingassetgettersetter.getCHECKLIST_CD().size() > 0) {
                db.insertWINDOW_CHECKLISTData(mappingassetgettersetter);
                //  }
                // if (assetmastergettersetter.getSTATE_CD().size() > 0) {
                db.insertWINDOW_MAPPINGData(assetmastergettersetter);
                db.insertAssetMasterNewData(assetMasterNewGetterSetter);
                // }
                // if (categorygettersetter.getCategory_cd().size() > 0) {
                db.insertCategoryMasterData(categorygettersetter);
                //  }
                if (mappingWindowChecklistGetterSetter.getWINDOW_CD().size() > 0) {
                    db.insertMappingWindowChecklistData(mappingWindowChecklistGetterSetter);
                } else {
                    db.deleteMappingWindowChecklistData();
                }
                if (nonworkinggettersetter.getReason_cd().size() > 0) {
                    db.insertNonWorkingReasonData(nonworkinggettersetter);
                }
                if (windowChecklistAnswerGetterSetter.getANSWER_CD().size() > 0) {
                    db.insertWindowChecklistAnswerData(windowChecklistAnswerGetterSetter);
                } else {
                    db.deleteWindowChecklistAnswerData();
                }

                data.value = 100;
                data.name = "Finishing";
                publishProgress(data);
                return resultHttp;
            } catch (MalformedURLException e) {

                final AlertMessage message = new AlertMessage(
                        CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION, "download", e);
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        message.showMessage();
                    }
                });

            } catch (IOException e) {
                final AlertMessage message = new AlertMessage(
                        CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_SOCKETEXCEPTION, "socket", e);

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub

                        message.showMessage();

                    }
                });

            } catch (Exception e) {
                final AlertMessage message = new AlertMessage(CompleteDownloadActivity.this,
                        AlertMessage.MESSAGE_EXCEPTION + e, "download", e);

                e.getMessage();
                e.printStackTrace();
                e.getCause();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

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
                AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_DOWNLOAD, "success", null);
                message.showMessage();
            } else {
                AlertMessage message = new AlertMessage(CompleteDownloadActivity.this, AlertMessage.MESSAGE_JCP_FALSE + result, "success", null);
                message.showMessage();
            }


        }

    }


}

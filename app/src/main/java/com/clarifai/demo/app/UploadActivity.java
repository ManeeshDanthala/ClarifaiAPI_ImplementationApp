package com.clarifai.demo.app;

import android.app.ProgressDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.dto.input.ClarifaiInput;

public class UploadActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    //private String imgurl;
    private String title;
    private String desc;
    private ArrayList<HashMap<String,String>> arrayList;
    private GenAdapter genAdapter;
    private byte[] barray;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent intent = getIntent();
        //imgurl = intent.getStringExtra("imgurl");
        title = intent.getStringExtra("model");
        desc = intent.getStringExtra("desc");
        String reqimg = intent.getStringExtra("req");

        try {

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(reqimg));
            // Log.d(TAG, String.valueOf(bitmap));
            //img1.setImageBitmap(bitmap);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            barray = stream.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        }

        progressDialog=new ProgressDialog(UploadActivity.this);
        progressDialog.setMessage("Hang on for a moment...Takes a minute...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        arrayList = new ArrayList<>();
        genAdapter = new GenAdapter(this,arrayList);
        listView = (ListView) findViewById(R.id.general_lvew);
        listView.setAdapter(genAdapter);

        TextView titlegen = (TextView)  findViewById(R.id.titleGen);
        TextView descgen = (TextView) findViewById(R.id.descGen);
        ImageView genImg = (ImageView) findViewById(R.id.general_img);

        titlegen.setText(title);
        descgen.setText(desc);
        Log.e("check",""+reqimg);
        genImg.setImageURI(Uri.parse(reqimg));

        if(title.equalsIgnoreCase("General")){
            new PredictGeneral().execute();
        }
        if(title.equalsIgnoreCase("Apparel")){
            new PredictApparel().execute();
        }
        if(title.equalsIgnoreCase("Celebrity")){
            new PredictCelebrity().execute();
        }
        if(title.equalsIgnoreCase("Colors")){
            new PredictColors().execute();
        }
        if(title.equalsIgnoreCase("Food")){
            new PredictFood().execute();
        }
        if(title.equalsIgnoreCase("Logo")){
            new PredictLogo().execute();
        }
    }
    private void updateUi(){
        genAdapter.notifyDataSetChanged();
        LinearLayout lp = (LinearLayout) findViewById(R.id.headings);
        TextView t = (TextView) findViewById(R.id.unable);
        if(arrayList.isEmpty()){
            listView.setVisibility(View.GONE);
            t.setVisibility(View.VISIBLE);
            lp.setVisibility(View.GONE);
        }
        else{
            listView.setVisibility(View.VISIBLE);
            t.setVisibility(View.GONE);
            lp.setVisibility(View.VISIBLE);
        }
    }
    private void updateUiColor(){
        genAdapter.notifyDataSetChanged();
        LinearLayout lp = (LinearLayout) findViewById(R.id.headings);
        TextView t = (TextView) findViewById(R.id.unable);
        TextView conc = (TextView) findViewById(R.id.concept);
        TextView proba = (TextView) findViewById(R.id.proba);
        if(arrayList.isEmpty()){
            listView.setVisibility(View.GONE);
            t.setVisibility(View.VISIBLE);
            lp.setVisibility(View.GONE);
        }
        else{
            listView.setVisibility(View.VISIBLE);
            t.setVisibility(View.GONE);
            conc.setText("Color");
            proba.setText("Density");
            lp.setVisibility(View.VISIBLE);
        }
    }
    private class PredictGeneral extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("bf253372515d4913813b3322b1963d38")
                    .buildSync();
            //Log.e("imgurl",imgurl);
            String jsonString = client.getDefaultModels().generalModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray concepts = data1.getJSONArray("concepts");
                    for(int i=0;i<concepts.length();i++){
                        JSONObject temp = concepts.getJSONObject(i);
                        String name = temp.getString("name");
                        double prob = temp.getDouble("value");
                        HashMap<String,String> h = new HashMap();

                        h.put("name",name);
                        h.put("prob",""+RoundTo2Decimals(prob));
                        if(prob>0.25){
                            arrayList.add(h);
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    private class PredictApparel extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("bf253372515d4913813b3322b1963d38")
                    .buildSync();
            //Log.e("imgurl",imgurl);
            String jsonString = client.getDefaultModels().apparelModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray concepts = data1.getJSONArray("concepts");
                    for(int i=0;i<concepts.length();i++){
                        JSONObject temp = concepts.getJSONObject(i);
                        String name = temp.getString("name");
                        double prob = temp.getDouble("value");
                        HashMap<String,String> h = new HashMap();

                        h.put("name",name);
                        h.put("prob",""+RoundTo2Decimals(prob));
                        if(prob>0.25){
                            arrayList.add(h);
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    private class PredictCelebrity extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("bf253372515d4913813b3322b1963d38")
                    .buildSync();
            //Log.e("imgurl",imgurl);
            String jsonString = client.getDefaultModels().celebrityModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray regions = data1.getJSONArray("regions");
                    if(regions.length()>0){
                        JSONObject firstFeature2 = regions.getJSONObject(0);
                        JSONObject data2 = firstFeature2.getJSONObject("data");
                        JSONObject face = data2.getJSONObject("face");
                        JSONObject identity = face.getJSONObject("identity");
                        JSONArray concepts = identity.getJSONArray("concepts");

                        for(int i=0;i<concepts.length();i++){
                            JSONObject temp = concepts.getJSONObject(i);
                            String name = temp.getString("name");
                            double prob = temp.getDouble("value");
                            HashMap<String,String> h = new HashMap();

                            h.put("name",name);
                            h.put("prob",""+RoundTo2Decimals(prob));
                            if(prob>0.5){
                                arrayList.add(h);
                            }
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    private class PredictColors extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("bf253372515d4913813b3322b1963d38")
                    .buildSync();
//            Log.e("imgurl",imgurl);
            String jsonString = client.getDefaultModels().colorModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUiColor();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray colors = data1.getJSONArray("colors");
                    for(int i=0;i<colors.length();i++){
                        JSONObject temp = colors.getJSONObject(i);
                        JSONObject data2 = temp.getJSONObject("w3c");
                        String name = data2.getString("name");
                        String hex = data2.getString("hex");
                        double prob = temp.getDouble("value");
                        HashMap<String,String> h = new HashMap();

                        h.put("name",name+"   "+hex);
                        h.put("prob",""+RoundTo2Decimals(prob));
                        Log.e(name+"   "+hex,(RoundTo2Decimals(prob)*100)+"");
//                        if(prob>0.25){
                            arrayList.add(h);
//                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    private class PredictFood extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("bf253372515d4913813b3322b1963d38")
                    .buildSync();
//            Log.e("imgurl",imgurl);
            String jsonString = client.getDefaultModels().foodModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray concepts = data1.getJSONArray("concepts");
                    for(int i=0;i<concepts.length();i++){
                        JSONObject temp = concepts.getJSONObject(i);
                        String name = temp.getString("name");
                        double prob = temp.getDouble("value");
                        HashMap<String,String> h = new HashMap();

                        h.put("name",name);
                        h.put("prob",""+RoundTo2Decimals(prob));
                        if(prob>0.25){
                            arrayList.add(h);
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    private class PredictLogo extends AsyncTask<URL,Void,String> {
        @Override
        protected String doInBackground(URL... urls) {
            ClarifaiClient client = new ClarifaiBuilder("bf253372515d4913813b3322b1963d38")
                    .buildSync();
//            Log.e("imgurl",imgurl);
            String jsonString = client.getDefaultModels().logoModel().predict().withInputs(ClarifaiInput.forImage(barray))
                    .executeSync().rawBody();
            Log.e("JSON",jsonString);
            extractFeatureFromJson(jsonString);
            return "";
        }
        @Override
        protected void onPostExecute(String s) {
            updateUi();
            progressDialog.dismiss();
        }
        private void extractFeatureFromJson(String jsonstr){
            try{
                JSONObject baseJsonResponse = new JSONObject(jsonstr);
                JSONArray featureArray = baseJsonResponse.getJSONArray("outputs");
                if (featureArray.length() > 0) {
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject data1 = firstFeature.getJSONObject("data");
                    JSONArray regions = data1.getJSONArray("regions");
                    for(int j=0;j<regions.length();j++){
                        JSONObject temp2 = regions.getJSONObject(j);
                        JSONObject data3 = temp2.getJSONObject("data");
                        JSONArray concepts = data3.getJSONArray("concepts");

                        for(int i=0;i<concepts.length();i++){
                            JSONObject temp = concepts.getJSONObject(i);
                            String name = temp.getString("name");
                            double prob = temp.getDouble("value");
                            HashMap<String,String> h = new HashMap();

                            h.put("name",name);
                            h.put("prob",""+RoundTo2Decimals(prob));
                            if(prob>0.25){
                                arrayList.add(h);
                            }
                        }
                    }
                }
            }
            catch(Exception e){
                Log.e("JSON","Exception");
            }
        }
    }
    double RoundTo2Decimals(double val) {
        DecimalFormat df2 = new DecimalFormat("###.###");
        return Double.valueOf(df2.format(val));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

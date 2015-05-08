package com.freefly3104.satoshi.mymap01;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;

public class MapsActivity extends FragmentActivity
        implements LocationListener, LocationSource, LoaderManager.LoaderCallbacks<JSONArray>, RadioButtonDialogOnOkClickListener {

    private static final String URLTEXT = "https://aed.azure-mobile.net/api/AEDSearch?lat=";
    private static final int ADDRESSLOADER_ID = 0;

    private String travelMode = "walking";
    private String text = "ルートが地図上に表示されていません。";
    private String startAddress ="";
    private String endAddress ="";

    // RadioButtonDialogの初期値
    private int default_RadioButton_index = 0;

    private String[] items = {"walking", "driving"};

    // 既存マーカーを消去する為
    private List<Marker> markerArray = new ArrayList<>();
    private Marker marker = null;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private OnLocationChangedListener mListener;
    private LocationManager locationManager;

    private double mLat = 0;
    private double mLon = 0;

    private LatLng mlatlng = null;
    private LatLng dlatlng = null;

    private Polyline polyline = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //アクションバーを非表示にします
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_maps);

        //画面がスリープ状態になるのを避けます
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button btnRouteShowDialog = (Button) findViewById(R.id.btnRouteInfo);
        btnRouteShowDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showScrollViewDialog();
            }

        });

        Button btnMode = (Button) findViewById(R.id.btnMode);
        btnMode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showRadioButtonDialog();
            }

        });

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        if(locationManager != null){

            //GPSが利用可能か
            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //ネットワークが利用可能か
            boolean netIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(gpsIsEnabled){

                //requestLocationUpdatesの引数
                //（プロバイダ、ミリ秒による通知間隔の指定long型、メーター単位での移動距離float型、リスナー）
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000L, 5.0f, this);

            }else{

                onGpsService();

            }

            if(netIsEnabled){

                //requestLocationUpdatesの引数
                //（プロバイダ、ミリ秒による通知間隔の指定long型、メーター単位での移動距離float型、リスナー）
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, 5000L, 5.0f, this);

            }

        }else{
            //トースト表示
            Toast.makeText(this, "LocationManager is null", Toast.LENGTH_SHORT).show();
        }

        super.onResume();
        setUpMapIfNeeded();
    }

    private void onGpsService(){

        OkCancelDialog dialogFragment = OkCancelDialog
                .newInstance(R.string.dialog_title, R.string.dialog_message);
        dialogFragment.setOnOkClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent callGPSSettingIntent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(callGPSSettingIntent);
                dialog.dismiss();
            }
        });
        dialogFragment.setOnCancelClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogFragment.show(getFragmentManager(), "dialog_fragment");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpMapIfNeeded();

        if(locationManager != null){
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    protected void onStop() {

        if(locationManager != null){
            locationManager.removeUpdates(this);
        }

        super.onStop();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setLocationSource(this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(mListener != null) {

            mListener.onLocationChanged(location);
            mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(
                    location.getLatitude(), location.getLongitude())));

            // 現在地の緯度・経度を取得
            mLat = location.getLatitude();
            mLon = location.getLongitude();

            mlatlng = new LatLng(mLat, mLon);

            getLoaderManager().restartLoader(ADDRESSLOADER_ID, null, this);

        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    @Override
    public void deactivate() {

    }

    @Override
    public Loader<JSONArray> onCreateLoader(int id, Bundle args) {

        String url = URLTEXT + mLat + "&lng=" + mLon;
        return new JsonLoader(this, url);

    }

    @Override
    public void onLoaderReset(Loader<JSONArray> loader) {

    }

    @Override
    public void onLoadFinished(Loader<JSONArray> loader, JSONArray data) {

        final HashMap<String,List<Info>> hashMap = new HashMap<>();
        List<Info> ret;
        Info info;

        if (data != null) {

            // 既存のマーカーを消す処理
            for (int i = 0 ; i < markerArray.size() ; i++){
                markerArray.get(i).remove();
            }
            markerArray.clear();

            try {

                for (int i = 0; i < data.length(); i++) {

                    JSONObject jsData = data.getJSONObject(i);

                    String locationName = jsData.getString("LocationName");
                    String prefecture = jsData.getString("Perfecture");

                    String city = jsData.getString("City");
                    String addressArea = jsData.getString("AddressArea");
                    String facilityName = jsData.getString("FacilityName");
                    String facilityPlace = jsData.getString("FacilityPlace");
                    String contactPoint = jsData.getString("ContactPoint");
                    String contactTelephone = jsData.getString("ContactTelephone");

                    // 住所を連結
                    String addressAll = isNull(prefecture + city + addressArea);

                    double latitude = jsData.getDouble("Latitude");
                    double longitude = jsData.getDouble("Longitude");

                    // ロケーション情報をリストに格納
                    info = new Info();
                    info.setName(locationName);
                    info.setAddress(addressAll);
                    info.setFacilityName(facilityName);
                    info.setFacilityPlace(facilityPlace);
                    info.setContactPoint(contactPoint);
                    info.setContactTelephone(contactTelephone);
                    info.setLatitude(latitude);
                    info.setLongitude(longitude);

                    ret = new ArrayList<>();
                    ret.add(info);

                    // 名称をキーにロケーション情報をマップに格納
                    hashMap.put(locationName, ret);

                    // リサイズする
                    Resources res = this.getResources();
                    Bitmap bmp_orig = BitmapFactory.decodeResource(res, R.drawable.marker01);
                    Bitmap bmp_resized = Bitmap.createScaledBitmap(bmp_orig, 90, 90, false);
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bmp_resized);

                    MarkerOptions options = new MarkerOptions();
                    options.position(new LatLng(latitude, longitude));
                    options.title(locationName);
                    options.snippet(isNull(contactTelephone));
                    options.icon(icon);
                    marker = mMap.addMarker(options);

                    markerArray.add(marker); // リストに格納（削除する為）

                    mMap.setOnMarkerClickListener(new OnMarkerClickListener(){

                        @Override
                        public boolean onMarkerClick(Marker marker) {

                        if(polyline != null){
                            polyline.remove();
                        }

                        dlatlng = marker.getPosition();
//                        default_RadioButton_index = 0;
//                        travelMode = "walking";
                        routeSearch(dlatlng);

                        String name = marker.getTitle();

                        // 検証用
//                        int testCnt = hashMap.get(name).size();
//                        Toast.makeText(MapsActivity.this, "" + testCnt, Toast.LENGTH_SHORT).show();

                        String address = hashMap.get(name).get(0).getAddress();
                        String facilityName = hashMap.get(name).get(0).getFacilityName();
                        String facilityPlace = hashMap.get(name).get(0).getFacilityPlace();
                        String contactPoint = hashMap.get(name).get(0).getContactPoint();
                        String contactTelephone = hashMap.get(name).get(0).getContactTelephone();

                        DialogFragment infoFragment = InfoDialogFragment.newInstance(
                            name,address,facilityName,facilityPlace,contactPoint,contactTelephone);

                            infoFragment.show(getFragmentManager(), "infoDialog");

                        return false;

                        }
                    });

                }

            } catch (JSONException e) {
                Log.d("onLoadFinished", "JSONのパースに失敗しました。 JSONException=" + e);
            }


        }else{
            Log.d("onLoadFinished", "onLoadFinished error!");
        }

    }

    public String isNull(String s){
        if(s.equals("null")){
            return "";
        }
        return s;
    }

    private void routeSearch(LatLng dest){

        String url = getDirectionsUrl(mlatlng, dest);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);

    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String sensor = "sensor=false";

        //パラメータ
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&language=ja" +
                            "&mode=" + travelMode;
        //JSON指定
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
    }

    // 第1引数：doInBackgroundメソッドの引数の型
    // 第2引数：onProgressUpdateメソッドの引数の型
    // 第3引数　onPostExecuteメソッドの戻り値の型
    private class DownloadTask extends AsyncTask<String, Void, String> {

        //非同期の処理　　　String...　は可変長引数を渡すことができます
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

        }

    }

    private String downloadUrl(String strUrl) throws IOException {

        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception download url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /*parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{

                jObject = new JSONObject(jsonData[0]);

                ParseJsonpOfDirectionAPI parser = new ParseJsonpOfDirectionAPI();
                ReturnValues values =  parser.parse(jObject);
                routes = values.routes;
                text = values.temp;
                startAddress = values.startAddress;
                endAddress = values.endAddress;
                // routes = parser.parse(jObject);

            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            super.onPostExecute(result);

            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            if(result.size() != 0){

                for(int i=0;i<result.size();i++){
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    List<HashMap<String, String>> path = result.get(i);

                    for(int j=0;j<path.size();j++){
                        HashMap<String,String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    //ポリライン
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(0x550000ff);

                }

                //描画
                polyline = mMap.addPolyline(lineOptions);

                // text = Html.fromHtml(posinfo).toString();

            }else{
                mMap.clear();
                Toast.makeText(MapsActivity.this, "ルート情報を取得できませんでした", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void showScrollViewDialog() {

        // HTMLのテキストを通常の文字列へ
        String s = Html.fromHtml(text).toString();

        String newText = s + "\n\nスタート付近の住所\n\n" + startAddress + "\n\nゴール付近の住所\n\n" + endAddress;

        DialogFragment dialogFragment = ScrollViewFragment
                .newInstance(R.string.tvRouteInfo, newText);
        dialogFragment.show(getFragmentManager(), "dialog_fragment");

    }

    public void showRadioButtonDialog() {

        DialogFragment radioButtonFragment = RadioButtonFragment
                .newInstance(R.string.mode, items, default_RadioButton_index);

        radioButtonFragment.show(getFragmentManager(), "RadioButtonDialogFragment");

        //リスナー
        ((RadioButtonFragment) radioButtonFragment).setListener(this);

    }

    @Override
    public void onRadioButtonDialogOkClicked(Bundle args) {

        int selectedId = args.getInt("KEY_MYDIALOG");

        switch (selectedId) {
            case 0:

                travelMode = "walking";

                //既にマーカーの座標があり、モードが違うなら、もう一度ルート検索を実行します（モードが切り替わった場合）
                if(dlatlng != null && default_RadioButton_index != selectedId){
                    if(polyline != null){
                        polyline.remove();
                    }
                    routeSearch(dlatlng);  //再ルート検索
                }

                default_RadioButton_index = 0;

                break;
            case 1:

                travelMode = "driving";

                //既にマーカーの座標があり、モードが違うなら、もう一度ルート検索を実行します（モードが切り替わった場合）
                if(dlatlng != null && default_RadioButton_index != selectedId){
                    if(polyline != null){
                        polyline.remove();
                    }
                    routeSearch(dlatlng);  //再ルート検索
                }

                default_RadioButton_index = 1;

                break;
            default:
        }

    }

}

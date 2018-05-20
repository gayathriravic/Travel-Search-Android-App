package com.example.gayathri.places;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request.Method;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.volley.toolbox.Volley.newRequestQueue;


public class Tab3Fragment extends Fragment implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener{

    public RequestQueue mRequestQueue;
    public RequestQueue mRequestQueue1;
    public Polyline polyline1;
    private StringRequest mStringRequest1;
    public String waySelected="driving";
    public Double customLong, customLat;
    AutoCompleteTextView editTextFromLocation;
    private StringRequest mStringRequest;
    private Spinner categorySpinner;
    double finalToLocReceivedLat ;
    double finalToLocReceivedLong;
    private static final String TAG_RESULT = "predictions";
    public String from_location_name="";
    ArrayList<String> auto_complete_names;
    ArrayAdapter<String> adapter;
    private static final String TAG = "ErorrLogVj";
    String autoCompleteUrl = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    String key = "AIzaSyDkE3ez-fPWeOcVI5jLs2lMdfvUhU9NU5A";


    String[] wayList = {"Driving","Walking","Bicycling","Transit"};
    //Directions variables

    public static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_BLUE_ARGB = 0xff0000ff;

    public static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);


    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);


    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);


    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);



    private List<LatLng> resultsfromAll;
    String jsonResponseOfRow = null;

    SupportMapFragment mapFragment;

    @SuppressLint("ValidFragment")
    public Tab3Fragment(String jsonStr) {
        Log.d("ReceivedMaps",jsonStr);
        jsonResponseOfRow = jsonStr;
    }

    public Tab3Fragment() {

    }

    private GoogleMap googlemapSaved;
    private boolean firstTimeEntered = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.maps_layout, container, false);
        categorySpinner = view.findViewById(R.id.modeOfTravel);
        editTextFromLocation = (AutoCompleteTextView) view.findViewById(R.id.fromLoc);
        editTextFromLocation.setThreshold(0);

        auto_complete_names = new ArrayList<String>();

        editTextFromLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof String){
                    Log.d(" Tab3fRAGMENTlOG", "onItemClick:chosse" + item.toString());
                    from_location_name =item.toString();
                    getCallDirections(waySelected);

                }
            }
        });

        editTextFromLocation.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (s.toString().length() <= 25) {
                    auto_complete_names = new ArrayList<String>();
                    getAutoSearchResults(s.toString());
                }

            }
        });

        addItemsOnSpinner(view);
        addListenerOnSpinnerItemSelection(view);


        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        if(mapFragment == null ) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map,mapFragment).commit();

        }
        mapFragment.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        callToGetDirections(googleMap);
        googlemapSaved = googleMap;
    }

    public void callToGetDirections(GoogleMap googleMap) {
        double toLocReceivedLat = 0;
        double toLocReceivedLong = 0;
        String nameOfPlace ="Destination";
        try {
            JSONObject receiveLatittude = new JSONObject(jsonResponseOfRow);
            JSONObject result = receiveLatittude.getJSONObject("result");
            Log.d(TAG, "callToGetDirections: " +result);
            nameOfPlace = result.getString("name");

            JSONObject geom = result.getJSONObject("geometry").getJSONObject("location");
            Log.d("ReceivedMaps","Entered");
            Log.d("ReceivedMaps",geom.toString());
            toLocReceivedLat = Double.parseDouble(geom.getString("lat"));
            toLocReceivedLong  = Double.parseDouble(geom.getString("lng"));

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "callToGetDirections:  exception" +e.getMessage());
        }
        Log.d(TAG, "callToGetDirections: ");
        finalToLocReceivedLat = toLocReceivedLat;
        finalToLocReceivedLong = toLocReceivedLong;


        LatLng fromLoc = new LatLng(finalToLocReceivedLat, finalToLocReceivedLong);
        googleMap.addMarker(new MarkerOptions().position(fromLoc)
                .title(nameOfPlace));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(fromLoc));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(fromLoc)      // Sets the center of the map to Mountain View
                .zoom(13)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    public void addItemsOnSpinner(View view) {

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, wayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Log.d("checking",categorySpinner.toString());
        categorySpinner.setAdapter(dataAdapter);
    }
    public void getCallDirections(String modeOfTravel) {

        googlemapSaved.clear();


        LatLng toLoc = new LatLng(finalToLocReceivedLat, finalToLocReceivedLong);


        //googlemapSaved.moveCamera(CameraUpdateFactory.newLatLng(toLoc));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(toLoc)      // Sets the center of the map to Mountain View
                .zoom(10)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        googlemapSaved.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        double toLocReceivedLat = 0;
        double toLocReceivedLong = 0;
        String nameofToLocation = "Destination";
        try {
            JSONObject receiveLatittude = new JSONObject(jsonResponseOfRow);

            JSONObject result = receiveLatittude.getJSONObject("result");
            nameofToLocation = result.getString("name");
            JSONObject geom = result.getJSONObject("geometry").getJSONObject("location");
            toLocReceivedLat = Double.parseDouble(geom.getString("lat"));
            toLocReceivedLong = Double.parseDouble(geom.getString("lng"));
            Log.d("logggggg", "getCallDirections:  " + toLocReceivedLat + " " + toLocReceivedLong);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("Tab3FragmentLog", "getCallDirections: " + e.getMessage());
        }

        Log.d("SpinnerListener", String.valueOf(toLocReceivedLat));
        Log.d("SpinnerListener", String.valueOf(toLocReceivedLong));
        //RequestQueue initialized

        googlemapSaved.addMarker(new MarkerOptions().position(toLoc).title(nameofToLocation));
        mRequestQueue1 = newRequestQueue(getActivity());
        String url = "";
        if (from_location_name.isEmpty()){
            url = "http://travelandentertainmentsearch-env.us-east-2.elasticbeanstalk.com/directions/fromLocString?origin=" + SearchFragment.latitude + "," + SearchFragment.longitude + "&destination=" + toLocReceivedLat + "," + toLocReceivedLong + "&mode=" + modeOfTravel;
            getResults(url,googlemapSaved,SearchFragment.latitude,SearchFragment.longitude,"Current Location");
        }
        else{
            url = "http://travelandentertainmentsearch-env.us-east-2.elasticbeanstalk.com/directions/fromLocString?origin="+ URLEncoder.encode(from_location_name) +"&destination="+toLocReceivedLat+","+toLocReceivedLong+"&mode="+modeOfTravel;
            getCustomLatandLong(url);

        }
        //String Request initialized
        Log.d("url",url);
    }
    public void getCustomLatandLong(final String url){
        String customURL="";
        try {
            Log.d(TAG, "getCustomLatandLong: hereee");
            customURL ="https://maps.googleapis.com/maps/api/geocode/json?address="+ URLEncoder.encode(from_location_name, "UTF-8") + "&key=" + key;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, customURL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject js = new JSONObject(response);
                            Log.d(TAG, "onResponse: " +js.toString());
                            JSONArray array =  js.getJSONArray("results");
                            Log.d(TAG, "onResponse: " + array.toString());
                            JSONObject geometry = (JSONObject) array.getJSONObject(0).get("geometry");
                            geometry = (JSONObject) geometry.get("location");
                            Double lat =((Double) geometry.get("lat"));
                            Double longi =((Double) geometry.get("lng"));
                            getResults(url,googlemapSaved,lat,longi,from_location_name);
                        } catch (Exception e)
                        {
                            Log.d(TAG, "Errrror: " + e.getMessage() + e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("error :", error.toString());
            }
        });
        queue.add(stringRequest);




    }
    public void getResults(String url, GoogleMap googlemapSaved, Double fromLoclat, Double fromLoclongi,String fromLocName)
    {
//                double finalToLocReceivedLat = toLocReceivedLat;
//                double finalToLocReceivedLong = toLocReceivedLong;


        LatLng fromHereLocation = new LatLng(fromLoclat, fromLoclongi);

        googlemapSaved.addMarker(new MarkerOptions().position(fromHereLocation).title(fromLocName));


        mStringRequest1 = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                JSONObject reader = null;
                JSONObject c=null;
                String polypoints = null;
                Log.d("SpinnerListenerEnter",response);

                try {
                    reader = new JSONObject(response);
                    Log.d(TAG, "onResponse: reader" +reader);
                    JSONArray results = reader.getJSONArray("routes");
                    c  = results.getJSONObject(0);
                    JSONObject polypoints1 = c.getJSONObject("overview_polyline");
                    polypoints = polypoints1.getString("points");

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("getDirections", "onResponse: " +  " error here  " + e.getMessage());
                }

                Log.d(TAG, "onResponse: "+ polypoints);
                List<LatLng> resultPoly = PolyUtil.decode(polypoints);

                Log.d("SpinnerListener",resultPoly.toString());
                if(polyline1 != null)
                    polyline1.remove();
                polyline1= Tab3Fragment.this.googlemapSaved.addPolyline(new PolylineOptions()
                        .clickable(true).addAll(resultPoly));
                // Store a data object with the polyline, used here to indicate an arbitrary type.
                polyline1.setTag("A");
                // Style the polyline.
                stylePolyline(polyline1);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.d("SpinnerListenerError", error.toString());
            }
        });
        mRequestQueue1.add(mStringRequest1);
    }


    public void addListenerOnSpinnerItemSelection(View view) {

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                waySelected = parentView.getItemAtPosition(position).toString();
                waySelected =waySelected.toLowerCase();
                Log.d("SpinnerListener", waySelected);
//                Toast.makeText(getActivity(),
//                        "OnItemSelectedListener : " + parentView.getItemAtPosition(position),
//                        Toast.LENGTH_SHORT).show();
                if(firstTimeEntered == false)
                    getCallDirections(waySelected);
                else
                    firstTimeEntered = false;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }


        });
    }

    private void stylePolyline(Polyline polyline) {

        Log.d(TAG, "stylePolyline: " +polyline.toString());
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

        polyline.setEndCap(new RoundCap());
        polyline.setWidth(POLYLINE_STROKE_WIDTH_PX);
        polyline.setColor(COLOR_BLUE_ARGB);
        polyline.setJointType(JointType.ROUND);
    }



    @Override
    public void onPolylineClick(Polyline polyline) {
        // Flip from solid stroke to dotted stroke pattern.
        if ((polyline.getPattern() == null) || (!polyline.getPattern().contains(DOT))) {
            polyline.setPattern(PATTERN_POLYLINE_DOTTED);
        } else {
            // The default pattern is a solid stroke.
            polyline.setPattern(null);
        }

        // Toast.makeText(this, "Route type "+polyline.getTag().toString(),Toast.LENGTH_SHORT).show();

    }
    public void getAutoSearchResults(String text) {


        Log.d("autoSearchResults", autoCompleteUrl);
        autoCompleteUrl= "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        try {
            autoCompleteUrl+="input="+ URLEncoder.encode(text,"utf-8")+"&key="+key;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getSearchResults: Came?");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Method.GET, autoCompleteUrl,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    Log.d(TAG, "onResponse: Came?");
                    JSONArray ja = response.getJSONArray(TAG_RESULT);

                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject c = ja.getJSONObject(i);
                        String description = c.getString("description");
                        Log.d("description", description);
                        auto_complete_names.add(description);
                    }

                    adapter = new ArrayAdapter<String>(
                            getActivity().getApplicationContext(),
                            android.R.layout.simple_list_item_1, auto_complete_names) {
                        @Override
                        public View getView(int position,
                                            View convertView, ViewGroup parent) {
                            View view = super.getView(position,
                                    convertView, parent);
                            TextView text = (TextView) view
                                    .findViewById(android.R.id.text1);
                            text.setTextColor(Color.BLACK);
                            return view;
                        }
                    };
                    editTextFromLocation.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (Exception e) {
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " +"Errrrorrrrr here" +error.getMessage());
            }
        });
        queue.add(jsonObjReq);

    }







}
package com.example.gayathri.places;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Movie;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gayathri.places.FavouriteHelper;
import com.example.gayathri.places.MainActivity;

import com.example.gayathri.places.PhotoFragment;
import com.example.gayathri.places.R;
import com.example.gayathri.places.SectionsPageAdapter;
import com.example.gayathri.places.Tab1Fragment;
import com.example.gayathri.places.Tab3Fragment;
import com.example.gayathri.places.Tab4Fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class PlaceActivity extends AppCompatActivity {
    private static final String TAG = "PlaceActivity";
    private ViewPager mViewPager;
    private ProgressDialog progress;
    final PlaceActivity pActivity = this;


    Bundle bundle = new Bundle();
    Bundle extras;
    TabLayout tabLayout;

    //final TextView mTextView = (TextView) findViewById(R.id.textView3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progress=new ProgressDialog(this);
        progress.setMessage("Fetching details");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.show();
        Log.d(TAG, "onCreate: Starting.");
        extras = getIntent().getExtras();
        final String placeID = extras.getString("place_id");

        RequestQueue queue = Volley.newRequestQueue(this);
        //String placeID = "ChIJOaegwbTHwoARg7zN_9nq5Uc";
        //usc: ChIJ7aVxnOTHwoARxKIntFtakKo Dominos: ChIJOaegwbTHwoARg7zN_9nq5Uc
        String url ="https://maps.googleapis.com/maps/api/place/details/json?placeid="+placeID+"&key=AIzaSyAAPdwltp5ESHLjlL39b0cB1fwPknX8FB0";


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {

                            setContentView(R.layout.place_details);
                            Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
                            setSupportActionBar(toolbar);
                            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                            JSONObject json = new JSONObject(response);
                            String name = "", address="", phone = "", website="", price_level="", google_url="", rating="", reviews="", icon="",yelp_components="";
                            if (new JSONObject(json.get("result").toString()).has("name")) {
                                name = new JSONObject(json.get("result").toString()).get("name").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("formatted_address")) {
                                address = new JSONObject(json.get("result").toString()).get("formatted_address").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("formatted_phone_number")) {
                                phone = new JSONObject(json.get("result").toString()).get("formatted_phone_number").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("website")) {
                                website = new JSONObject(json.get("result").toString()).get("website").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("price_level")) {
                                price_level = new JSONObject(json.get("result").toString()).get("price_level").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("url")) {
                                google_url = new JSONObject(json.get("result").toString()).get("url").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("rating")) {
                                rating = new JSONObject(json.get("result").toString()).get("rating").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("reviews")) {
                                reviews = new JSONObject(json.get("result").toString()).get("reviews").toString();
                            }

                            if (new JSONObject(json.get("result").toString()).has("address_components")) {
                                yelp_components = new JSONObject(json.get("result").toString()).get("address_components").toString();
                            }
                            if (new JSONObject(json.get("result").toString()).has("icon")) {
                                icon = new JSONObject(json.get("result").toString()).get("icon").toString();
                            }
                            bundle.putString("name", name);
                            bundle.putString("address", address);
                            bundle.putString("phone", phone);
                            bundle.putString("website", website);
                            bundle.putString("price_level", price_level);
                            bundle.putString("google_url", google_url);
                            bundle.putString("rating", rating);
                            bundle.putString("reviews", reviews);
                            bundle.putString("place_id", placeID);
                            bundle.putString("yelp_components", yelp_components);
                            Log.d("bundle", reviews);
//                            bundle.putO("reviews", reviews);
                            SectionsPageAdapter mSectionsPageAdapter;
                            mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
                            mViewPager = (ViewPager) findViewById(R.id.container);
                            //setupViewPager(mViewPager);
                            SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
                            Fragment frag = new Tab1Fragment();
                            frag.setArguments(bundle);
                            adapter.addFragment(frag, "INFO");
                            frag = new PhotoFragment();
                            frag.setArguments(bundle);
                            adapter.addFragment(frag, "PHOTO");
                            frag = new Tab3Fragment(response);
                            //frag.setArguments(bundle);
                            adapter.addFragment(frag, "MAP");
                            frag = new Tab4Fragment();
                            frag.setArguments(bundle);
                            adapter.addFragment(frag, "REVIEWS");
                            mViewPager.setAdapter(adapter);
                            mViewPager.setAdapter(adapter);
                            tabLayout = (TabLayout) findViewById(R.id.tabs);
                            tabLayout.setupWithViewPager(mViewPager);
//                            tabLayout.getTabAt(0).setIcon(R.drawable.info_outline);
//                            tabLayout.getTabAt(1).setIcon(R.drawable.photos);
//                            tabLayout.getTabAt(2).setIcon(R.drawable.maps);
//                            tabLayout.getTabAt(3).setIcon(R.drawable.review);
                            setupTabIcons();
                            //toolbar.setTitle(name);
                            ImageView tweetbutton=(ImageView) findViewById(R.id.twitterbutton);
                            final String finalName = name;
                            final String finalAddress =address;
                            final String finalWebsite = website;
                            final String finalIcon = icon;
                            TextView tvTitle = (TextView) findViewById(R.id.detailToolbarText);
                            tvTitle.setText(name);
                            tweetbutton.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://twitter.com/intent/tweet?text="+"Check out "+ finalName + " located at "+finalAddress+" website: "+finalWebsite ));
                                    startActivity(intent);
                                }
                            });
                            final ImageView favButton = (ImageView ) findViewById(R.id.favbutton);
                            final FavouriteHelper favHelper = new FavouriteHelper(pActivity);
                            if(favHelper.checkInFavourites(placeID) >-1){
                                favButton.setImageResource(R.drawable.white_fill_nb);
                            }
                            else{
                                favButton.setImageResource(R.drawable.white_border_white);
                            }
                            favButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String place_id = placeID;
                                    int index =favHelper.checkInFavourites(place_id);
                                    if(index>-1)
                                    {
                                        favHelper.deleteFromFavorites(place_id,index);
                                        favButton.setImageResource(R.drawable.white);
                                        Toast toast = Toast.makeText(pActivity,finalName+" was removed from Favorites.",Toast.LENGTH_SHORT);
                                        toast.show();

                                    }
                                    else{
                                        List<String> row = new ArrayList<String>();
                                        row.add(place_id);
                                        row.add(finalName);
                                        row.add(finalAddress);
                                        row.add(finalIcon);
                                        favHelper.addtoFavorites(row);

                                        favButton.setImageResource(R.drawable.white_fill_nb);
                                        Toast toast = Toast.makeText(pActivity,row.get(1)+" was added to Favorites.",Toast.LENGTH_SHORT);
                                        toast.show();

                                    }

                                }
                            });
                            progress.hide();
                            // progress.hide();
                        } catch (Exception e) {
                            Log.i("error", e.getMessage());
                        }

//                        mTextView.setText("Response is: "+ response.substring(0,500));
                        //bundle.putString("key", "response is: ");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                bundle.putString("key", "that didn't work");
            }
        });
        queue.add(stringRequest);
    }

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("INFO");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(R.drawable.info_outline, 0, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("PHOTOS");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.photos, 0, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("MAP");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(R.drawable.maps, 0, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("REVIEWS");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(R.drawable.review, 0, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Log.d("hhhhhhhhhhhhhhh","pressed");
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                this.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
package com.example.gayathri.places;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class ResultsListView extends AppCompatActivity {

    private RecyclerView photosRecyclerView;
    private TextView noResultsTextView;
    private RecyclerView.Adapter adapter;
    private List<Bitmap> photosBitmaps;
    //    private List<int[]> dimensions;
    private String placeID;
    private int currentDisplayNumber = 0;

    private void getPhotosDetails() throws Throwable {
        try {
            placeID = "ChIJ7aVxnOTHwoARxKIntFtakKo";
            final GeoDataClient mGeoDataClient;
            mGeoDataClient = Places.getGeoDataClient(this, null);
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeID);
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    try {
                        PlacePhotoMetadataResponse result = task.getResult();

                        PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

                        final int length = photoMetadataBuffer.getCount();

                        if (length == 0) {
                            noResultsTextView.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "No photos available", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (int i = 0; i < length; i++) {
                            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                            int height = photoMetadata.getMaxHeight();
                            int width = photoMetadata.getMaxWidth();
//                            dimensions.add(new int[]{height, width});
                            System.out.print("\n\nh : " + height + "  |  w : " + width + "\n\n");
                            final Task<PlacePhotoResponse> placePhotoResponse = mGeoDataClient.getPhoto(photoMetadata);

                            placePhotoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                                @Override
                                public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                    try {
                                        PlacePhotoResponse p = task.getResult();
                                        photosBitmaps.add(p.getBitmap());
                                        currentDisplayNumber++;
                                        if (currentDisplayNumber == length)
                                            setView();
                                    } catch (Throwable e) {
                                        noResultsTextView.setVisibility(View.VISIBLE);
                                        Toast.makeText(getApplicationContext(), "Unable to fetch photos", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    } catch (Throwable e) {
                        noResultsTextView.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Unable to fetch photos", Toast.LENGTH_LONG).show();
                    }

                }

            });
        } catch (Throwable e) {
            noResultsTextView.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "Unable to fetch photos", Toast.LENGTH_LONG).show();
        }

    }

    private void setView() throws Throwable {

        adapter = new PhotosAdapter(this, photosBitmaps, null);

        photosRecyclerView.setAdapter(adapter);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_results_list_view);

            noResultsTextView = (TextView) findViewById(R.id.noResultsTextView);
            noResultsTextView.setVisibility(View.INVISIBLE);

            photosRecyclerView = (RecyclerView) findViewById(R.id.resultsListRecyclerView);
            photosRecyclerView.setHasFixedSize(true);
            photosRecyclerView.setLayoutManager(new LinearLayoutManager(this));

            photosBitmaps = new ArrayList<>();
//            dimensions = new ArrayList<>();

            currentDisplayNumber = 0;
            getPhotosDetails();
        } catch(Throwable e) {
            Toast.makeText(getApplicationContext(), "Unable to fetch photos", Toast.LENGTH_LONG).show();
        }

    }
}

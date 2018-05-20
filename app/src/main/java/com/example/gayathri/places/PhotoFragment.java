package com.example.gayathri.places;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PhotoFragment extends android.support.v4.app.Fragment {

    private RecyclerView photosRecyclerView;
    private TextView noResultsTextView;
    private RecyclerView.Adapter adapter;
    private List<Bitmap> photosBitmaps;
    private String placeID;
    private int currentDisplayNumber = 0;
    private View photosView;
    private List<int[]> dimensions;

    public PhotoFragment() {
        Log.d("Logg", "PhotoFragment:   got created");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        photosView = inflater.inflate(R.layout.activity_results_list_view, container, false);

        try {

            if(getArguments() != null)  {
                placeID = getArguments().getString("place_id");
                Log.d(" pHOTOfRAGMENTLOG", "onCreateView: placeID1 is" +placeID );

                noResultsTextView = (TextView) photosView.findViewById(R.id.noResultsTextView);
                noResultsTextView.setVisibility(View.INVISIBLE);

                Log.d(" pHOTOfRAGMENTLOG", "onCreateView: placeID2 is" +placeID );

                photosRecyclerView = (RecyclerView) photosView.findViewById(R.id.resultsListRecyclerView);
                photosRecyclerView.setHasFixedSize(true);
                photosRecyclerView.setLayoutManager(new LinearLayoutManager(photosView.getContext()));
                Log.d(" pHOTOfRAGMENTLOG", "onCreateView: placeID3 is" +placeID );

                photosBitmaps = new ArrayList<>();
                dimensions = new ArrayList<>();

                currentDisplayNumber = 0;
                getPhotosDetails();
            }
        } catch(Throwable e) {
            Toast.makeText(photosView.getContext(), "Unable to fetch photos1", Toast.LENGTH_LONG).show();
        }
        return photosView;
    }

    private void getPhotosDetails() throws Throwable {
        try {
            Log.d(" pHOTOfRAGMENTLOG", "onCreateView: placeID4 is" +placeID );
            final GeoDataClient mGeoDataClient;
            mGeoDataClient = Places.getGeoDataClient(photosView.getContext(), null);
            final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeID);
            Log.d(" pHOTOfRAGMENTLOG", "onCreateView: placeID5 is" +placeID );
            photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                    try {
                        Log.d(" pHOTOfRAGMENTLOG", "onCreateView: placeID6 is" +placeID );
                        PlacePhotoMetadataResponse result = task.getResult();

                        PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();

                        final int length = photoMetadataBuffer.getCount();
                        Log.d(" pHOTOfRAGMENTLOG", "onCreateView: placeID6 is" +length );

                        if (length == 0) {
                            noResultsTextView.setVisibility(View.VISIBLE);
                            Toast.makeText(photosView.getContext(), "No photos available", Toast.LENGTH_LONG).show();
                            return;
                        }

                        for (int i = 0; i < length; i++) {
                            PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                            int height = photoMetadata.getMaxHeight();
                            int width = photoMetadata.getMaxWidth();
                            dimensions.add(new int[]{height, width});
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
                                        Toast.makeText(photosView.getContext(), "Unable to fetch photos2", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    } catch (Throwable e) {
                        noResultsTextView.setVisibility(View.VISIBLE);
                        Toast.makeText(photosView.getContext(), "Unable to fetch photos3", Toast.LENGTH_LONG).show();
                    }

                }

            });
        } catch (Throwable e) {
            e.printStackTrace();
            noResultsTextView.setVisibility(View.VISIBLE);

        }
    }

    private void setView() throws Throwable {
        Log.d(" pHOTOfRAGMENTLOG", "heeeeeeereee : " + dimensions.size());
        adapter = new PhotosAdapter(photosView.getContext(), photosBitmaps, dimensions);
        photosRecyclerView.setAdapter(adapter);
    }
}

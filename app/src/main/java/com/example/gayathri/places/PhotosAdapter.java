package com.example.gayathri.places;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.view.View;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.CustomViewHolder> {

    private List<Bitmap> photosBitmaps = null;
    private List<int []> dimensions = null;
    private Context context;

    public PhotosAdapter(Context context, List<Bitmap> photosBitmaps, List<int []> dimensions) {
        this.dimensions = dimensions;
        this.photosBitmaps = photosBitmaps;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.photos_layout, null);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        int h = dimensions.get(position)[0];
        int w = dimensions.get(position)[1];

        Log.d("OnBindViewHolderlog", "onBindViewHolder: " + h +  ' '  + w  );

        h = (int)(((float)h/w) * 1050);
        w = 1050;

        Log.d("OnBindViewHolderlog", "onBindViewHolder: " + h +  ' '  + w  );



        holder.placePhotoImageView.getLayoutParams().height = h;
        holder.placePhotoImageView.getLayoutParams().width = w;
        holder.placePhotoImageView.setImageBitmap(photosBitmaps.get(position));
//        if(position == getItemCount() - 1)
//            holder.placePhotoImageView.requestLayout();

    }

    @Override
    public int getItemCount() {
        return photosBitmaps.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        public ImageView placePhotoImageView;
        public CustomViewHolder (View view) {
            super(view);
            placePhotoImageView =  (ImageView) view.findViewById(R.id.placePhotoImageView);
        }
    }
}

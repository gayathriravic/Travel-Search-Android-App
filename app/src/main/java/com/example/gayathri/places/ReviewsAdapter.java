package com.example.gayathri.places;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder>{

    List<Review> reviews;

    List<Review> yelp_reviews;
    String url ;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name, time, text;
        public RatingBar review_rating_bar;
        public ImageView photo_url;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.review_name);
            //rating = (TextView) view.findViewById(R.id.review_rating);
            photo_url = (ImageView) view.findViewById(R.id.review_photo);
            review_rating_bar = (RatingBar) view.findViewById(R.id.review_rating_bar);
//            photo_url = (ImageView) R.id.review_photo);
            time = (TextView) view.findViewById(R.id.review_time);
            text = (TextView) view.findViewById(R.id.review_text);
            //view.setOnClickListener(this);
        }
//        @Override
//        public void onClick(View view) {
//            Log.d("onclick listerner", "clicked");
////            String url="https://www.google.com";
//            Intent i = new Intent(Intent.ACTION_VIEW);
//            i.setData(Uri.parse(url));
//            view.getContext().startActivity(i);
//        }
    }

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.review_list_row, viewGroup, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){
        final Review review = reviews.get(position);
        Log.d("ReviewAdapterLog", "onBindViewHolder: " + position + " " + review.getName());
        holder.name.setText(review.getName());
        //holder.rating.setText(review.getRating());
        holder.review_rating_bar.setRating(Float.parseFloat(review.getRating()));
        holder.time.setText(review.getTime_created());
        Picasso.get().load(review.getPhoto_url()).into(holder.photo_url);
//        holder.photo_url.setText(review.getPhoto_url());
        holder.text.setText(review.getText());
//        url = review.getUser_url();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!review.getUser_url().isEmpty()){
                    String url= review.getUser_url();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    v.getContext().startActivity(i);
                }
            }
        });
    }
    /*public ReviewsAdapter(List<Movie> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new ReviewsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());
    }*/

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}

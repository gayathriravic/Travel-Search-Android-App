package com.example.gayathri.places;

public class FavWrapper {

    Favourite fav ;
    String place_id;
    FavWrapper(Favourite f, String pid)
    {
        fav =f;
        place_id =pid;
    }
    public String toString() {
        return "Favorite{" + "name='" + fav.getName() + '\'' +
                ", address='" + fav.getAddress() + '\'' +
                ", placeid='"+place_id+'\''+
                '}';
    }



}

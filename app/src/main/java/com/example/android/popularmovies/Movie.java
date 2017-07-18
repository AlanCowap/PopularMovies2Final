package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by alex.fanning on 17/05/2017.
 */


//Details on implementing parceable interfaces from https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable


public class Movie implements Parcelable {
    //TODO AWESOME Creating a Movie is good design.


    private int id;
    private String origTitle;
    private String synoposis;
    private double userRating;
    private String releaseDate;
    private String posterPath;

    private String posterPathLocal;


    public Movie(int _id, String _origTitle, String _synoposis, double _userRating, String _releaseDate, String _posterPath){
        id = _id;
        origTitle=_origTitle;
        synoposis = _synoposis;
        userRating = _userRating;
        releaseDate = _releaseDate;
        posterPath =_posterPath;
    }
    public Movie(int _id, String _origTitle, String _synoposis, double _userRating, String _releaseDate, String _posterPath,String _posterPathLocal){
        id = _id;
        origTitle=_origTitle;
        synoposis = _synoposis;
        userRating = _userRating;
        releaseDate = _releaseDate;
        posterPath =_posterPath;
        posterPathLocal = _posterPathLocal;
    }



    //Parceable part
    public Movie(Parcel in){
        String[] data = new String[6];
        in.readStringArray(data);

        id = Integer.parseInt(data[0]);
        origTitle=data[1];
        synoposis = data[2];
        userRating = Double.parseDouble(data[3]);
        releaseDate = data[4];
        posterPath =data[5];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String[] dataToSend = new String[6];
        dataToSend[0] = String.valueOf(id);
        dataToSend[1] = origTitle;
        dataToSend[2] = synoposis;
        dataToSend[3] = String.valueOf(userRating);
        dataToSend[4] = releaseDate;
        dataToSend[5] = posterPath;

        dest.writeStringArray(dataToSend);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrigTitle() {
        return origTitle;
    }


    public String getSynoposis() {
        return synoposis;
    }


    public double getUserRating() {
        return userRating;
    }



    public String getReleaseDate() {
        return releaseDate;
    }



    public String getPosterPath() {
        return posterPath;
    }



    public void setPosterPathLocal(String posterPathLocal) {
        this.posterPathLocal = posterPathLocal;
    }
    public String getPosterPathLocal() {
        return posterPathLocal;
    }


}

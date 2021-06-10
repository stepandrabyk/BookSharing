package com.hfad.booksharing;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class Book implements Parcelable {
    String id;
    String ownerId;
    String name;
    String authors;
    String topic;
    String genre;
    Boolean isActive;
    String url;


    public Book(String ownerId, String name, String authors, Boolean isActive) {
        this.ownerId = ownerId;
        this.name = name;
        this.authors = authors;
        this.isActive = isActive;
        id = UUID.randomUUID().toString();

    }

    public Book (){
        id = UUID.randomUUID().toString();
        isActive=true;
    }


    protected Book(Parcel in) {
        id = in.readString();
        ownerId = in.readString();
        name = in.readString();
        authors = in.readString();
        topic = in.readString();
        genre = in.readString();
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        url = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    //getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url= url;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthors() {

        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(ownerId);
        parcel.writeString(name);
        parcel.writeString(authors);
        parcel.writeString(topic);
        parcel.writeString(genre);
        parcel.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        parcel.writeString(url);
    }
}

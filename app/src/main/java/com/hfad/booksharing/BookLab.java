package com.hfad.booksharing;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BookLab {
    private static BookLab sBookLab;
    DatabaseReference ref;
    private ArrayList<Book> mBooks;
    private Book getBook_(int id)
    {
        return mBooks.get(id);
    }
    public  Book getBook(int id)
    {
        return mBooks.get(id);
    }
    public static BookLab get(Context context){
        if(sBookLab==null)
        {
            sBookLab = new BookLab(context);
        }
        return sBookLab;
    }
    private BookLab (Context context)
    {


    }
    public void setBooks(List<Book>books){
        mBooks.clear();
        mBooks.addAll(books);
    }
    public void addBook(Book newBook){
        mBooks.add(newBook);
    }
    public ArrayList<Book> getBooks(){
        return mBooks;
    }
    public Book getBook(String id)
    {
        for(Book book : mBooks)
        {
            if(book.getId().equals(id))
            {
                return book;
            }
        }
        return null;
    }
}

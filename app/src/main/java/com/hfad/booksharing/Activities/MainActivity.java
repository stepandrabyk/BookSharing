package com.hfad.booksharing.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.booksharing.Book;
import com.hfad.booksharing.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private RecyclerView mRecyclerView;
    private BookAdapter mAdapter;
    private Book mBook;
    private List<Book> books = new ArrayList<>();
    private static final int START_CODE = 100;
    private static final int CONT_CODE = 200;
    private String LOG_TAG="!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser() == null){
            Intent intent = new Intent(getApplicationContext(),Login.class);
            finish();
            startActivity(intent);
        }
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        String userID="";
        try{
            FirebaseUser s = fAuth.getCurrentUser();
            userID = fAuth.getCurrentUser().getUid();
        }catch (NullPointerException e){

        }

        mRecyclerView= findViewById(R.id.list_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Book newBook = new Book();
                newBook.setOwnerId(fAuth.getCurrentUser().getUid());
                Intent intent = new Intent(getApplicationContext(),BookEdit.class);
                intent.putExtra("Book",newBook);
                startActivityForResult(intent,START_CODE);
            }
        });
        mFirebaseDatabase.getReference().child("Books").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                mAdapter.addBook(book);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Book book = snapshot.getValue(Book.class);
                mAdapter.updateBook(book);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mAdapter = new BookAdapter(books,this);
        mRecyclerView.setAdapter(mAdapter);
        updateUI();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logOutItem)
            logout();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        updateUI();
    }

    public void logout( ){
        fAuth.signOut();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK)
        {
            if(requestCode == START_CODE){
                Book newBook = (Book) data.getParcelableExtra("Book");
                mAdapter.addBook(newBook);
                books.add(newBook);
            }
            if(requestCode == CONT_CODE){
                Book newBook = (Book) data.getParcelableExtra("Book");
                mAdapter.updateBook(newBook);
                updateBook(newBook);
            }
            //Toast.makeText(getApplicationContext(),"ERRORO shoto tam",Toast.LENGTH_LONG).show();
        }
        //Intent intent = getIntent();

        updateUI();
    }
    public void updateBook(Book book){
        int i=0;
        for(Book b:books){
            i++;
            if(b.getId().equals(book.getId()))
                books.set(i-1,book);
        }
    }
    private void updateUI(){
        mAdapter.notifyDataSetChanged();
    }

    private class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mTopicTextView;
        private ImageView mBookImageView;
        private BookHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.book_list_item,parent,false));
            mTitleTextView =  itemView.findViewById(R.id.name);
            mTopicTextView = itemView.findViewById(R.id.topic);
            mBookImageView = itemView.findViewById(R.id.image_book);
            itemView.setOnClickListener(this);

        }
        public void bind(Book book){
            mBook = book;
            mTitleTextView.setText(mBook.getName());
            mTopicTextView.setText(mBook.getTopic());
            StorageReference storage = FirebaseStorage.getInstance().getReference();
// Create a reference to a file from a Google Cloud Storage URI
            StorageReference images = storage.child("images");
            StorageReference image = storage.child("images/"+mBook.getId()+".jpeg");
            String imageUrl = image.getDownloadUrl().toString();
            try {
                final File localFile = File.createTempFile(mBook.getId(),"jpeg");
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                mBookImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }




            //if(mBook.getImage()!=null)
              //  mBookImageView.setImageBitmap(mBook.getImage());
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Toast.makeText(getApplicationContext(),"clocked"+ Integer.toString( position),Toast.LENGTH_LONG).show();
            String uID = fAuth.getCurrentUser().getUid();
            String adUID=mAdapter.getClickedBook(position).getOwnerId();
            if(fAuth.getCurrentUser().getUid().equals(mAdapter.getClickedBook(position).getOwnerId()))
            {
                Intent intent = new Intent(getApplicationContext(), BookEdit.class);
                intent.putExtra("Book",mAdapter.getClickedBook(position));
                startActivityForResult(intent,CONT_CODE);
            }
            else {
                Book book = mAdapter.getClickedBook(position);
                Intent intent = new Intent(getApplicationContext(), BookDetailsActivity.class);
                intent.putExtra("Book",book);
                Log.d(LOG_TAG, "startActivity");
                startActivity(intent);
            }

        }
    }



    public class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private LayoutInflater inflater;
        private List<Book> mBooks = new ArrayList<>();


        public BookAdapter(List<Book> books, Context context){
            this.inflater = LayoutInflater.from(context);
            mBooks=books;
        }
        public void setItems(Collection<Book> tweets) {
            mBooks.addAll(tweets);
            notifyDataSetChanged();
        }
        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new BookHolder(layoutInflater,parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            Book book = mBooks.get(position);
            holder.bind(book);
        }




        @Override
        public int getItemCount() {
            return mBooks.size();
        }
        
        public Book getClickedBook(int position)
        {
            return mBooks.get(position);
        }
        public void addBook(Book book){
            mBooks.add(book);
            mAdapter.notifyDataSetChanged();
        }
        public void updateBook(Book book){
            int i=0;
            for(Book b:mBooks){
                i++;
                if(b.getId().equals(book.getId()))
                    mBooks.set(i-1,book);
            }
            mAdapter.notifyDataSetChanged();
        }
    }

}
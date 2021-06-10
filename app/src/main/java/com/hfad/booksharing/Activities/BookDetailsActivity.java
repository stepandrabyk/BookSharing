package com.hfad.booksharing.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfad.booksharing.Book;
import com.hfad.booksharing.R;

public class BookDetailsActivity extends AppCompatActivity {
    Book curBook;
    Button btnStartMess;
    ImageView image;
    TextView nameTV,topicTV,authorsTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        nameTV = findViewById(R.id.textName);
        topicTV = findViewById(R.id.textTopic);
        authorsTV = findViewById(R.id.textAuthors);
        Intent intent = getIntent();

        curBook = (Book) intent.getParcelableExtra("Book");
        btnStartMess = findViewById(R.id.btnGetBook);
        btnStartMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        initBook();
    }
    private void initBook(){
        nameTV.setText(curBook.getName());
        topicTV.setText(curBook.getTopic());
        authorsTV.setText(curBook.getAuthors());
    }
}
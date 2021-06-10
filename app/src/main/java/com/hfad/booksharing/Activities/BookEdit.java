package com.hfad.booksharing.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hfad.booksharing.Book;
import com.hfad.booksharing.R;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class BookEdit extends AppCompatActivity {
    private StorageReference mReference;
    ImageView editImage;
    EditText editName, editAuthors,editTopic,editGenre;
    Book curBook;
    Button btnSave;
    Uri imageUri;
    Bitmap imageBitmap;
    FirebaseStorage storage;
    StorageReference storageReference;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private static final int PICK_IMAGE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_edit);
        init();
        mReference = FirebaseStorage.getInstance().getReference("Images");

        Intent intent = getIntent();

        curBook = (Book) intent.getParcelableExtra("Book");
        editName.setText(curBook.getName());
        editTopic.setText(curBook.getTopic());
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),"clocked",Toast.LENGTH_LONG).show();
                openGalery();
            }
        });

    }

    @Override
    protected void onPause() {

        super.onPause();

    }

    private void uploadImage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(imageUri != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference ref = storageReference.child("images/"+ curBook.getId());

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(BookEdit.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){



                    }else{
                        Toast.makeText(BookEdit.this, "Error happened during the upload process", Toast.LENGTH_LONG ).show();
                    }
                }
            });
            String url = ref.getDownloadUrl().toString();
            curBook.setUrl(ref.getDownloadUrl().toString());


        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }
    private void openGalery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent,PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri=data.getData();

            try{
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                editImage.setImageBitmap(imageBitmap);

            }
            catch (Exception e){
                Toast.makeText(getApplicationContext(),"ERRORO shoto tam",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void init(){


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();
        editName=findViewById(R.id.edit_name);
        editAuthors=findViewById(R.id.edit_authors);
        editTopic=findViewById(R.id.edit_topic);
        editGenre=findViewById(R.id.edit_genre);
        editImage=findViewById(R.id.edit_image_book);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //curBook.setImage(imageBitmap);
                curBook.setTopic(editTopic.getText().toString());
                curBook.setName(editName.getText().toString());
               // curBook.setAuthors(editTopic.getText().toString());
                curBook.setGenre(editGenre.getText().toString());
                DatabaseReference mBooksId = mDatabaseReference.child("Books").child(curBook.getId());

                uploadImage();
                mDatabaseReference.child("Books").child(curBook.getId()).setValue(curBook);
                Intent intent = new Intent();
                intent.putExtra("Book", curBook);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }
}
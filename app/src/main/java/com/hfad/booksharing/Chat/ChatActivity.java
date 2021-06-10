package com.hfad.booksharing.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hfad.booksharing.R;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;
    FirebaseDatabase mFirebaseDatabase;
    List<Message> mMessages = new ArrayList<>();
    RelativeLayout activity_main;
    Button button;
    private ChatAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mMessages.add(new Message("Hello",FirebaseAuth.getInstance().getCurrentUser().getEmail()));
        mMessages.add(new Message("Hello2","1,1,2,1243"));
        activity_main = findViewById(R.id.activity_main);
        button = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.editText);
                FirebaseDatabase.getInstance().getReference("Chats").push()
                        .setValue(new Message(input.getText().toString(),
                                FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                input.setText("");
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {

        } else {
            //init();
         //   displayChat();
        }
    }

    private void init() {
        mFirebaseDatabase.getReference().child("Chats").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message m = snapshot.getValue(Message.class);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
    }

  /*  private void displayChat() {
        RecyclerView listMessages = findViewById(R.id.listView);
        FirebaseRecyclerAdapter<Message,RecyclerView.ViewHolder> adapter;

        final FirebaseRecyclerOptions<Message> options = new FirebaseRecyclerOptions.Builder<Message>()
                .setQuery(queryforDisplayMessages, Message.class)
                .setLifecycleOwner(this)
                .build();

        adapter=new FirebaseUserChatAdapter(options);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        mRecyclerView.setAdapter(adapter);
        listMessages.setLayoutManager(new LinearLayoutManager(this));
        listMessages.setAdapter(mMessageAdapter);

    }*/
    private class SendMessageHolder extends RecyclerView.ViewHolder{
        TextView textMessage, autor, timeMessage;
        Message mMessage;
        public SendMessageHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.message_list_send_item,parent,false));
            textMessage = (TextView)findViewById(R.id.tvMessage);
            autor = (TextView)findViewById(R.id.tvUser);
            timeMessage = (TextView)findViewById(R.id.tvTime);

        }
        public SendMessageHolder(View view) {
            super(view);
            textMessage = (TextView)findViewById(R.id.tvMessage);
            autor = (TextView)findViewById(R.id.tvUser);
            timeMessage = (TextView)findViewById(R.id.tvTime);

        }
        public void bind(Message m){
            mMessage = m;
            textMessage.setText(m.getTextMessage());
            autor.setText(m.getAutor());
            timeMessage.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", m.getTimeMessage()));
            StorageReference storage = FirebaseStorage.getInstance().getReference();

        }
    }
    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{
        TextView textMessage, autor, timeMessage;
        Message mMessage;
        public ReceivedMessageHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.message_list_received_item,parent,false));
            textMessage = (TextView)findViewById(R.id.tvMessage);
            autor = (TextView)findViewById(R.id.tvUser);
            timeMessage = (TextView)findViewById(R.id.tvTime);

        }
        public ReceivedMessageHolder(View view) {
            super(view);
            textMessage = (TextView)findViewById(R.id.tvMessage);
            autor = (TextView)findViewById(R.id.tvUser);
            timeMessage = (TextView)findViewById(R.id.tvTime);

        }
        public void bind(Message m){
            mMessage = m;
            textMessage.setText(m.getTextMessage());
            autor.setText(m.getAutor());
            timeMessage.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", m.getTimeMessage()));
            StorageReference storage = FirebaseStorage.getInstance().getReference();

        }
    }
    private class ChatAdapter extends FirebaseRecyclerAdapter<Message,RecyclerView.ViewHolder> {
        private final String TAG=ChatAdapter.class.getSimpleName();

        private static final int VIEW_TYPE_MESSAGE_SENT = 1;
        private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;


        public ChatAdapter(@NonNull FirebaseRecyclerOptions<Message> options) {
            super(options);
        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;

            if (viewType == VIEW_TYPE_MESSAGE_SENT) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_list_send_item, parent, false);
                return new SendMessageHolder(view);
            } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.message_list_received_item, parent, false);
                return new ReceivedMessageHolder(view);
            }

            return null;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Message m = getItem(position);
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SendMessageHolder) holder).bind(m);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(m);
            }
        }

        @Override
        protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Message model) {
            Message m = getItem(position);
            switch (holder.getItemViewType()) {
                case VIEW_TYPE_MESSAGE_SENT:
                    ((SendMessageHolder) holder).bind(m);
                    break;
                case VIEW_TYPE_MESSAGE_RECEIVED:
                    ((ReceivedMessageHolder) holder).bind(m);
            }
        }


    }


}

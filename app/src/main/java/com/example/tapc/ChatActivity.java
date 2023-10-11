package com.example.tapc;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tapc.model.RoomModel;
import com.example.tapc.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private TextView tvTitle;
    private EditText etChat;
    private Button btChat;
    private String uid;
    private String name;
    private String roomKEY;
    private String roomTitle;
    private RecyclerView rvChat;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvTitle = (TextView) findViewById(R.id.ac_tvTitle);
        etChat = (EditText) findViewById(R.id.ac_etChat);
        btChat = (Button) findViewById(R.id.ac_btChat);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("userName").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        roomKEY = getIntent().getStringExtra("roomKEY");
        roomTitle = getIntent().getStringExtra("roomTitle");
        rvChat = (RecyclerView) findViewById(R.id.ac_rvChat);

        tvTitle.setText(roomTitle);
        Toast.makeText(getApplicationContext(), roomTitle + "방에 입장하셨습니다.", Toast.LENGTH_LONG).show();

        btChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomModel.Comment comment = new RoomModel.Comment();
                comment.name = name;
                comment.message = etChat.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("rooms").child(roomKEY).child("comments").push().setValue(comment);
                etChat.setText("");
            }
        });
        rvChat.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        rvChat.setAdapter(new RecyclerViewAdapater());
    }

    class RecyclerViewAdapater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<RoomModel.Comment> commentList;
        public RecyclerViewAdapater() {
            commentList = new ArrayList<>();
            FirebaseDatabase.getInstance().getReference().child("rooms").child(roomKEY).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    commentList.clear();
                    for(DataSnapshot item : dataSnapshot.getChildren()) {
                        commentList.add(item.getValue(RoomModel.Comment.class));
                        rvChat.scrollToPosition(commentList.size()-1);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ((MessageViewHolder)holder).textView_name.setText(commentList.get(position).name + commentList.get(position).colon);
            ((MessageViewHolder)holder).textView_message.setText(commentList.get(position).message);
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        private class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_name;
            public TextView textView_message;

            public MessageViewHolder(View view) {
                super(view);
                textView_name = (TextView) view.findViewById(R.id.ic_tvName);
                textView_message = (TextView) view.findViewById(R.id.ic_tvChat);
            }
        }
    }
}

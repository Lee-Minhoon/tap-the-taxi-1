package com.example.tapc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tapc.model.RoomModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class GenerateActivity extends AppCompatActivity {

    private EditText etTitle;
    private Button btCreate;
    private Intent itCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);

        btCreate = (Button) findViewById(R.id.ag_btCreate);
        etTitle = (EditText) findViewById(R.id.ag_etTitle);

        btCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stCreateTitle = etTitle.getText().toString();
                if (stCreateTitle.length() < 1) {
                    Toast.makeText(getApplicationContext(),"제목이 잘못되었습니다.",
                            Toast.LENGTH_LONG).show();
                } else {
                    createRoom(stCreateTitle);
                }
            }
        });
    }

    public void createRoom(String title) {
        final RoomModel room = new RoomModel();
        room.userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        room.roomTitle = title;
        String Key = FirebaseDatabase.getInstance().getReference().child("rooms").push().getKey();
        room.roomKEY = Key;
        FirebaseDatabase.getInstance().getReference().child("rooms").child(Key).setValue(room).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                itCreate = new Intent(GenerateActivity.this, ChatActivity.class);
                itCreate.putExtra("roomKEY", room.roomKEY);
                itCreate.putExtra("roomTitle", room.roomTitle);
                startActivity(itCreate);
                GenerateActivity.this.finish();
            }
        });
    }
}

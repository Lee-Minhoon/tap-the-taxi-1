package com.example.tapc;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {
    private ImageView ivProfile;
    private TextView tvID;
    private TextView tvName;
    private TextView tvAccount;
    private Button btLogout;
    private String uid;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        ivProfile = (ImageView) findViewById(R.id.aa_ivProfile);
        tvID = (TextView) findViewById(R.id.aa_tvID);
        tvName = (TextView) findViewById(R.id.aa_tvName);
        tvAccount = (TextView) findViewById(R.id.aa_tvAccount);
        btLogout = (Button) findViewById(R.id.aa_btLogout);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Profile = (String) dataSnapshot.child("userProfile").getValue();
                String ID = (String) dataSnapshot.child("userID").getValue();
                String name = (String) dataSnapshot.child("userName").getValue();
                String Account = (String) dataSnapshot.child("userAccount").getValue();

                Glide.with(AccountActivity.this).load(Profile).into(ivProfile);
                tvID.setText("이메일 : " + ID);
                tvName.setText("이름 : " + name);
                tvAccount.setText("계좌번호 : " + Account);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                AccountActivity.this.finish();
            }
        });
    }
}

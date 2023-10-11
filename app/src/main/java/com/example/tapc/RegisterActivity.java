package com.example.tapc;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tapc.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private LinearLayout rlRegister;
    private EditText etRegisterID;
    private EditText etRegisterPW;
    private EditText etRegisterName;
    private EditText etRegisterAccount;
    private Button btRegister;
    private ImageView ivProfile;
    private Uri urProfile = null;
    private Intent itRegister;
    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rlRegister = (LinearLayout) findViewById(R.id.ar_llRegister);
        etRegisterID = (EditText) findViewById(R.id.ar_etRegisterID);
        etRegisterPW = (EditText) findViewById(R.id.ar_etRegisterPW);
        etRegisterName = (EditText) findViewById(R.id.ar_etRegisterName);
        etRegisterAccount = (EditText) findViewById(R.id.ar_etRegisterAccount);
        btRegister = (Button) findViewById(R.id.ar_btRegister);
        ivProfile = (ImageView) findViewById(R.id.ar_ivProfile);
        itRegister = new Intent(RegisterActivity.this, LoginActivity.class);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        rlRegister.setBackgroundColor(Color.parseColor(splash_background));

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stRegisterID = etRegisterID.getText().toString();
                String stRegisterPW = etRegisterPW.getText().toString();
                String stRegisterName = etRegisterName.getText().toString();
                String stRegisterAccount = etRegisterAccount.getText().toString();

                if (stRegisterID.length() < 3 || stRegisterPW.length() < 6
                        || stRegisterName.length() == 0 || stRegisterAccount.length() == 0) {
                    Toast.makeText(getApplicationContext(),"형식에 맞지 않습니다.",
                            Toast.LENGTH_LONG).show();
                } else {
                    if (urProfile == null) {
                        Toast.makeText(getApplicationContext(),"프로필을 삽입해주세요.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        registerUser(stRegisterID, stRegisterPW, stRegisterName, stRegisterAccount);
                    }
                }
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });
    }

    public void registerUser(final String email, String password, final String Name, final String Account) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String uid = task.getResult().getUser().getUid();
                            FirebaseStorage.getInstance().getReference().child("userImages").child(uid).putFile(urProfile)
                                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            Task<Uri> uriTask = FirebaseStorage.getInstance().getReference().child("userImages").child(uid).getDownloadUrl();
                                            while (!uriTask.isSuccessful());
                                            Uri downloadUrl = uriTask.getResult();
                                            String imageUrl = String.valueOf(downloadUrl);
                                            UserModel userModel = new UserModel();
                                            userModel.userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            userModel.userProfile = imageUrl;
                                            userModel.userID = email;
                                            userModel.userName = Name;
                                            userModel.userAccount = Account;

                                            FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    RegisterActivity.this.finish();
                                                }
                                            });
                                            Toast.makeText(RegisterActivity.this, "회원가입에 성공하였습니다.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "회원가입에 실패하였습니다.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK) {
            ivProfile.setImageURI(data.getData());
            urProfile = data.getData();
        }
    }
}

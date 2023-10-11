package com.example.tapc;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity {

    private LinearLayout llLogin;
    private EditText etLoginID;
    private EditText etLoginPW;
    private Button btRegister;
    private Button btLogin;
    private Intent itLogin;
    private Intent itRegister;
    private FirebaseAuth mAuth;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        llLogin = (LinearLayout) findViewById(R.id.al_llLogin);
        etLoginID = (EditText) findViewById(R.id.al_etLoginID);
        etLoginPW = (EditText) findViewById(R.id.al_etLoginPW);
        btRegister = (Button) findViewById(R.id.al_btRegister);
        btLogin = (Button) findViewById(R.id.al_btLogin);
        itLogin = new Intent(LoginActivity.this, MainActivity.class);
        itRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        String splash_background = mFirebaseRemoteConfig.getString("splash_background");
        llLogin.setBackgroundColor(Color.parseColor(splash_background));

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(itRegister);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stLoginID = etLoginID.getText().toString();
                String stLoginPW = etLoginPW.getText().toString();

                if (stLoginID.length() < 3 || stLoginPW.length() < 6) {
                    Toast.makeText(getApplicationContext(),"너무 짧습니다.",
                            Toast.LENGTH_LONG).show();
                } else {
                    login(stLoginID, stLoginPW);
                }
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    startActivity(itLogin);
                    LoginActivity.this.finish();
                } else {

                }
            }
        };
    }

    public void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "로그인에 성공하였습니다.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "로그인에 실패하였습니다.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(authStateListener);
    }
}
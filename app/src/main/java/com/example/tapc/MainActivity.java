package com.example.tapc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.tapc.fragment.RoomFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private Intent itCreate;
    private Intent itAccount;
    private Intent itLogout;
    private BottomNavigationView bnMainBar;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction().replace(R.id.am_flList, new RoomFragment()).commit();

        itCreate = new Intent(MainActivity.this, GenerateActivity.class);
        itAccount = new Intent(MainActivity.this, AccountActivity.class);
        itLogout = new Intent(MainActivity.this, LoginActivity.class);
        bnMainBar = (BottomNavigationView) findViewById(R.id.am_bnMainBar);
        mAuth = FirebaseAuth.getInstance();

        bnMainBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bar_home:
                        return true;
                    case R.id.bar_create:
                        startActivity(itCreate);
                        return true;
                    case R.id.bar_account:
                        startActivity(itAccount);
                        return true;
                }
                return false;
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                } else {
                    startActivity(itLogout);
                    MainActivity.this.finish();
                }
            }
        };
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

package com.itt.shippingapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.itt.shippingapp.Fragment.LoginFragment;
import com.itt.shippingapp.R;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (auth.getCurrentUser() != null) {
            if (auth.getCurrentUser().getEmail().equals("admin@email.com"))
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
            else if (auth.getCurrentUser().isEmailVerified()) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
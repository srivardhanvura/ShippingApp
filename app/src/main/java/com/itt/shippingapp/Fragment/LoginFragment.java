package com.itt.shippingapp.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itt.shippingapp.Activity.AdminActivity;
import com.itt.shippingapp.Activity.HomeActivity;
import com.itt.shippingapp.Activity.LoginActivity;
import com.itt.shippingapp.R;

import java.util.HashMap;

public class LoginFragment extends Fragment {

    EditText email, pass;
    Button signin, signup, google, forgot;
    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.email);
        pass = view.findViewById(R.id.pass);
        signin = view.findViewById(R.id.signin);
        signup = view.findViewById(R.id.signup);
        google = view.findViewById(R.id.google);
        forgot = view.findViewById(R.id.forgot);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        final GoogleSignInClient gsCleint = GoogleSignIn.getClient(getActivity(), gso);

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().trim().isEmpty() && !pass.getText().toString().trim().isEmpty()) {
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                        Toast.makeText(getActivity(), "Enter valid email", Toast.LENGTH_SHORT).show();
                    } else if (pass.getText().toString().trim().length() < 6) {
                        Toast.makeText(getActivity(), "Password too short", Toast.LENGTH_SHORT).show();
                    } else if (email.getText().toString().trim().equals("admin@email.com") && pass.getText().toString().equals("123456")) {
                        auth.signInWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            startActivity(new Intent(getActivity(), AdminActivity.class));
                                        }
                                    }
                                });
                    } else {
                        auth.signInWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim())
                                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            checkIfEmailVerified();
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(getActivity(), "Enter valid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.frame, new SignupFragment()).commit();
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.frame, new ForgotFragment()).commit();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = gsCleint.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(getActivity(), "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            String userID = auth.getCurrentUser().getUid();
                            HashMap<String, String> userData = new HashMap<>();


                            if (user.getDisplayName() != null && !user.getDisplayName().isEmpty())
                                userData.put("Name", user.getDisplayName());
                            else
                                userData.put("Name", "");

                            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
                                userData.put("Phone", user.getPhoneNumber());
                            else
                                userData.put("Phone", "");

                            if (user.getEmail() != null && !user.getEmail().isEmpty())
                                userData.put("Email", user.getEmail());
                            else
                                userData.put("Email", "");

                            if (user.getPhotoUrl() != null && !user.getPhotoUrl().toString().isEmpty())
                                userData.put("Photo", user.getPhotoUrl().toString());
                            else
                                userData.put("Photo", "");

                            db.child("Users").child(userID).setValue(userData);
                            Toast.makeText(getActivity(), "Google sign in success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getActivity(), HomeActivity.class));
                        } else {
                            Toast.makeText(getActivity(), "Google sign in failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            startActivity(new Intent(getActivity(), HomeActivity.class));
        } else {
            Toast.makeText(getActivity(), "Verify your email", Toast.LENGTH_SHORT).show();
            FirebaseAuth.getInstance().signOut();
        }
    }
}
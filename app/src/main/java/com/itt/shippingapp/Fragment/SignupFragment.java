package com.itt.shippingapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itt.shippingapp.R;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        final EditText email, pass, name, phone;
        Button signup = view.findViewById(R.id.signupSU);
        email = view.findViewById(R.id.emailSU);
        pass = view.findViewById(R.id.passSU);
        name = view.findViewById(R.id.nameSU);
        phone = view.findViewById(R.id.phoneSU);
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!email.getText().toString().trim().isEmpty() && !pass.getText().toString().trim().isEmpty() && !phone.getText().toString().trim().isEmpty() && !name.getText().toString().trim().isEmpty()) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches() && !Patterns.PHONE.matcher(phone.getText().toString().trim()).matches()) {
                        Toast.makeText(getActivity(), "Enter valid email and mobile number", Toast.LENGTH_SHORT).show();
                    } else if (pass.getText().toString().trim().length() < 6) {
                        Toast.makeText(getActivity(), "Password too short", Toast.LENGTH_SHORT).show();
                    } else {
                        auth.createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim()).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task1) {
                                            if (task1.isSuccessful()) {
                                                String userID = auth.getCurrentUser().getUid();
                                                HashMap<String, String> userData = new HashMap<>();
                                                userData.put("Name", name.getText().toString().trim());
                                                userData.put("Phone", phone.getText().toString().trim());
                                                userData.put("Email", email.getText().toString().trim());
                                                userData.put("Photo", "");
                                                db.child("Users").child(userID).setValue(userData);
                                                Toast.makeText(getActivity(), "A verification mail has been sent to your registered email. Verify your email and login", Toast.LENGTH_SHORT).show();
                                                getFragmentManager().beginTransaction().replace(R.id.frame, new LoginFragment()).commit();
                                            } else {
                                                Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getActivity(), "Some error occurred", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(getActivity(), new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), "createUserWithEmail:onFailure: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "Enter valid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;

    }
}
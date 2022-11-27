package com.itt.shippingapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itt.shippingapp.R;

public class TrackFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputLayout til;
        final EditText et;
        final Button btn;

        til = view.findViewById(R.id.trackTl);
        et = view.findViewById(R.id.trackET);
        btn = view.findViewById(R.id.trackBtn);

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                til.setError(null);
                final String text = et.getText().toString().trim();
                if (text.isEmpty())
                    til.setError("Enter tracking ID");
                else if (text.length() != 10)
                    til.setError("Tracking ID invalid");
                else {
                    db.child("Transaction").child(text).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.container, new SingleOrderFragment(text))
                                        .commit();
                            } else
                                til.setError("Tracking ID invalid");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
}
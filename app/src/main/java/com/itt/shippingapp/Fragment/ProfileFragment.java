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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itt.shippingapp.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final String uid = auth.getUid();
        final String[] name = new String[1];
        final String[] email = new String[1];
        final String[] phone = new String[1];
        final String[] picURL = new String[1];
        final boolean[] miss = {false};

        final TextView nameTxt = view.findViewById(R.id.pro_name);
        final TextView emailTxt = view.findViewById(R.id.pro_email);
        final TextView phoneTxt = view.findViewById(R.id.pro_num);
        final CircleImageView circle = view.findViewById(R.id.pic);
        final TextView missing = view.findViewById(R.id.missingTxt);
        final Button missingBtn = view.findViewById(R.id.missingBtn);

        db.child("Users").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                miss[0]=false;
                for (DataSnapshot data : snapshot.getChildren()) {
                    switch (data.getKey()) {
                        case "Name":
                            name[0] = data.getValue().toString();
                            if (name[0].equals(""))
                                miss[0] = true;
                            break;
                        case "Email":
                            email[0] = data.getValue().toString();
                            if (email[0].equals(""))
                                miss[0] = true;
                            break;
                        case "Phone":
                            phone[0] = data.getValue().toString();
                            if (phone[0].equals(""))
                                miss[0] = true;
                            break;
                        case "Photo":
                            picURL[0] = data.getValue().toString();
                            if (picURL[0].equals(""))
                                miss[0] = true;
                            break;
                    }
                }
                if (miss[0]) {
                    missing.setVisibility(View.VISIBLE);
                    missingBtn.setVisibility(View.VISIBLE);
                }
                else
                    missing.setVisibility(View.GONE);

                nameTxt.setText(name[0].trim());
                emailTxt.setText(email[0].trim());
                phoneTxt.setText(phone[0].trim());

                if (!picURL[0].equals("")) {
                    Picasso.get().load(picURL[0]).fit().centerCrop()
                            .error(R.drawable.dp)
                            .placeholder(R.drawable.dp)
                            .into(circle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        missingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment2(uid, name[0], email[0], phone[0], picURL[0])).commit();
            }
        });
    }
}
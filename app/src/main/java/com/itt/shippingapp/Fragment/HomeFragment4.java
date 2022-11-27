package com.itt.shippingapp.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itt.shippingapp.Activity.DistRangeActivity;
import com.itt.shippingapp.Activity.WeightRangeActivity;
import com.itt.shippingapp.R;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HomeFragment4 extends Fragment {

    double dist;
    int vol, weight;
    LatLng src, dst;
    String recEmail;

    HomeFragment4(double d, int v, int w, LatLng s, LatLng ds, String r) {
        dist = d == 0.0 ? 3000.0 : d;
        weight = w;
        vol = v;
        src = s;
        dst = ds;
        recEmail = r;
    }

    FirebaseAuth firebaseAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home4, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Set<String> keys = new HashSet<>();

        firebaseAuth = FirebaseAuth.getInstance();
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Transaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    keys.add(dataSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TextView sender = view.findViewById(R.id.emailSender);
        TextView receiver = view.findViewById(R.id.emailRec);
        TextView sendLoc = view.findViewById(R.id.senderLoc);
        TextView recLoc = view.findViewById(R.id.recLoc);
        TextView distTxt = view.findViewById(R.id.dist_range);
        TextView weightTxt = view.findViewById(R.id.weight);
        TextView volTxt = view.findViewById(R.id.vol_range);
        TextView costTxt = view.findViewById(R.id.cost_text);
        TextView details = view.findViewById(R.id.btn_home4);
        TextView distBtn = view.findViewById(R.id.btn2_home4);
        Button confirm = view.findViewById(R.id.btn3_home4);

        sender.setText("From: " + firebaseAuth.getCurrentUser().getEmail());
        receiver.setText("To: " + recEmail);
        sendLoc.setText("From: " + src);
        recLoc.setText("To: " + dst);
        distTxt.setText("Dist: " + dist);
        weightTxt.setText("Weight: " + weight);
        volTxt.setText("Vol: " + vol);

        int distCost = 0;
        int weiCost = 0;

        if (dist <= 200)
            distCost = (int) dist * 20;
        else if (dist <= 400)
            distCost = (int) dist * 16;
        else if (dist <= 700)
            distCost = (int) dist * 14;
        else
            distCost = (int) dist * 10;


        if (weight <= 10)
            weiCost = (int) weight * 15;
        else if (weight <= 20)
            weiCost = (int) weight * 12;
        else if (weight <= 30)
            weiCost = (int) weight * 10;
        else if (weight <= 40)
            weiCost = (int) weight * 8;
        else
            weiCost = (int) weight * 6;

        final int cost = distCost + weiCost;


        costTxt.setText("Cost:" + cost);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), WeightRangeActivity.class));
            }
        });

        distBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), DistRangeActivity.class));
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String trnID = randomAlphaNumeric(10);
                while (keys.contains(trnID))
                    trnID = randomAlphaNumeric(10);

                Date da = new Date();
                String date = da.getDate() + "/" + da.getMonth() + "/" + (da.getYear() + 1900);
                String time = da.getHours() + ":" + da.getMinutes();

                HashMap<String, String> userData = new HashMap<>();
                userData.put("Uid", firebaseAuth.getUid());
                userData.put("Sender", firebaseAuth.getCurrentUser().getEmail());
                userData.put("Receiver", recEmail);
                userData.put("Sender location", src.toString());
                userData.put("Receiver location", dst.toString());
                userData.put("Weight", String.valueOf(weight));
                userData.put("Status", "Not Delivered");
                userData.put("Cost", String.valueOf(cost));
                userData.put("Timestamp", date + "," + time);

                db.child("Transaction").child(trnID).setValue(userData);
                db.child("Previous").child(firebaseAuth.getUid()).child(trnID).setValue("Not Delivered");

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                transaction.replace(R.id.container, new HomeFragment5(trnID));
                transaction.commit();
            }
        });
    }

    private static final String ALPHA_NUMERIC_STRING = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}

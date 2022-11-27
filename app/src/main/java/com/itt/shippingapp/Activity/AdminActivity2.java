package com.itt.shippingapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itt.shippingapp.R;

import java.util.ArrayList;

public class AdminActivity2 extends AppCompatActivity {
    String trnId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin2);

        trnId = getIntent().getStringExtra("id");

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final FirebaseAuth auth = FirebaseAuth.getInstance();

        final Spinner spinner = findViewById(R.id.adm_spin);
        final TextView txt = findViewById(R.id.adm2);
        final Button btn = findViewById(R.id.adm_btn);
        txt.setText(trnId);

        final String[] uid = new String[1];
        final String[] cur = new String[1];
        ArrayList<String> list = new ArrayList<>();

        list.add("Delivered");
        list.add("Not Delivered");
        list.add("Shipped");
        list.add("Cancelled");

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        db.child("Transaction").child(trnId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                StringBuilder str = new StringBuilder();
                for (DataSnapshot data : snapshot.getChildren()) {
                    str.append(data.getKey()).append(":").append(data.getValue()).append("\n\n");

                    if (data.getKey().equals("Uid"))
                        uid[0] = data.getValue().toString();

                    else if (data.getKey().equals("Status"))
                        cur[0] = data.getValue().toString();
                }
                txt.setText(str.toString());
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getPosition(cur[0]));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sel = spinner.getSelectedItem().toString();
                if (!sel.equals(cur[0])) {
                    db.child("Transaction").child(trnId).child("Status").setValue(sel);
                    db.child("Previous").child(uid[0]).child(trnId).setValue(sel);
                }
            }
        });
    }
}
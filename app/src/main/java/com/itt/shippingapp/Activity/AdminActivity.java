package com.itt.shippingapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itt.shippingapp.R;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        Button logout = findViewById(R.id.adm_logout);
        final ListView listView = findViewById(R.id.adm_rec);
        SearchView searchView = findViewById(R.id.adm_search);
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final ArrayList<String> ids2 = new ArrayList<>();
        final ArrayList<String> status2 = new ArrayList<>();
        final ArrayList<String> ids = new ArrayList<>();
        final ArrayList<String> status = new ArrayList<>();
        final ArrayAdapter[] adapter = new ArrayAdapter[1];


        db.child("Transaction").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ids.clear();
                status.clear();
                ids2.clear();
                status2.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    ids.add(data.getKey());
                    ids2.add(data.getKey());
                    for (DataSnapshot d : data.getChildren()) {
                        if (d.getKey().equals("Status")) {
                            status.add(d.getValue().toString());
                            status2.add(d.getValue().toString());
                            break;
                        }
                    }
                }
                adapter[0] = new ArrayAdapter(AdminActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, ids) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText(ids.get(position));
                        text2.setText(status.get(position));
                        return view;
                    }
                };
                listView.setAdapter(adapter[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                ids2.clear();
                status2.clear();

                for (int i = 0; i < ids.size(); i++) {
                    if (ids.get(i).contains(query)) {
                        ids2.add(ids.get(i));
                        status2.add(status.get(i));
                    }
                }

                adapter[0] = new ArrayAdapter(AdminActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, ids2) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText(ids2.get(position));
                        text2.setText(status2.get(position));
                        return view;
                    }
                };
                listView.setAdapter(adapter[0]);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                ids2.clear();
                status2.clear();

                for (int i = 0; i < ids.size(); i++) {
                    if (ids.get(i).contains(newText)) {
                        ids2.add(ids.get(i));
                        status2.add(status.get(i));
                    }
                }

                adapter[0] = new ArrayAdapter(AdminActivity.this, android.R.layout.simple_list_item_2, android.R.id.text1, ids2) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText(ids2.get(position));
                        text2.setText(status2.get(position));
                        return view;
                    }
                };
                listView.setAdapter(adapter[0]);

                return false;
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(AdminActivity.this, LoginActivity.class));
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AdminActivity.this, AdminActivity2.class);
                intent.putExtra("id", ids2.get(position));
                startActivity(intent);
            }
        });
    }
}
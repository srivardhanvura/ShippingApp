package com.itt.shippingapp.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itt.shippingapp.Adapter.PreviousAdapter;
import com.itt.shippingapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PreviousFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_previous, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView recyclerView = view.findViewById(R.id.recycle);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        final HashMap<String, String> hash = new HashMap<>();
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayList<String> status = new ArrayList<>();
        final TextView no_text = view.findViewById(R.id.no_bookings);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        db.child("Previous").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                status.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    hash.put(data.getKey(), data.getValue().toString());
                }
                if (hash.size() == 0) {
                    no_text.setVisibility(View.VISIBLE);
                } else {
                    HashMap<String, String> temp = sortHash(hash);
                    Iterator hmIterator = temp.entrySet().iterator();
                    while (hmIterator.hasNext()) {
                        Map.Entry mapElement = (Map.Entry) hmIterator.next();
                        arrayList.add(mapElement.getKey().toString());
                        status.add(mapElement.getValue().toString());
                    }
//                    sort(arrayList, status);
                    PreviousAdapter adapter = new PreviousAdapter(arrayList, status, getActivity());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private HashMap<String, String> sortHash(HashMap<String, String> hash) {
        List<Map.Entry<String, String>> list =
                new LinkedList<Map.Entry<String, String>>(hash.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
            public int compare(Map.Entry<String, String> o1,
                               Map.Entry<String, String> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        HashMap<String, String> temp = new LinkedHashMap<String, String>();
        for (Map.Entry<String, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    private void sort(ArrayList<String> arrayList, ArrayList<String> status) {

        int i = 0, j = arrayList.size() - 1;

        while (i < j) {
            if (status.get(i).equals("Delivered")) {
                status.set(i, status.get(j));
                status.set(j, "Delivered");
                String temp = arrayList.get(i);
                arrayList.set(i, arrayList.get(j));
                arrayList.set(j, temp);
                i--;
                j--;
            }
            i++;
        }
    }
}
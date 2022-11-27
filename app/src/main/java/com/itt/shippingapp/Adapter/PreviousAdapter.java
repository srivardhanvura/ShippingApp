package com.itt.shippingapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.itt.shippingapp.Fragment.SingleOrderFragment;
import com.itt.shippingapp.R;

import java.util.ArrayList;

public class PreviousAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    ArrayList<String> arrayList;
    ArrayList<String> arrayList2;
    Context context;

    public PreviousAdapter(ArrayList<String> a, ArrayList<String> a2, Context c) {
        arrayList = a;
        arrayList2 = a2;
        context = c;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 1;
        else if (position == arrayList.size() - 1)
            return 3;
        return 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1)
            return new PreviousViewholder1(LayoutInflater.from(context).inflate(R.layout.single_previous1, parent, false));
        else if (viewType == 2)
            return new PreviousViewholder2(LayoutInflater.from(context).inflate(R.layout.single_previous2, parent, false));
        return new PreviousViewholder3(LayoutInflater.from(context).inflate(R.layout.single_previous3, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        if (holder.getItemViewType() == 1) {
            PreviousViewholder1 viewholder1 = (PreviousViewholder1) holder;
            viewholder1.textView1.setText(arrayList.get(position));
            viewholder1.textView2.setText(arrayList2.get(position));

            viewholder1.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new SingleOrderFragment(arrayList.get(position)))
                            .commit();
                }
            });
        } else if (holder.getItemViewType() == 2) {
            PreviousViewholder2 viewholder1 = (PreviousViewholder2) holder;
            viewholder1.textView1.setText(arrayList.get(position));
            viewholder1.textView2.setText(arrayList2.get(position));

            viewholder1.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new SingleOrderFragment(arrayList.get(position)))
                            .commit();
                }
            });
        } else {
            PreviousViewholder3 viewholder1 = (PreviousViewholder3) holder;
            viewholder1.textView1.setText(arrayList.get(position));
            viewholder1.textView2.setText(arrayList2.get(position));

            viewholder1.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new SingleOrderFragment(arrayList.get(position)))
                            .commit();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class PreviousViewholder1 extends RecyclerView.ViewHolder {

        TextView textView1;
        TextView textView2;
        RelativeLayout layout;

        public PreviousViewholder1(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.single_text_top1);
            textView2 = itemView.findViewById(R.id.single_text_top2);
            layout = itemView.findViewById(R.id.rel1);
        }
    }

    class PreviousViewholder2 extends RecyclerView.ViewHolder {

        TextView textView1;
        TextView textView2;
        RelativeLayout layout;

        public PreviousViewholder2(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.single_text1);
            textView2 = itemView.findViewById(R.id.single_text2);
            layout = itemView.findViewById(R.id.rel2);
        }
    }

    class PreviousViewholder3 extends RecyclerView.ViewHolder {

        TextView textView1;
        TextView textView2;
        RelativeLayout layout;

        public PreviousViewholder3(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.single_text_bot1);
            textView2 = itemView.findViewById(R.id.single_text_bot2);
            layout = itemView.findViewById(R.id.rel3);
        }
    }
}

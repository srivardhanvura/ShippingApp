package com.itt.shippingapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itt.shippingapp.R;

public class WeightRangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_range);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        TextView tv1 = findViewById(R.id.price5);
        TextView tv2 = findViewById(R.id.price6);
        TextView tv3 = findViewById(R.id.price7);
        TextView tv4 = findViewById(R.id.price8);
        TextView tv5 = findViewById(R.id.price9);
        TextView close = findViewById(R.id.weight_close);

        tv1.setText(tv1.getText().toString() + " 15/Kg");
        tv2.setText(tv2.getText().toString() + " 12/Kg");
        tv3.setText(tv3.getText().toString() + " 10/Kg");
        tv4.setText(tv4.getText().toString() + " 8/Kg");
        tv5.setText(tv5.getText().toString() + " 6/Kg");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
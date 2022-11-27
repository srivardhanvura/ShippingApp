package com.itt.shippingapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.itt.shippingapp.R;

public class DistRangeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dist_range);

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

        TextView tv1 = findViewById(R.id.price1);
        TextView tv2 = findViewById(R.id.price2);
        TextView tv3 = findViewById(R.id.price3);
        TextView tv4 = findViewById(R.id.price4);
        TextView close = findViewById(R.id.dist_close);

        tv1.setText(tv1.getText().toString() + " 20/KM");
        tv2.setText(tv2.getText().toString() + " 16/KM");
        tv3.setText(tv3.getText().toString() + " 14/KM");
        tv4.setText(tv4.getText().toString() + " 10/KM");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
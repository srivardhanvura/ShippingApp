package com.itt.shippingapp.Fragment;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.itt.shippingapp.R;
import com.xw.repo.BubbleSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;

public class HomeFragment3 extends Fragment {

    LatLng src, dst;

    HomeFragment3(LatLng l1, LatLng l2) {
        src = l1;
        dst = l2;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home3, container, false);
    }

    //    ProgressBar progressBar;
    SupportMapFragment supportMapFragment;
    BubbleSeekBar s1, s2, s3, s4;
    Button submit;
    String srcCity = "", dstCity = "";
    double exact;
    EditText rec;
    TextInputLayout textInputLayout;
    int weight = 0, height = 0, length = 0, width = 0;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        progressBar = view.findViewById(R.id.progress);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map3);

        try {
            getDistance();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        supportMapFragment.getMapAsync(callback);

        s1 = view.findViewById(R.id.weight_seek);
        s2 = view.findViewById(R.id.width_seek);
        s3 = view.findViewById(R.id.height_seek);
        s4 = view.findViewById(R.id.length_seek);
        submit = view.findViewById(R.id.submit_home3);
        rec = view.findViewById(R.id.recEmailEt);
        textInputLayout = view.findViewById(R.id.recEmail);

        s1.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                weight = progress;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        s2.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                width = progress;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        s3.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                height = progress;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });
        s4.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                length = progress;
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (width == 0 || weight == 0 || height == 0 || length == 0) {
                    Toast.makeText(getActivity(), "Measurements cannot be zero", Toast.LENGTH_SHORT).show();
                } else if (rec.getText().toString().trim().isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(rec.getText().toString().trim()).matches()) {
                    textInputLayout.setError("Enter a valid email address");
                } else if(FirebaseAuth.getInstance().getCurrentUser().getEmail().equals(rec.getText().toString().trim())){
                    textInputLayout.setError("Sender and receiver email cannot be same");
                }else {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                    transaction.replace(R.id.container, new HomeFragment4(exact, (height * width * length), weight, src, dst, rec.getText().toString().trim()));
                    transaction.commit();
                }
            }
        });
    }

    private void getDistance() throws JSONException {
        double lat1 = src.latitude, lat2 = dst.latitude, lon1 = src.longitude, lon2 = dst.longitude;

        JSONObject object1 = new JSONObject();
        object1.put("lat", lat1);
        object1.put("lng", lon1);
        JSONObject object2 = new JSONObject();
        object2.put("lat", lat2);
        object2.put("lng", lon2);

        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject2 = new JSONObject();
        jsonObject1.put("latLng", object1);
        jsonObject2.put("latLng", object2);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(jsonObject1);
        jsonArray.put(jsonObject2);

        JSONObject obj = new JSONObject();
        obj.put("locations", jsonArray);
        final String mRequestBody = obj.toString();

        String url = "https://www.mapquestapi.com/directions/v2/routematrix?key=sNu4PaEFcwGBuC6tESdimOYUiuX6eTzZ";

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
//                    progressBar.setVisibility(View.GONE);
                    JSONObject res = new JSONObject(response);
                    JSONArray dist = res.getJSONArray("distance");
                    exact = Double.parseDouble(dist.get(1).toString()) * 1.60934;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {

            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style));

            LatLng sydney = new LatLng(src.latitude, src.longitude);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Source").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
//            googleMap.addCircle(new CircleOptions().center(sydney).radius(1000.0).strokeColor(Color.BLACK).fillColor(0x30ff0000).strokeWidth(2));
            sydney = new LatLng(dst.latitude, dst.longitude);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//            googleMap.addCircle(new CircleOptions().center(sydney).radius(1000.0).strokeColor(Color.BLACK).fillColor(0x3000ff00).strokeWidth(2));

            PolylineOptions polylineOptions = new PolylineOptions().add(src);
            polylineOptions.add(dst);
            polylineOptions.color(Color.WHITE);
            polylineOptions.clickable(false);
            googleMap.addPolyline(polylineOptions);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(src);
            builder.include(dst);
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));


//            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);

//            double d = SphericalUtil.computeDistanceBetween(src, dst);
//            double h = SphericalUtil.computeHeading(src, dst);
//            LatLng p = SphericalUtil.computeOffset(src, d * 0.5, h);
//            double x = (1 - 0.5 * 0.5) * d * 0.5 / (2 * 0.5);
//            double r = (1 + 0.5 * 0.5) * d * 0.5 / (2 * 0.5);
//
//            LatLng c = SphericalUtil.computeOffset(p, x, h + 90.0);
//
//            PolylineOptions options = new PolylineOptions();
//            options.color(Color.WHITE);
//            List<PatternItem> pattern = Arrays.asList(new Dash(30), new Gap(20));
//
//            double h1 = SphericalUtil.computeHeading(c, src);
//            double h2 = SphericalUtil.computeHeading(c, dst);
//
//            int numpoints = 100;
//            double step = (h2 - h1) / numpoints;
//            for (int i = 0; i < numpoints; i++) {
//                LatLng pi = SphericalUtil.computeOffset(c, r, h1 + i * step);
//                options.add(pi);
//            }
//            googleMap.addPolyline(options.width(10).color(Color.MAGENTA).geodesic(false).pattern(pattern));
        }
    };
}

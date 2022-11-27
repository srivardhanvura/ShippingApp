package com.itt.shippingapp.Activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itt.shippingapp.Fragment.AboutFragment;
import com.itt.shippingapp.Fragment.HomeFragment;
import com.itt.shippingapp.Fragment.PreviousFragment;
import com.itt.shippingapp.Fragment.ProfileFragment;
import com.itt.shippingapp.Fragment.TrackFragment;
import com.itt.shippingapp.R;

import java.util.ArrayList;
import java.util.Arrays;

import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout;
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView;
import nl.psdcompany.duonavigationdrawer.views.DuoOptionView;
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle;

public class HomeActivity extends AppCompatActivity implements DuoMenuView.OnMenuClickListener {

    private MenuAdapter mMenuAdapter;
    private ViewHolder mViewHolder;
    FirebaseAuth auth;
    private ArrayList<String> mTitles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        auth = FirebaseAuth.getInstance();

        FirebaseDatabase db = FirebaseDatabase.getInstance();

        mTitles = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.menuOptions)));
        mViewHolder = new ViewHolder();

        handleToolbar();
        handleMenu();
        handleDrawer();

        // Show main fragment in container
        goToFragment(new HomeFragment());
        mMenuAdapter.setViewSelected(0);
        setTitle(mTitles.get(0));

        View headerView = mViewHolder.mDuoMenuView.getHeaderView();
        final TextView name = headerView.findViewById(R.id.duo_view_header_text_title);
        final TextView sub_name = headerView.findViewById(R.id.duo_view_header_text_sub_title);

        View footerView = mViewHolder.mDuoMenuView.getFooterView();
        TextView footerText = footerView.findViewById(R.id.duo_view_footer_text);
        footerText.setText("Logout");

        db.getReference().child("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.getKey().equalsIgnoreCase("name"))
                        name.setText("" + dataSnapshot.getValue());

                    else if (dataSnapshot.getKey().equalsIgnoreCase("email"))
                        sub_name.setText("" + dataSnapshot.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void handleToolbar() {
        setSupportActionBar(mViewHolder.mToolbar);
    }

    private void handleDrawer() {
        DuoDrawerToggle duoDrawerToggle = new DuoDrawerToggle(this,
                mViewHolder.mDuoDrawerLayout,
                mViewHolder.mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        mViewHolder.mDuoDrawerLayout.setDrawerListener(duoDrawerToggle);
        duoDrawerToggle.syncState();

    }

    private void handleMenu() {
        mMenuAdapter = new MenuAdapter(mTitles);
        mViewHolder.mDuoMenuView.setOnMenuClickListener(this);
        mViewHolder.mDuoMenuView.setAdapter(mMenuAdapter);
    }

    @Override
    public void onFooterClicked() {
        auth.signOut();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
    }

    @Override
    public void onHeaderClicked() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, new ProfileFragment()).commit();
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment).commit();
    }

    @Override
    public void onOptionClicked(int position, Object objectClicked) {

        setTitle(mTitles.get(position));

        mMenuAdapter.setViewSelected(position);

        switch (position) {
            case 0:
                goToFragment(new HomeFragment());
                break;
            case 1:
                goToFragment(new TrackFragment());
                break;
            case 2:
                goToFragment(new PreviousFragment());
                break;
            case 3:
                goToFragment(new ProfileFragment());
                break;
            case 4:
                goToFragment(new AboutFragment());
                break;
        }
        // Close the drawer
        mViewHolder.mDuoDrawerLayout.closeDrawer();
    }

    private class ViewHolder {
        private DuoDrawerLayout mDuoDrawerLayout;
        private DuoMenuView mDuoMenuView;
        private Toolbar mToolbar;

        ViewHolder() {
            mDuoDrawerLayout = (DuoDrawerLayout) findViewById(R.id.drawer);
            mDuoMenuView = (DuoMenuView) mDuoDrawerLayout.getMenuView();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
        }
    }

    static class MenuAdapter extends BaseAdapter {
        private ArrayList<String> mOptions;
        private ArrayList<DuoOptionView> mOptionViews = new ArrayList<>();

        MenuAdapter(ArrayList<String> options) {
            mOptions = options;
        }

        @Override
        public int getCount() {
            return mOptions.size();
        }

        @Override
        public Object getItem(int position) {
            return mOptions.get(position);
        }

        void setViewSelected(int position) {

            for (int i = 0; i < mOptionViews.size(); i++) {
                if (i == position) {
                    mOptionViews.get(i).setSelected(true);
                } else {
                    mOptionViews.get(i).setSelected(false);
                }
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final String option = mOptions.get(position);

            // Using the DuoOptionView to easily recreate the demo
            final DuoOptionView optionView;
            if (convertView == null) {
                optionView = new DuoOptionView(parent.getContext());
            } else {
                optionView = (DuoOptionView) convertView;
            }

            optionView.bind(option, null, null);
            mOptionViews.add(optionView);
            return optionView;
        }
    }

//    @Override
//    public void onBackPressed() {
//
//        int count = getSupportFragmentManager().getBackStackEntryCount();
//
//        if (count == 0) {
//            super.onBackPressed();
//            Intent a = new Intent(Intent.ACTION_MAIN);
//            a.addCategory(Intent.CATEGORY_HOME);
//            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(a);
//        } else {
//            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            transaction.replace(R.id.container, new HomeFragment()).commit();
//        }
//    }
}
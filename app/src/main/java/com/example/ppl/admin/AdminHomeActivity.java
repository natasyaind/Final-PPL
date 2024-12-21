package com.example.ppl.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.ppl.R;

public class AdminHomeActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private LinearLayout home_layout, analysis_layout, category_layout, profile_layout;
    private ImageView iv_home, iv_analysis, iv_category, iv_profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        fragmentManager = getSupportFragmentManager();
        initView();

        loadFragment(new AdminHomeFragment(), AdminHomeFragment.class.getSimpleName());
        setActiveMenu(home_layout);

        home_layout.setOnClickListener(this::onClick);
        analysis_layout.setOnClickListener(this::onClick);
        category_layout.setOnClickListener(this::onClick);
        profile_layout.setOnClickListener(this::onClick);

        fragmentManager.addOnBackStackChangedListener(this::updateActiveFragment);
    }

    private void onClick(View view) {
        if (view == home_layout) {
            loadFragment(new AdminHomeFragment(), AdminHomeFragment.class.getSimpleName());
            setActiveMenu(home_layout);
        } else if (view == analysis_layout) {
            loadFragment(new AdminAnalysisFragment(), AdminAnalysisFragment.class.getSimpleName());
            setActiveMenu(analysis_layout);
        } else if (view == category_layout) {
            loadFragment(new CategoryFragment(), CategoryFragment.class.getSimpleName());
            setActiveMenu(category_layout);
        } else if (view == profile_layout) {
            loadFragment(new ProfileFragment(), ProfileFragment.class.getSimpleName());
            setActiveMenu(profile_layout);
        }
    }

    private void loadFragment(Fragment fragment, String tag) {
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, tag)
                .addToBackStack(tag)
                .commit();
    }

    private void setActiveMenu(LinearLayout selectedLayout) {
        resetMenu();
        if (selectedLayout == home_layout) {
            iv_home.setAlpha(1.0f);
        } else if (selectedLayout == analysis_layout) {
            iv_analysis.setAlpha(1.0f);
        } else if (selectedLayout == category_layout) {
            iv_category.setAlpha(1.0f);
        } else if (selectedLayout == profile_layout) {
            iv_profile.setAlpha(1.0f);
        }
    }

    private void resetMenu() {
        iv_home.setAlpha(0.5f);
        iv_analysis.setAlpha(0.5f);
        iv_category.setAlpha(0.5f);
        iv_profile.setAlpha(0.5f);
    }

    private void updateActiveFragment() {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof AdminHomeFragment) {
            setActiveMenu(home_layout);
        } else if (currentFragment instanceof AdminAnalysisFragment) {
            setActiveMenu(analysis_layout);
        } else if (currentFragment instanceof CategoryFragment) {
            setActiveMenu(category_layout);
        } else if (currentFragment instanceof ProfileFragment) {
            setActiveMenu(profile_layout);
        }
    }

    private void initView() {
        iv_home = findViewById(R.id.iv_home);
        iv_analysis = findViewById(R.id.iv_analysis);
        iv_category = findViewById(R.id.iv_category);
        iv_profile = findViewById(R.id.iv_profile);
        home_layout = findViewById(R.id.home_layout);
        analysis_layout = findViewById(R.id.analysis_layout);
        category_layout = findViewById(R.id.category_layout);
        profile_layout = findViewById(R.id.profile_layout);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frame_layout);
        if (currentFragment instanceof AdminHomeFragment) {
        } else {
            super.onBackPressed();
        }
    }

}

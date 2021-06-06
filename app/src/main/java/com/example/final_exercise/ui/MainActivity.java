package com.example.final_exercise.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.final_exercise.R;
import com.example.final_exercise.databinding.ActivityMainBinding;
import com.example.final_exercise.ui.auth.ProfileFragment;
import com.example.final_exercise.ui.tracking.TrackingFragment;
import com.example.final_exercise.ui.todo.TodoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private BottomNavigationView navigationView;
    private ViewPagerAdapter adapter;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navigationView = findViewById(R.id.navigation);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),
                2);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int
                    positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        navigationView.getMenu().findItem(R.id.todos).setChecked(true);
                        break;
                    case 1:
                        navigationView.getMenu().findItem(R.id.profile).setChecked(true);
                        break;
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.todos:
                        binding.viewPager.setCurrentItem(0);
                        break;
                    case R.id.profile:
                        binding.viewPager.setCurrentItem(1);
                        break;
                }
                return true;
            }
        });
        contextOfApplication = getApplicationContext();
    }

    public class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final int pageNum;

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
            this.pageNum = behavior;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TodoFragment();
//                case 1:
//                    return new TrackingFragment();
                case 1:
                    return new ProfileFragment();
                default:
                    return new TodoFragment();
            }
        }

        @Override
        public int getCount() {
            return pageNum;
        }
    }
    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }
}
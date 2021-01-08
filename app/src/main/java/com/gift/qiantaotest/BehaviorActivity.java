package com.gift.qiantaotest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BehaviorActivity extends AppCompatActivity {
    private TabLayoutMediator mediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behavior);
        ViewPager2 vp = findViewById(R.id.nest_content);
        vp.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return MusicFragment.newInstance();
            }

            @Override
            public int getItemCount() {
                return 4;
            }
        });
        TabLayout tlLayout = findViewById(R.id.tl_layout);
        String[] title = {"热门", "专辑", "视频", "咨询"};
        mediator = new TabLayoutMediator(tlLayout, vp, (tab, position) -> {
            tab.setText(title[position]);
        });

        for (int i = 0; i < 4; i++) {
            TabLayout.Tab tab = tlLayout.newTab();
            tlLayout.addTab(tab);
        }

        tlLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mediator.attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mediator.detach();
        super.onDestroy();
    }
}
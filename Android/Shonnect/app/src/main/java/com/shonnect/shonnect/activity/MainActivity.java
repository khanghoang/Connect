package com.shonnect.shonnect.activity;

import com.shonnect.shonnect.R;
import com.shonnect.shonnect.fragment.ConversationListFragment;
import com.shonnect.shonnect.fragment.ShopListFragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MainActivity extends BaseInjectedActivity {

    private TabLayout tabLayout;
    private ViewPager vpContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        vpContent = (ViewPager) findViewById(R.id.vp_content);
        vpContent.setAdapter(new TabAdapter(getSupportFragmentManager()));
        tabLayout.addTab(tabLayout.newTab().setText("Shops"));
        tabLayout.addTab(tabLayout.newTab().setText("Conversation"));
        tabLayout.setupWithViewPager(vpContent);
        vpContent.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private static class TabAdapter extends FragmentPagerAdapter {


        public TabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new ShopListFragment();
            } else {
                return new ConversationListFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Shops";
                case 1:
                    return "Conversation";
            }
            return null;
        }
    }
}

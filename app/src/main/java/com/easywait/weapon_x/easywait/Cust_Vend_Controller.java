package com.easywait.weapon_x.easywait;

import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

public class Cust_Vend_Controller extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cust_vend);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setTabTextColors( R.color.tab_color_when_not_selected , Color.BLACK );

        tabLayout.getTabAt( 0 ).setIcon( R.drawable.cust );
        tabLayout.getTabAt( 1 ).setIcon( R.drawable.vend );
        tabLayout.getTabAt( 2 ).setIcon( R.drawable.profile );

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    Customer cust = new Customer();
                    return cust;
                case 1:
                    Vendor vend = new Vendor();
                    return vend;
                case 2:
                    Profile prof = new Profile();
                    return prof;

            }

            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {

                case 0:
                    return "CUSTOMER";
                case 1:
                    return "VENDOR";
                case 2:
                    return "PROFILE";

            }

            return null;

        }

    }

}

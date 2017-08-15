package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

        mViewPager.setOffscreenPageLimit( 3 );

        String operating_as = getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE).
                getString( "operating_as" , null );

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setTabTextColors( Color.LTGRAY , Color.BLACK );

        tabLayout.getTabAt( 0 ).setIcon( R.drawable.cust );
        tabLayout.getTabAt( 1 ).setIcon( R.drawable.vend );
        tabLayout.getTabAt( 2 ).setIcon( R.drawable.profile );

        if ( operating_as != null ) {

            if ( operating_as.equals( "customer" ) ) {

                TabLayout.Tab tab = tabLayout.getTabAt( 0 );

                tab.select();

            } else {

                TabLayout.Tab tab = tabLayout.getTabAt( 1 );

                tab.select();

            }

        }

    }

    private class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {

                case 0:
                    return new Customer();

                case 1:
                    return new Root_Fragment_Vendor();

                case 2:
                    return new Profile();

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

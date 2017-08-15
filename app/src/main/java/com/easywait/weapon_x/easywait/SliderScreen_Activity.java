package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.easywait.weapon_x.easywait.SignUp_Activity.MyPreferences;

public class SliderScreen_Activity extends AppCompatActivity {

    private ViewPager viewPager;

    private LinearLayout dotsLayout;

    private int[] layouts;

    private Button btnSkip, btnNext;

    private com.easywait.weapon_x.easywait.PreferenceManager prefManager;

    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView( R.layout.activity_slider_screen );

        View view = findViewById(R.id.slider_screen);

        view.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        // layouts of all welcome sliders
        layouts = new int[]{
                R.layout.slider_screen_1,
                R.layout.slider_screen_2,
                R.layout.slider_screen_3,
                R.layout.slider_screen_4};

        // adding bottom dots
        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();

        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.setOffscreenPageLimit( 4 );

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(1);
                if (current < layouts.length) {
                    // move to next screen
                    ((ViewPager) findViewById(R.id.view_pager)).setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });

    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {

        return viewPager.getCurrentItem() + i;

    }

    private void launchHomeScreen() {

        Intent intent = new Intent();

        intent.putExtra( "activity_to_be_called" , "cust_vend" );

        setResult( 1 , intent );

        finish();

    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            addBottomDots(position);

            if (position == layouts.length - 1) {

                // last page. make button text to CONTINUE
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);

            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            //Window w = getWindow(); // in Activity's onCreate() for instance
            // w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    /**
     * View pager adapter
     */
    private class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            if ( position == layouts.length - 2 ) {

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        view.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

                    }

                });

                Button vendor_button = (Button) view.findViewById(R.id.vendor_button);
                Button customer_button = (Button) view.findViewById(R.id.customer_button);

                name = (EditText) view.findViewById( R.id.user_name );

                name.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                        if ( i == EditorInfo.IME_ACTION_DONE ) {

                            SharedPreferences sharedpreferences = getSharedPreferences( MyPreferences , Context.MODE_APPEND );
                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString( "user_name" , name.getText().toString().trim() );
                            editor.apply();

                            view.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

                        }

                        return false;

                    }

                });

                vendor_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Toast toast = Toast.makeText( SliderScreen_Activity.this , "Operating as Vendor!" , Toast.LENGTH_SHORT );
                        toast.setGravity( Gravity.CENTER , 0 , 0 );
                        toast.show();

                        SharedPreferences sharedpreferences = getSharedPreferences( MyPreferences , Context.MODE_APPEND );
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString( "operating_as" , "vendor" );

                        if ( ! TextUtils.isEmpty( name.getText().toString().trim() )  )

                            editor.putString( "user_name" , name.getText().toString().trim() );

                        editor.apply();

                        ( ( ViewPager ) findViewById( R.id.view_pager ) ).setCurrentItem( getItem( 1 ) );

                    }

                });

                customer_button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Toast toast = Toast.makeText( SliderScreen_Activity.this , "Operating as Customer!" , Toast.LENGTH_SHORT );
                        toast.setGravity( Gravity.CENTER , 0 , 0 );
                        toast.show();

                        SharedPreferences sharedpreferences = getSharedPreferences( MyPreferences , Context.MODE_APPEND );
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString( "operating_as" , "customer" );

                        if ( ! TextUtils.isEmpty( name.getText().toString().trim() )  )

                            editor.putString( "user_name" , name.getText().toString().trim() );

                        editor.apply();

                        ( ( ViewPager ) findViewById( R.id.view_pager ) ).setCurrentItem( getItem( 1 ) );

                    }

                });

            } else if ( position == layouts.length - 1 ) {

                Button sign_in = (Button) view.findViewById(R.id.sign_in_btn);
                Button sign_up = (Button) view.findViewById(R.id.sign_up_btn);

                sign_in.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent();

                        intent.putExtra( "activity_to_be_called" , "sign_in" );

                        setResult( 1 , intent );

                        finish();

                    }

                });

                sign_up.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent();

                        intent.putExtra( "activity_to_be_called" , "sign_up" );

                        setResult( 1 , intent );

                        finish();

                    }

                });

            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Intent intent = new Intent( SliderScreen_Activity.this , Cust_Vend_Controller.class );
//
//        startActivity( intent );
//
//        finish();
//
//    }

}
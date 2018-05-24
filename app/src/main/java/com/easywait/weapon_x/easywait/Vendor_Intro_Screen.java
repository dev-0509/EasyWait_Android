package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import static android.content.ContentValues.TAG;
import static com.easywait.weapon_x.easywait.Globals.isSignedIn;

public class Vendor_Intro_Screen extends android.support.v4.app.Fragment {

    View rootView;

    Toolbar toolbar;

    private Snackbar snackbar = null;

    private boolean is_intro_fragment = false;

    private String check_for_vendor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView( inflater , container , savedInstanceState );

        rootView = inflater.inflate(R.layout.slider_screen_1, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

            }

        });

        check_for_vendor = getContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE).
                getString( "operating_as" , null );

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        if ( check_for_vendor != null && check_for_vendor.equals( "vendor" ) )

            toolbar.setVisibility( View.GONE );

        is_intro_fragment = true;

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        fetchToken();

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 1 && is_intro_fragment) {

                    Log.d( TAG , "called!" );

                    toolbar.setVisibility( View.GONE );

                    if (snackbar != null)

                        if (isSignedIn)

                            snackbar = null;

                        else

                            snackbar.show();

                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

                if (snackbar != null)

                    snackbar.dismiss();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });

    }

    private void fetchToken() {

        final SharedPreferences shared = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        String token = shared.getString("token", null);

        if( token == null ) {

            snackbar = Snackbar.make(rootView, "New Vendor?", Snackbar.LENGTH_INDEFINITE)
                .setAction("SIGN UP!", new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    snackbar.dismiss();

                    snackbar = null;

                    Intent intent = new Intent(getActivity(), SignUp_Activity.class);

                    startActivity(intent);

                }

            });

            if ( ! ( check_for_vendor == null ) && check_for_vendor.equals( "vendor" ) )

                    snackbar.setActionTextColor(getResources().getColor(R.color.snackbar)).show();

                else

                    snackbar.setActionTextColor(getResources().getColor(R.color.snackbar));

        }

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        is_intro_fragment = false;

    }

}

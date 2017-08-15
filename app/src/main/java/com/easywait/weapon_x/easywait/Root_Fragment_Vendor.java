package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Root_Fragment_Vendor extends Fragment {

    private String user_email, user_password;

    private View rootView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

		super.onCreateView(inflater , container , savedInstanceState);

        rootView = inflater.inflate(R.layout.root_fragment_vendor, container, false);

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        user_email = getContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                .getString( "email" , null );

        user_password = getContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                .getString( "password" , null );

        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.root_frame_vendor, new Vendor());

        transaction.commit();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        String new_user_email, new_user_password;

        if ( isVisibleToUser && rootView != null ) {

            new_user_email = getContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                    .getString( "email" , null );

            new_user_password = getContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                    .getString( "password" , null );

            if ( new_user_email == null && new_user_password == null )

                return;

            if ( ! ( new_user_email.equals( user_email ) && new_user_password.equals( new_user_password ) ) ) {

                user_email = new_user_email;
                user_password = new_user_password;

                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction();

                transaction.replace(R.id.root_frame_vendor, new Vendor());

                transaction.commit();

            }

        }

    }

}

package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import static com.easywait.weapon_x.easywait.R.layout.activity_profile;

public class Profile extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(activity_profile, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

            }

        });

        Button sign_in_button = (Button) rootView.findViewById(R.id.sign_in);
        Button sign_up_button = (Button) rootView.findViewById(R.id.sign_up);

        sign_in_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent( getActivity() , SignIn_Activity.class );

                startActivity( intent );

            }

        });

        sign_up_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent( getActivity() , SignUp.class );

                startActivity( intent );

            }

        });

        return rootView;

    }

}

package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static com.easywait.weapon_x.easywait.Globals.isSignedIn;

public class SignIn_Activity extends AppCompatActivity {

    private EditText email;

    private EditText password;

    String user_email, user_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        email = ( EditText ) findViewById( R.id.email );
        password = ( EditText ) findViewById( R.id.password );

        Button sign_in_button = (Button) findViewById(R.id.sign_in_button);

        fetchCredentials();

        if ( user_email != null && user_password != null ) {

            email.setText( user_email );
            password.setText( user_password );

        }

        sign_in_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if( TextUtils.isEmpty( email.getText().toString().trim() ) ) {

                    Toast.makeText( getApplicationContext() , "Missed your Email !" , Toast.LENGTH_SHORT).show();

                    return;

                }

                if( TextUtils.isEmpty( password.getText().toString().trim() ) ) {

                    Toast.makeText( getApplicationContext() , "Missed your Password !" , Toast.LENGTH_SHORT).show();

                    return;

                }

                isSignedIn = true;

                new SignIn_Method().signIn( email.getText().toString().trim() ,
                                            password.getText().toString().trim() ,
                                            getApplicationContext() );

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                finish();

            }

        });

    }

    private void fetchCredentials() {

        user_email = SignIn_Activity.this.getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                .getString( "email" , null );

        user_password = SignIn_Activity.this.getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                .getString( "password" , null );

    }

}

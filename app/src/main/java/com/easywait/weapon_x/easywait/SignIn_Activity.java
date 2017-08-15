package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.easywait.weapon_x.easywait.Globals.isSignedIn;
import static com.easywait.weapon_x.easywait.Globals.server;
import static com.easywait.weapon_x.easywait.SignUp_Activity.MyPreferences;

public class SignIn_Activity extends AppCompatActivity {

    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    private EditText email;
    private EditText password;

    private String user_email, user_password;
    private String access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon( R.drawable.ic_account_circle_black_24dp );
        toolbar.setTitle( R.string.signin );

        email = (EditText) findViewById( R.id.email );
        password = ( EditText ) findViewById( R.id.password );

        Button sign_in_button = (Button) findViewById(R.id.sign_in_button);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

        fetchCredentials();

        if ( user_email != null && user_password != null ) {

            email.setText( user_email );
            password.setText( user_password );

        }

        sign_in_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if( TextUtils.isEmpty( email.getText().toString().trim() ) ) {

                    Toast.makeText(getApplicationContext(), "Missed your Email !", Toast.LENGTH_SHORT).show();

                    return;

                }

                if( TextUtils.isEmpty( password.getText().toString().trim() ) ) {

                    Toast.makeText( getApplicationContext() , "Missed your Password !" , Toast.LENGTH_SHORT).show();

                    return;

                }

                isSignedIn = true;

                signIn();

            }

        });

    }

    private void fetchCredentials() {

        user_email = SignIn_Activity.this.getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                .getString( "email" , null );

        user_password = SignIn_Activity.this.getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE)
                .getString( "password" , null );

    }

    private void signIn() {

        String sign_in_url = server + "api/signin";

        StringRequest stringRequest = new StringRequest( Request.Method.POST , sign_in_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject json = new JSONObject( response );

                            access_token = json.getString( "token" );

                            saveUserCredentials( email.getText().toString().trim() ,
                                    password.getText().toString().trim() );

                            setResult( 2 );

                            finish();

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if ( error.networkResponse.statusCode == 401 )

                            Toast.makeText( SignIn_Activity.this , "Invalid Credentials" , Toast.LENGTH_LONG).show();

                        access_token = null;

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put( KEY_EMAIL , email.getText().toString().trim() );
                params.put( KEY_PASSWORD , password.getText().toString().trim() );

                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );

    }

    private void saveUserCredentials( String email , String password ) {

        SharedPreferences sharedpreferences = getSharedPreferences( MyPreferences , Context.MODE_APPEND );
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString( "token" , access_token );
        editor.putString( "email" , email );
        editor.putString( "password" , password );
        editor.apply();

        Toast.makeText( SignIn_Activity.this , "Welcome Back!" , Toast.LENGTH_SHORT ).show();

    }

}

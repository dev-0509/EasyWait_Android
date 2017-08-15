package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
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

import static com.easywait.weapon_x.easywait.Globals.server;

public class SignUp_Activity extends AppCompatActivity {

    public static final String MyPreferences = "MyPrefs";

    private static final String KEY_USERNAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD= "password";

    private EditText name;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon( R.drawable.ic_group_add_black_24dp );

        name = (EditText) findViewById( R.id.name );
        email = ( EditText ) findViewById( R.id.email );
        password = ( EditText ) findViewById( R.id.password );

        Button sign_up_button = (Button) findViewById(R.id.sign_up_button);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(email.getWindowToken(), 0);

        sign_up_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                registerNewUser( name.getText().toString().trim() ,
                        email.getText().toString().trim() ,
                        password.getText().toString().trim() );

            }

        });

    }

    private void registerNewUser( final String name , final String email , final String password ) {

        String sign_up_url = server + "api/signup";

        if( TextUtils.isEmpty( name ) ) {

            Toast toast = Toast.makeText(this, "Missed your Name!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER , 0 , 0 );
            toast.show();

            return;

        }

        if( TextUtils.isEmpty( email ) ) {

            Toast toast = Toast.makeText(this, "Missed your Email!", Toast.LENGTH_SHORT);
            toast.setGravity( Gravity.CENTER , 0 , 0 );
            toast.show();

            return;

        }

        if( TextUtils.isEmpty( password ) ) {

            Toast toast = Toast.makeText(this, "Missed your Password!", Toast.LENGTH_SHORT);
            toast.setGravity( Gravity.CENTER , 0 , 0 );
            toast.show();

            return;

        }

        StringRequest stringRequest = new StringRequest( Request.Method.POST , sign_up_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject json = new JSONObject( response );

                            SignUp_Activity.this.saveUserCredentials( json );

                            Toast.makeText( SignUp_Activity.this, "Welcome to EasyWait!", Toast.LENGTH_LONG).show();

                            setResult( 2 );

                            finish();

                        }

                        catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        
                        if ( error.networkResponse.statusCode == 401 )

                            Toast.makeText( SignUp_Activity.this , "Already Registered!" , Toast.LENGTH_SHORT ).show();

                        else {

                            Toast toast = Toast.makeText(SignUp_Activity.this, "Registration failed! Please try later...", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();

                        }

                    }

                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put( KEY_USERNAME, name );
                params.put( KEY_EMAIL, email );
                params.put( KEY_PASSWORD, password );

                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this );
        requestQueue.add( stringRequest );

    }

    private void saveUserCredentials( JSONObject json ) {

        SharedPreferences sharedpreferences = getSharedPreferences( MyPreferences , Context.MODE_APPEND );
        SharedPreferences.Editor editor = sharedpreferences.edit();

        try {

            editor.putString( "token" , json.getString( "token" ) );
            editor.putString( "email" , email.getText().toString().trim() );
            editor.putString( "password" , password.getText().toString().trim() );

        } catch ( Exception e ) {

            e.printStackTrace();

        }

        editor.apply();

    }

}

package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.SharedPreferences;
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
import static com.easywait.weapon_x.easywait.SignUp.MyPreferences;

class SignIn_Method {

    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";

    private String access_token;

    boolean signIn(final String email, final String password, final Context context) {

        String sign_in_url = server + "api/signin";

        StringRequest stringRequest = new StringRequest( Request.Method.POST , sign_in_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject json = new JSONObject( response );

                            access_token = json.getString( "token" );

                            saveUserCredentials( context , email , password );

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        access_token = null;

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put( KEY_EMAIL , email );
                params.put( KEY_PASSWORD , password );

                return params;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( context );
        requestQueue.add( stringRequest );

        return true;

    }

    private void saveUserCredentials( Context context , String email , String password ) {

        SharedPreferences sharedpreferences = context.getSharedPreferences( MyPreferences , Context.MODE_APPEND );
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString( "token" , access_token );
        editor.putString( "email" , email );
        editor.putString( "password" , password );
        editor.apply();

        Toast.makeText( context , "Welcome Back!" , Toast.LENGTH_SHORT ).show();

    }

}

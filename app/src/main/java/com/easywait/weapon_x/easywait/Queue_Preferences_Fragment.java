package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.easywait.weapon_x.easywait.Globals.server;

public class Queue_Preferences_Fragment extends Fragment {

    private static final String KEY_INITIAL_FREE_SLOTS = "initial_free_slots";
    private static final String KEY_RECURRING_FREE_SLOT = "recurring_free_slot";

    private EditText initial_slot_no, recurring_slot_no;

    private Button set;

    private String token;

    private String queue_id;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_queue_preferences, container, false);

        initial_slot_no = (EditText) rootView.findViewById( R.id.initial_slot_number );

        recurring_slot_no = (EditText) rootView.findViewById( R.id.recurring_slot_number );

        set = (Button) rootView.findViewById( R.id.set );

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        queue_id = getArguments().getString( "queue_id" ).substring( 0 , 2 );

        fetchToken();

        getQueuePreferences();

        set.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty( initial_slot_no.getText().toString().trim() ) ||
                        TextUtils.isEmpty( recurring_slot_no.getText().toString().trim() ))

                    Toast.makeText( getContext() , "Please specify the number of Slots!", Toast.LENGTH_SHORT).show();

                else

                    setQueuePreferences();

            }

        });

    }

    private void fetchToken() {

        token = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE).getString( "token" , null );

    }

    private void getQueuePreferences() {

        String preferences_url = server + "api/queue/" + queue_id + "/preferences";

        StringRequest stringRequest = new StringRequest(Request.Method.GET , preferences_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            initial_slot_no.setText( object.getString( "initial_free_slots" ) );

                            recurring_slot_no.setText( object.getString( "recurring_free_slot" ) );

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }) {
            @Override
            public Map<String, String> getHeaders () {

                Map<String, String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

    private void setQueuePreferences() {

        String preferences_url = server + "api/queue/" + queue_id + "/preferences";

        StringRequest stringRequest = new StringRequest(Request.Method.POST , preferences_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Toast.makeText( getContext() , "Preferences Successfully Set!", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put( KEY_INITIAL_FREE_SLOTS , initial_slot_no.getText().toString().trim() );
                params.put( KEY_RECURRING_FREE_SLOT , recurring_slot_no.getText().toString().trim() );

                return params;

            }
            @Override
            public Map<String, String> getHeaders () {

                Map<String, String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

}

package com.easywait.weapon_x.easywait;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
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

class Appointment_Handler {

    private String token;

    private static final String KEY_ACTION = "action";
    private static final String KEY_REFERENCE = "reference";
    private static final String KEY_POSITION = "position";

    void appointmentHandler(final String action , final String queue_id , final String reference , final String position , final Context context , final Activity activity) {

        fetchToken( context );

        String appointments_url = server + "api/queue/" + queue_id + "/appointment";

        StringRequest stringRequest = new StringRequest( Request.Method.POST , appointments_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            if ( action.equals( "book" ) ) {

                                JSONObject object = new JSONObject( response );

                                Toast.makeText( context , "Position " + object.getString( "position" ) +
                                                " booked in Queue " + queue_id , Toast.LENGTH_LONG ).show();

                            } else {

                                Toast.makeText( context , "Appointment for Position " + position +
                                                " cancelled in Queue " + queue_id , Toast.LENGTH_LONG ).show();

                            }

                            new Appointment_Handler().fetchAppointments( queue_id , null ,
                                    context , activity );

                        } catch ( Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if ( action.equals( "book" ) )

                            Toast.makeText( context , "Booking Failed! Please try later..." , Toast.LENGTH_LONG).show();

                        else

                            Toast.makeText( context , "Cancellation aborted, please try later..." , Toast.LENGTH_LONG).show();

                    }

                }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put(KEY_ACTION , action);

                if ( action.equals( "book" ) )

                    params.put(KEY_REFERENCE, reference);

                else

                    params.put(KEY_POSITION, position);

                return params;

            }
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( context );
        requestQueue.add( stringRequest );

    }

    void fetchAppointments(final String queue_id , final ViewGroup container , final Context context, final Activity activity) {

        fetchToken( context );

        String appointments_url = server + "api/queue/" + queue_id + "/appointment" ;

        StringRequest stringRequest = new StringRequest( Request.Method.GET , appointments_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            final JSONObject object = new JSONObject( response );

                            if ( context instanceof Cust_Vend_Controller ) {

                                Snackbar.make(container, "Active Appointments", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("VIEW", new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {

                                                Intent intent = new Intent(activity, Appointments.class);

                                                intent.putExtra("appointments_list", object.toString());
                                                intent.putExtra( "queue_id" , queue_id );

                                                activity.startActivity(intent);

                                            }

                                        }).setActionTextColor(activity.getResources().getColor(R.color.snackbar)).show();

                            } else if ( container == null ) {

                                Intent intent = new Intent(activity, Appointments.class);

                                intent.putExtra("appointments_list", object.toString());

                                intent.putExtra( "queue_id" , queue_id );

                                activity.startActivity(intent);

                            }

                        } catch ( Exception e ) {

                            if ( container == null ) {

                                Intent intent = new Intent(activity, Appointments.class);

                                intent.putExtra("appointments_list", "NA");

                                activity.startActivity(intent);

                            } else

                                Snackbar.make( container , "No Active Appointments" , Snackbar.LENGTH_LONG )
                                        .setAction( "Action" , null ).setDuration( Snackbar.LENGTH_INDEFINITE ).show();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Snackbar.make(container, "Appointment Administration?", Snackbar.LENGTH_INDEFINITE)
                                .setAction("SIGN IN", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        Intent intent = new Intent(activity, SignIn_Activity.class);

                                        activity.startActivity(intent);

                                    }

                                }).setActionTextColor(activity.getResources().getColor(R.color.snackbar)).show();

                    }

                }) {
            @Override
            public Map<String, String> getHeaders() {

                Map<String, String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put( "Authorization" , auth );

                return headers;

            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue( context );
        requestQueue.add( stringRequest );

    }

    private void fetchToken(Context context) {

        SharedPreferences shared = context.getSharedPreferences( "MyPrefs" , Context.MODE_PRIVATE);

        token = shared.getString( "token" , null );

    }

}

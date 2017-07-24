package com.easywait.weapon_x.easywait;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.SharedPreferences;

import org.json.JSONObject;

import static com.easywait.weapon_x.easywait.Globals.server;
import static com.easywait.weapon_x.easywait.R.layout.activity_customer;

public class Customer extends Fragment {

    private String q_details_url = server + "api/queue/";

    private FloatingActionButton search_queue;

    private TextView queue_details;

    private ImageView queue_image;

    private Button book_button;

    private EditText queue_id;

    Snackbar snackbar;

    String token = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(activity_customer, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

            }

        });

        search_queue = ( FloatingActionButton ) rootView.findViewById( R.id.search_q_button );

        queue_details = ( TextView ) rootView.findViewById( R.id.q_details );

        queue_image = ( ImageView ) rootView.findViewById( R.id.queue_image );
        queue_image.setVisibility( View.INVISIBLE );

        book_button = ( Button ) rootView.findViewById( R.id.book_button );
        book_button.setVisibility( View.INVISIBLE );

        queue_id = ( EditText ) rootView.findViewById( R.id.queue_id );

        fetchToken( container );

        setQueueDetails( container );

        book_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final SharedPreferences shared = getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

                token = shared.getString( "token" , null );

                if ( token == null )

                    Toast.makeText(getContext(), "Please Login to continue", Toast.LENGTH_SHORT).show();

                else {

                    Intent intent = new Intent(getActivity(), Book_Appointment.class);

                    intent.putExtra("queue_id", queue_id.getText().toString().trim());

                    startActivity(intent);

                }

            }

        });

        return rootView;

    }

    private void fetchToken(ViewGroup container) {

        final SharedPreferences shared = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        String email, password;

        token = shared.getString( "token" , null );
        email = shared.getString( "email" , null );
        password = shared.getString( "password" , null );

        if( token == null ) {

            snackbar = Snackbar.make( container , "New to EasyWait?" , Snackbar.LENGTH_INDEFINITE )
                    .setAction( "SIGN UP!" , new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent( getActivity() , SignUp.class );

                            startActivity( intent );

                        }

                    });

            snackbar.setActionTextColor( getResources().getColor( R.color.snackbar ) ).show();

        } else {

            if( new SignIn_Method().signIn(email, password, getContext()) )

                token = shared.getString( "token" , null );

        }

    }

    private void setQueueDetails(final ViewGroup container) {

        search_queue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if( snackbar != null )

                    snackbar.dismiss();

                InputMethodManager imm = ( InputMethodManager ) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
                imm.hideSoftInputFromWindow( queue_id.getWindowToken() , 0 );

                queue_details.setText( "" );

                book_button.setVisibility( View.INVISIBLE );
                queue_details.setVisibility( View.INVISIBLE );
                queue_image.setVisibility( View.INVISIBLE );

                String url_copy = q_details_url;

                if ( TextUtils.isEmpty( queue_id.getText().toString().trim() ) ) {

                    Snackbar.make( getView() , "Please Specify a Queue Id!" , Snackbar.LENGTH_LONG )
                            .setAction("Action", null ).show();

                    return;

                } else

                    url_copy += queue_id.getText().toString().trim();

                getAndDisplayDetails( url_copy , container );

            }

        });

    }

    private void getAndDisplayDetails(String queue_details_url , final ViewGroup container ) {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, queue_details_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            displayQueueDetails( object , container );

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if( error.networkResponse.statusCode == 401  )

                            Toast.makeText( getContext() , "Invalid Queue Id" , Toast.LENGTH_LONG).show();

                        else

                            Toast.makeText( getContext() , "Server is taking too long to respond\n\nPlease refresh your connection" , Toast.LENGTH_LONG).show();

                    }

                });

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

    private void displayQueueDetails(final JSONObject object , final ViewGroup container) {

        final String details[] = new String[ 5 ];

        try {

            details[ 0 ] = "Queue Name: " + object.getString( "name" );

            details[ 1 ] = "Current Position: " + object.getString( "position" );

            if ( object.getString( "accepting_appointments" ).equals( "1" ) ) {

                details[ 2 ] = "Appointments Open";

            } else

                details[ 2 ] = "Appointments Closed";

        } catch ( Exception e ) {

            e.printStackTrace();

        }

        ObjectAnimator.ofFloat( queue_id , "translationY" , 0.0f , -150.0f )
                .setDuration( 1200 ).start();

        final ObjectAnimator objectAnimator =  ObjectAnimator.ofFloat( search_queue , "translationY" , 0.0f , -150.0f )
                .setDuration( 1200 );

        objectAnimator.start();

        objectAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                queue_details.setText( details[ 0 ] + "\n\n" );
                queue_details.append( details[ 1 ] + "\n\n" );
                queue_details.append( details[ 2 ] );

                queue_details.setVisibility( View.VISIBLE );
                queue_image.setVisibility( View.VISIBLE );

                if( details[ 2 ].equals( "Appointments Open" ) )

                    book_button.setVisibility(View.VISIBLE);

                new Appointment_Handler().fetchAppointments( queue_id.getText().toString().trim() ,
                        container , getContext() , getActivity() );

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });

    }

}

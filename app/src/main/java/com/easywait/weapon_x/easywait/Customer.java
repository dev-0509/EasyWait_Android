package com.easywait.weapon_x.easywait;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.easywait.weapon_x.easywait.Globals.isSignedIn;
import static com.easywait.weapon_x.easywait.Globals.server;
import static com.easywait.weapon_x.easywait.SignUp.MyPreferences;

public class Customer extends Fragment {

    public static final String KEY_ACTION = "action";
    public static final String KEY_REFERENCE = "reference";
    public static final String KEY_POSITION = "position";

    private String q_details_url = server + "api/queue/";

    private FloatingActionButton search_queue;

    private TextView queue_details;
    private TextView active_appointments;

    private ImageView queue_image;
    private ImageView home;

    private Button book_button;

    private EditText queue_id;

    private ViewGroup viewGroup = null;

    private Snackbar snackbar;

    private String token = null;

    private ListView appointments_list;

    List<String> list = new ArrayList<>();

    ArrayAdapter<String> adapter;

    View rootView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.activity_customer, container, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

            }

        });

        search_queue = ( FloatingActionButton ) rootView.findViewById( R.id.search_q_button );

        queue_details = ( TextView ) rootView.findViewById( R.id.q_details );
        queue_details.setVisibility( View.INVISIBLE );
        active_appointments = (TextView) rootView.findViewById( R.id.active_appointments );
        active_appointments.setVisibility( View.INVISIBLE );

        queue_image = ( ImageView ) rootView.findViewById( R.id.queue_image );
        home = (ImageView) rootView.findViewById( R.id.home );
        queue_image.setVisibility( View.INVISIBLE );
        home.setVisibility( View.VISIBLE );

        book_button = ( Button ) rootView.findViewById( R.id.book_button );
        book_button.setVisibility( View.INVISIBLE );

        queue_id = ( EditText ) rootView.findViewById( R.id.queue_id );

        appointments_list = (ListView) rootView.findViewById( R.id.appointments_list );
        appointments_list.setVisibility( View.INVISIBLE );

        viewGroup = container;

        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        String q_id = getContext().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE).getString( "queue_id" , null );

        if ( q_id != null ) {

            String url = q_details_url;

            queue_id.setText(q_id);

            getAndDisplayDetails( url + q_id );

            fetchActiveAppointments();

        }

        fetchToken();

        setQueueDetails();

        book_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final SharedPreferences shared = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                token = shared.getString("token", null);

                if (token == null)

                    Toast.makeText(getContext(), "Please Sign In to continue", Toast.LENGTH_SHORT).show();

                else {

                    final android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );

                    final View view = LayoutInflater.from( getContext() ).inflate( R.layout.fragment_alert_dialog_2, null );

                    builder.setIcon( R.drawable.ic_delete_forever_black_48dp )

                            .setPositiveButton("Book", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    EditText book_reference = (EditText) view.findViewById( R.id.booking_reference );

                                    if ( TextUtils.isEmpty( book_reference.getText().toString().trim() ) )

                                        Toast.makeText( getContext() , "Please Name your Appointment!", Toast.LENGTH_SHORT).show();

                                    else

                                        appointmentHandler( "book" , book_reference.getText().toString().trim() );

                                }

                            })

                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })

                            .setIcon( R.mipmap.book_image )

                            .setTitle( "Place an Appointment" );

                    builder.setView( view );

                    AlertDialog dialog = builder.create();

                    dialog.show();

                    dialog.getButton( DialogInterface.BUTTON_POSITIVE).setTextColor( getResources().getColor( R.color.black ) );
                    dialog.getButton( DialogInterface.BUTTON_NEGATIVE).setTextColor( getResources().getColor( R.color.black ) );

                }

            }

        });

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if ( tab.getPosition() == 0 ) {

                    if (snackbar != null)

                        if ( isSignedIn ) {

                            isSignedIn = false;

                            snackbar = null;

                        } else

                            snackbar.show();

                    if ( queue_details.getVisibility() == View.VISIBLE ) {

                        String url = q_details_url;

                        getAndDisplayDetails( url + queue_id.getText().toString().trim() );

                    }

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

        appointments_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                final String pos;

                if ( ((TextView) view).getText().toString().substring( 1 , 2 ).equals( " " ) )

                    pos = ((TextView) view).getText().toString().substring( 0 , 1 );

                else if ( ((TextView) view).getText().toString().substring( 2 , 3 ).equals( " " ) )

                    pos = ((TextView) view).getText().toString().substring( 0 , 2 );

                else

                    pos = ((TextView) view).getText().toString().substring( 0 , 3 );

                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder( getContext() );

                View v = LayoutInflater.from( getContext() ).inflate( R.layout.fragment_alert_dialog_1, null );

                builder.setIcon( R.drawable.ic_delete_forever_black_48dp )

                        .setTitle( "Cancel Appointment?" )

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                appointmentHandler( "cancel" , pos );

                                list.remove( position );

                                adapter.notifyDataSetChanged();

                                Toast.makeText( getContext() , "Appointment for Position " + pos + " cancelled" , Toast.LENGTH_SHORT)
                                        .show();

                                if ( list.isEmpty() ) {

                                    active_appointments.setVisibility( View.INVISIBLE );
                                    appointments_list.setVisibility( View.INVISIBLE );

                                    snackbar = Snackbar.make( rootView , "No Active Appointments", Snackbar.LENGTH_INDEFINITE)
                                            .setAction("Action", null);

                                    snackbar.show();

                                }

                            }

                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });

                builder.setView( v );

                android.support.v7.app.AlertDialog dialog = builder.create();

                dialog.show();

                dialog.getButton( DialogInterface.BUTTON_POSITIVE ).setTextColor( getResources().getColor( R.color.black ) );
                dialog.getButton( DialogInterface.BUTTON_NEGATIVE ).setTextColor( getResources().getColor( R.color.black ) );

            }

        });

    }

    private void fetchToken() {

        final SharedPreferences shared = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        String email, password;

        token = shared.getString( "token" , null );
        email = shared.getString( "email" , null );
        password = shared.getString( "password" , null );

        if( token == null ) {

            snackbar = Snackbar.make(viewGroup, "New to EasyWait?", Snackbar.LENGTH_INDEFINITE)
                    .setAction("SIGN UP!", new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(getActivity(), SignUp.class);

                            startActivity(intent);

                        }

                    });

            snackbar.setActionTextColor(getResources().getColor(R.color.snackbar)).show();

        } else {

            if( new SignIn_Method().signIn(email, password, getContext()) )

                token = shared.getString( "token" , null );

        }

    }

    private void setQueueDetails() {

        search_queue.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                fetchToken();

                SharedPreferences sharedpreferences = getContext().getSharedPreferences( MyPreferences , Context.MODE_APPEND );
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString( "queue_id" , queue_id.getText().toString().trim() );
                editor.apply();

                if( snackbar != null )

                    snackbar.dismiss();

                InputMethodManager imm = ( InputMethodManager ) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
                imm.hideSoftInputFromWindow( queue_id.getWindowToken() , 0 );

                queue_details.setText( "" );

                book_button.setVisibility( View.INVISIBLE );
                queue_details.setVisibility( View.INVISIBLE );
                queue_image.setVisibility( View.INVISIBLE );
                home.setVisibility( View.INVISIBLE );
                appointments_list.setVisibility( View.INVISIBLE );
                active_appointments.setVisibility( View.INVISIBLE );

                fetchActiveAppointments();

                String url_copy = q_details_url;

                if ( TextUtils.isEmpty( queue_id.getText().toString().trim() ) ) {

                    Snackbar.make( viewGroup , "Please Specify a Queue Id!" , Snackbar.LENGTH_LONG )
                            .setAction("Action", null ).show();

                    return;

                } else

                    url_copy += queue_id.getText().toString().trim();

                getAndDisplayDetails( url_copy );

            }

        });

        queue_id.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {

                    SharedPreferences sharedpreferences = getContext().getSharedPreferences( MyPreferences , Context.MODE_APPEND );
                    SharedPreferences.Editor editor = sharedpreferences.edit();

                    editor.putString( "queue_id" , queue_id.getText().toString().trim() );
                    editor.apply();

                    if( snackbar != null )

                        snackbar.dismiss();

                    InputMethodManager imm = ( InputMethodManager ) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
                    imm.hideSoftInputFromWindow( queue_id.getWindowToken() , 0 );

                    queue_details.setText( "" );

                    book_button.setVisibility( View.INVISIBLE );
                    queue_details.setVisibility( View.INVISIBLE );
                    queue_image.setVisibility( View.INVISIBLE );
                    home.setVisibility( View.INVISIBLE );
                    appointments_list.setVisibility( View.INVISIBLE );
                    active_appointments.setVisibility( View.INVISIBLE );

                    fetchActiveAppointments();

                    String url_copy = q_details_url;

                    if ( TextUtils.isEmpty( queue_id.getText().toString().trim() ) ) {

                        Snackbar.make( viewGroup , "Please Specify a Queue Id!" , Snackbar.LENGTH_LONG )
                                .setAction("Action", null ).show();

                        return false;

                    } else

                        url_copy += queue_id.getText().toString().trim();

                    getAndDisplayDetails( url_copy );

                }

                return false;

            }

        });

    }

    private void getAndDisplayDetails(String queue_details_url) {

        home.setVisibility( View.INVISIBLE );

        StringRequest stringRequest = new StringRequest(Request.Method.GET, queue_details_url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            displayQueueDetails( object );

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

    private void displayQueueDetails(final JSONObject object) {

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

        if ( queue_details.getVisibility() == View.VISIBLE ) {

            queue_details.setText( details[ 0 ] + "\n\n" );
            queue_details.append( details[ 1 ] + "\n\n" );
            queue_details.append( details[ 2 ] );

            if ( details[ 2 ].equals( "Appointments Closed" ) )

                book_button.setVisibility( View.INVISIBLE );

            else

                book_button.setVisibility( View.VISIBLE );

        } else {

            ObjectAnimator.ofFloat(queue_id, "translationY", 0.0f, -300.0f)
                    .setDuration(1200).start();

            final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(search_queue, "translationY", 0.0f, -300.0f)
                    .setDuration(1200);

            objectAnimator.start();

            objectAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    queue_details.setText(details[0] + "\n\n");
                    queue_details.append(details[1] + "\n\n");
                    queue_details.append(details[2]);

                    queue_details.setVisibility(View.VISIBLE);
                    queue_image.setVisibility(View.VISIBLE);

                    if (details[2].equals("Appointments Open"))

                        book_button.setVisibility(View.VISIBLE);

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

    private void fetchActiveAppointments() {

        String appointments_url = server + "api/queue/" + queue_id.getText().toString().trim() + "/appointment";

        StringRequest stringRequest = new StringRequest(Request.Method.GET , appointments_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject(response);

                            JSONArray appointments = object.getJSONArray("appointments");

                            list.clear();

                            for (int i = 0; i < appointments.length(); i++) {

                                JSONObject object1 = (JSONObject) appointments.get(i);

                                list.add(object1.getString("position") + " : " + object1.getString("reference"));

                            }

                            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);

                            appointments_list.setAdapter(adapter);

                            active_appointments.setVisibility( View.VISIBLE );
                            appointments_list.setVisibility( View.VISIBLE );

                        } catch (Exception e) {

                            snackbar = Snackbar.make( rootView , "No Active Appointments", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Action", null);

                            snackbar.show();

                        }

                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        snackbar = Snackbar.make( rootView , "Sign In for more", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Action", null);

                        snackbar.show();

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

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

    private void appointmentHandler(final String action , final String parameter) {

        final String appointments_url = server + "api/queue/" + queue_id.getText().toString().trim() + "/appointment";

        StringRequest stringRequest = new StringRequest( Request.Method.POST , appointments_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject(response);

                            if ( action.equals( "book" ) ) {

                                list.add( object.getString( "position" ) + " : " + object.getString( "reference" ) );

                                if ( appointments_list.getVisibility() == View.VISIBLE )

                                    adapter.notifyDataSetChanged();

                                else {

                                    adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);

                                    appointments_list.setAdapter(adapter);

                                    active_appointments.setVisibility( View.VISIBLE );
                                    appointments_list.setVisibility( View.VISIBLE );

                                    snackbar.dismiss();
                                    snackbar = null;

                                }

                            }

                        } catch (Exception e) {

                            Toast.makeText( getContext() , "error here!", Toast.LENGTH_SHORT).show();

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText( getContext() , "error!", Toast.LENGTH_SHORT).show();

                    }

                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                params.put(KEY_ACTION, action);

                if (action.equals("book"))

                    params.put(KEY_REFERENCE, parameter);

                else

                    params.put(KEY_POSITION, parameter);

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

        RequestQueue requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add(stringRequest);

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        if ( snackbar != null )

            snackbar.dismiss();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

        super.setUserVisibleHint(isVisibleToUser);

        if ( isVisibleToUser && rootView != null )

            token = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE).getString( "token" , null );

    }

}
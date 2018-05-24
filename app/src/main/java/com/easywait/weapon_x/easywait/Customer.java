package com.easywait.weapon_x.easywait;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import android.widget.RelativeLayout;
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

import static com.easywait.weapon_x.easywait.Globals.internet_access;
import static com.easywait.weapon_x.easywait.Globals.isSignedIn;
import static com.easywait.weapon_x.easywait.Globals.server;
import static com.easywait.weapon_x.easywait.SignUp_Activity.MyPreferences;

public class Customer extends Fragment {

    public static final String KEY_ACTION = "action";
    public static final String KEY_REFERENCE = "reference";
    public static final String KEY_POSITION = "position";

    private String q_details_url = server + "api/queue/";

    private TextView queue_name_id;
    private TextView active_appointments;
    private TextView current_position;
    private TextView book_text_1;
    private TextView book_text_2;
    private TextView currently_serving_text;
    private TextView app_name;

    private ImageView queue_image;
    private ImageView book_button_1;
    private ImageView book_button_2;

    private RelativeLayout relativeLayout_1, relativeLayout_2;

    private ViewGroup viewGroup = null;

    private Snackbar snackbar;

    private String token = null, queue_id = null, details[] = new String[ 5 ];

    private ListView appointments_list;

    List<String> list = new ArrayList<>();

    ArrayAdapter<String> adapter;

    View rootView;

    boolean active_appointments_present = false;

    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

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

        rootView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

                return false;
            }

        });

        queue_name_id = ( TextView ) rootView.findViewById( R.id.q_name_id );
        queue_name_id.setVisibility( View.INVISIBLE );
        active_appointments = (TextView) rootView.findViewById( R.id.active_appointments );
        active_appointments.setVisibility( View.INVISIBLE );
        current_position = (TextView) rootView.findViewById( R.id.current_position );
        current_position.setVisibility( View.INVISIBLE );

        book_text_1 = (TextView) rootView.findViewById( R.id.book_text_1 );
        book_text_2 = (TextView) rootView.findViewById( R.id.book_text_2 );
        book_text_1.setVisibility( View.INVISIBLE );
        book_text_2.setVisibility( View.INVISIBLE );

        currently_serving_text = (TextView) rootView.findViewById( R.id.currently_serving_text );
        currently_serving_text.setVisibility( View.INVISIBLE );

        app_name = (TextView) rootView.findViewById( R.id.easywait );

        queue_image = ( ImageView ) rootView.findViewById( R.id.queue_image );
        queue_image.setVisibility( View.INVISIBLE );

        book_button_1 = (ImageView) rootView.findViewById( R.id.book_button_1 );
        book_button_1.setVisibility( View.INVISIBLE );

        book_button_2 = (ImageView) rootView.findViewById( R.id.book_button_2 );
        book_button_2.setVisibility( View.INVISIBLE );

        relativeLayout_1 = (RelativeLayout) rootView.findViewById( R.id.description_1 );
        relativeLayout_2 = (RelativeLayout) rootView.findViewById( R.id.description_2 );

        appointments_list = (ListView) rootView.findViewById( R.id.appointments_list );
        appointments_list.setVisibility( View.INVISIBLE );

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        toolbar.setTitle( "  Search Queue" );
        toolbar.setTitleTextColor( getResources().getColor( R.color.white ) );
        toolbar.bringToFront();

        viewGroup = container;

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_customer, menu);

        MenuItem item = menu.findItem( R.id.search );
        item.setVisible( true );

        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setQueryHint( "Search Queue..." );

        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                searchView.setQueryHint( "Enter Queue Id..." );

            }

        });

        searchView.setInputType( InputType.TYPE_CLASS_NUMBER );
        searchView.setIconifiedByDefault( false );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                queue_id = query.trim();

                relativeLayout_1.setVisibility( View.INVISIBLE );
                relativeLayout_2.setVisibility( View.INVISIBLE );
                app_name.setVisibility( View.INVISIBLE );

                queue_image.setVisibility( View.VISIBLE );

                setQueueDetails();

                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;

            }

        });

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ( item.getItemId() == R.id.refresh ) {

            Toast.makeText( getContext() , "Status Refreshed!" , Toast.LENGTH_SHORT ).show();

            fetchActiveAppointments(false);

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        queue_id = getContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("queue_id", null);

        fetchToken();

        if (queue_id != null) {

            relativeLayout_1.setVisibility( View.INVISIBLE );
            relativeLayout_2.setVisibility( View.INVISIBLE );
            app_name.setVisibility( View.INVISIBLE );

            queue_image.setVisibility( View.VISIBLE );

            fetchActiveAppointments(false);

        }

        //setQueueDetails();

        book_button_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final SharedPreferences shared = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                token = shared.getString("token", null);

                if (token == null)

                    Toast.makeText(getContext(), "Please Sign In to continue", Toast.LENGTH_SHORT).show();

                else {

                    final android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_alert_dialog_2, null);

                    builder.setIcon(R.drawable.ic_delete_forever_black_48dp)

                            .setPositiveButton("Book", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    EditText book_reference = (EditText) view.findViewById(R.id.booking_reference);

                                    if (TextUtils.isEmpty(book_reference.getText().toString().trim()))

                                        Toast.makeText(getContext(), "Please Name your Appointment!", Toast.LENGTH_SHORT).show();

                                    else

                                        appointmentHandler("book", book_reference.getText().toString().trim());

                                }

                            })

                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })

                            .setIcon(R.mipmap.book_image)

                            .setTitle("Place an Appointment");

                    builder.setView(view);

                    AlertDialog dialog = builder.create();

                    dialog.show();

                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

                }

            }

        });

        book_button_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_alert_dialog_2, null);

                builder.setIcon(R.drawable.ic_delete_forever_black_48dp)

                        .setPositiveButton("Book", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                EditText book_reference = (EditText) view.findViewById(R.id.booking_reference);

                                if (TextUtils.isEmpty(book_reference.getText().toString().trim()))

                                    Toast.makeText(getContext(), "Please Name your Appointment!", Toast.LENGTH_SHORT).show();

                                else

                                    appointmentHandler("book", book_reference.getText().toString().trim());

                            }

                        })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        })

                        .setIcon(R.mipmap.book_image)

                        .setTitle("Place an Appointment");

                builder.setView(view);

                AlertDialog dialog = builder.create();

                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            }

        });

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {

                    toolbar.setVisibility( View.VISIBLE );

                    if (snackbar != null)

                        if (isSignedIn)

                            snackbar = null;

                        else

                            snackbar.show();

                    if (queue_name_id.getVisibility() == View.VISIBLE)

                        fetchActiveAppointments(false);

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

                if (((TextView) view).getText().toString().substring(1, 2).equals(" "))

                    pos = ((TextView) view).getText().toString().substring(0, 1);

                else if (((TextView) view).getText().toString().substring(2, 3).equals(" "))

                    pos = ((TextView) view).getText().toString().substring(0, 2);

                else

                    pos = ((TextView) view).getText().toString().substring(0, 3);

                final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

                View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_alert_dialog_3, null);

                builder.setIcon(R.drawable.ic_delete_forever_black_48dp)

                        .setTitle("Cancel Appointment?")

                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                appointmentHandler("cancel", pos);

                                list.remove(position);

                                adapter.notifyDataSetChanged();

                                Toast.makeText(getContext(), "Appointment for Position " + pos + " cancelled", Toast.LENGTH_SHORT)
                                        .show();

                                if (list.isEmpty()) {

                                    active_appointments.setVisibility(View.INVISIBLE);
                                    appointments_list.setVisibility(View.INVISIBLE);

                                    snackbar = Snackbar.make(rootView, "No Active Appointments", Snackbar.LENGTH_INDEFINITE)
                                            .setAction("Action", null);

                                    snackbar.show();

                                    if (details[2].equals("Appointments Open")) {

                                        book_text_2.setVisibility(View.INVISIBLE);
                                        book_button_2.setVisibility(View.INVISIBLE);

                                        book_text_1.setVisibility(View.VISIBLE);
                                        book_button_1.setVisibility(View.VISIBLE);

                                    } else {

                                        book_text_2.setVisibility(View.INVISIBLE);
                                        book_button_2.setVisibility(View.INVISIBLE);
                                        book_text_1.setVisibility(View.INVISIBLE);
                                        book_button_1.setVisibility(View.INVISIBLE);

                                    }

                                }

                            }

                        })

                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }

                        });

                builder.setView(v);

                android.support.v7.app.AlertDialog dialog = builder.create();

                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            }

        });

    }

    private void fetchToken() {

        final SharedPreferences shared = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        String email, password;

        token = shared.getString( "token" , null );
        email = shared.getString( "email" , null );
        password = shared.getString( "password" , null );

        //if( token == null ) {
        if( token != null ) {

//            snackbar = Snackbar.make(viewGroup, "New to EasyWait?", Snackbar.LENGTH_INDEFINITE)
//                    .setAction("SIGN UP!", new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//
//                            snackbar.dismiss();
//
//                            snackbar = null;
//
//                            Intent intent = new Intent(getActivity(), SignUp_Activity.class);
//
//                            startActivity(intent);
//
//                        }
//
//                    });
//
//            snackbar.setActionTextColor(getResources().getColor(R.color.snackbar)).show();
//
//        } else {

                if( new SignIn_Method().signIn(email, password, getContext()) )

                    token = shared.getString( "token" , null );

        }

    }

    private void setQueueDetails() {

        //search_queue.setOnClickListener(new View.OnClickListener() {

//            @Override
//            public void onClick(View v) {
//
//                InputMethodManager imm = ( InputMethodManager ) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
//                imm.hideSoftInputFromWindow( q_id_box.getWindowToken() , 0 );
//
//                if ( TextUtils.isEmpty( q_id_box.getText().toString().trim() ) ) {
//
//                    Toast.makeText( getContext() , "Please Specify a Queue Id!", Toast.LENGTH_SHORT).show();
//
//                    return;
//
//                }
//
//                queue_id = q_id_box.getText().toString().trim();

                active_appointments_present = false;

                SharedPreferences sharedpreferences = getContext().getSharedPreferences( MyPreferences , Context.MODE_APPEND );
                SharedPreferences.Editor editor = sharedpreferences.edit();

                editor.putString( "queue_id" , queue_id );
                editor.apply();

                if( snackbar != null )

                    snackbar.dismiss();

                clearUIViews();

                String url = q_details_url;

                getAndDisplayDetails( url + queue_id );

                fetchActiveAppointments( true );

        //});

        //q_id_box.setOnEditorActionListener(new TextView.OnEditorActionListener() {

//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //if ( actionId == EditorInfo.IME_ACTION_SEARCH ) {

//                    InputMethodManager imm = ( InputMethodManager ) getActivity().getSystemService( Context.INPUT_METHOD_SERVICE );
//                    imm.hideSoftInputFromWindow( q_id_box.getWindowToken() , 0 );
//
//                    if ( TextUtils.isEmpty( q_id_box.getText().toString().trim() ) ) {
//
//                        Toast.makeText( getContext() , "Please Specify a Queue Id!", Toast.LENGTH_SHORT).show();
//
//                        return false;
//
//                    }
//
//                    queue_id = q_id_box.getText().toString().trim();
//
//                    active_appointments_present = false;
//
//                    clearUIViews();
//
//                    SharedPreferences sharedpreferences = getContext().getSharedPreferences( MyPreferences , Context.MODE_APPEND );
//                    SharedPreferences.Editor editor = sharedpreferences.edit();
//
//                    editor.putString( "queue_id" , queue_id );
//                    editor.apply();
//
//                    if( snackbar != null )
//
//                        snackbar.dismiss();
//
//                    fetchActiveAppointments( true );
//
//                    String url = q_details_url;
//
//                    getAndDisplayDetails( url + queue_id );
//
//                }
//
//                return false;
//
//            }
//
//        });

    }

    private void getAndDisplayDetails(String queue_details_url) {

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

        try {

            details[ 0 ] = "Queue " + queue_id + " : " + object.getString( "name" );

            details[ 1 ] = object.getString( "position" );

            if ( object.getString( "accepting_appointments" ).equals( "1" ) ) {

                details[ 2 ] = "Appointments Open";

            } else

                details[ 2 ] = "Appointments Closed";

        } catch ( Exception e ) {

            e.printStackTrace();

        }

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences( MyPreferences , Context.MODE_APPEND );
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putString( "q_name_d" , details[ 0 ] );
        editor.putString( "posiiton" , details[ 1 ] );
        editor.apply();

        if ( queue_name_id.getVisibility() == View.VISIBLE ) {

            queue_name_id.setText( details[ 0 ] );

            current_position.setText( details[ 1 ] );

            if ( details[ 2 ].equals( "Appointments Open" ) ) {

                if (active_appointments_present) {

                    book_text_1.setVisibility(View.INVISIBLE);
                    book_button_1.setVisibility(View.INVISIBLE);

                    book_text_2.setVisibility(View.VISIBLE);
                    book_button_2.setVisibility(View.VISIBLE);

                } else {

                    book_text_2.setVisibility(View.INVISIBLE);
                    book_button_2.setVisibility(View.INVISIBLE);

                    book_text_1.setVisibility(View.VISIBLE);
                    book_button_1.setVisibility(View.VISIBLE);

                }

            }

            else {

                Toast.makeText( getContext() , "Appointments to this Queue are currently Closed" , Toast.LENGTH_LONG).show();

                book_text_1.setVisibility( View.INVISIBLE );
                book_button_1.setVisibility( View.INVISIBLE );
                book_text_2.setVisibility( View.INVISIBLE );
                book_button_2.setVisibility( View.INVISIBLE );

            }

        } else {

            final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(queue_image, "translationX", 0.0f, -150.0f)
                    .setDuration(1000);

            objectAnimator.start();

            objectAnimator.addListener(new Animator.AnimatorListener() {

                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    queue_name_id.setText( details[ 0 ] );

                    current_position.setText( details[ 1 ] );

                    queue_name_id.setVisibility(View.VISIBLE);
                    current_position.setVisibility(View.VISIBLE);
                    currently_serving_text.setVisibility( View.VISIBLE );

                    if ( details[ 2 ].equals( "Appointments Open" ) ) {

                        if (active_appointments_present) {

                            book_text_2.setVisibility(View.VISIBLE);
                            book_button_2.setVisibility(View.VISIBLE);

                        } else {

                            book_text_1.setVisibility(View.VISIBLE);
                            book_button_1.setVisibility(View.VISIBLE);

                        }

                    }

                    else {

                        Toast.makeText( getContext() , "Appointments to this Queue are currently Closed" , Toast.LENGTH_LONG).show();

                        book_text_1.setVisibility( View.INVISIBLE );
                        book_button_1.setVisibility( View.INVISIBLE );
                        book_text_2.setVisibility( View.INVISIBLE );
                        book_button_2.setVisibility( View.INVISIBLE );

                    }

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

    private void fetchActiveAppointments( final boolean isCalledOnButtonCLick ) {

        final String appointments_url = server + "api/queue/" + queue_id + "/appointment";

        StringRequest stringRequest = new StringRequest(Request.Method.GET , appointments_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        if ( ! isCalledOnButtonCLick ) {

                            String url = q_details_url;

                            getAndDisplayDetails(url + queue_id);

                        }

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

                            active_appointments_present = true;

                            active_appointments.setVisibility( View.VISIBLE );
                            appointments_list.setVisibility( View.VISIBLE );

                        } catch (Exception e) {

                            snackbar = Snackbar.make( rootView , "No Active Appointments", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Action", null);

                            snackbar.show();

                            list.clear();

                            if ( adapter != null )

                                adapter.notifyDataSetChanged();

                            active_appointments.setVisibility( View.INVISIBLE );
                            appointments_list.setVisibility( View.INVISIBLE );

                            active_appointments_present = false;

                        }

                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if ( ! isCalledOnButtonCLick ) {

                            String url = q_details_url;

                            getAndDisplayDetails(url + queue_id);

                        }

                        snackbar = Snackbar.make(viewGroup, "Sign In for More", Snackbar.LENGTH_INDEFINITE)
                                .setAction("SIGN IN", new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {

                                        if ( snackbar != null )

                                            snackbar.dismiss();

                                        Intent intent = new Intent(getActivity(), SignIn_Activity.class);

                                        startActivityForResult( intent , 1 , new Bundle() );

                                    }

                                });

                        snackbar.setActionTextColor(getResources().getColor(R.color.snackbar)).show();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == 1 ) {

            book_text_1.setVisibility( View.INVISIBLE );
            book_button_1.setVisibility( View.INVISIBLE );

            fetchToken();

            fetchActiveAppointments( false );

        }

    }

    private void appointmentHandler(final String action , final String parameter) {

        final String appointments_url = server + "api/queue/" + queue_id + "/appointment";

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

                                    book_text_1.setVisibility( View.INVISIBLE );
                                    book_button_1.setVisibility( View.INVISIBLE );

                                    book_text_2.setVisibility( View.VISIBLE );
                                    book_button_2.setVisibility( View.VISIBLE );

                                }

                            }

                        } catch (Exception e) {

                            Toast.makeText( getContext() , e.toString(), Toast.LENGTH_SHORT).show();

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

    private void clearUIViews() {

        queue_name_id.setText( "" );
        current_position.setText( "" );

        book_text_1.setVisibility( View.INVISIBLE );
        book_button_1.setVisibility( View.INVISIBLE );
        book_text_2.setVisibility( View.INVISIBLE );
        book_button_2.setVisibility( View.INVISIBLE );

        currently_serving_text.setVisibility( View.INVISIBLE );
        queue_name_id.setVisibility( View.INVISIBLE );
        appointments_list.setVisibility( View.INVISIBLE );
        active_appointments.setVisibility( View.INVISIBLE );

    }

}
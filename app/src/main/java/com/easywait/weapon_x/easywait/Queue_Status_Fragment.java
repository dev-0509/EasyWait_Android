package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.baoyz.widget.PullRefreshLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.easywait.weapon_x.easywait.Globals.server;

public class Queue_Status_Fragment extends Fragment {

    private static final String KEY_ACTION = "action";

    String queue_id;

    TextView position;
    TextView q_id_name;

    ImageView cancel_appointments;

    ListView appointment_list;

    Switch aSwitch;

    Button next;
    Button reset;

    FloatingActionButton set_preferences;

    private String token;

    Snackbar snackbar = null;

    PullRefreshLayout pull_to_refresh;

    RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_queue_status, container, false);

        position = (TextView) rootView.findViewById( R.id.position );
        q_id_name = (TextView) rootView.findViewById( R.id.q_id );

        next = (Button) rootView.findViewById( R.id.move_next );
        reset = (Button) rootView.findViewById( R.id.reset );

        cancel_appointments = (ImageView) rootView.findViewById( R.id.cancel_all_appointments );
        cancel_appointments.setVisibility( View.INVISIBLE );

        appointment_list = (ListView) rootView.findViewById( R.id.appointment_list );

        set_preferences = (FloatingActionButton) rootView.findViewById( R.id.preferences );

        aSwitch = (Switch) rootView.findViewById( R.id.Switch );

        pull_to_refresh = (PullRefreshLayout) rootView.findViewById( R.id.swipe_to_refresh );

        return rootView;

    }

    @Override
    public void onResume() {

        super.onResume();
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//
//        super.onViewCreated(view, savedInstanceState);

        Log.d( TAG , "on view created for queue status fragment called" );

        queue_id = getArguments().getString( "queue_id" ).substring( 0 , 2 );

        q_id_name.setText( "Queue " + queue_id + "  :  " + getArguments().getString( "queue_id" ).substring( 5 ) );

        fetchToken();

        fetchAppointments();

        updateCurrentPosition();

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if ( tab.getPosition() == 1 ) {

                    if (snackbar != null)

                        snackbar.show();

                    fetchAppointments();

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

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                aSwitch.setSwitchTextAppearance( getContext() , R.style.SwitchTextAppearance );

                if ( isChecked ) {

                    aSwitch.setText( "Open" );

                    performAppointmentAction( "open" );

                } else {

                    aSwitch.setText( "Closed" );

                    performAppointmentAction( "close" );

                }

            }

        });

        next.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                position.setText( Integer.toString( ( Integer.parseInt( position.getText().toString() ) ) + 1 ) );

                performQueueAction( "movenext" );

            }

        });

        reset.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                position.setText( "0" );

                performQueueAction( "reset" );

            }

        });

        cancel_appointments.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                appointment_list.setVisibility( View.INVISIBLE );
                cancel_appointments.setVisibility( View.INVISIBLE );

                snackbar = Snackbar.make(getView(), "All Appointments Inactive", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Action", null);

                snackbar.show();

                performAppointmentAction( "reset" );

            }

        });

        set_preferences.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();

                bundle.putString( "queue_id" , queue_id );

                Queue_Preferences_Fragment queue_preferences_fragment = new Queue_Preferences_Fragment();

                queue_preferences_fragment.setArguments( bundle );

                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                transaction.addToBackStack( null );

                transaction.replace(R.id.root_frame_vendor, queue_preferences_fragment);

                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                transaction.commit();

            }

        });

        pull_to_refresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                pull_to_refresh.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        fetchAppointments();

                        pull_to_refresh.setRefreshing( false );

                    }

                }, 3000);

            }

        });

    }

    private void fetchToken() {

        token = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE).getString( "token" , null );

    }

    private void fetchAppointments() {

        String appointments_url = server + "api/queue/" + queue_id + "/appointment";

        StringRequest stringRequest = new StringRequest(Request.Method.GET , appointments_url ,
            new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    try {

                        List<String> list = new ArrayList<>();

                        JSONObject object = new JSONObject(response);

                        JSONArray appointments = object.getJSONArray("appointments");

                        list.clear();

                        for (int i = 0; i < appointments.length(); i++) {

                            JSONObject object1 = (JSONObject) appointments.get(i);

                            list.add(object1.getString("position") + " : " + object1.getString("reference"));

                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);

                        appointment_list.setAdapter(adapter);

                        appointment_list.setVisibility( View.VISIBLE );
                        cancel_appointments.setVisibility( View.VISIBLE );

                        if ( snackbar != null ) {

                            snackbar.dismiss();

                            snackbar = null;

                        }

                    } catch (Exception e) {

                        snackbar = Snackbar.make(getView(), "No Active Appointments", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Action", null);

                        snackbar.show();

                        appointment_list.setVisibility( View.INVISIBLE );
                        cancel_appointments.setVisibility( View.INVISIBLE );

                    }

                }

            },
            new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {

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

        requestQueue = Volley.newRequestQueue( getActivity() );
        requestQueue.add( stringRequest );

    }

    private void updateCurrentPosition() {

        String q_details_url = server + "api/queue/" + queue_id ;

        StringRequest stringRequest = new StringRequest(Request.Method.GET , q_details_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONObject object = new JSONObject( response );

                            position.setText( object.getString( "position" ) );

                            if ( object.getString( "accepting_appointments" ).equals( "1" ) ) {

                                aSwitch.setText( "Open" );

                                aSwitch.setChecked( true );

                            } else {

                                aSwitch.setText( "Closed" );

                                aSwitch.setChecked( false );

                            }

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                });

        requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

    private void performQueueAction( final String action ) {

        String action_url = server + "api/queue/" + queue_id;

        StringRequest stringRequest = new StringRequest(Request.Method.POST , action_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

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
                params.put( KEY_ACTION , action );

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

        requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

    private void performAppointmentAction( final String action ) {

        String action_url = server + "api/queue/" + queue_id + "/appointment";

        StringRequest stringRequest = new StringRequest(Request.Method.POST , action_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

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
                params.put( KEY_ACTION , action );

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

        requestQueue = Volley.newRequestQueue( getContext() );
        requestQueue.add( stringRequest );

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        if ( snackbar != null )

            snackbar.dismiss();

        requestQueue.cancelAll( TAG );

    }

}

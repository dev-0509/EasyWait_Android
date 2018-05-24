package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
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
import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.easywait.weapon_x.easywait.Globals.server;

public class Queue_Status_Fragment extends Fragment {

    private static final String KEY_ACTION = "action";

    private String queue_id;

    //private TextView position;
    private TextView q_id_name;

    private FloatingActionButton cancel_appointments;

    private ListView appointment_list;

    private Switch aSwitch;

    private ImageButton next, reset, done;

    private NumberPicker numberPicker;

    private String token;

    private Snackbar snackbar = null;

    private View rootView = null;

    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_queue_status, container, false);

        //position = (TextView) rootView.findViewById( R.id.position );
        q_id_name = (TextView) rootView.findViewById( R.id.q_id );

        next = (ImageButton) rootView.findViewById( R.id.move_next );
        reset = (ImageButton) rootView.findViewById( R.id.reset );
        done = (ImageButton) rootView.findViewById( R.id.done );
        done.setVisibility( View.INVISIBLE );

        cancel_appointments = (FloatingActionButton) rootView.findViewById( R.id.cancel_all_appointments );
        cancel_appointments.setVisibility( View.INVISIBLE );

        appointment_list = (ListView) rootView.findViewById( R.id.appointment_list );

        aSwitch = (Switch) rootView.findViewById( R.id.Switch );

        numberPicker = (NumberPicker) rootView.findViewById( R.id.numberPicker );
        numberPicker.setMinValue( 0 );
        numberPicker.setMaxValue( 500 );
        numberPicker.setWrapSelectorWheel( false );

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        toolbar.setTitle( "Queue Preferences" );
        toolbar.setVisibility( View.VISIBLE );

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_vendor_change_position, menu);

        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if ( item.getItemId() == R.id.q_preferences ) {

            if ( snackbar != null )

                snackbar.dismiss();

            snackbar = null;

            Bundle bundle = new Bundle();

            bundle.putString("queue_id", queue_id);

            Queue_Preferences_Fragment queue_preferences_fragment = new Queue_Preferences_Fragment();

            queue_preferences_fragment.setArguments(bundle);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();

            transaction.addToBackStack(null);

            transaction.replace(R.id.root_frame_vendor, queue_preferences_fragment);

            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            transaction.commit();

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onResume() {

        super.onResume();

        if ( rootView.getParent() != null ) {

            queue_id = getArguments().getString( "queue_id" );
            String queue_name = getArguments().getString( "queue_name" );

            q_id_name.setText( "Queue " + queue_id + "  :  " + queue_name );

            fetchToken();

            fetchAppointments();

            updateCurrentPosition();

            TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {

                    if (tab.getPosition() == 1) {

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

                    aSwitch.setSwitchTextAppearance(getContext(), R.style.SwitchTextAppearance);

                    if (isChecked) {

                        aSwitch.setText("Open");

                        performAppointmentAction("open");

                    } else {

                        aSwitch.setText("Closed");

                        performAppointmentAction("close");

                    }

                }

            });

            next.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    next.setAlpha( 0.5f );
                    next.setEnabled( false );

                    performQueueAction( "movenext" , false );

                }

            });

            reset.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    numberPicker.setValue( 0 );

                    performQueueAction( "reset" , false );

                }

            });

            cancel_appointments.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(final View v) {

                    final android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    final View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_alert_dialog_1, null);

                    final EditText input_number = (EditText) view.findViewById(R.id.rand_number);
                    final TextView rand_number = (TextView) view.findViewById(R.id.number);

                    rand_number.setText(Integer.toString(new Random().nextInt(10)));

                    builder.setIcon(R.drawable.ic_error_black_24dp)

                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    if (TextUtils.isEmpty(input_number.getText().toString().trim()))

                                        Toast.makeText(getContext(), "Number cannot be blank!", Toast.LENGTH_SHORT).show();

                                    else {

                                        if (input_number.getText().toString().equals(rand_number.getText().toString())) {

                                            appointment_list.setVisibility(View.INVISIBLE);
                                            cancel_appointments.setVisibility(View.INVISIBLE);

                                            snackbar = Snackbar.make(rootView, "All Appointments Inactive", Snackbar.LENGTH_INDEFINITE)
                                                    .setAction("Action", null);

                                            snackbar.show();

                                            performAppointmentAction("reset");

                                        } else

                                            Toast.makeText(getContext(), "Verification Unsuccessful", Toast.LENGTH_SHORT).show();

                                    }

                                }

                            })

                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }

                            })

                            .setTitle("Please verify your action!");

                    builder.setView(view);

                    AlertDialog dialog = builder.create();

                    dialog.show();

                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

                }

            });

        }

        numberPicker.setOnScrollListener(new NumberPicker.OnScrollListener() {

            @Override
            public void onScrollStateChange(NumberPicker numberPicker, int i) {

                if ( i == SCROLL_STATE_TOUCH_SCROLL )

                    done.setVisibility(View.INVISIBLE);

                if ( i == SCROLL_STATE_IDLE ) {

                    done.setVisibility( View.VISIBLE );

                    done.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            done.setVisibility( View.INVISIBLE );

                            performQueueAction( "movenext" , true );

                        }

                    });

                }

            }

        });

    }

    private void fetchToken() {

        token = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE).getString( "token" , null );

    }

    private void fetchAppointments() {

        if ( rootView.getParent() != null ) {

            String appointments_url = server + "api/queue/" + queue_id + "/appointment";

            StringRequest stringRequest = new StringRequest(Request.Method.GET, appointments_url,
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

                                appointment_list.setVisibility(View.VISIBLE);
                                cancel_appointments.setVisibility(View.VISIBLE);

                                if (snackbar != null) {

                                    snackbar.dismiss();

                                    snackbar = null;

                                }

                            } catch (Exception e) {

                                snackbar = Snackbar.make(rootView, "No Active Appointments", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Action", null);

                                snackbar.show();

                                appointment_list.setVisibility(View.INVISIBLE);
                                cancel_appointments.setVisibility(View.INVISIBLE);

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
                    headers.put("Authorization", auth);

                    return headers;

                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);

        }

    }

    private void updateCurrentPosition() {

        if ( rootView.getParent() != null ) {

            String q_details_url = server + "api/queue/" + queue_id;

            StringRequest stringRequest = new StringRequest(Request.Method.GET, q_details_url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            try {

                                JSONObject object = new JSONObject(response);

                                numberPicker.setValue( Integer.parseInt( object.getString( "position" ) ) );
                                //position.setText(object.getString("position"));

                                if (object.getString("accepting_appointments").equals("1")) {

                                    aSwitch.setText("Open");

                                    aSwitch.setChecked(true);

                                } else {

                                    aSwitch.setText("Closed");

                                    aSwitch.setChecked(false);

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

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);

        }

    }

    private void performQueueAction(final String action , final boolean called_on_scroll ) {

        if ( rootView.getParent() != null ) {

            String action_url = server + "api/queue/" + queue_id;

            StringRequest stringRequest = new StringRequest(Request.Method.POST, action_url,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {

                            if ( action.equals( "movenext" ) && !called_on_scroll ) {

                                next.setAlpha( 1.0f );
                                next.setEnabled( true );

                                numberPicker.setValue( numberPicker.getValue() + 1 );
                                //position.setText(Integer.toString((Integer.parseInt(position.getText().toString())) + 1));

                            }

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
                    params.put(KEY_ACTION, action);

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

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);

        }

    }

    private void performAppointmentAction( final String action ) {

        if ( rootView.getParent() != null ) {

            String action_url = server + "api/queue/" + queue_id + "/appointment";

            StringRequest stringRequest = new StringRequest(Request.Method.POST, action_url,
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
                    params.put(KEY_ACTION, action);

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

            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(stringRequest);

        }

    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();

        if ( snackbar != null )

            snackbar.dismiss();

        snackbar = null;

    }

}
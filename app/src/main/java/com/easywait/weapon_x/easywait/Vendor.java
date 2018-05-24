package com.easywait.weapon_x.easywait;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
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
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.easywait.weapon_x.easywait.Globals.isSignedIn;
import static com.easywait.weapon_x.easywait.Globals.server;
import static com.easywait.weapon_x.easywait.R.layout.activity_vendor;

public class Vendor extends Fragment {

    private static final String KEY_QUEUE_NAME = "name";

    String q_name;

    TextView active_queues;

    ImageView queue_image;

    ListView q_list;

    String token;

    Snackbar snackbar;

    List<String> list = new ArrayList<>();

    ArrayAdapter<String> adapter;

    View rootView = null;

    List<String> q_id_array = new ArrayList<>(), q_name_array = new ArrayList<>();

    Toolbar toolbar;

    RelativeLayout relativeLayout;

    private boolean is_vendor_fragment = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView( inflater , container , savedInstanceState );

        rootView = inflater.inflate(activity_vendor, container, false);

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

        active_queues = (TextView) rootView.findViewById(R.id.active_queues);
        active_queues.setVisibility(View.INVISIBLE);

        queue_image = (ImageView) rootView.findViewById(R.id.queue);
        queue_image.setVisibility(View.INVISIBLE);

        q_list = (ListView) rootView.findViewById(R.id.q_list);
        q_list.setVisibility(View.INVISIBLE);

        relativeLayout = (RelativeLayout) rootView.findViewById( R.id.description_3 );
        relativeLayout.setVisibility( View.INVISIBLE );

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);

        toolbar.setTitle( "Add a Queue..." );
        toolbar.setVisibility( View.VISIBLE );

        is_vendor_fragment = true;

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        getActivity().getMenuInflater().inflate(R.menu.menu_vendor_add_queue, menu);

        MenuItem item = menu.findItem( R.id.add_queue );
        final SearchView searchView = (SearchView) item.getActionView();

        searchView.setQueryHint( "Add a Queue..." );

        searchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                searchView.setQueryHint( "Name your Queue   ..." );

            }

        });

        searchView.setIconifiedByDefault( false );
        searchView.setImeOptions( EditorInfo.IME_ACTION_DONE );

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                q_name = query;

                publishNewQueue();

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        active_queues.setVisibility(View.INVISIBLE);
        queue_image.setVisibility(View.INVISIBLE);
        q_list.setVisibility(View.INVISIBLE);

        TabLayout tabLayout = (TabLayout) getActivity().findViewById(R.id.tabs);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 1) {

                    if ( is_vendor_fragment )

                        toolbar.setVisibility( View.VISIBLE );

                    if (snackbar != null)

                        if (isSignedIn)

                            snackbar = null;

                        else if (token == null)

                            snackbar.show();

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

        fetchToken();

        if (token == null) {

            FragmentTransaction trans = getFragmentManager().beginTransaction();

            trans.replace( R.id.root_frame_vendor, new Vendor_Intro_Screen() , "Vendor_Intro_Screen" ); //.replace(R.id.root_frame_vendor, new Vendor_Intro_Screen());

            trans.commit();

//            snackbar = Snackbar.make(rootView, "New Vendor?", Snackbar.LENGTH_INDEFINITE)
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
//            snackbar.setActionTextColor(getResources().getColor(R.color.snackbar));

        } else {

            fetchAllQueues();
//
//            add_q_button.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//
//                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
//
//                    if (TextUtils.isEmpty(q_name.getText().toString().trim()))
//
//                        Toast.makeText(getContext(), "Please reference your Queue!", Toast.LENGTH_SHORT).show();
//
//                    else
//
//                        publishNewQueue();
//
//                }
//
//            });

        }

        q_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle bundle = new Bundle();

                bundle.putString("queue_id", q_id_array.get( position ));
                bundle.putString("queue_name", q_name_array.get(position));

                Queue_Status_Fragment queue_status_fragment = new Queue_Status_Fragment();

                queue_status_fragment.setArguments(bundle);

                FragmentTransaction trans = getFragmentManager().beginTransaction();

                trans.replace(R.id.root_frame_vendor, queue_status_fragment);

                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                trans.addToBackStack(null);

                trans.commit();

            }

        });

    }

    private void fetchToken() {

        final SharedPreferences shared = this.getActivity().getSharedPreferences("MyPrefs" , Context.MODE_PRIVATE);

        String email, password;

        token = shared.getString( "token" , null );
        email = shared.getString( "email" , null );
        password = shared.getString( "password" , null );

        if( token != null )

            if (new SignIn_Method().signIn(email, password, getContext()))

                token = shared.getString("token", null);

    }

    private void fetchAllQueues() {

        String fetch_queues_url = server + "api/queue";

        StringRequest stringRequest = new StringRequest(Request.Method.GET , fetch_queues_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(final String response) {

                        try {

                            new JSONObject(response);

                            if (q_list.getVisibility() == View.VISIBLE) {

                                JSONObject object = new JSONObject(response);

                                JSONArray queue = object.getJSONArray("queues");

                                list.clear();

                                for (int i = 0; i < queue.length(); i++) {

                                    JSONObject object1 = (JSONObject) queue.get(i);

                                    list.add(object1.getString("id") + " : " + object1.getString("name"));

                                    q_id_array.add( object1.getString( "id" ) );
                                    q_name_array.add( object1.getString( "name" ) );

                                }

                                adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);

                                q_list.setAdapter(adapter);

                                q_list.setVisibility(View.VISIBLE);
                                active_queues.setVisibility(View.VISIBLE);
                                queue_image.setVisibility(View.VISIBLE);

                            }

                            else {
//
//                                ObjectAnimator.ofFloat(q_name, "translationY", 0.0f, -350.0f)
//                                        .setDuration(1200).start();
//
//                                final ObjectAnimator object = ObjectAnimator.ofFloat(add_q_button, "translationY", 0.0f, -350.0f)
//                                        .setDuration(1200);
//
//                                object.start();
//
//                                object.addListener(new Animator.AnimatorListener() {
//
//                                    @Override
//                                    public void onAnimationStart(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {

                                        try {

                                            JSONObject object = new JSONObject(response);

                                            JSONArray queue = object.getJSONArray("queues");

                                            list.clear();

                                            for (int i = 0; i < queue.length(); i++) {

                                                JSONObject object1 = (JSONObject) queue.get(i);

                                                list.add(object1.getString("id") + " : " + object1.getString("name"));

                                                q_id_array.add( object1.getString( "id" ) );
                                                q_name_array.add( object1.getString( "name" ) );

                                            }

                                            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);

                                            q_list.setAdapter(adapter);

                                            q_list.setVisibility(View.VISIBLE);
                                            active_queues.setVisibility(View.VISIBLE);
                                            queue_image.setVisibility(View.VISIBLE);

                                        } catch (Exception e) {

                                            relativeLayout.setVisibility( View.VISIBLE );

                                            Snackbar.make(rootView, "No Active Queues", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null).show();

                                        }

                                    }

//                                    @Override
//                                    public void onAnimationCancel(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animator animation) {
//
//                                    }
//
//                                });

                        } catch ( Exception e ) {

                            relativeLayout.setVisibility( View.VISIBLE );

                            Snackbar.make( rootView , "No Active Queues" , Snackbar.LENGTH_LONG )
                                    .setAction("Action", null).show();

                        }

                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Snackbar.make( rootView , "Error!" , Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }

                }) {

            @Override
            public Map<String , String> getHeaders() {

                Map<String , String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this.getContext() );
        requestQueue.add( stringRequest );

    }

    private void publishNewQueue() {

        String publish_q_url = server + "api/queue";

        StringRequest stringRequest = new StringRequest(Request.Method.POST , publish_q_url ,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(final String response) {

                        try {

                            if ( new JSONObject( response ).getString( "error" ).equals( "true" ) )

                                Toast.makeText( getContext() , "Queue creation limit reached!" , Toast.LENGTH_SHORT).show();

                            else if ( q_list.getVisibility() == View.VISIBLE ) {

                                JSONObject object = new JSONObject( response );

                                JSONArray queue = object.getJSONArray( "queuelist" );

                                JSONObject object1 = (JSONObject) queue.get( queue.length() - 1 );

                                list.add( object1.getString( "id" ) + " : " + object1.getString( "name" ) );

                                q_id_array.add( object1.getString( "id" ) );
                                q_name_array.add( object1.getString( "name" ) );

                                q_list.setSelection( queue.length() - 1 );

                                adapter.notifyDataSetChanged();

                            } else {

//                                ObjectAnimator.ofFloat(q_name, "translationY", 0.0f, -350.0f)
//                                        .setDuration(1200).start();
//
//                                final ObjectAnimator object = ObjectAnimator.ofFloat(add_q_button, "translationY", 0.0f, -350.0f)
//                                        .setDuration(1200);
//
//                                object.start();
//
//                                object.addListener(new Animator.AnimatorListener() {
//
//                                    @Override
//                                    public void onAnimationStart(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationEnd(Animator animation) {

                                        try {

                                            JSONObject object = new JSONObject(response);

                                            JSONArray queue = object.getJSONArray("queuelist");

                                            list.clear();

                                            for (int i = 0; i < queue.length(); i++) {

                                                JSONObject object1 = (JSONObject) queue.get(i);

                                                list.add(object1.getString("id") + " : " + object1.getString("name"));

                                                q_id_array.add( object1.getString( "id" ) );
                                                q_name_array.add( object1.getString( "name" ) );

                                            }

                                            adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);

                                            q_list.setAdapter(adapter);

                                            q_list.setVisibility(View.VISIBLE);
                                            active_queues.setVisibility(View.VISIBLE);
                                            queue_image.setVisibility(View.VISIBLE);

                                            relativeLayout.setVisibility( View.INVISIBLE );

                                        } catch (Exception e) {

                                            snackbar = Snackbar.make( rootView , "No Active Queues", Snackbar.LENGTH_LONG)
                                                    .setAction("Action", null);

                                        }

                                    }
//
//                                    @Override
//                                    public void onAnimationCancel(Animator animation) {
//
//                                    }
//
//                                    @Override
//                                    public void onAnimationRepeat(Animator animation) {
//
//                                    }
//
//                                });

//                            }

                        } catch (Exception e) {

                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }

                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText( getContext() , "Something went wrong...\nPlease try again later", Toast.LENGTH_LONG).show();

                    }

                }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put(KEY_QUEUE_NAME, q_name);// q_name.getText().toString().trim());

                return params;

            }

            @Override
            public Map<String , String> getHeaders() {

                Map<String , String> headers = new HashMap<>();

                String auth = "Bearer " + token;
                headers.put("Authorization", auth);

                return headers;

            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue( this.getContext() );
        requestQueue.add( stringRequest );

    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        is_vendor_fragment = false;

    }

}
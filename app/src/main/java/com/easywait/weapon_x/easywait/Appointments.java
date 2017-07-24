package com.easywait.weapon_x.easywait;

import android.content.Context;
import android.sax.RootElement;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class Appointments extends AppCompatActivity {

    private TextView appointments;

    private EditText position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

        appointments = ( TextView ) findViewById( R.id.appointments );

        RelativeLayout relative_layout = (RelativeLayout) findViewById(R.id.relative_layout);

        relative_layout.setOnClickListener(new View.OnClickListener() {

           @Override
           public void onClick(View v) {

               InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
               imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

           }

        });

        position = ( EditText ) findViewById( R.id.cancel_position );

        Button cancel = (Button) findViewById(R.id.cancel_button);

        final Snackbar snackbar = displayAppointments( relative_layout );

        position.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if ( snackbar != null )

                    snackbar.dismiss();

            }

        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new Appointment_Handler().appointmentHandler( "cancel" , getIntent().getExtras().getString( "queue_id" ) ,
                        null , position.getText().toString().trim() , getApplicationContext() , Appointments.this );

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }

        });

    }

    private Snackbar displayAppointments( RelativeLayout relative_layout ) {

        Snackbar snackbar = null;

        String string = getIntent().getExtras().getString( "appointments_list" );

        if ( string.equals( "NA" ) ) {

            appointments.setText( "No Appointments" );

            snackbar = Snackbar.make(relative_layout, "No Active Appointments", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).setDuration(Snackbar.LENGTH_INDEFINITE);

            snackbar.show();

            return snackbar;

        }

        try {

            JSONObject object = new JSONObject( string );

            JSONArray appointments_list = object.getJSONArray( "appointments" );

            int temp = 0;

            for( int i = 0 ; i < appointments_list.length() ; i++ ) {

                if ( i > 1 ) {

                    snackbar = Snackbar.make(relative_layout, "Scroll Up for all Appointments", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).setDuration(Snackbar.LENGTH_INDEFINITE);

                    snackbar.show();

                }

                JSONObject object1 = (JSONObject) appointments_list.get(i);

                String appoint_name = object1.getString( "reference" );
                String position = object1.getString( "position" );

                if( temp == 0 ) {

                    appointments.setText( "Position : " + position + "  -  " + appoint_name + "\n\n" );
                    ++temp;

                } else {

                    appointments.append( "Position : " + position + "  -  " + appoint_name + "\n\n" );

                }

            }

        } catch ( Exception e ) {

            e.printStackTrace();

        }

        return snackbar;

    }

}

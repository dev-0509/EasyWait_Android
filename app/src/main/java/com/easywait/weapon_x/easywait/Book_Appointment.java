package com.easywait.weapon_x.easywait;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Book_Appointment extends AppCompatActivity {

    private EditText booking_reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_appointment);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relative_layout);

        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

            }

        });

        booking_reference = ( EditText ) findViewById( R.id.booking_reference );

        TextView queue_id_display = (TextView) findViewById(R.id.queue_id_display);

        Button book = (Button) findViewById(R.id.book);

        final String queue_id = getIntent().getExtras().getString( "queue_id" );

        queue_id_display.setText( "Queue " + queue_id );

        book.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                if( TextUtils.isEmpty( booking_reference.getText().toString().trim() ) ) {

                    Toast.makeText(getApplicationContext(), "Please specify a reference to your Appointment!", Toast.LENGTH_SHORT).show();

                    return;

                }

                new Appointment_Handler().appointmentHandler( "book" , queue_id ,
                                                            booking_reference.getText().toString().trim() ,
                                                            null , getApplicationContext() , Book_Appointment.this );

            }

        });

    }

}

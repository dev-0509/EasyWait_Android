package com.easywait.weapon_x.easywait;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Splash_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature( Window.FEATURE_NO_TITLE );

        setContentView(R.layout.activity_splash);

        View view = findViewById(R.id.splash_activity);

        view.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION );

        ImageView app_icon = (ImageView) findViewById(R.id.app_icon);

        TextView easy = (TextView) findViewById(R.id.easy);
        TextView wait = (TextView) findViewById(R.id.wait);

        // Checking for first time launch
        final PreferenceManager prefManager = new com.easywait.weapon_x.easywait.PreferenceManager( this );

        if ( prefManager.isFirstTimeLaunch() ) {

            Animation appear = AnimationUtils.loadAnimation( this , R.anim.appear_transition_1);

            app_icon.startAnimation(appear);
            easy.startAnimation(appear);
            wait.startAnimation(appear);

        } else {

            Animation appear = AnimationUtils.loadAnimation( this , R.anim.appear_transition_2 );

            app_icon.startAnimation(appear);
            easy.startAnimation(appear);
            wait.startAnimation(appear);

        }

        Thread thread = new Thread() {

            public void run() {

                try {

                    if ( prefManager.isFirstTimeLaunch() )

                        sleep( 8000 );

                    else

                        sleep( 1000 );

                } catch (Exception e) {

                    e.printStackTrace();

                } finally {

                    Intent intent;

                    if ( prefManager.isFirstTimeLaunch() ) {

                        prefManager.setFirstTimeLaunch( false );

                        intent = new Intent( Splash_Activity.this , SliderScreen_Activity.class );

                        startActivityForResult( intent , 1 );

                    } else {

                        intent = new Intent( Splash_Activity.this , Cust_Vend_Controller.class );

                        startActivity( intent );

                        finish();

                    }

                }

            }

        };

        thread.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == 1 ) {

            if ( data.getExtras().getString( "activity_to_be_called" ).equals( "sign_in" ) )

                startActivityForResult( new Intent( Splash_Activity.this , SignIn_Activity.class ) , 2 );

            else if ( data.getExtras().getString( "activity_to_be_called" ).equals( "sign_up" ) )

                startActivityForResult( new Intent( Splash_Activity.this , SignUp_Activity.class ) , 2 );

            else if ( data.getExtras().getString( "activity_to_be_called" ).equals( "cust_vend" ) ) {

                startActivityForResult(new Intent(Splash_Activity.this, Cust_Vend_Controller.class), 2);

                finish();

            }

        } else if ( requestCode == 2 ) {

            startActivity(new Intent(Splash_Activity.this, Cust_Vend_Controller.class));

            finish();

        }

    }

}

package com.easywait.weapon_x.easywait;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class CheckInternetBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if ( ! isNetworkAvailable( context ) )

            Toast.makeText( context , "No Internet" , Toast.LENGTH_SHORT ).show();

    }

    public static boolean isNetworkAvailable( Context context ) {

        try {

            ConnectivityManager conn = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            if ( ( networkInfo != null && networkInfo.isConnected() ) )

                return true;

        } catch ( Exception e ) {

            return false;

        }

        return false;

    }

}

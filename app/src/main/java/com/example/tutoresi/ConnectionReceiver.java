package com.example.tutoresi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

/**
 * Permet de vérifier la connexion internet en background.
 * BroadCastReceivers permet d'écouter les signaux du système ou d'autres applications
 */
public class ConnectionReceiver extends BroadcastReceiver {

    private boolean isConnected;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            showAlertDialog(context,context.getString(R.string.noConnectionInternetTitle),
                    context.getString(R.string.noConnectionInternetMsg));
        }
    }


    /**
     * Affiche une alert dialog avec un titre et un message donné.
     * Et termine l'activity au click du boutton.
     * @param  context context.
     * @param title title du dialog
     * @param message message du dialog
     */
    public void showAlertDialog(final Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.icon)
                .setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finishAffinity(); // clear all stack of activities previously opened by an application.
                    }
                })
                .create().show();
    }

}
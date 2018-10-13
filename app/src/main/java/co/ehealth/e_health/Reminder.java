package co.ehealth.e_health;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;

public class Reminder extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        MediaPlayer mediaplayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaplayer.start();


    }

//    private void displayAlert(final Context context)
//    {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setMessage("Are you sure you want to exit?").setCancelable(
//                false).setPositiveButton("Yes",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                }).setNegativeButton("No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
}

package co.ehealth.e_health;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.muddzdev.styleabletoast.StyleableToast;

public class Reminder extends BroadcastReceiver {

    public static final String CHANNEL_ID = "co.ehealth.e_health.ANDROID";

    @Override
    public void onReceive(Context context, Intent intent) {

        Vibrator vibrator = (Vibrator) context
                .getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);

        MediaPlayer mediaplayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaplayer.start();

    }

}

package com.feedback.sync;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.feedback.db_helper.DbController;
import com.feedback.model.Feedback;
import com.feedback.model.Success;
import com.feedback.restCall.APIClient;
import com.feedback.restCall.APIInterface;
import com.feedback.utils.NetworkUtils;
import com.feedback.utils.SharedPrefUtils;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shridhar on 30/6/17.
 */

public class FeedbackSync extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isConnected = NetworkUtils.isNetworkConnected(context);
        boolean isSync = SharedPrefUtils.getSyncStatus(context);
        DbController controller = new DbController(context);
        APIInterface service = APIClient.getClient().create(APIInterface.class);

        if (isConnected && !isSync) {
//            Log.d("Sync", "Syncing");
//            Toast.makeText(context, "Syncing", Toast.LENGTH_SHORT).show();
            ;
            sendFeedback(context, controller,service );
//            new MainActivity().syncFeedback();
//            Intent newIntent = new Intent(context, MainActivity.class);
//            newIntent.putExtra("submit",true);
//            context.startActivity(newIntent);
        } else {
            Toast.makeText(context, "Please Connect to Internet / Already Synced", Toast.LENGTH_LONG).show();
        }
    }

    public void sendFeedback(final Context context, final DbController controller, APIInterface service) {
        List<Feedback> feedback = controller.getFeedBack();
        if (!feedback.isEmpty()) {
            Call<Success> sendFeedback = service.sendFeedback(feedback);
            sendFeedback.enqueue(new Callback<Success>() {
                @Override
                public void onResponse(Call<Success> call, Response<Success> response) {
                    if (response.isSuccessful()) {
                        controller.dropTable("FEEDBACK");
                        SharedPrefUtils.updateSyncStatus(context, true);
                        Toast.makeText(context, "Sucessfully updated in server", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "Error while Updating in server", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Success> call, Throwable t) {
                    Log.d("Failure", t.toString());
                    Log.d("Sucess/Fail", call + "");
                }
            });
        }
    }

    public void setAlarm(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, FeedbackSync.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        Log.d("Alarm", "Started");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(calendar.MINUTE, 00);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
    }

}

package info.admirsabanovic.arenafight.handler;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import info.admirsabanovic.arenafight.activities.ChooseRaceActivity;

/**
 * Created by critical on 4/27/15.
 */
public class EventExecutor {
    public static synchronized void startEvent(String event, JSONObject json){
        if(event.equals("tosth")){
            //do next
        }
    }

    public static synchronized void startNewActivityEvent(Context context, Class activityClass, JSONObject data){
            Intent intent = new Intent(context, activityClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("json", data.toString());
            context.startActivity(intent);
    }
}

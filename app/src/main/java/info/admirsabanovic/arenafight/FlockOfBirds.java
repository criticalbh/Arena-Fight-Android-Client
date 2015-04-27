package info.admirsabanovic.arenafight;


import android.util.Log;

/**
 * Created by critical on 4/27/15.
 */
public class FlockOfBirds implements MoodListener {
    public void moodReceived(MoodEvent event) {
        if( event.getOnEvent().equals("admir") )
        {
            Log.v("kljuc", "updsfsdfali smo");
            System.out.println( "Birds are singing!" );
        }
    }

}

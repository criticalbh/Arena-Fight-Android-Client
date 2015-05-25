package info.admirsabanovic.arenafight.handler;

import android.content.Context;
import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import info.admirsabanovic.arenafight.activities.GameActivity;
import info.admirsabanovic.arenafight.activities.HomeActivity;

/**
 * Created by critical on 4/27/15.
 */
public class SocketListener {

    public SocketListener(String event){
        _event = event;
    }
    public SocketListener(String event, Context ctx){
        _event = event;
        _context = ctx;
    }
    public SocketListener(String event, Context ctx, Class newActivity){
        _event = event;
        _context = ctx;
        _newActivity = newActivity;
    }

    private Emitter.Listener emitter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    if(_context != null && _event.equals("newActivity")){
                        try {
                            boolean first_login = data.getBoolean("first_login");
                            if(first_login == false){
                                EventExecutor.startNewActivityEvent(_context, GameActivity.class, data);
                            }else{
                                EventExecutor.startNewActivityEvent(_context, _newActivity, data);
                            }
                        } catch (JSONException e) {
                            return;
                        }
                    }
                }
            }).start();
        }
    };

    public String get_event() {
        return _event;
    }

    public void set_event(String _event) {
        this._event = _event;
    }

    public Context get_context() {
        return _context;
    }

    public void set_context(Context _context) {
        this._context = _context;
    }

    public Class get_newActivity() {
        return _newActivity;
    }

    public void set_newActivity(Class _newActivity) {
        this._newActivity = _newActivity;
    }

    public Emitter.Listener getEmitter(){
        return emitter;
    }

    private String _event;
    private Context _context;
    private Class _newActivity;
}

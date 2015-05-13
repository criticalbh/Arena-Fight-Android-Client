package info.admirsabanovic.arenafight.tcp;

/**
 * Created by critical on 4/20/15.
 */

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class SocketIO {
    private static Socket mSocket = null;

    protected SocketIO() {
        // Exists only to defeat instantiation.
    }
    public static Socket getInstance() {
        if(mSocket == null) {
            try {
                mSocket = IO.socket("http://192.168.3.137:3000/");
            } catch (URISyntaxException e) {}
        }
        return mSocket;
    }
}

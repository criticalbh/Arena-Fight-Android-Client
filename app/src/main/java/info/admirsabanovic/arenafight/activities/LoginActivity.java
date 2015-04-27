
package info.admirsabanovic.arenafight.activities;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import info.admirsabanovic.arenafight.R;
import info.admirsabanovic.arenafight.handler.SocketListener;
import info.admirsabanovic.arenafight.tcp.SocketIO;


public class LoginActivity extends Activity {

    EditText    Username;
    EditText    Password;
    Button      LoginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        SocketListener socketListener = new SocketListener(
                "newActivity",
                getApplicationContext(),
                ChooseRaceActivity.class
        );

        SocketIO.getInstance().connect();
        SocketIO.getInstance().on("loginResponse", socketListener.getEmitter());

        Username = (EditText) findViewById(R.id.txtUsername);
        Password = (EditText) findViewById(R.id.txtPassword);
        LoginBtn = (Button) findViewById(R.id.btnLogin);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketIO.getInstance().emit("login",
                        Username.getText().toString(),
                        Password.getText().toString());
            }
        });
    }
}

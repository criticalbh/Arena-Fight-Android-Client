
package info.admirsabanovic.arenafight;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import info.admirsabanovic.arenafight.tcp.SocketIO;


public class LoginActivity extends Activity {

    EditText    Username;
    EditText    Password;
    Button      LoginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        SocketIO.getInstance().connect();
        SocketIO.getInstance().on("loginResponse", loginResponse);
        //
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

    private Emitter.Listener loginResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    /*int id;
                    int numUsers;

                    int race;
                    int u_class;
                    String username;*/

                    try {
                        boolean first_login;
                        first_login = data.getBoolean("first_login");
                        if(first_login == true){
                            //parsing JSONObject obj = new JSONObject(getIntent().getStringExtra("json"));
                            Intent intent = new Intent(LoginActivity.this, ChooseRaceActivity.class);
                            intent.putExtra("json", data.toString());
                            startActivity(intent);
                        }else{
                            //@TODO go to main menu
                        }
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };
}

package info.admirsabanovic.arenafight.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import info.admirsabanovic.arenafight.R;
import info.admirsabanovic.arenafight.tcp.SocketIO;


public class ChooseClassActivity extends Activity {


    Button btnMage;
    Button btnWar;
    JSONObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_class);

        try {
            obj = new JSONObject(getIntent().getStringExtra("json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnMage = (Button) findViewById(R.id.btnMage);
        btnWar = (Button) findViewById(R.id.btnWar);
        btnMage.setOnClickListener(onClickListener);
        btnWar.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.btnMage:
                    goToHome(1);
                    break;
                case R.id.btnWar:
                    goToHome(2);
                    break;
            }
        }
    };

    private void savePlayer(){
        SocketIO.getInstance().emit("savePlayer", obj);
    }

    private void goToHome(int u_class){
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        obj.remove("class");
        try {
            obj.put("class", u_class);
            obj.put("first_login", false);
            savePlayer();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("json", obj.toString());
        startActivity(intent);
    }
}

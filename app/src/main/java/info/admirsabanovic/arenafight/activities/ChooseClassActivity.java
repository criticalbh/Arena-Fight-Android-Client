package info.admirsabanovic.arenafight.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import info.admirsabanovic.arenafight.R;


public class ChooseClassActivity extends ActionBarActivity {


    Button btnMage;
    Button btnWar;
    JSONObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_class);

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
    private void goToHome(int u_class){
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        obj.remove("class");
//        try {
//            obj.put("class", u_class);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        intent.putExtra("json", obj.toString());
//        startActivity(intent);
    }
}

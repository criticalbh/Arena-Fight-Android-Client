package info.admirsabanovic.arenafight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class ChooseRaceActivity extends Activity {

    Button btnHuman;
    Button btnOrc;
    JSONObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_race);
        try {
            obj = new JSONObject(getIntent().getStringExtra("json"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        btnHuman = (Button) findViewById(R.id.btnHuman);
        btnOrc = (Button) findViewById(R.id.btnOrc);


        btnHuman.setOnClickListener(onClickListener);
        btnOrc.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            switch(v.getId()){
                case R.id.btnHuman:
                    goSelectClass(1);
                    break;
                case R.id.btnOrc:
                    goSelectClass(2);
                    break;
            }
        }
    };
    private void goSelectClass(int race){
        Intent intent = new Intent(this, ChooseClassActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION);
        obj.remove("race");
        try {
            obj.put("race", race);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtra("race", obj.toString());
        startActivity(intent);
    }
}

package info.admirsabanovic.arenafight.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import info.admirsabanovic.arenafight.R;

public class HomeActivity extends Activity {

    TextView onlinePlayers;
    JSONObject obj;
    Map<String, Integer> characterImageMap;
    ImageView characterHolder;
    ProgressBar healthBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initialResources();
        try {
            obj = new JSONObject(getIntent().getStringExtra("json"));
            updateWorldStatistics(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //1 human 2 orc
    //1 mage 2 warrior
    void initialResources(){
        onlinePlayers = (TextView)findViewById(R.id.txtOnlinePlayers);
        characterImageMap = new HashMap<String, Integer>();
        characterImageMap.put("humanmage", R.drawable.humanmage);
        characterImageMap.put("humanwarrior", R.drawable.humanwarrior);
        characterImageMap.put("orcmage", R.drawable.orcmage);
        characterImageMap.put("orcwarrior", R.drawable.orcwarrior);
        characterHolder = (ImageView)findViewById(R.id.characterHolder);
        healthBar = (ProgressBar)findViewById(R.id.healthBar);
    }

    void updateWorldStatistics(JSONObject data) throws JSONException {
        int numberOnlinePlayers = data.getInt("numUsers");
        onlinePlayers.setText(String.valueOf(numberOnlinePlayers));
        int race = data.getInt("race");
        int clas = data.getInt("class");
        setCharacterHolder(race, clas);
    }

    void setCharacterHolder(int r, int c){
        if(r == 1 && c == 1)
            characterHolder.setImageResource(characterImageMap.get("humanmage"));
        else if(r == 1 && c == 2)
            characterHolder.setImageResource(characterImageMap.get("humanwarrior"));
        else if (r == 2 && c == 1)
            characterHolder.setImageResource(characterImageMap.get("orcmage"));
        else if (r == 2 && c == 2)
            characterHolder.setImageResource(characterImageMap.get("orcwarrior"));
    }

}

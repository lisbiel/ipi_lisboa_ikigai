package br.com.lisboa.ikigai;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar =
                findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        Configuration c = getResources().getConfiguration();
        int screenLayout = c.screenLayout;
        int tamanhoDaTela = screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;
        if (tamanhoDaTela ==
                Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }
}

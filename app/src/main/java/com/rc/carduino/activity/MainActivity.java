package com.rc.carduino.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.rc.carduino.R;
import com.rc.carduino.fragment.ControllerFragment;
import com.rc.carduino.fragment.PreferencesFragment;

/*
 * This activity hosts the controller fragment
 */

public class MainActivity extends AppCompatActivity implements ControllerFragment.OnControllerFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.controller_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Controller");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preferences, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // go to preferences activity
                Intent intent = new Intent(this, PreferencesActivity.class);
                startActivity(intent);
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // communication link between this activity and hosted fragment (ControllerFragment)
    @Override
    public void onControllerFragmentInteraction(Uri uri) {
        // do nothing for now
    }
}

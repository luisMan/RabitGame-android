package com.agpfd.whackamole;



import java.io.IOException;

import com.agpfd.whackamole.R;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class WhackAMoleActivity extends Activity implements OnKeyListener{

    
    private WhackAMoleView myWhackAMoleView;

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        					 WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.whackamole_layout);
        myWhackAMoleView = (WhackAMoleView) findViewById(R.id.mole);
        myWhackAMoleView.setKeepScreenOn(true);
        myWhackAMoleView.setOnKeyListener(this);
      
		
    }

   
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		Toast.makeText(getApplicationContext(), "back has been press",Toast.LENGTH_LONG).show();
		if(v.getId() == KeyEvent.KEYCODE_BACK)
		{
			Toast.makeText(getApplicationContext(), "back has been press",Toast.LENGTH_LONG).show();
			return true;
		}
		return false;
	}

    
}
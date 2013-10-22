package media.player.activities;

import java.util.ArrayList;

import media.player.models.Music;

import com.example.media.player.audiolys.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class AudioActivity extends Activity {
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);        
	        setContentView(R.layout.activity_music);
	        

	    		ArrayList<Music> musics = (ArrayList<Music>) getIntent().getExtras().getSerializable("listMusics");
	    		Log.e("musics", musics.toString());
	 }
}

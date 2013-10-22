package media.player.activities;

import java.util.ArrayList;

import com.example.media.player.audiolys.R;

import media.player.fragments.AudioFragment;
import media.player.fragments.AudioFragment.Orders;
import media.player.fragments.AudioFragment.onPlayButtonClickListener;
import media.player.models.Music;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Button;

public class AudioActivity extends Activity implements
		onPlayButtonClickListener {

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);

		ArrayList<Music> musics = (ArrayList<Music>) getIntent().getExtras()
				.getSerializable("listMusics");
		int selectedMusic = getIntent().getExtras().getInt("selectedMusic");

		AudioFragment audioFragment = (AudioFragment) getFragmentManager()
				.findFragmentById(R.id.audio_fragment);

		audioFragment.sendData(musics, selectedMusic);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onPlayButtonClick(Orders order) {
		// TODO Auto-generated method stub
		AudioFragment audioFragment = (AudioFragment) getFragmentManager()
				.findFragmentById(R.id.audio_fragment);

		if (audioFragment != null) {
			switch (order) {
			case PLAY:
				audioFragment.playMusic();
				break;
			default:
				break;
			}
		}
	}
}

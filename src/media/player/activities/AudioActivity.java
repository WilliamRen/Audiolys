package media.player.activities;

import java.util.ArrayList;
import com.example.media.player.audiolys.R;
import media.player.fragments.AudioFragment;
import media.player.fragments.AudioFragment.Orders;
import media.player.fragments.AudioFragment.onChangeEvents;
import media.player.fragments.ViewerFragment;
import media.player.models.Music;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.WindowManager;

public class AudioActivity extends Activity implements onChangeEvents {

	/* Variables */
	AudioFragment audioFragment = null;
	ViewerFragment viewerFragment = null;
	ArrayList<Music> musics;
	/* End of Variables */

	@SuppressWarnings("unchecked")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music);
		
		// prevent activity from sleeping
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		musics = (ArrayList<Music>) getIntent().getExtras().getSerializable(
				"listMusics");
		int selectedMusic = getIntent().getExtras().getInt("selectedMusic");

		// Get the fragments from the fragment manager
		audioFragment = (AudioFragment) getFragmentManager().findFragmentById(
				R.id.audio_fragment);
		viewerFragment = (ViewerFragment) getFragmentManager()
				.findFragmentById(R.id.viewer_fragment);

		// Send data
		viewerFragment.sendImageToViewer(musics.get(selectedMusic), Orders.PLAY);
		audioFragment.sendData(musics, selectedMusic);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onChangeE(Orders o, int s) {
		
		/* Show info about memory */
		/*ActivityManager activityManager = (ActivityManager) getBaseContext()
				.getSystemService(ACTIVITY_SERVICE);
		android.app.ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(memoryInfo);

		// Memory
		Log.i("simon", " memoryInfo.availMem " + memoryInfo.availMem + "\n");
		Log.i("simon", " memoryInfo.lowMemory " + memoryInfo.lowMemory + "\n");
		Log.i("simon", " memoryInfo.threshold " + memoryInfo.threshold + "\n");
		 */

		// Depends of the order received - change the animation in ViewerFragment
		switch (o) {
		case NEXT:
			//Log.w("simon", "test callback");
			viewerFragment.sendImageToViewer(musics.get(s), Orders.NEXT);
			break;

		case PREVIOUS:
			viewerFragment.sendImageToViewer(musics.get(s), Orders.PREVIOUS);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
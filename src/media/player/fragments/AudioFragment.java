package media.player.fragments;

import java.util.ArrayList;
import com.example.media.player.audiolys.R;
import media.player.models.AudioPlayer;
import media.player.models.Music;
import media.player.models.ShakeDetector;
import media.player.models.ShakeDetector.OnShakeListener;
import media.player.utils.HeadsetPlugReceiver;
import media.player.utils.MediaUtils;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioFragment extends Fragment implements OnClickListener,
		SeekBar.OnSeekBarChangeListener {

	/* Variables */
	private AudioPlayer audioPlayer;
	private SeekBar progressBarMusic;
	private Handler musicHandler = new Handler();
	private TextView textViewCurrentPosition;
	private TextView textviewTotalDuration;
	private TextView textViewMusicName;
	private TextView textViewNumberTracks;
	private ImageView playButton;
	private ImageView nextButton;
	private ImageView previousButton;
	private ImageView repeatButton;
	private ImageView shuffleButton;
	private onChangeEvents mCallBackEvent;
	//private ArrayList<Music> musics = new ArrayList<Music>();
	//private int selectedMusic = -1;
	private Repeat isRepeating = Repeat.NONE;
	private boolean isShuffling = false;
	private boolean isShakeActivated = false;
	private SharedPreferences prefs;
	private HeadsetPlugReceiver headsetPlugReceiver;
	// The following are used for the shake detection
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private ShakeDetector mShakeDetector;

	// Enumeration
	public enum Repeat {
		NONE, ONE, ALL;
	}
	public enum Orders {
		PLAY, PAUSE, NEXT, PREVIOUS;
	}
	public enum Event {
		PUSH, END;
	}

	/* Enf of Variables */

	public interface onChangeEvents {
		public void onChangeE(Orders o, int selectedMusic);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallBackEvent = (onChangeEvents) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnNavButtonClickListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Get the view from the layout
		View v = inflater.inflate(R.layout.audio_fragment, container, false);

		// Bind Java code and XML
		progressBarMusic = (SeekBar) v.findViewById(R.id.seekBarAudio);
		textViewMusicName = (TextView) v.findViewById(R.id.textViewMusicName);
		textViewNumberTracks = (TextView) v
				.findViewById(R.id.textViewNumberTracks);
		textViewCurrentPosition = (TextView) v
				.findViewById(R.id.textViewMusicCurrentPosition);
		textviewTotalDuration = (TextView) v
				.findViewById(R.id.textViewMusicTotalDuration);
		playButton = (ImageView) v.findViewById(R.id.imageViewPlayButton);
		nextButton = (ImageView) v.findViewById(R.id.imageViewNextButton);
		previousButton = (ImageView) v
				.findViewById(R.id.imageViewPreviousButton);
		repeatButton = (ImageView) v.findViewById(R.id.imageViewRepeat);
		shuffleButton = (ImageView) v.findViewById(R.id.imageViewShuffle);

		// Set a listener on buttons
		playButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		previousButton.setOnClickListener(this);
		repeatButton.setOnClickListener(this);
		shuffleButton.setOnClickListener(this);
		progressBarMusic.setOnSeekBarChangeListener(this);
		textViewMusicName.setSelected(true); // make the text moving

		// Set an audio manager + context
		AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		audioPlayer = new AudioPlayer(am);
		
		audioPlayer.mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				playNext(Event.END); // When the player reach the end of the song
			}
		});

		// Get the user's preferences - shaking
		prefs = getActivity().getSharedPreferences("myUserSettings", 0);
		isShakeActivated = prefs.getBoolean("shake", false);

		// ShakeDetector initialization
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mShakeDetector = new ShakeDetector();
		mShakeDetector.setOnShakeListener(new OnShakeListener() {
			@Override
			public void onShake(int count) {
				// If playing and shake is activated - change music
				if (audioPlayer.isPlaying() && isShakeActivated) {
					audioPlayer.shake2Shuffle();
					refresh();// refresh the display
					mCallBackEvent.onChangeE(Orders.NEXT, audioPlayer.getSelectedMusic());
					positionInTracks();
				}
			}
		});

		// Headphones plugged/unplugged detector
		headsetPlugReceiver = new HeadsetPlugReceiver(this);
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.HEADSET_PLUG");
		getActivity().registerReceiver(headsetPlugReceiver, intentFilter);

		// configure menu
		setHasOptionsMenu(true);

		// Return view
		return v;
	}

	// Inflate a specific menu
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.audio, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.itemShake).setChecked(isShakeActivated);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences.Editor editor = prefs.edit();

		// Handle item selection
		switch (item.getItemId()) {
		
		case R.id.itemShake:
			// Get the "shake" value
			if (item.isChecked()) {
				item.setChecked(false);
				isShakeActivated = false;
				editor.putBoolean("shake", false);
			} else {
				item.setChecked(true);
				isShakeActivated = true;
				editor.putBoolean("shake", true);
			}
			editor.commit();
			//Log.w("simon", "shake : " + isShakeActivated);
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// Data sent from AudioActivity
	public void sendData(ArrayList<Music> musics, int selectedMusic) {
		this.audioPlayer.load(musics, selectedMusic);
		this.audioPlayer.loading();
		playMusic();
		refresh();
	}

	/*********************************************************************************/
	/** Implemented methods **/
	/*********************************************************************************/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// Click on play button
		case R.id.imageViewPlayButton:
			playMusic();
			break;

		// Click on next button
		case R.id.imageViewNextButton:
			playNext(Event.PUSH);
			break;

		// Click on previous button
		case R.id.imageViewPreviousButton:
			playPrevious();
			break;

		// Click on repeat button
		case R.id.imageViewRepeat:
			repeatSongs();
			break;
		
		// Click on shuffle button
		case R.id.imageViewShuffle:
			shuffleSongs();
			break;

		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
	}

	// When user start touching/clicking the progressbar
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// remove function callback - bug otherwise
		musicHandler.removeCallbacks(progressBarUpdateTime);
	}

	// When user stop touching/clicking the progressbar
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// remove function callback - bug otherwise
		musicHandler.removeCallbacks(progressBarUpdateTime);
		int currentPosition = MediaUtils.progressToTimer(
				progressBarMusic.getProgress(), audioPlayer.getDuration());

		// forward or backward to certain seconds
		audioPlayer.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/*********************************************************************************/
	/** End of implemented methods **/
	/*********************************************************************************/

	/*********************************************************************************/
	/** Required methods related to audio playing **/
	/*********************************************************************************/

	public void refresh() {
		textViewMusicName.setText(this.audioPlayer.getSongName());
		textViewNumberTracks.setText(this.audioPlayer.getNumberOfTracks());
		positionInTracks();
		this.progressBarMusic.setMax(100);
		this.progressBarMusic.setProgress(0);
	}

	// Replace icons : greys or reds
	public void positionInTracks() {
		// + 2
		// - selectedMusic starts from 0 while musics.size() counts the number
		// of rows
		// - we want to know if selectedMusic goes over musics.size()
		this.nextButton.setImageResource(0);
		this.nextButton.setBackgroundResource(0);
		this.previousButton.setImageResource(0);
		this.previousButton.setBackgroundResource(0);

		int selectedMusic = this.audioPlayer.getSelectedMusic();
		int size = this.audioPlayer.getMusic().size();
		
		if (selectedMusic + 2 > size) {
			this.nextButton.setImageResource(R.drawable.next_button_grey);
			this.previousButton
					.setBackgroundResource(R.drawable.selector_previous_button);
		} else if (selectedMusic - 1 < 0) {
			this.previousButton
					.setImageResource(R.drawable.previous_button_grey);
			this.nextButton
					.setBackgroundResource(R.drawable.selector_next_button);
		} else {
			this.nextButton
					.setBackgroundResource(R.drawable.selector_next_button);
			this.previousButton
					.setBackgroundResource(R.drawable.selector_previous_button);
		}
	}

	// Function called when a music is about to be playing
	public void playMusic() {
		if(this.audioPlayer.play())
		{
			playButton.setBackgroundResource(R.drawable.selector_pause_button);
			updateProgressBar();
		}
		else
		{
			playButton.setBackgroundResource(R.drawable.selector_play_button);
		}
	}

	public void playNext(Event ev) {
		if(this.audioPlayer.next(isRepeating, ev, isShuffling, playButton))
		{
			refresh();// refresh the display
			mCallBackEvent.onChangeE(Orders.NEXT, this.audioPlayer.getSelectedMusic());
		}
		positionInTracks();
	}

	// Play previous song
	public void playPrevious() {
		if(this.audioPlayer.previous(previousButton, isRepeating))
		{
			refresh(); // refresh the display
			mCallBackEvent.onChangeE(Orders.PREVIOUS, this.audioPlayer.getSelectedMusic());
		}
		positionInTracks();
	}

	// Update the progressbar, see progressBarUpdateTime
	public void updateProgressBar() {
		// Delayed call to progressBarUpdateTime function - 100 milliseconds
		musicHandler.postDelayed(progressBarUpdateTime, 100);
	}

	// Update the progressbar
	private Runnable progressBarUpdateTime = new Runnable() {
		public void run() {

			long totalDuration = audioPlayer.getDuration();
			long currentDuration = audioPlayer.getCurrentPosition();

			// Update textviews
			textViewCurrentPosition.setText(MediaUtils
					.millisecondsToTimer(currentDuration));
			textviewTotalDuration.setText(MediaUtils
					.millisecondsToTimer(totalDuration));

			// Updating progress bar
			int progress = (int) (MediaUtils.getProgressPercentage(
					currentDuration, totalDuration));
			progressBarMusic.setProgress(progress);

			// Delayed call to itself - 100 milliseconds
			musicHandler.postDelayed(this, 100);
		}
	};

	public void repeatSongs() {

		// this.repeatButton.setImageResource(0);
		this.repeatButton.setBackgroundResource(0);
		switch (isRepeating) {

		// Change from "repeat nothing" state to "repeat just one"
		case NONE:
			// this.repeatButton.setImageResource(R.drawable.repeatone);
			this.repeatButton
					.setBackgroundResource(R.drawable.selector_repeat_one_button);
			this.audioPlayer.repeat(true);
			isRepeating = Repeat.ONE;
			break;

		// Change from "repeat just one" state to "repeat all"
		case ONE:
			// this.repeatButton.setImageResource(R.drawable.repeat);
			this.repeatButton
					.setBackgroundResource(R.drawable.selector_repeat_all_button);
			this.audioPlayer.repeat(false);
			isRepeating = Repeat.ALL;
			break;

		// Change from "repeat all" state to "repeat nothing"
		case ALL:
			isRepeating = Repeat.NONE;
			// this.repeatButton
			// .setImageResource(R.drawable.repeat_button_released);
			this.repeatButton
					.setBackgroundResource(R.drawable.selector_repeat_button);
			break;
		}
	}

	// Shuffle songs
	public void shuffleSongs() {

		if (isShuffling) {
			shuffleButton
					.setBackgroundResource(R.drawable.selector_shuffle_button);
			isShuffling = false;
		} else {
			shuffleButton
					.setBackgroundResource(R.drawable.selector_shuffle_on_button);
			isShuffling = true;
		}
	}

	/********************************************************************************
	 * /** End of required methods related to audio playing
	 **/
	/*********************************************************************************/

	/*********************************************************************************/
	/** Related to the lifecycle of the application **/
	/*********************************************************************************/

	public void onStop() {
		musicHandler.removeCallbacks(progressBarUpdateTime);
		super.onStop();
	};

	@Override
	public void onPause() {
		mSensorManager.unregisterListener(mShakeDetector); 
		musicHandler.removeCallbacks(progressBarUpdateTime);
		audioPlayer.pause(); // pause player
		playButton.setBackgroundResource(R.drawable.selector_play_button);
		super.onPause();
	}

	@Override
	public void onResume() {
		mSensorManager.registerListener(mShakeDetector, mAccelerometer,
				SensorManager.SENSOR_DELAY_UI); // headphone listener
		super.onResume();
	}

	@Override
	public void onDestroy() {
		audioPlayer.stop(); // stop player
		if (headsetPlugReceiver != null) {
			getActivity().unregisterReceiver(headsetPlugReceiver);
			headsetPlugReceiver = null;
		}
		super.onDestroy();
	}

	/*********************************************************************************/
	/** End of functions related to the lifecycle of the application **/
	/*********************************************************************************/
}
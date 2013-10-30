package media.player.fragments;

import java.util.ArrayList;
import java.util.Random;

import com.example.media.player.audiolys.R;
import media.player.models.AudioPlayer;
import media.player.models.Music;
import media.player.models.ShakeDetector;
import media.player.models.ShakeDetector.OnShakeListener;
import media.player.utils.MediaUtils;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
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
	private ArrayList<Music> musics = new ArrayList<Music>();
	private int selectedMusic = -1;
	private Repeat isRepeating = Repeat.NONE;
	private boolean isShuffling = false;
	
	
	// The following are used for the shake detection
		private SensorManager mSensorManager;
		private Sensor mAccelerometer;
		private ShakeDetector mShakeDetector;

	public enum Repeat {
		NONE, ONE, ALL;
	}

	public enum Orders {
		PLAY, PAUSE, NEXT, PREVIOUS;
	}

	public enum Event {
		PUSH, END;
	}

	/* EOVariables */

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
		AudioManager am = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);

		audioPlayer = new AudioPlayer(am);
		
		// ShakeDetector initialization
		mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new OnShakeListener() {
 
            @Override
            public void onShake(int count) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                //handleShakeEvent(count);
            	selectedMusic = giveMeARandomNumber(0, musics.size()-1);
            	playNext(Event.PUSH);
            }
        });

		// Return view
		return v;
	}

	// Receive data from AudioActivity
	public void sendData(ArrayList<Music> musics, int selectedMusic) {

		this.selectedMusic = selectedMusic;
		this.musics = musics;

		this.audioPlayer.loading(musics.get(selectedMusic));
		refresh();
		playMusic();

		// When the player finishes to read the song
		this.audioPlayer.mediaPlayer
				.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

					public void onCompletion(MediaPlayer mp) {
						// TODO Auto-generated method stub
						playNext(Event.END);
					}
				});
	}

	/*********************************************************************************/
	/** Implemented methods **/
	/*********************************************************************************/

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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

		case R.id.imageViewRepeat:
			repeatSongs();
			break;

		case R.id.imageViewShuffle:
			shuffleSongs();
			break;

		default:
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// remove function callback - bug otherwise
		musicHandler.removeCallbacks(progressBarUpdateTime);
	}

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
		textViewMusicName.setText(this.musics.get(selectedMusic).getTitle());
		textViewNumberTracks.setText("Track " + (selectedMusic + 1) + "/"
				+ musics.size());
		positionInTracks();
		this.progressBarMusic.setMax(100);
		this.progressBarMusic.setProgress(0);
	}

	public void positionInTracks() {
		// + 2
		// - selectedMusic starts from 0 while musics.size() counts the number
		// of rows
		// - we want to know if selectedMusic goes over musics.size()
		this.nextButton.setImageResource(0);
		this.nextButton.setBackgroundResource(0);
		this.previousButton.setImageResource(0);
		this.previousButton.setBackgroundResource(0);

		if (selectedMusic + 2 > musics.size()) {
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
		// is the player playing?
		if (this.audioPlayer.isPlaying()) {
			// Change play to pause button
			playButton.setBackgroundResource(R.drawable.selector_play_button);
			this.audioPlayer.pause();
		} else {
			this.audioPlayer.play();
			playButton.setBackgroundResource(R.drawable.selector_pause_button);
			// Defines default values for the seekbar
			updateProgressBar();
		}
	}

	public void playNext(Event ev) {

		boolean flag = false;
		// Check if a music is playing
		if (this.audioPlayer.isPlaying() || ev.equals(Event.END))
			flag = true;

		// Which type of repeat
		if (isRepeating.equals(Repeat.ALL)) {

			// Reach the end of the list
			if ((selectedMusic + 2) > this.musics.size()) {
				selectedMusic = -1;
				playNext(Event.END);
			} else {
				if(isShuffling)
					selectedMusic = giveMeARandomNumber(0, musics.size()-1);
				else
					selectedMusic++;
				this.audioPlayer.loading(musics.get(selectedMusic));
				if (flag)
					this.audioPlayer.play();

				refresh();// refresh the display
				mCallBackEvent.onChangeE(Orders.NEXT, selectedMusic);
			}
		} else if (isRepeating.equals(Repeat.ONE)) {

			if ((selectedMusic + 2) > this.musics.size()) {

			} else {
				if (ev.equals(Event.PUSH)) {
					selectedMusic++;
					this.audioPlayer.loading(musics.get(selectedMusic));
					if (flag)
						this.audioPlayer.play();

					refresh();// refresh the display
					mCallBackEvent.onChangeE(Orders.NEXT, selectedMusic);
				}
				this.audioPlayer.repeat(true);
			}

		} else {
			
			if ((selectedMusic + 2) > this.musics.size()) { // Reach the end of the list
				this.playButton.setBackgroundResource(R.drawable.selector_play_button);
			} else {
				if(isShuffling)
					selectedMusic = giveMeARandomNumber(0, musics.size()-1);
				else
					selectedMusic++;
				
				this.audioPlayer.loading(musics.get(selectedMusic));
				if (flag)
					this.audioPlayer.play();

				refresh();// refresh the display
				mCallBackEvent.onChangeE(Orders.NEXT, selectedMusic);
			}
		}
		positionInTracks();
	}

	public void playPrevious() {

		boolean flag = false;
		if (this.audioPlayer.isPlaying())
			flag = true;

		if (selectedMusic - 1 < 0) {
			// Reach the beginning of the music list
		} else {
			this.previousButton
					.setBackgroundResource(R.drawable.previous_button_released);
			selectedMusic--;
			this.audioPlayer.loading(musics.get(selectedMusic));
			if (flag)
				this.playMusic();
			refresh(); // refresh the display
			mCallBackEvent.onChangeE(Orders.PREVIOUS, selectedMusic);

			if (isRepeating.equals(Repeat.ONE))
				this.audioPlayer.repeat(true);
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

		//this.repeatButton.setImageResource(0);
		this.repeatButton.setBackgroundResource(0);
		switch (isRepeating) {

		// Change from "repeat nothing" state to "repeat just one"
		case NONE:
			//this.repeatButton.setImageResource(R.drawable.repeatone);
			this.repeatButton.setBackgroundResource(R.drawable.selector_repeat_one_button);
			this.audioPlayer.repeat(true);
			isRepeating = Repeat.ONE;
			break;

		// Change from "repeat just one" state to "repeat all"
		case ONE:
			//this.repeatButton.setImageResource(R.drawable.repeat);
			this.repeatButton.setBackgroundResource(R.drawable.selector_repeat_all_button);
			this.audioPlayer.repeat(false);
			isRepeating = Repeat.ALL;
			break;

		// Change from "repeat all" state to "repeat nothing"
		case ALL:
			isRepeating = Repeat.NONE;
			//this.repeatButton
			//		.setImageResource(R.drawable.repeat_button_released);
			this.repeatButton.setBackgroundResource(R.drawable.selector_repeat_button);
			break;
		}
	}

	public void shuffleSongs() {

		if(isShuffling)
		{
			shuffleButton.setBackgroundResource(R.drawable.selector_shuffle_button);
			isShuffling = false;
		}
		else
		{
			shuffleButton.setBackgroundResource(R.drawable.selector_shuffle_on_button);
			isShuffling = true;

		}
	}
	
	public int giveMeARandomNumber(int min, int max)
	{
		Random r = new Random();
		int rand= r.nextInt(max - min + 1) + min;
		return rand;
	}

	/********************************************************************************
	 * /** End of required methods related to audio playing
	 **/
	/*********************************************************************************/

	/*********************************************************************************/
	/** Related to the lifecycle of the application **/
	/*********************************************************************************/

	public void onStop() {
		Log.w("simon", "stop");
		musicHandler.removeCallbacks(progressBarUpdateTime);
		// audioPlayer.stop(); // simon - bug onStop - correct it out
		Log.w("simon", "finish stop");
		super.onStop();
	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

		Log.w("simon", "pause");
		mSensorManager.unregisterListener(mShakeDetector);
		musicHandler.removeCallbacks(progressBarUpdateTime);
		audioPlayer.pause();
		playButton.setBackgroundResource(R.drawable.selector_play_button);
		Log.w("simon", "finish pause");
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
		Log.w("simon", "resume");
		Log.w("simon", "finish resume");
		super.onResume();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.w("simon", "destroy");
		audioPlayer.stop();
		Log.w("simon", "finish destroy");
		super.onDestroy();
	}
}

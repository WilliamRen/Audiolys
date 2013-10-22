package media.player.fragments;

import java.util.ArrayList;

import com.example.media.player.audiolys.R;

import media.player.activities.AudioActivity;
import media.player.models.AudioPlayer;
import media.player.models.Music;
import media.player.utils.MediaUtils;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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

	private ImageView playButton;
	private ImageView nextButton;
	private ImageView previousButton;

	ArrayList<Music> musics = new ArrayList<Music>();
	int selectedMusic = -1;

	/* EOVariables */

	// Enumerate : give orders
	public enum Orders {
		PLAY, PAUSE, NEXT, PREVIOUS, STOP;
	}

	public onPlayButtonClickListener mCallBack;

	public interface onPlayButtonClickListener {
		public void onPlayButtonClick(Orders order);
	}

	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallBack = (onPlayButtonClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnNavButtonClickListener");
		}
	}

	public void sendData(ArrayList<Music> musics, int selectedMusic) {
		this.musics = musics;
		this.selectedMusic = selectedMusic;
		initMusic();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Get the view from the layout
		View v = inflater.inflate(R.layout.audio_fragment, container, false);

		// Bind Java code and XML
		progressBarMusic = (SeekBar) v.findViewById(R.id.seekBarAudio);
		textViewCurrentPosition = (TextView) v
				.findViewById(R.id.textViewMusicCurrentPosition);
		textviewTotalDuration = (TextView) v
				.findViewById(R.id.textViewMusicTotalDuration);
		playButton = (ImageView) v.findViewById(R.id.imageViewPlayButton);
		nextButton = (ImageView) v.findViewById(R.id.imageViewNextButton);
		previousButton = (ImageView) v.findViewById(R.id.imageViewPreviousButton);

		// Set a listener on buttons
		playButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		previousButton.setOnClickListener(this);

		progressBarMusic.setOnSeekBarChangeListener(this);

		// Set an audio manager + context
		AudioManager am = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);

		audioPlayer = new AudioPlayer(am);

		// Return view
		return v;
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
			//mCallBack.onPlayButtonClick(Orders.PLAY);
			break;

		// Click on next button
		case R.id.imageViewNextButton:
			nextMusic();
			break;

		// Click on previous button
		case R.id.imageViewPreviousButton:
			previousMusic();
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
		// TODO Auto-generated method stub
		musicHandler.removeCallbacks(progressBarUpdateTime); // remove function
																// callback -
																// bug otherwise
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		musicHandler.removeCallbacks(progressBarUpdateTime); // remove function
																// callback -
																// bug otherwise
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

	public void initMusic() {
		Log.w("simon",String.valueOf(selectedMusic));
		audioPlayer.loading(musics.get(selectedMusic));
		this.progressBarMusic.setMax(100);
		this.progressBarMusic.setProgress(0);
		playMusic();
	}

	// Function called when a music is about to be playing
	public void playMusic() {
		// play music
		
		if(this.audioPlayer.isPlaying())
		{
			// Change play to pause button
			playButton.setBackgroundResource(R.drawable.selector_play_button);
			this.audioPlayer.pause();
		}
		else
		{
			this.audioPlayer.play();
			playButton.setBackgroundResource(R.drawable.selector_pause_button);
			// Defines default values for the seekbar
			updateProgressBar();			
		}
	}
	
	public void nextMusic(){
		if(this.selectedMusic + 1 > this.musics.size())
		{
			// Reached the end of the list
		}
		else
		{
			this.selectedMusic++;
			this.audioPlayer.stop();
			initMusic();
		}
	}
	
	public void previousMusic(){
		if(this.selectedMusic - 1 < 0)
		{
			// Reached the beginning of the list
		}
		else
		{
			this.selectedMusic--;
			this.audioPlayer.stop();
			initMusic();
		}
	}

	/*
	 * public void forwardMusic() { int currentPosition =
	 * audioPlayer.getCurrentPosition(); int duration =
	 * audioPlayer.getDuration(); int goToPosition = currentPosition +
	 * FORWARD_SECONDS; if (goToPosition <= duration)
	 * audioPlayer.seekTo(goToPosition); else audioPlayer.seekTo(duration);
	 * 
	 * Log.d("simon", "test forward done " + goToPosition);
	 * 
	 * }
	 * 
	 * public void backwardMusic() { int currentPosition =
	 * audioPlayer.getCurrentPosition(); int goToPosition = currentPosition -
	 * BACKWARD_SECONDS; if (goToPosition >= 0)
	 * audioPlayer.seekTo(goToPosition); else audioPlayer.seekTo(0);
	 * Log.d("simon", "test backward done " + goToPosition); }
	 */

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

	/*********************************************************************************/
	/** End of required methods related to audio playing **/
	/*********************************************************************************/

	/*********************************************************************************/
	/** Related to the lifecycle of the application **/
	/*********************************************************************************/

	public void onStop() {
		super.onStop();
		musicHandler.removeCallbacks(progressBarUpdateTime);
		audioPlayer.stop(); // simon - bug onStop - correct it out
	};

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		musicHandler.removeCallbacks(progressBarUpdateTime);
		audioPlayer.pause();
		super.onPause();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

}

package media.player.fragments;

import com.example.media.player.audiolys.R;

import media.player.models.AudioPlayer;
import media.player.utils.MediaPlayer;
import android.app.Fragment;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class AudioFragment extends Fragment implements OnClickListener, SeekBar.OnSeekBarChangeListener {

	/* Variables */
	private AudioPlayer audioPlayer;
	private SeekBar progressBarMusic;
	private Handler musicHandler = new Handler();
	private TextView textViewCurrentPosition;
	private TextView textviewTotalDuration;

	/* EOVariables */

	// Enumerate : give orders
	public enum Orders {
		PLAY, PAUSE, NEXT, PREVIOUS, STOP;
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

		// Set a listener on buttons
		v.findViewById(R.id.imageViewPlayButton).setOnClickListener(this);
		v.findViewById(R.id.imageViewNextButton).setOnClickListener(this);
		v.findViewById(R.id.imageViewPreviousButton).setOnClickListener(this);
		progressBarMusic.setOnSeekBarChangeListener(this);

		// Set an audio manager + context
		AudioManager am = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);
		audioPlayer = new AudioPlayer(am);

		initMusic();

		// Return view
		return v;
	}

	/*********************************************************************************/
	/**		Implemented methods														**/
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
			audioPlayer.next();
			break;

		// Click on previous button
		case R.id.imageViewPreviousButton:
			audioPlayer.previous();
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
		musicHandler.removeCallbacks(progressBarUpdateTime); // remove function callback - bug otherwise 
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		 musicHandler.removeCallbacks(progressBarUpdateTime); // remove function callback - bug otherwise 
		 int currentPosition = MediaPlayer.progressToTimer(progressBarMusic.getProgress(), audioPlayer.getDuration());
		 
		 // forward or backward to certain seconds
	     audioPlayer.seekTo(currentPosition);
	     
	     // update timer progress again
	     updateProgressBar();
	}

	/*********************************************************************************/
	/**		End of implemented methods												**/
	/*********************************************************************************/
	
	/*********************************************************************************/
	/**		Required methods related to audio playing								**/
	/*********************************************************************************/

	public void initMusic() {

	}

	// Function called when a music is about to be playing
	public void playMusic() {
		// play music
		this.audioPlayer.play();

		// Defines default values for the seekbar
		this.progressBarMusic.setMax(100);
		this.progressBarMusic.setProgress(0);

		updateProgressBar();
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
			textViewCurrentPosition.setText(MediaPlayer
					.millisecondsToTimer(currentDuration));
			textviewTotalDuration.setText(MediaPlayer
					.millisecondsToTimer(totalDuration));

			// Updating progress bar
			int progress = (int) (MediaPlayer.getProgressPercentage(currentDuration,
					totalDuration));
			progressBarMusic.setProgress(progress);

			// Delayed call to itself - 100 milliseconds
			musicHandler.postDelayed(this, 100);
		}
	};
	
	/*********************************************************************************/
	/**		End of required methods related to audio playing						**/
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

package media.player.fragments;

import java.util.ArrayList;
import com.example.media.player.audiolys.R;
import media.player.models.AudioPlayer;
import media.player.models.Music;
import media.player.utils.MediaUtils;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
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
import android.widget.Toast;

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
	private onChangeEvents mCallBackEvent;
	private ArrayList<Music> musics = new ArrayList<Music>();
	private int selectedMusic = -1;

	public enum Orders {
		PLAY, PAUSE, NEXT, PREVIOUS;
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
		textViewNumberTracks = (TextView) v.findViewById(R.id.textViewNumberTracks);
		textViewCurrentPosition = (TextView) v
				.findViewById(R.id.textViewMusicCurrentPosition);
		textviewTotalDuration = (TextView) v
				.findViewById(R.id.textViewMusicTotalDuration);
		playButton = (ImageView) v.findViewById(R.id.imageViewPlayButton);
		nextButton = (ImageView) v.findViewById(R.id.imageViewNextButton);
		previousButton = (ImageView) v
				.findViewById(R.id.imageViewPreviousButton);

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
						playNext();
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
			playNext();
			break;

		// Click on previous button
		case R.id.imageViewPreviousButton:
			playPrevious();
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
		textViewNumberTracks.setText("Track "+selectedMusic+"/"+musics.size());
		this.progressBarMusic.setMax(100);
		this.progressBarMusic.setProgress(0);
	}

	public void initMusic() {
		// Log.w("simon",String.valueOf(selectedMusic));
		// audioPlayer.loading(musics.get(selectedMusic));

		updateProgressBar();
		// playMusic();
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

	public void playNext() {
		
		boolean flag = false;
		// Check if a music is playing
		if(this.audioPlayer.isPlaying())
			flag = true;
		
		if (selectedMusic + 1 >= this.musics.size()) {
			// Reach the end of the music list
		} else {
			selectedMusic++;
			this.audioPlayer.loading(musics.get(selectedMusic));
			this.playMusic();
			refresh();// refresh the display
			
			mCallBackEvent.onChangeE(Orders.NEXT, selectedMusic);
		}	
	}

	public void playPrevious() {
		
		boolean flag = false;
		if(this.audioPlayer.isPlaying())
			flag = true;
		
		if (selectedMusic - 1 <= 0) {
			// Reach the beginning of the music list
		} else {
			selectedMusic--;
			this.audioPlayer.loading(musics.get(selectedMusic));
			if(flag)
				this.playMusic();
			refresh(); // refresh the display
			mCallBackEvent.onChangeE(Orders.PREVIOUS, selectedMusic);
		}
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

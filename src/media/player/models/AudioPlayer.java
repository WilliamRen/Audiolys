package media.player.models;

import java.io.IOException;
import java.util.ArrayList;
import com.example.media.player.audiolys.R;
import media.player.fragments.AudioFragment.Event;
import media.player.fragments.AudioFragment.Repeat;
import media.player.utils.MediaUtils;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.ImageView;

public class AudioPlayer implements AudioManager.OnAudioFocusChangeListener {

	/* Variables */
	private final AudioManager audioManager;
	public MediaPlayer mediaPlayer;
	ArrayList<Music> musics;
	int selectedMusic = -1;
	int volume_level = 0;
	/* End of Variables */
	
	// Constructor
	public AudioPlayer(AudioManager audioManager) {
		this.audioManager = audioManager;
		this.mediaPlayer = new MediaPlayer();
	}
	
	 public boolean requestFocus() {
	        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
	            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
	            AudioManager.AUDIOFOCUS_GAIN);
	 }
	 
	 public boolean abandonFocus() {
	        return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
	        		audioManager.abandonAudioFocus(this);
	 }

	// Load a set of musics
	public void load(ArrayList<Music> m, int s)
	{
		musics = m;
		selectedMusic = s;
	}
	
	// Prepare a music to be played
	public void loading()
	{
		requestFocus();
		try {
			// music that is going to be played
			this.mediaPlayer.reset();
			this.mediaPlayer.setDataSource(musics.get(selectedMusic).getMusic().getAbsolutePath());
			this.mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Play media
	public boolean play() {
		boolean result = false;
		if(this.mediaPlayer.isPlaying())
		{
			this.mediaPlayer.pause();
			result = false;
		}
		else
		{
			this.mediaPlayer.start();
			result = true;
		}
		return result;
	}

	// Pause media
	public void pause() {
		this.mediaPlayer.pause();
		abandonFocus();
	}

	// Play next media
	public boolean next(Repeat r, Event e, boolean s, ImageView bp) {
		
		boolean refreshDisplay = false;
		boolean flag = false;
		// Check if a music is playing
		if (this.mediaPlayer.isPlaying() || e.equals(Event.END))
			flag = true;

		// Which type of repeat
		if (r.equals(Repeat.ALL)) {

			// Reach the end of the list
			if ((selectedMusic + 2) > this.musics.size()) {
				selectedMusic = -1;
				refreshDisplay = true;
				next(r, Event.END, s, bp);
			// Not the end of the list	
			} else {
				// Shuffle 
				if (s)
					selectedMusic = MediaUtils.giveMeARandomNumber(0, musics.size() - 1);
				else // or not 
					selectedMusic++;
					loading(); // load a new music
					
				if (flag)
					play(); // play if media was playing before

				refreshDisplay = true;
			}
		} else if (r.equals(Repeat.ONE)) {

			if ((selectedMusic + 2) > this.musics.size()) {

			} else {
				if (e.equals(Event.PUSH)) {
					selectedMusic++;
					loading();
					
					if (flag)
						play();

					refreshDisplay = true;
				}
				repeat(true);
			}

		} else {
			// Reach the end of the list
			if ((selectedMusic + 2) > this.musics.size()) { 
				bp.setBackgroundResource(R.drawable.selector_play_button);
			} else {
				if (s)
					selectedMusic = MediaUtils.giveMeARandomNumber(0, musics.size() - 1);
				else
					selectedMusic++;

				loading();
				if (flag) {
					play();
					bp.setBackgroundResource(R.drawable.selector_pause_button);

				} else {
					bp.setBackgroundResource(R.drawable.selector_play_button);
				}
				refreshDisplay = true;
			}
		}
		return refreshDisplay;
	}

	// Play previous media
	public boolean previous(ImageView prev, Repeat r) {
		
		boolean refreshDisplay = false;
		boolean flag = false;
		if (this.mediaPlayer.isPlaying())
			flag = true;

		if (selectedMusic - 1 < 0) {
			// Reach the beginning of the music list
		} else {
			prev.setBackgroundResource(R.drawable.previous_button_released);
			selectedMusic--;
			loading();
			if (flag)
				play();
			
			refreshDisplay = true;

			if (r.equals(Repeat.ONE))
				repeat(true);
		}
		return refreshDisplay;
	}

	// Stop media
	public void stop() {
		this.mediaPlayer.stop();
		this.mediaPlayer.release();
		abandonFocus();
	}

	// is media playing
	public boolean isPlaying() {		
		return this.mediaPlayer.isPlaying();
	}

	// Get song name
	public String getSongName()
	{
		return this.musics.get(selectedMusic).getTitle();
	}
	
	// Get number of tracks
	public String getNumberOfTracks()
	{
		return "Track " + (selectedMusic + 1) + "/"+ musics.size();
	}
	
	// Get duration of the track
	public int getDuration() {
		return this.mediaPlayer.getDuration();
	}

	// Get the current position of the track
	public int getCurrentPosition() {
		return this.mediaPlayer.getCurrentPosition();
	}
	
	// Get the selected music
	public int getSelectedMusic(){
		return selectedMusic;
	}
	
	// Get the list of musics
	public ArrayList<Music> getMusic()
	{
		return musics;
	}

	// Go to -
	public void seekTo(int currentPosition) {
		mediaPlayer.seekTo(currentPosition);
	}
	
	// Repeat a specific music
	public void repeat(boolean state)
	{
		this.mediaPlayer.setLooping(state);
	}

	// Shake 2 shuffle
	public void shake2Shuffle()
	{
		selectedMusic = MediaUtils.giveMeARandomNumber(0, musics.size() - 1); // random
		loading();
		play();
	}
	
	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub
		Log.w("simon","test audio focus"+focusChange);
		switch(focusChange)
		{
		// Info about audio focus
		// http://developer.android.com/reference/android/media/AudioManager.html#AUDIOFOCUS_GAIN
		// focus gained
		case 1:
			volumeUp();
			break;
		case 2:
			volumeUp();
			break;
		case 3:
			volumeUp();
			break;
		case 4:
			volumeUp();
			break;
		
		// focus lost
		case -3:
			volumeDown();
			break;
		case -1:
			volumeDown();
			break;
		case -2:
			volumeDown();
			break;
		
			default:
				break;
		}
	}
	
	// Turn the volume down
	public void volumeUp()
	{
		Log.w("simon","volume"+volume_level);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume_level, 1);
	}
	
	// Turn the volume up
	public void volumeDown()
	{
		volume_level= audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 1, 1);
	}
}

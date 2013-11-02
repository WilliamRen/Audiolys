package media.player.models;

import java.io.IOException;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class AudioPlayer implements AudioManager.OnAudioFocusChangeListener, OnCompletionListener{

	/* Variables */
	private final AudioManager audioManager;
	public MediaPlayer mediaPlayer;
	/* End of Variables */
	
	public AudioPlayer(AudioManager audioManager) {
		this.audioManager = audioManager;
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setOnCompletionListener(this);
	}
	
	public void loading(Music music)
	{
		try {
			// music that is going to be played
			this.mediaPlayer.reset();
			this.mediaPlayer.setDataSource(music.getMusic().getAbsolutePath());
			this.mediaPlayer.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void play() {
		this.mediaPlayer.start();
	}

	public void pause() {
		this.mediaPlayer.pause();
	}

	public boolean next() {
		return false;
	}

	public boolean previous() {
		return false;

	}

	public void stop() {
		this.mediaPlayer.stop();
	}

	public boolean isPlaying() {		
		return this.mediaPlayer.isPlaying();
	}

	public String getSongName()
	{
		//return this.musics.get(selectedMusic).getTitle();
		return "";
	}
	
	public int getDuration() {
		return this.mediaPlayer.getDuration();
	}

	public int getCurrentPosition() {
		return this.mediaPlayer.getCurrentPosition();
	}

	public void seekTo(int currentPosition) {
		mediaPlayer.seekTo(currentPosition);
	}
	
	public void repeat(boolean state)
	{
		this.mediaPlayer.setLooping(state);
	}

	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub
		Log.w("simon","test audio focus");
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		Log.w("simon","completion from audio player");
	}
}

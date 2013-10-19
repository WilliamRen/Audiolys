package media.player.models;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class AudioPlayer implements AudioManager.OnAudioFocusChangeListener {

	private final AudioManager audioManager;
	private MediaPlayer mediaPlayer;

	public AudioPlayer(AudioManager audioManager) {
		this.audioManager = audioManager;
		this.mediaPlayer = new MediaPlayer();
	}

	public void load(int position) {
	}

	public void play() {
		try {
			this.mediaPlayer
					.setDataSource("/storage/sdcard0/Music/Tryo - 2008 - Ce Que L'on Sème/09 Marcher droit.mp3");
			this.mediaPlayer.prepare();
			this.mediaPlayer.start();
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
		return false;
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

	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub
	}

}

package media.player.models;

import java.io.IOException;
import java.util.ArrayList;

import media.player.fragments.AudioFragment;
import media.player.fragments.AudioFragment.Repeat;

import android.app.Activity;
import android.app.FragmentManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.widget.Button;

public class AudioPlayer implements AudioManager.OnAudioFocusChangeListener, OnCompletionListener{

	private final AudioManager audioManager;
	public MediaPlayer mediaPlayer;	
	/*private ArrayList<Music> musics;
	private int selectedMusic;*/
	
	
	public AudioPlayer(AudioManager audioManager) {
		this.audioManager = audioManager;
		this.mediaPlayer = new MediaPlayer();
		this.mediaPlayer.setOnCompletionListener(this);
	}
	
	public void init(ArrayList<Music> m, int selmu)
	{
		/*this.selectedMusic = selmu;
		this.musics = m;*/
	}
	
	/*public void load(int position) {
		try {
			// music that is going to be played
			this.mediaPlayer.reset();
			//this.mediaPlayer.setDataSource(musics.get(position).getMusic().getAbsolutePath());
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
	}*/

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
		
	}
}

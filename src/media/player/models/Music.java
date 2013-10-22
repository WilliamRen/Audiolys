package media.player.models;

import java.io.File;
import java.io.Serializable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Music implements Serializable{

	File music;
	String title;
	String band;
	String image;
	int duration;

	public Music(File music) {
		super();
		this.music = music;
		this.title = researchTitle(music.getName());
		this.image = researchImage(music);
		this.duration = 0;
	}

	public File getMusic() {
		return music;
	}

	public void setMusic(File music) {
		this.music = music;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBand() {
		return band;
	}

	public void setBand(String band) {
		this.band = band;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	// removing extension of the file to have the musique name.
	private String researchTitle(String musicname) {
		Log.e("title", musicname);
		return musicname;
	}

	private String researchImage(File musicFile) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "Music [music=" + music + ", title=" + title + ", band=" + band
				+ ", image=" + image + ", duration=" + duration + "]";
	}
}

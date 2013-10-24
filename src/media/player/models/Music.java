package media.player.models;

import java.io.File;
import java.io.Serializable;

import media.player.utils.Storage;

public class Music implements Serializable{

	File music = null;
	String title = null;
	String band = null;
	String image = null;

	public Music(File music) {
		super();
		this.music = music;
		this.title = researchTitle(music.getName());
		this.image = researchImage(music);
		this.band = researchBand(music);
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
	
	//removing extension of the file to have the musique name.
	private String researchTitle(String musicname) {
		String name = musicname.substring(0, musicname.length()-4);
		return name;
	}

	private String researchImage(File musicFile) {
		return Storage.getAlbumCover(musicFile.getParentFile()).getAbsolutePath();
	}
	
	private String researchBand(File music) {
		char[] musicFolderArray = music.getParent().toCharArray();
		int i = musicFolderArray.length - 1;
		while(musicFolderArray[i]!= '/'){
			i--;
		}
		return music.getParent().substring(i+1,music.getParent().length());
	}

	@Override
	public String toString() {
		return "Music [music=" + music + ", title=" + title + ", band=" + band
				+ ", image=" + image + "]";
	}
}

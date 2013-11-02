package media.player.models;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import media.player.utils.Storage;

// Band models
public class Band implements Serializable{

	private static final long serialVersionUID = 1L;
	File band = null;
	String name = null;
	ArrayList<Music> musics = new ArrayList<Music>();

	public Band(File f) {
		super();
		this.band = f;
		this.name = researchBand(f);
		this.musics = Storage.getFiles(f);
	}
	
	public File getBand() {
		return band;
	}

	public void setBand(File band) {
		this.band = band;
	}

	public ArrayList<Music> getMusics() {
		return musics;
	}

	public void setMusics(ArrayList<Music> musics) {
		this.musics = musics;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	//Get the name of the band by getting the last part of the file path
	private String researchBand(File folder) {
		char[] musicFolderArray = folder.toString().toCharArray();
		int i = musicFolderArray.length - 1;
		while(musicFolderArray[i]!= '/'){
			i--;
		}
		return folder.toString().substring(i+1,folder.toString().length());
	}	
}

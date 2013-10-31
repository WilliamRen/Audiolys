package media.player.models;

import java.io.File;
import java.io.Serializable;
import android.media.MediaMetadataRetriever;
import media.player.utils.Storage;

public class Music implements Serializable{

	private static final long serialVersionUID = 1L;
	File music = null;
	String title = null;
	String band = null;
	String image = null;

	public Music(File music) {
		super();
		this.music = music;
		MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
		mediaMetadataRetriever.setDataSource(music.getAbsolutePath());
		this.title = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
		if(this.title==null){
			this.title=researchTitle(music.getName());
		}
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

	//Get the image URI of the cover in the folder
	private String researchImage(File musicFile) {
		return Storage.getAlbumCover(musicFile.getParentFile()).getAbsolutePath();
	}
	
	//Get music name if not in metadata by removing extension of the file.
    private String researchTitle(String musicname) {
            String name = musicname.substring(0, musicname.length()-4);
            return name;
    }
	
    //Get the name of the band by getting the last part of the file path
	private String researchBand(File music) {
		char[] musicFolderArray = music.getParent().toCharArray();
		int i = musicFolderArray.length - 1;
		while(musicFolderArray[i]!= '/'){
			i--;
		}
		return music.getParent().substring(i+1,music.getParent().length());
	}
}

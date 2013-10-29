package media.player.models;

import java.io.File;
import java.io.Serializable;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import media.player.utils.Storage;

public class Music implements Serializable{

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
		this.image = researchImage(music);
		this.band = researchBand(music);
		/*Log.i("metadata", "album " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
		Log.i("metadata", "album artiste " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST));
		Log.i("metadata", "artiste " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
		Log.i("metadata", "author " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_AUTHOR));
		Log.i("metadata", "bitrate " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE));
		Log.i("metadata", "cd track number " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER));
		Log.i("metadata", "composer " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_COMPOSER));
		Log.i("metadata", "date " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
		Log.i("metadata", "Disc number " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER));
		Log.i("metadata", "duration " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
		Log.i("metadata", "genre " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
		Log.i("metadata", "title " + mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));*/
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

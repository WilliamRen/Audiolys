package media.player.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import media.player.models.Band;
import media.player.models.Music;
import android.os.Environment;
import android.provider.ContactsContract.Directory;
import android.util.Log;

public class Storage {
	
	public enum State {
		NOTAVAILABLE, MOUNTED, READONLY, WRITEABLE;
	}

	//Checking if the external storage is plugged and ready to use
	public static State checkStorage() {
		String state = Environment.getExternalStorageState();
		State returnState = State.NOTAVAILABLE;
		if (Environment.MEDIA_MOUNTED.equals(state))
			returnState = State.MOUNTED;
		else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))
			returnState = State.READONLY;
		else
			returnState = State.NOTAVAILABLE;

		return returnState;
	}

	//Get a list of musics
	public static ArrayList<Music> getFiles(File directory) {
		ArrayList<Music> awl = new ArrayList<Music>();
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				awl.addAll(getFiles(f));
			} else if (f.isFile() && isAudioFile(f)) {
				File music = f;
				Music aw = new Music(music);
				awl.add(aw);
			}
		}
		return awl;
	}
	
	//Get a list of folders in the music directory on the phone
	public static ArrayList<Band> getFolders(File directory) {
		ArrayList<Band> directories = new ArrayList<Band>();
		for (File f : directory.listFiles()) {
			if (f.isDirectory()) {
				directories.add(new Band(f));
			} 
		}
		return directories;
	}

	// Is the file a music?!
	public static boolean isAudioFile(File f) {
		boolean flag = false;

		if (f.getAbsolutePath().toLowerCase().endsWith(".mp3") || f.getAbsolutePath().toLowerCase().endsWith(".m4a"))
			flag = true;

		return flag;
	}

	// Get the cover of the folder
	public static File getAlbumCover(File f) {
		File returnFile = null;
		File[] t = f.listFiles();
		for (File s : t) {
			if (s.getAbsolutePath().toLowerCase().endsWith(".jpg")) {
				returnFile = s;
			}
		}
		return returnFile;
	}
}

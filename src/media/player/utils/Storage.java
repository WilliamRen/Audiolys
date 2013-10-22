package media.player.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import media.player.models.Music;

import android.os.Environment;
import android.util.Log;

public class Storage {
	
	public enum State {
		NOTAVAILABLE, MOUNTED, READONLY, WRITEABLE;
	}

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

	public static ArrayList<Music> getFiles(File directory) {
		ArrayList<Music> awl = new ArrayList<Music>();
		for (File f : directory.listFiles()) {
			//Log.d("music", f.getAbsolutePath());
			if (f.isDirectory()) {
				awl.addAll(getFiles(f));
			} else if (f.isFile() && isAudioFile(f)) {
				//Log.d("cover", getAlbumCover(f.getParentFile()).getAbsolutePath());
				//File cover = f.getParentFile();
				File music = f;
				//String parent = f.getParentFile().getAbsolutePath();
				Music aw = new Music(music);
				//Log.d("Music Item", aw.toString());
				awl.add(aw);
			}
		}
		return awl;
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
		Log.d("music", "test" + returnFile);
		return returnFile;
	}
}

package media.player.asynctask;

import java.io.File;
import java.util.ArrayList;

import media.player.activities.MainActivity;
import media.player.models.Music;
import media.player.utils.Storage;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public interface LoadMusicRunnable extends Runnable{

	public void beforeExecute();
	public void run();
	public void afterExecute();

}

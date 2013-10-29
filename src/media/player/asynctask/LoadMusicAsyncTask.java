package media.player.asynctask;

import java.io.File;
import java.util.ArrayList;

import media.player.models.Band;
import media.player.utils.Storage;

import android.os.AsyncTask;
import android.os.Environment;

public class LoadMusicAsyncTask extends AsyncTask<Void, Void, ArrayList<Band>>{
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	@Override
	protected ArrayList<Band> doInBackground(Void... params) {

		File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        ArrayList<Band> folders = new ArrayList<Band>();
        folders = Storage.getFolders(musicDirectory);
		return folders;
	}
	
  	@Override
  	protected void onPostExecute(ArrayList<Band> result) {
		super.onPostExecute(result);
	}

}

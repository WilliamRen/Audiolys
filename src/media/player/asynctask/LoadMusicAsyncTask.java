package media.player.asynctask;

import java.io.File;
import java.util.ArrayList;

import media.player.models.Band;
import media.player.utils.Storage;

import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

public class LoadMusicAsyncTask extends AsyncTask<Void, Void, ArrayList<Band>>{
	ProgressBar mProgress;
	
	public LoadMusicAsyncTask(ProgressBar prog) {
		super();
		this.mProgress = prog;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgress.setVisibility(View.VISIBLE);
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
  		mProgress.setVisibility(View.GONE);
		super.onPostExecute(result);
	}

}

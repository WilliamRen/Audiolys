package media.player.asynctask;

import java.io.File;
import java.util.ArrayList;
import media.player.activities.MainActivity;
import media.player.models.Band;
import media.player.utils.Storage;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.widget.ProgressBar;

public class LoadMusicAsyncTask extends AsyncTask<Void, Void, ArrayList<Band>> {
	
	/* Variables */
	ProgressBar mProgress;
	Context context;
	ArrayList<Band> folders;
	/* End of variables */
	
	// Constructor
	public LoadMusicAsyncTask(ProgressBar prog, Context context) {
		super();
		this.mProgress = prog;
		this.context = context;
	}

	// Before executing the AsyncTask
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgress.setVisibility(View.VISIBLE);
	}

	// Executing the AsyncTask
	@Override
	protected ArrayList<Band> doInBackground(Void... params) {

		File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
		folders = new ArrayList<Band>();
		folders = Storage.getFolders(musicDirectory);
		return null; // simon : change that
	}

	// After executing the AsyncTask
	@Override
	protected void onPostExecute(ArrayList<Band> result) {
		super.onPostExecute(result);
		mProgress.setVisibility(View.GONE);
		((MainActivity) context).callFromAsyncTask(folders); // call the main activity
	}
}

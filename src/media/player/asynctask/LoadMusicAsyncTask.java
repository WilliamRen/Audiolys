package media.player.asynctask;

import java.io.File;
import java.util.ArrayList;

import com.example.media.player.audiolys.R;

import media.player.models.Band;
import media.player.utils.Storage;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadMusicAsyncTask extends AsyncTask<Void, Void, ArrayList<Band>>{
	private Activity myActivity;
	ProgressBar mProgress;
	TextView mText;
	
	public LoadMusicAsyncTask(Activity myActivity) {
		super();
		this.myActivity = myActivity;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mText = (TextView) myActivity.findViewById(R.id.textLoading);
		mProgress = (ProgressBar) myActivity.findViewById(R.id.loadingBar);
		mProgress.setVisibility(View.VISIBLE);
		mText.setVisibility(View.VISIBLE);
		Log.i("async pre", "Youhou on preexecute");
	}
	
	@Override
	protected ArrayList<Band> doInBackground(Void... params) {

		File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        ArrayList<Band> folders = new ArrayList<Band>();
        folders = Storage.getFolders(musicDirectory);
		Log.i("async exec", "Youhou on execute");
		return folders;
	}
	
  	@Override
  	protected void onPostExecute(ArrayList<Band> result) {
		mProgress.setVisibility(View.GONE);
		mText.setVisibility(View.GONE);
		Log.i("async post", "Youhou on postexecute");
		super.onPostExecute(result);
	}

}

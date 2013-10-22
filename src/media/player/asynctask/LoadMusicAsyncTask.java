package media.player.asynctask;

import java.io.File;
import java.util.ArrayList;
import media.player.models.Music;
import media.player.utils.Storage;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class LoadMusicAsyncTask extends AsyncTask<Void, Void, ArrayList<Music>>{

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	@Override
	protected ArrayList<Music> doInBackground(Void... params) {

		File musicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        Log.e("test",musicDirectory.toString());
        ArrayList<Music> musics = new ArrayList<Music>();
		musics = Storage.getFiles(musicDirectory);
		return musics;
	}
	
//	@Override
//	protected void onPostExecute(List<Music> result) {
//		// TODO Auto-generated method stub
//		super.onPostExecute(result);
//	}

}

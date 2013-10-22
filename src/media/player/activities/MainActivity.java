package media.player.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import media.player.asynctask.LoadMusicAsyncTask;
import media.player.models.Music;

import com.example.media.player.audiolys.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemClickListener {
	ArrayList<HashMap<String,String>> listItem;
	public ArrayList<Music> musics;
	ListView listMusic;
   
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_main);
        
        listItem = new ArrayList<HashMap<String,String>>();
		musics = new ArrayList<Music>();

	    LoadMusicAsyncTask lmat = new LoadMusicAsyncTask();
        
        try {
			musics = lmat.execute().get();
	        listItem = toHashMap();
	        Log.e("music", listItem.toString());
	        SimpleAdapter miseEnFormeItems = new SimpleAdapter(
					getApplicationContext(), listItem, R.layout.musiclist_item,
					new String[] { "title", "group"}, new int[] {
							R.id.musictitle, R.id.musicband});
	        listMusic = (ListView) findViewById(R.id.listView_music);
	        listMusic.setAdapter(miseEnFormeItems);
	        listMusic.setOnItemClickListener(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    }

    //Create a HashMap
    private ArrayList<HashMap<String, String>> toHashMap() {
		ArrayList<HashMap<String, String>> listMusic = new ArrayList<HashMap<String,String>>();
		for (Music music : musics) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("title", music.getTitle());
				hm.put("group", music.getBand());
				hm.put("image", music.getImage());
				listMusic.add(hm);
		}
		return listMusic;
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.listView_music:
	        //Toast.makeText(getApplicationContext(), "Music:"+musics.get(arg2),Toast.LENGTH_LONG).show();
	        Music clickMusic = musics.get(arg2);
			Intent playerIntent = new Intent(this, AudioActivity.class);
			Bundle data = new Bundle();
			data.putSerializable("listMusics", musics);
			data.putInt("selectedMusic", arg2);
			playerIntent.putExtras(data);
			startActivity(playerIntent);
			break;

		default:
			Toast.makeText(getApplicationContext(), "faux", Toast.LENGTH_SHORT).show();
			break;
		}		
	}
}

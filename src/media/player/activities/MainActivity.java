package media.player.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import media.player.asynctask.LoadMusicAsyncTask;
import media.player.models.Music;
import media.player.utils.SimpleAdapterPerso;

import com.example.media.player.audiolys.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemClickListener {
	List<HashMap<String, Object>> listItem;
	ArrayList<Music> musics;
	ListView listMusic;
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_main);
        
        listItem = new ArrayList<HashMap<String,Object>>();
		musics = new ArrayList<Music>();

	    LoadMusicAsyncTask lmat = new LoadMusicAsyncTask();
        
        try {
			musics = lmat.execute().get();
	        listItem = toMap();
	        //Log.e("music", listItem.toString());
	        SimpleAdapterPerso listAdapter = new SimpleAdapterPerso(
					getApplicationContext(), listItem, R.layout.musiclist_item,
					new String[] {"image", "title", "group"}, new int[] {
							R.id.imageViewBitmap, R.id.musictitle, R.id.musicband});
	        listMusic = (ListView) findViewById(R.id.listView_music);
	        listMusic.setAdapter(listAdapter);
	        listMusic.setOnItemClickListener(this);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    }

    //Create a HashMap
    private List<HashMap<String, Object>> toMap() {
    	ArrayList<HashMap<String, Object>> listMusic = new ArrayList<HashMap<String, Object>>();
		for (Music music : musics) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("title", music.getTitle());
				hm.put("group", music.getBand());
				hm.put("image", BitmapFactory.decodeFile(music.getImage()));
				listMusic.add(hm);
		}
		return listMusic;
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
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			break;

		default:
			break;
		}		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}

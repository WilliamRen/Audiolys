package media.player.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import media.player.asynctask.LoadMusicAsyncTask;
import media.player.models.Band;
import media.player.models.Music;
import media.player.utils.SimpleAdapterPerso;

import com.example.media.player.audiolys.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemClickListener {
	ProgressBar loading;
	List<HashMap<String, Object>> listBands;
	List<HashMap<String, Object>> listMusics;
	ArrayList<Music> musics;
	ArrayList<Band> bands;
	ListView listMusic;
	ListView listBand;
	Boolean isBand = true;
	
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
        
        //loading = (ProgressBar) this.findViewById(R.id.loading);
        listBands = new ArrayList<HashMap<String,Object>>();
        listMusics = new ArrayList<HashMap<String,Object>>();
		musics = new ArrayList<Music>();
        listBand = (ListView) findViewById(R.id.listView_bands);
        listMusic = (ListView) findViewById(R.id.listView_music);
		
	    LoadMusicAsyncTask lmat = new LoadMusicAsyncTask();
		try {
			bands = lmat.execute().get();
	        listBands = bandToMap();

	        SimpleAdapterPerso listBandAdapter = new SimpleAdapterPerso(
					getApplicationContext(), listBands, R.layout.musiclist_item,
					new String[] {"title"}, new int[] {R.id.musictitle});
	        listBand.setAdapter(listBandAdapter);
	        listBand.setOnItemClickListener(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    //Create a HashMap
    private List<HashMap<String, Object>> bandToMap() {
    	ArrayList<HashMap<String, Object>> listMusic = new ArrayList<HashMap<String, Object>>();
		for (Band band : bands) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("title", band.getName());
				listMusic.add(hm);
		}
		return listMusic;
	}

    private List<HashMap<String, Object>> musicToMap(ArrayList<Music> musicList) {
    	ArrayList<HashMap<String, Object>> listMusic = new ArrayList<HashMap<String, Object>>();
		for (Music music : musicList) {
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
			Intent playerIntent = new Intent(this, AudioActivity.class);
			Bundle data = new Bundle();
			data.putSerializable("listMusics", musics);
			data.putInt("selectedMusic", arg2);
			playerIntent.putExtras(data);
						
			startActivity(playerIntent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			break;

		case R.id.listView_bands:
			isBand = false;
			listBand.setVisibility(View.INVISIBLE);
			listMusic.setVisibility(View.VISIBLE);
			musics = bands.get(arg2).getMusics();
			listMusics = musicToMap(musics);
			
			SimpleAdapterPerso listMusicAdapter = new SimpleAdapterPerso(
					getApplicationContext(), listMusics, R.layout.musiclist_item,
					new String[] {"image", "title", "group"}, new int[] {
							R.id.imageViewBitmap, R.id.musictitle, R.id.musicband});
	        listMusic.setAdapter(listMusicAdapter);
	        listMusic.setOnItemClickListener(this);
			
		default:
			break;
		}
		
		
	}
	
	@Override
	public void onBackPressed() {
	    if(isBand){
	    	super.onBackPressed();
	    }else{
	    	listBand.setVisibility(View.VISIBLE);
			listMusic.setVisibility(View.INVISIBLE);
			isBand = true;
	    }
	}
}

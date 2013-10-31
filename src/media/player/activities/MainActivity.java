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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnItemClickListener, OnItemLongClickListener {
	ProgressBar loading;
	List<HashMap<String, Object>> listBands;
	List<HashMap<String, Object>> listMusics;
	ArrayList<Music> musics;
	ArrayList<Band> bands;
	ListView listViewMusic;
	ListView listViewBand;
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
        
        listBands = new ArrayList<HashMap<String,Object>>();
        listMusics = new ArrayList<HashMap<String,Object>>();
		musics = new ArrayList<Music>();
        listViewBand = (ListView) findViewById(R.id.listView_bands);
        listViewMusic = (ListView) findViewById(R.id.listView_music);

	    LoadMusicAsyncTask lmat = new LoadMusicAsyncTask(this);
		
	    try {
	    	bands = lmat.execute().get();
			
	        listBands = bandToMap();
	        SimpleAdapterPerso listBandAdapter = new SimpleAdapterPerso(
					getApplicationContext(), listBands, R.layout.musiclist_item,
					new String[] {"image", "title"}, new int[] {R.id.imageViewBitmap, R.id.musictitle});
	        listViewBand.setAdapter(listBandAdapter);
	        listViewBand.setOnItemLongClickListener(this);
	        listViewBand.setOnItemClickListener(this);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	//Put all bands in a hashmap to show it in a ListView
    private List<HashMap<String, Object>> bandToMap() {
    	ArrayList<HashMap<String, Object>> listMusic = new ArrayList<HashMap<String, Object>>();
    	HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("title", "Play all musics");
		hm.put("image", R.drawable.play_button_released);
		listMusic.add(hm);
		for (Band band : bands) {
				hm = new HashMap<String, Object>();
				hm.put("title", band.getName());
				hm.put("image", BitmapFactory.decodeFile(band.getMusics().get(0).getImage()));
				listMusic.add(hm);
		}
		return listMusic;
	}

    //Put all musics in a hashmap to show it in a ListView
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

    //On a simple click on a list, we start an intent or show the listview with the music from the selected band
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg0.getId()) {
		case R.id.listView_music:
			startAudioIntent(arg2);
			break;

		case R.id.listView_bands:
			if(arg2==0){
				musics = getAllMusics();
				startAudioIntent(0);
			}else{
				isBand = false;
				listViewBand.setVisibility(View.INVISIBLE);
				listViewMusic.setVisibility(View.VISIBLE);
				musics = bands.get(arg2-1).getMusics();
				listMusics = musicToMap(musics);
				
				SimpleAdapterPerso listMusicAdapter = new SimpleAdapterPerso(
						getApplicationContext(), listMusics, R.layout.musiclist_item,
						new String[] {"image", "title", "group"}, new int[] {
								R.id.imageViewBitmap, R.id.musictitle, R.id.musicband});
		        listViewMusic.setAdapter(listMusicAdapter);
		        listViewMusic.setOnItemClickListener(this);
			}
		default:
			break;
		}
	}
	
	//Get all musics from all bands to play it as a playlist
	private ArrayList<Music> getAllMusics() {
		ArrayList<Music> allMusics = new ArrayList<Music>();
		for (Band band : bands) {
			allMusics.addAll(band.getMusics());
		}
		return allMusics;
	}

	// On long click on an band folder, we play all this folder
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
		if(arg2==0){
			musics = getAllMusics();
		}else{
			musics = bands.get(arg2-1).getMusics();
		}
		startAudioIntent(0);
		return false;
	}

	//Start the music player when music is selected
	public void startAudioIntent(int selectedMusic){
		Intent playerIntent = new Intent(this, AudioActivity.class);
		Bundle data = new Bundle();
		data.putSerializable("listMusics", musics);
		data.putInt("selectedMusic", selectedMusic);
		playerIntent.putExtras(data);
					
		startActivity(playerIntent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);	
	}
	
	//The back press is now modify in order to not quit the application when we are on the list of musics
	@Override
	public void onBackPressed() {
	    if(isBand){
	    	super.onBackPressed();
	    }else{
	    	listViewBand.setVisibility(View.VISIBLE);
			listViewMusic.setVisibility(View.INVISIBLE);
			isBand = true;
	    }
	}
}

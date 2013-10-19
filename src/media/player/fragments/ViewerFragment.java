package media.player.fragments;

import com.example.media.player.audiolys.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewSwitcher;

public class ViewerFragment extends Fragment{
	
	private ViewSwitcher vs;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		// Inflate the View
		View v = inflater.inflate(R.layout.viewer_fragment, container, false);
		vs = (ViewSwitcher) v.findViewById(R.id.viewSwitcherCase);
		
		// Return the View
		return v;
	}

}

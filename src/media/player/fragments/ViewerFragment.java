package media.player.fragments;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import media.player.animation.AnimationFactory;
import media.player.animation.AnimationFactory.FlipDirection;
import media.player.fragments.AudioFragment.Orders;
import media.player.models.Music;
import com.example.media.player.audiolys.R;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ViewFlipper;

public class ViewerFragment extends Fragment {

	private ViewFlipper vf;
	private ArrayList<Bitmap> bitmapList = new ArrayList<Bitmap>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the View
		View v = inflater.inflate(R.layout.viewer_fragment, container, false);
		vf = (ViewFlipper) v.findViewById(R.id.viewFlipperCase);
		// Return the View
		return v;
	}

	public void sendImageToViewer(Music music, Orders o) {
		String path = music.getImage();

		if (path != null) {
			
			ImageView image = new ImageView(getActivity().getApplicationContext());
			image.setPadding(100, 100, 100, 100);
			Bitmap bitmap = new BitmapFactory().decodeFile(path);
			image.setImageBitmap(bitmap);
			vf.addView(image);
			bitmapList.add(bitmap);

			
			switch (o) {
			case NEXT:
				AnimationFactory.flipTransition(vf, FlipDirection.RIGHT_LEFT);
				break;

			case PREVIOUS:
				AnimationFactory.flipTransition(vf, FlipDirection.LEFT_RIGHT);
				break;

			default:
				vf.showNext();
				break;
			}
			
			if(vf.getChildCount() > 2)
			{
				Log.w("simon","supérieur à 2");
				vf.removeViewAt(0);
				Log.w("simon", ""+bitmapList.size());
				bitmapList.get(0).recycle();
				bitmapList.remove(0);
			}
			else
			{
				Log.w("simon","inférieur à 2");
			}
		}
	}
}

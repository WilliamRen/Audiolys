package media.player.utils;

import java.util.List;
import java.util.Map;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

// Used for displayed image + text in a listview
public class SimpleAdapterPerso extends SimpleAdapter {
	
	/* Variables */
	private int mResource;
	private int[] mTo;
	private String[] mFrom;
	private List<? extends Map<String, ?>> mData;
	/* End of Variables */
	
	// Constructor
	public SimpleAdapterPerso(Context context, List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);

		mResource = resource;
		mData = data;
		mTo = to;
		mFrom = from;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		v = createViewFromResourceEx(v, position, convertView, parent, mResource);
		return v;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v = super.getDropDownView(position, convertView, parent);
		v = createViewFromResourceEx(v, position, convertView, parent,
				mResource);
		return v;
	}

	private View createViewFromResourceEx(View v, int position,
			View convertView, ViewGroup parent, int resource) {
		bindView(position, v);
		return v;
	}

	private void bindView(int position, View view) {
		final Map<?, ?> dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = getViewBinder();
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}

				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, data, text);
				}

				if (!bound) {
					if (v instanceof ImageView) {
						if (data instanceof Bitmap) {
							setViewImage((ImageView) v, (Bitmap) data);
						}
					}
				}
			}
		}
	}

	private void setViewImage(ImageView imageView, Bitmap bitmap) {
		imageView.setImageBitmap(bitmap);
	}
}

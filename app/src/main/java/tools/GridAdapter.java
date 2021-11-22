package tools;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
	private Context mContext;
	ArrayList<data> mydata;
    float dpHeight;
    float dpWidth;
	public GridAdapter(Context c, ArrayList<data> data) {
		mContext = c;
		mydata = data;
        DisplayMetrics displayMetrics = c.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
	//	mThumbIds = imagesId;
	}

	public int getCount() {
		return mydata.size();
	}

	public Object getItem(int position) {
		return null;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) { // if it's not recycled, initialize some
									// attributes

			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams((int)dpWidth/2,(int)dpHeight/2));
			imageView.setScaleType(ImageView.ScaleType.CENTER);
			imageView.setPadding(0,25 , 0, 25 );
			//imageView.setBackgroundResource(R.drawable.bord);
		} else {
			imageView = (ImageView) convertView;
		}
		Picasso.with(mContext).load(mydata.get(position).getImageId()).fit().centerInside().into(imageView);
//		imageView.setImageBitmap(mThumbIds.get(position));
//		imageView.setImageResource(mThumbIds.get(position));
		return imageView;
	}

	


		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

//		public long getItemId(int position) {
//			return mThumbIds.get(position);
//		}

}
package tools;


import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekm.youtubevr3dvideosprod.R;
import com.ekm.youtubevr3dvideosprod.Settings;

public class ImageAdapter2 extends BaseAdapter {
    private SharedPreferences pref;
    private Activity activity;
int pos;
    public ImageAdapter2(Activity activity) {
        this.activity = activity;
        pref = PreferenceManager
                .getDefaultSharedPreferences(activity);
        pos = pref.getInt("border", Settings.borderDefault);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return Settings.colors.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder {
        public ImageView imgViewFlag;
        public TextView txtViewTitle;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final  ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();

        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.item, null);

            // view.txtViewTitle = (TextView) convertView
            // .findViewById(R.ids.grid_item_text);
            view.imgViewFlag = (ImageView) convertView
                    .findViewById(R.id.grid_item_image);

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();
        }

        // view.txtViewTitle.setText(myNames[position]);
        System.out.println(position + "    " + Settings.colors.indexOf(pos));
        if (position==Settings.colors.indexOf(pos)) {
            view.imgViewFlag.setImageResource(R.drawable.check2);}
        else {
            view.imgViewFlag.setImageResource(R.drawable.lock);
        }

        view.imgViewFlag.setBackgroundResource(Settings.colors.get(position));
//
//       view.imgViewFlag.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View views) {
//                view.imgViewFlag.setImageResource(R.drawable.check2);
//
//            }
//        });
        return convertView;
    }



}
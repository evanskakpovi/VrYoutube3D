package tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekm.youtubevr3dvideosprod.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class StackAdapterNoSelect extends ArrayAdapter<data>{

    Context context;
    int layoutResourceId;   
    //data[] data = null;
    List<data> data = new ArrayList<data>();
   Typeface font;
    public StackAdapterNoSelect(Context context, int layoutResourceId, List<data> myData) {
        super(context, layoutResourceId, myData);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = myData;
//        font = Typeface.createFromAsset(context.getAssets(), "fonts/Beautiful Every Time.ttf");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        StackHolder holder;
       
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
           
            holder = new StackHolder();
        //    holder.num = (TextView)row.findViewById(R.ids.view1);
            holder.image = (ImageView)row.findViewById(R.id.image);
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.channel = (TextView) row.findViewById(R.id.channel);
            holder.playlist = (ImageView) row.findViewById(R.id.playlist);
            holder.dates = (TextView) row.findViewById(R.id.date);
            holder.duration = (TextView) row.findViewById(R.id.duration);
          //  holder.txtDescription = (TextView)row.findViewById(R.ids.description);
          //  holder.layout = (LinearLayout) row.findViewById(R.ids.layout);
            row.setTag(holder);
        }
        else
        {
            holder = (StackHolder)row.getTag();
        }


        final data myData = data.get(position);
//        if (myData.type==0) {
//            holder.playlist.setVisibility(View.VISIBLE);
//            holder.playlist.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    System.out.println("Playlist should play");
////                    Intent intent = new Intent(context, MainHolderPlaylistActivity.class);
////                  //  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////                    intent.putExtra(MainStarterActivity.vidId2, myData.extra);
////                    System.out.println("geting used is: "+myData.extra);
////                    context.startActivity(intent);
//                }
//            });
//        } else {
//            holder.playlist.setVisibility(View.INVISIBLE);
//        }
        holder.title.setText(myData.getTitle()+"");
        holder.dates.setText(myData.getDate() + myData.getCount());
        holder.channel.setText(myData.getChannel() + "");
        holder.duration.setText(myData.getDuration());
        Picasso.with(holder.image.getContext()).load(myData.getImageId()).fit().centerInside().into(holder.image);


     //   Picasso.with(context).load(myData.getImageId()).into(holder.image);

    //    System.out.println(myData.imageId);
        return row;
    }
   
    static class StackHolder
    {
        TextView title, channel;
        TextView dates, duration;
        ImageView image;
        ImageView playlist;
    }
}
package com.ekm.youtubevr3dvideosprod;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import java.util.List;

import tools.data;

/**
 * Created by EKM-VITAL on 12/9/2016.
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListHolder> {

    private List<data> myData;

    public ListAdapter(List<data> myData) {
        this.myData = myData;
    }
    @Override
    public ListAdapter.ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stack_list, parent, false);
        return new ListHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ListAdapter.ListHolder holder, int position) {
        data myDataitem = myData.get(position);
        holder.bind(myDataitem);
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public static class ListHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {

        private TextView title, channel;
        private TextView dates, duration;
        private ImageView image;
        private ImageView playlist;
    //    private data myData;

        public  static final String ListKey ="LIST";
        public ListHolder(View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            channel = (TextView) itemView.findViewById(R.id.channel);
            playlist = (ImageView) itemView.findViewById(R.id.playlist);
            dates = (TextView) itemView.findViewById(R.id.date);
            duration = (TextView) itemView.findViewById(R.id.duration);
            //itemView.setOnClickListener(this);
        }

        public void bind(data myData) {
            //this.myData = myData;
            Picasso.with(image.getContext()).load(myData.getImageId()).fit().centerInside().into(image);
            title.setText(myData.getTitle()+"");
            dates.setText(myData.getDate() + myData.getCount());
            channel.setText(myData.getChannel() + "");
            duration.setText(myData.getDuration());

        }

        @Override
        public void onClick(View view) {
            System.out.println("clicked");
        }
    }
}

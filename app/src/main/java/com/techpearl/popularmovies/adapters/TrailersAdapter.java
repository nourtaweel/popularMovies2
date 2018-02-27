package com.techpearl.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.model.Video;

import java.util.List;

/**
 * Created by Nour on 2/27/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private List<Video> videos;
    
    public TrailersAdapter(List<Video> videos){
        this.videos = videos;
    }
    
    public void setVideos(List<Video> videos){
        this.videos = videos;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewItem = inflater.inflate(R.layout.trailer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(viewItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(videos.get(position).getKey());
    }

    @Override
    public int getItemCount() {
        if(videos == null){
            return 0;
        }
        return videos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mTrailerImage;
        private Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            mTrailerImage = (ImageView) itemView.findViewById(R.id.trailerImageView);
            mContext = itemView.getContext();
        }
        private void bind(String trailerKey){
            Picasso.with(mContext)
                    .load(constructYoutubeImagePath(trailerKey, mContext))
                    .into(mTrailerImage);
        }
    }

    private String constructYoutubeImagePath(String key, Context context) {
        return context.getString(R.string.youtube_format, key);
    }
}

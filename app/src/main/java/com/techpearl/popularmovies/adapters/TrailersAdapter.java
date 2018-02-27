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
import com.techpearl.popularmovies.utils.YoutubeUtils;

import java.util.List;

/**
 * Created by Nour on 2/27/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.ViewHolder> {
    private List<Video> videos;
    private TrailerClickListener mListener;
    
    public TrailersAdapter(List<Video> videos, TrailerClickListener listener){
        this.videos = videos;
        this.mListener = listener;
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mTrailerImage;
        private Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            mTrailerImage = (ImageView) itemView.findViewById(R.id.trailerImageView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }
        private void bind(String trailerKey){
            Picasso.with(mContext)
                    .load(YoutubeUtils.constructYoutubeImagePath(trailerKey, mContext))
                    .into(mTrailerImage);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onTrailerClicked(videos.get(position).getKey());
        }
    }

    public interface TrailerClickListener{
        public void onTrailerClicked(String trailerKey);
    }


}

package com.techpearl.popularmovies.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.techpearl.popularmovies.R;
import com.techpearl.popularmovies.model.Video;
import com.techpearl.popularmovies.utils.YoutubeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Nour on 2/27/2018.
 * RecyclerView Adapter to view Trailers used in DetailsActivity
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View viewItem = inflater.inflate(R.layout.trailer_item, parent, false);
        return new ViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
        @BindView(R.id.trailerImageView) ImageView mTrailerImage;
        @BindView(R.id.youtubeImageView) ImageView mYoutubeIcon;
        private Context mContext;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = itemView.getContext();
            itemView.setOnClickListener(this);
        }
        private void bind(String trailerKey){
            mYoutubeIcon.setVisibility(View.INVISIBLE);
            Picasso.with(mContext)
                    .load(YoutubeUtils.constructYoutubeImagePath(trailerKey, mContext))
                    .error(R.color.placehloderColor)
                    .placeholder(R.drawable.ic_youtube_faded)
                    .into(mTrailerImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            mYoutubeIcon.setVisibility(View.VISIBLE);
                        }
                        @Override
                        public void onError() {
                        }
                    });

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mListener.onTrailerClicked(videos.get(position).getKey());
        }
    }

    public interface TrailerClickListener{
        void onTrailerClicked(String trailerKey);
    }


}

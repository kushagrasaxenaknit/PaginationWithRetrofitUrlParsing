package kushagra.com.paginationwithretrofiturlparsing.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.List;

import kushagra.com.paginationwithretrofiturlparsing.R;
import kushagra.com.paginationwithretrofiturlparsing.backgroundTask.DownloadFileTask;
import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.OnLoadMoreListener;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PaginationAdapterCallback;
import kushagra.com.paginationwithretrofiturlparsing.localDatabase.DatabaseControllerAllQuery;
import kushagra.com.paginationwithretrofiturlparsing.viewHolders.SongsViewHolder;

import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.TABLENAME;
import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.storagePath;

/**
 * Created by Kushagra Saxena on 26/03/2018.
 */


public class SongsPaginationDataAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private static List<OlaData> songCurrentList;
    protected Handler handler;
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private boolean endReached;
    private OnLoadMoreListener onLoadMoreListener;
    private PaginationAdapterCallback paginationAdapterCallback;
    private Context mContext;
    DatabaseControllerAllQuery databaseControllerAllQuery ;



    public SongsPaginationDataAdapter(List<OlaData> songReceivedtList, RecyclerView recyclerView, Context context) {
        songCurrentList = songReceivedtList;
        mContext=context;
        databaseControllerAllQuery = new DatabaseControllerAllQuery(mContext);
        handler=new Handler();
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if(!endReached) {
                                if (!loading
                                        && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                    // End has been reached
                                    // Do something
                                    Log.i("load more IN ADPATER = ", "loading = " + loading
                                            + "(lastVisibleItem + visibleThreshold)" + (lastVisibleItem + visibleThreshold));
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (onLoadMoreListener != null) {
                                                onLoadMoreListener.onLoadMore();
                                            }
                                        }
                                    });

                                    loading = true;
                                }

                            }//end reached
                        }
                    });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return songCurrentList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.songs_row_item, parent, false);

            vh = new SongsViewHolder(v,mContext,songCurrentList);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progress_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        OlaData result = songCurrentList.get(position); // song
        if (holder instanceof SongsViewHolder) {

            final SongsViewHolder songsVH = (SongsViewHolder) holder;

            songsVH.mMovieTitle.setText(result.getSong());

            songsVH.mMovieDesc.setText(result.getArtists());
            songsVH.shineButton.setBtnColor(Color.GRAY);
            songsVH.shineButton.setBtnFillColor(Color.RED);
            songsVH.shineButton.setShapeResource(R.raw.heart);
            OlaData olaData=songCurrentList.get(songsVH.getAdapterPosition());

            if(databaseControllerAllQuery.getFavStatus(olaData,TABLENAME))
            {

                songsVH.shineButton.setChecked(true);
            }
            else
            {

                songsVH.shineButton.setChecked(false);
            }


            songsVH.shineButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OlaData olaData=songCurrentList.get(songsVH.getAdapterPosition());

                    if(databaseControllerAllQuery.getFavStatus(olaData,TABLENAME))
                    {
                        databaseControllerAllQuery.removeSongsFavoriteStatus(songCurrentList.get(songsVH.getAdapterPosition()));
                        // setCallbackListener
                        if(paginationAdapterCallback!=null)
                        {
                            paginationAdapterCallback.retryPageLoad();

                        }


                    }
                    else
                    {
                        databaseControllerAllQuery.setSongsFavoriteStatus(songCurrentList.get(songsVH.getAdapterPosition()));



                    }
                }
            });


            // load movie thumbnail
            loadImage(result.getCoverImage())
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            // TODO: 08/11/16 handle failure
                            songsVH.mProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // image ready, hide progress now
                            songsVH.mProgress.setVisibility(View.GONE);
                            return false;   // return false if you want Glide to handle everything else.
                        }
                    })
                    .into(songsVH.mPosterImg);


        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }
    /**
     * Using Glide to handle image loading.
     * @param posterPath
     * @return Glide builder
     */
    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
        return Glide
                .with(mContext)
                .load( posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade();
    }

    public void setLoaded() {
        loading = false;
    }
    public void setEndReachedTrue() {
        endReached = true;
    }
    public void resetEndReached() {
        endReached = false;
    }

    @Override
    public int getItemCount() {
        return songCurrentList.size();
    }

       public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }
    public void setCallbackListener(PaginationAdapterCallback callbackListener) {
        this.paginationAdapterCallback = callbackListener;
    }




    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}
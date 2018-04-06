package kushagra.com.paginationwithretrofiturlparsing.viewHolders;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.exoplayer.PlayerActivity;
import com.sackcentury.shinebuttonlib.ShineButton;

import java.util.ArrayList;
import java.util.List;

import kushagra.com.paginationwithretrofiturlparsing.R;
import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PlaySongListener;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PlaySongListernerImplement;
import kushagra.com.paginationwithretrofiturlparsing.localDatabase.DatabaseControllerAllQuery;

/**
 * Created by Kushagra Saxena on 26/03/2018.
 */

public class SongsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {
    public TextView mMovieTitle;
    public TextView mMovieDesc;
    public ShineButton shineButton;
    public ImageView mPosterImg;
    public ProgressBar mProgress;
    public GestureDetector gestureDetector;
    Context context;

    List<OlaData> locMovieResults=new ArrayList<>();
    DatabaseControllerAllQuery databaseControllerAllQuery ;


    public SongsViewHolder(View itemView,Context con,List<OlaData> movieResults) {
        super(itemView);
        this.context=con;
        locMovieResults=movieResults;
        databaseControllerAllQuery = new DatabaseControllerAllQuery(context);




        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);

        mMovieTitle = (TextView) itemView.findViewById(R.id.movie_title);
        mMovieDesc = (TextView) itemView.findViewById(R.id.movie_desc);
        mPosterImg = (ImageView) itemView.findViewById(R.id.movie_poster);
        mProgress = (ProgressBar) itemView.findViewById(R.id.movie_progress);
        shineButton = (ShineButton) itemView.findViewById(R.id.shine_button);

    }

    @Override
    public void onClick(View view) {
        databaseControllerAllQuery.incPlayFrequency(locMovieResults.get(getAdapterPosition()));
        PlaySongListernerImplement playSongListernerImplement = new PlaySongListernerImplement();
        playSongListernerImplement.startSongListener(locMovieResults.get(getAdapterPosition()));



     //   Toast.makeText(context,locMovieResults.get(getAdapterPosition()).getSong() + "clicked ", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onLongClick(View view) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, locMovieResults.get(getAdapterPosition()).getUrl());
        context.startActivity(Intent.createChooser(i, "Share URL"));
        return true;
    }

}
package kushagra.com.paginationwithretrofiturlparsing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.exoplayer.PlayerActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.PermissionCallback;

import de.hdodenhof.circleimageview.CircleImageView;
import kushagra.com.paginationwithretrofiturlparsing.adapters.ViewPagerAdapter;
import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;
import kushagra.com.paginationwithretrofiturlparsing.fragments.AllSongsFragment;
import kushagra.com.paginationwithretrofiturlparsing.fragments.DownloadedSongFragment;
import kushagra.com.paginationwithretrofiturlparsing.fragments.FavouriteSongsFragment;
import kushagra.com.paginationwithretrofiturlparsing.fragments.RecentPlayedFragment;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.ComponentListener;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PlaySongListener;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PlaySongListernerImplement;

import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.ALL_SONGS;
import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.DOWNLOADED;
import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.FAVOURITES;
import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.RECENT;

public class MainActivity extends AppCompatActivity implements PlaySongListener{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
     AllSongsFragment allSongsFragment;
     DownloadedSongFragment downloadedSongFragment;
     FavouriteSongsFragment favouriteSongsFragment;
     RecentPlayedFragment recentPlayedFragment;
    private int[] tabIcons = {
            R.drawable.all_songs,R.drawable.ic_favorite,R.drawable.recent_songs};

    private SimpleExoPlayer player;
    private SimpleExoPlayerView playerView;
    private ComponentListener componentListener;

    private static final DefaultBandwidthMeter BANDWIDTH_METER =
            new DefaultBandwidthMeter();
    private long playbackPosition;
    private int currentWindow;
    private boolean playWhenReady = true;
    CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ALL_SONGS);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        componentListener = new ComponentListener();
        playerView = findViewById(R.id.exo_player);
        circleImageView =  findViewById(R.id.circularImage);



        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        allSongsFragment=new AllSongsFragment();
//        downloadedSongFragment =new DownloadedSongFragment();
        favouriteSongsFragment =new FavouriteSongsFragment();
        recentPlayedFragment =new RecentPlayedFragment();


        setupViewPager(viewPager);

        setupPlayListeners();


        //Setup Tabs
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    switch (position) {
                        case 0:
                            actionBar.setTitle(ALL_SONGS);
                          //  allSongsFragment.getWebServiceData();

                            break;
//                        case 1:
//                            actionBar.setTitle(DOWNLOADED);
//                            //(new DownloadedSongFragment())
//
//                            break;
                        case 1:
                            actionBar.setTitle(FAVOURITES);
                           //favouriteSongsFragment.getFavouriteData();
                            break;
                        case 2:
                            actionBar.setTitle(RECENT);
                            //recentPlayedFragment.getRecentData();
                            break;
                    }
                }
            }
        });
        setupTabIcons();


    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
       // tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }
    private void setupPlayListeners() {
        PlaySongListernerImplement.setPlaySongListener(this);

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(allSongsFragment, "ONE");
//        adapter.addFrag(downloadedSongFragment, "TWO");
        adapter.addFrag(favouriteSongsFragment, "THREE");
        adapter.addFrag(recentPlayedFragment, "THREE");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void startSong(final OlaData olaData) {
        playerView.setVisibility(View.VISIBLE);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent =new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("url",olaData.getUrl());
                intent.putExtra("playbackPosition",playbackPosition);
                intent.putExtra("currentWindow",currentWindow);

                startActivity(intent);
            }
        });

        // load movie thumbnail
        loadImage(olaData.getCoverImage())
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        // TODO: 08/11/16 handle failure

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        // image ready, hide progress now

                        return false;   // return false if you want Glide to handle everything else.
                    }
                })
                .into(circleImageView);
        initializePlayer(olaData.getUrl());
    }
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }
    private void initializePlayer(String url) {

        if (player == null) {

            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);



            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(this),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
            player.addListener(componentListener);
            player.addVideoDebugListener(componentListener);
            player.addAudioDebugListener(componentListener);
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
        }
        MediaSource mediaSource = buildMediaSource(Uri.parse(url));
        player.prepare(mediaSource, true, false);
    }

    private void releasePlayer() {
        if (player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.removeListener(componentListener);
            player.removeVideoDebugListener(componentListener);
            player.removeAudioDebugListener(componentListener);
            player.release();
            player = null;
        }
    }
    private MediaSource buildMediaSource(Uri uri) {

//    return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory("exoplayer-codelab"))
//            .createMediaSource(uri);
        return new ExtractorMediaSource.Factory( createDataSourceFactory(getApplicationContext(),"exoplayer-codelab",null))
                .createMediaSource(uri);
    }
    public DefaultDataSourceFactory createDataSourceFactory(Context context,
                                                            String userAgent, TransferListener<? super DataSource> listener) {
        // Default parameters, except allowCrossProtocolRedirects is true
        DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                listener,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true /* allowCrossProtocolRedirects */
        );

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                context,
                listener,
                httpDataSourceFactory
        );

        return dataSourceFactory;
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
    /**
     * Using Glide to handle image loading.
     * @param posterPath
     * @return Glide builder
     */
    private DrawableRequestBuilder<String> loadImage(@NonNull String posterPath) {
        return Glide
                .with(getApplicationContext())
                .load( posterPath)
                .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
                .centerCrop()
                .crossFade();
    }

}

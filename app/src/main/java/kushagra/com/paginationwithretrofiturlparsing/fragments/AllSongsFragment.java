package kushagra.com.paginationwithretrofiturlparsing.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import kushagra.com.paginationwithretrofiturlparsing.Api.MovieApi;
import kushagra.com.paginationwithretrofiturlparsing.Api.MovieService;
import kushagra.com.paginationwithretrofiturlparsing.R;
import kushagra.com.paginationwithretrofiturlparsing.Utils.CheckInternetConnection;
import kushagra.com.paginationwithretrofiturlparsing.adapters.SongsPaginationDataAdapter;
import kushagra.com.paginationwithretrofiturlparsing.backgroundTask.BackGroundTaskLoadJson;
import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.LinearLayoutManagerWrapper;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.OnLoadMoreListener;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.OnTaskCompleted;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PaginationAdapterCallback;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PlaySongListener;
import kushagra.com.paginationwithretrofiturlparsing.localDatabase.DatabaseControllerAllQuery;
import retrofit2.Call;

import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.TABLENAME;
import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.printOlaDataList;

/**
 * Created by Kushagra Saxena on 25/03/2018.
 */


public class AllSongsFragment extends Fragment  implements OnTaskCompleted ,PaginationAdapterCallback {
    private static final String TAG = "AllSongsFragment";




    private RecyclerView mRecyclerView;
    private SongsPaginationDataAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    // to keep track which pages loaded and next pages to load
    public static int pageNumber;
    private Context context;
    private List<OlaData> songsList;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;
    private boolean isSearchQuery=false;
    private String searchQuery="";

//    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    // DB Class to perform DB related operations
    DatabaseControllerAllQuery databaseControllerAllQuery ;
    protected Handler handler;
    CheckInternetConnection checkInternetConnection;
    public AllSongsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);//have this line of code to have options menu
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.all_songs_fragment, container, false);

        //Context
        context = getContext();
        databaseControllerAllQuery = new DatabaseControllerAllQuery(context);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.all_songs_recycler);
        pageNumber = 1;
        songsList = new ArrayList<OlaData>();
        handler = new Handler();
        checkInternetConnection=new CheckInternetConnection();

        progressBar = (ProgressBar) rootView.findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) rootView.findViewById(R.id.error_layout);
        btnRetry = (Button) rootView.findViewById(R.id.error_btn_retry);
        txtError = (TextView) rootView.findViewById(R.id.error_txt_cause);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        DividerItemDecoration itemDecor = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(itemDecor);
//        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) rootView.findViewById(R.id.main_swipe);
//
//        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
//            @Override public void onRefresh() {
//                // Do work to refresh the list here.
//                getWebServiceData();
//            }
//
//        });

        mLayoutManager = new LinearLayoutManager(context);


        // use a linear layout manager
       // mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(new LinearLayoutManagerWrapper(context,LinearLayoutManager.VERTICAL,false));

        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        // create an Object for Adapter
        mAdapter = new SongsPaginationDataAdapter(songsList, mRecyclerView,context);

        // set the adapter object to the Recyclerview
        mRecyclerView.setAdapter(mAdapter);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);;



        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWebServiceData();
            }
        });
        getWebServiceData();

        mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                    //add null , so the adapter will check view_type and show progress bar at bottom
                    songsList.add(null);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyItemInserted(songsList.size() - 1);

                        }
                    });


                    Log.i("load more fired = ", pageNumber + "");
                  ++pageNumber;
                  if(isSearchQuery)
                  {
                      getSearchedSongFromSql(searchQuery);
                  }
                  else
                  {
                      getAllSongFromSql();

                  }




            }
        });

        return rootView;
    }

    public void getWebServiceData() {
        clearPreviousResults();
        if(checkInternetConnection.isNetworkConnected(context))
        {
            // To ensure list is visible when retry button in error view is clicked
            hideErrorView();
            BackGroundTaskLoadJson backGroundTask = new BackGroundTaskLoadJson(context, this,this);
            backGroundTask.loadDataIntoList();

        }
        else
        {

            // Call setRefreshing(false) when the list has been refreshed.
//            mWaveSwipeRefreshLayout.setRefreshing(false);
            if(databaseControllerAllQuery.numberOfTuple(TABLENAME)>0) {

                hideErrorView();
//after hide error view set visibility gone
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, "Showing Offline data", Toast.LENGTH_SHORT).show();

                getAllSongFromSql();
            }
            else
            {
                progressBar.setVisibility(View.GONE);
                showErrorView(getResources().getString(R.string.error_msg_no_internet));
            }

        }


    }


    @Override
    public void onTaskCompleted(List<OlaData>  response) {

        // Call setRefreshing(false) when the list has been refreshed.
//       mWaveSwipeRefreshLayout.setRefreshing(false);
        Log.i("AllSongsFragment = ", "onTaskCompleted");

hideErrorView();
//after hide error view set visibility gone
        progressBar.setVisibility(View.GONE);

        //First insert list then do anything
        insertToSql(response);


        getAllSongFromSql();

    }
    public void insertToSql(List<OlaData>  response) {
        for(OlaData olaData: response)
        {
            databaseControllerAllQuery.insertSong(olaData,TABLENAME);
//olaData.setSong(olaData.getArtists());
//            databaseControllerAllQuery.insertSong(olaData,TABLENAME);
//            olaData.setSong(olaData.getCoverImage());
//            databaseControllerAllQuery.insertSong(olaData,TABLENAME);

        }

    }
    public void getAllSongFromSql() {


        List<OlaData>  response= databaseControllerAllQuery.getSongOfPage(TABLENAME, pageNumber);
        int size=databaseControllerAllQuery.numberOfTuple(TABLENAME);
        loadPage(response,size);
    }
    public void getSearchedSongFromSql(String searchQuery) {

        Log.i("getSearchedSongFromSql ", searchQuery+"");
        Log.i("getSearchedSongFromSql ", "pageNumber "+pageNumber+"");


        List<OlaData>  response= databaseControllerAllQuery.getSearchedSongByPage(TABLENAME, pageNumber,searchQuery);
        int size=databaseControllerAllQuery.numberOfTupleInSearchResult(TABLENAME,searchQuery);
        loadPage(response,size);
    }

        public void loadPage(List<OlaData>  response,int size) {
            Log.i("AllSongsFragment = ", "loadPage");
            Log.i("in if and pageNumber = ", pageNumber+"");
            Log.i("size is = ", size+"");




            if (pageNumber > 1) {

                songsList.remove(songsList.size() - 1);

                Log.i("in if and pageNumber = ", pageNumber+"");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemRemoved(songsList.size());
                }
            });

        }
//        // adding HashList to ArrayList
       // songsList.addAll(response);

if(response.size()==0||songsList.size()>=size)
{
    mAdapter.setEndReachedTrue();
}
else {
    for (OlaData olaData : response)
    //  for(OlaData olaData:response)
    {
        songsList.add(olaData);
        handler.post(new Runnable() {
            @Override
            public void run() {

                mAdapter.notifyItemInserted(songsList.size());


            }
        });
    }


    mAdapter.setLoaded();

}//else
            //again to not call load more
            if(songsList.size()>=size)
            {
                mAdapter.setEndReachedTrue();
            }

    }

    @Override
    public void retryPageLoad() {
//        getWebServiceData();

    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }


    public void showErrorView(String throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText((throwable));
        }
    }



    // Helpers -------------------------------------------------------------------------------------


    public void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }
//Search part

   @Override
   public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
       inflater.inflate(R.menu.search_menu, menu);
       super.onCreateOptionsMenu(menu,inflater);
       MenuItem search = menu.findItem(R.id.search);
       SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
       search(searchView);
   }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(context,"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void clearPreviousResults() {
        songsList.clear();
        mAdapter.notifyDataSetChanged();
        mAdapter.resetEndReached();
        pageNumber=1;
    }
    private void search(final SearchView searchView) {
        searchView.setQueryHint("Search Any Song");


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                clearPreviousResults();
                isSearchQuery=true;
                searchQuery=query;
                getSearchedSongFromSql(query);

                //getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                clearPreviousResults();

                if(newText!=null&&newText.trim().length()==0)
                {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);;


                    isSearchQuery=false;
                    getAllSongFromSql();
                }
                else {
                    ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);;

                    isSearchQuery=true;
                    searchQuery=newText;
                    getSearchedSongFromSql(newText);

                }

                return true;
            }
        });
    }


}

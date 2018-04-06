package kushagra.com.paginationwithretrofiturlparsing.backgroundTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import kushagra.com.paginationwithretrofiturlparsing.Api.MovieApi;
import kushagra.com.paginationwithretrofiturlparsing.Api.MovieService;
import kushagra.com.paginationwithretrofiturlparsing.R;
import kushagra.com.paginationwithretrofiturlparsing.Utils.CheckInternetConnection;
import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.OnTaskCompleted;
import kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement.PaginationAdapterCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Kushagra Saxena on 26/03/2018.
 */

public class BackGroundTaskLoadJson  {

    public OnTaskCompleted listener = null;//Call back interface
    private MovieService movieService;
    private PaginationAdapterCallback mCallback;

    Context context;

    List<OlaData> resultSongList  = new ArrayList<>();
    public BackGroundTaskLoadJson(Context context1, OnTaskCompleted listener1,PaginationAdapterCallback paginationAdapterCallback) {
        context = context1;
        mCallback = paginationAdapterCallback;
        listener = listener1;   //Assigning call back interface  through constructor

        //init service and load data
        movieService = MovieApi.getClient().create(MovieService.class);
    }



    //load the json data from url
    public void loadDataIntoList()
    {


        callTopRatedMoviesApi().enqueue(new Callback<List<OlaData>>() {
            @Override
            public void onResponse(Call<List<OlaData>> call, Response<List<OlaData>> response) {
                // Got data. Send it to adapter

                mCallback.hideErrorView();
                Log.i("Loading Page  number", "loadFirstPage: "+response);

                resultSongList = response.body();
                Log.i("result list", resultSongList.toString());

        listener.onTaskCompleted(resultSongList);
                //Collections.sort(results,OlaData.OlaDataSongArtist);

            }

            @Override
            public void onFailure(Call<List<OlaData>> call, Throwable t) {
                t.printStackTrace();
                mCallback.showErrorView(fetchErrorMessage(t));
            }
        });

    }

    private Call<List<OlaData>> callTopRatedMoviesApi() {
        return movieService.getTopRatedMovies();
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = context.getResources().getString(R.string.error_msg_unknown);
        CheckInternetConnection checkInternetConnection=new CheckInternetConnection();
        if (!(checkInternetConnection.isNetworkConnected(context))    ) {
            errorMsg = context.getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = context.getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }
}
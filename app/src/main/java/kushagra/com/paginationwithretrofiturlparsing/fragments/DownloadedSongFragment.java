package kushagra.com.paginationwithretrofiturlparsing.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kushagra.com.paginationwithretrofiturlparsing.R;

/**
 * Created by Kushagra Saxena on 25/03/2018.
 */


public class DownloadedSongFragment extends Fragment {

    public DownloadedSongFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.downloaded_song_fragment, container, false);
    }

}

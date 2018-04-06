package kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement;

import android.content.Context;

import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;

/**
 * Created by Kushagra Saxena on 06/04/2018.
 */

public   class PlaySongListernerImplement {
    public static PlaySongListener playSongListener = null;//Call back interface


    public static void setPlaySongListener(PlaySongListener playListener) {
        playSongListener=playListener;

    }
    public void startSongListener(OlaData olaData) {
        if(playSongListener!=null)
        {
            playSongListener.startSong(olaData);

        }

    }
}

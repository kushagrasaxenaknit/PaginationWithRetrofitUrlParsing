package kushagra.com.paginationwithretrofiturlparsing.Utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Kushagra Saxena on 28/03/2018.
 */

public class CheckInternetConnection {
    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    public boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

}

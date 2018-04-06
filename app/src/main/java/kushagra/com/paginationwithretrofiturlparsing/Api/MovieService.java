package kushagra.com.paginationwithretrofiturlparsing.Api;

import java.util.List;

import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.EXTENDED_URL;

/**
 * Created by Kushagra Saxena on 18/03/2018.
 */

public interface MovieService {

    @GET(EXTENDED_URL)
    Call<List<OlaData>> getTopRatedMovies();

}

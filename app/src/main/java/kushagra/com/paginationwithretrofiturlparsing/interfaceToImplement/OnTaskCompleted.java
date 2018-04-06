package kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement;

import java.util.List;

import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;

/**
 * Created by Kushagra Saxena on 26/03/2018.
 */

public interface OnTaskCompleted{

    void onTaskCompleted(List<OlaData>  response);
}
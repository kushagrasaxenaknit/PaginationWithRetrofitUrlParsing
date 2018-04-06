package kushagra.com.paginationwithretrofiturlparsing.interfaceToImplement;

/**
 * Created by Kushagra Saxena on 26/03/2018.
 */

public interface PaginationAdapterCallback {

    void retryPageLoad();
    void showErrorView(String throwable);
    void hideErrorView();
}

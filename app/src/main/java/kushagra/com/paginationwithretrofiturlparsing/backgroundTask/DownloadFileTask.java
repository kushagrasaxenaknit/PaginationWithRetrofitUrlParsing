package kushagra.com.paginationwithretrofiturlparsing.backgroundTask;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import kushagra.com.paginationwithretrofiturlparsing.R;

/**
 * Created by Kushagra Saxena on 30/03/2018.
 */


// background task to download file
public class DownloadFileTask extends AsyncTask<String,Integer,Void> {
    NotificationManager mNotifyManager;
    NotificationCompat.Builder mBuilder;
    private Context context;
    String storeDir="";

    public DownloadFileTask(Context context1,String path) {
        context = context1;
        storeDir=path;

    }

    protected void onPreExecute(){
        super.onPreExecute();
        mNotifyManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setContentTitle("File Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.downloaded_song);
        Toast.makeText(context, "Downloading the file... The download progress is on notification bar.", Toast.LENGTH_LONG).show();

    }

    protected Void doInBackground(String...params){
        URL url;
        int count;
        try {
            url = new URL(params[0]);
            String pathl="";
            try {
                File myDir = new File(storeDir);
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }


                if(myDir.exists()){
                    HttpURLConnection con=(HttpURLConnection)url.openConnection();
                    InputStream is=con.getInputStream();
                    String pathr=url.getPath();
                    String filename=pathr.substring(pathr.lastIndexOf('/')+1);
                    pathl=storeDir+"/"+filename+".mp3";
                    FileOutputStream fos=new FileOutputStream(pathl);
                    int lenghtOfFile = con.getContentLength();
                    byte data[] = new byte[4096];
                    long total = 0;
                    while ((count = is.read(data)) != -1) {
                        total += count;
                        // publishing the progress
                        publishProgress((int)((total*100)/lenghtOfFile));
                        // writing data to output file
                        fos.write(data, 0, count);
                    }

                    is.close();
                    fos.flush();
                    fos.close();
                }
                else{
                    Log.e("Error","Not found: "+storeDir);

                }

            } catch (Exception e) {
                e.printStackTrace();

            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;

    }

    protected void onProgressUpdate(Integer... progress) {

        mBuilder.setProgress(100, progress[0], false);
        // Displays the progress bar on notification
        mNotifyManager.notify(0, mBuilder.build());
    }

    protected void onPostExecute(Void result){
        mBuilder.setContentText("Download complete");
        // Removes the progress bar
        mBuilder.setProgress(0,0,false);
        mNotifyManager.notify(0, mBuilder.build());
    }

}
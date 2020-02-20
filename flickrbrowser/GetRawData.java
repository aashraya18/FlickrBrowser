package singal.aashray.flickrbrowser;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

enum DownloadStatus {IDLE,PROCESSING,NOT_INITIALIZED,FAILED_OR_EMPTY,OK}
// idle             not processing anything
// PROCESSING       downloading the data
// NOT INITIALIZED  URl is invalid so download can't take place
// FAILED OR EMPTY  Failed to download or data came back empty
// OK               got some valid data and download is successful


class GetRawData extends AsyncTask<String,Void,String> {
    private static final String TAG = "GetRawData";

    // where m stands for member variable and is generally used as a convention
    private DownloadStatus mDownloadStatus;
    private final OnDownloadComplete mCallback;

    interface OnDownloadComplete{
        void OnDownloadComplete(String data,DownloadStatus status);
    }
    // Can't understand the use of this mcallback and how mDownloadcomplete works????????
    public GetRawData(OnDownloadComplete callback) {
        this.mDownloadStatus = DownloadStatus.IDLE;
        mCallback = callback;
    }

    void runInSameThread(String s){
        Log.d(TAG, "runInSameThread: starts");

        //onPostExecute(doInBackground(s));
        if(mCallback != null)
        {
            String result = doInBackground(s);
            mCallback.OnDownloadComplete(result,mDownloadStatus);
        }

        Log.d(TAG, "runInSameThread: ends");
    }

    @Override
    protected void onPostExecute(String s) {

        //Log.d(TAG, "onPostExecute: parameter is" + s);
        if(mCallback != null){
            mCallback.OnDownloadComplete(s,mDownloadStatus);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        if(strings == null)
        {
            mDownloadStatus = DownloadStatus.NOT_INITIALIZED;
            return null;
        }
        try{
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(strings[0]);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int response = connection.getResponseCode();
            Log.d(TAG, "doInBackground: The response code was : "+ response);
            StringBuilder result = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

//            String line;
//            while(null != (line = reader.readline())){
           // Advantage of using for loop instead of while loop is that we are restricting the scope of the variable line making code more robust
            for(String line = reader.readLine();line !=null; line = reader.readLine()){
                result.append(line).append("\n");
            }
            mDownloadStatus = DownloadStatus.OK;
            return result.toString();

        }catch (MalformedURLException e){
            Log.e(TAG, "doInBackground: Invalid URL" + e.getMessage() );
        }catch (IOException e){
            Log.e(TAG, "doInBackground: IO Exception Returning data: " + e.getMessage() );
        }catch(SecurityException e){
            Log.e(TAG, "doInBackground: Security Exception. Needs permission?" + e.getMessage()  );
        }finally {
            //This block will be executed just before the return statement on line 65 if no exception is thrown
            if(connection!=null){
                connection.disconnect();
            }
            if(reader!=null){
                try{
                    reader.close();
                }catch (IOException e){
                    Log.e(TAG, "doInBackground: Error closing reader stream " + e.getMessage() );
                }
            }

        }
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }
}

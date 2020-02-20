package singal.aashray.flickrbrowser;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class GetFlickrJsonData extends AsyncTask<String,Void,List<Photo>> implements GetRawData.OnDownloadComplete {

    private static final String TAG = "GetFlickrJsonData";

    // Creating a list of object type photo initializing to null

    private  List<Photo> mPhotoList = null;
    private  String mBaseURL;
    private  String mLanguage;
    private boolean mMatchAll;
    private boolean runningOnSameThread = false;

    private final OnDataAvailable mCallBack;

    interface OnDataAvailable{
        void OnDataAvailable(List<Photo> data,DownloadStatus status);
    }

    public GetFlickrJsonData(String baseURL, String language, boolean matchAll, OnDataAvailable callBack) {
        Log.d(TAG, "GetFlickrJsonData: called");
        mBaseURL = baseURL;
        mLanguage = language;
        mMatchAll = matchAll;
        mCallBack = callBack;
    }

    void executeOnSameThread(String searchCriteria){
        Log.d(TAG, "executeOnSameThread: starts");
        runningOnSameThread = true;
        String desinationUri = createUri(searchCriteria,mLanguage,mMatchAll);

        ///  Not getting this callback thing???????????

        GetRawData getRawData = new GetRawData(this);
        getRawData.execute(desinationUri);
        Log.d(TAG, "executeOnSameThread: ends");
    }

    @Override
    protected void onPostExecute(List<Photo> photos) {
        Log.d(TAG, "onPostExecute: Starts");

        if(mCallBack!= null){
            mCallBack.OnDataAvailable(mPhotoList,DownloadStatus.OK);
        }
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Photo> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: starts");
        String destinationUri = createUri(strings[0],mLanguage,mMatchAll);

        GetRawData getRawData = new GetRawData(this);
        getRawData.runInSameThread(destinationUri);
        Log.d(TAG, "doInBackground: ends");

        return null;
    }

    private String createUri(String searchCriteria, String lang, boolean matchAll){
        Log.d(TAG, "createUri: starts");

        /*
        Uri uri = Uri.parse(mBaseURL);
        Uri.Builder builder = uri.buildUpon();
        builder = builder.appendQueryParameter("tags",searchCriteria);
        builder = builder.appendQueryParameter("tagmode",matchAll ? "ALL" : "ANY");
        builder = builder.appendQueryParameter("lang", lang);
        builder = builder.appendQueryParameter("format","json");
        builder = builder.appendQueryParameter("nojsoncallback","1");
        uri = builder.build();

        //So at bottom we have written the modified version of the above code that is quite simpler
        */

        return Uri.parse(mBaseURL).buildUpon()
                .appendQueryParameter("tags",searchCriteria)
                .appendQueryParameter("tagmode",matchAll ? "ALL" : "ANY")
                .appendQueryParameter("lang", lang)
                .appendQueryParameter("format","json")
                .appendQueryParameter("nojsoncallback","1")
                .build().toString();
    }

    @Override
    public void OnDownloadComplete(String data, DownloadStatus status) {
        Log.d(TAG, "OnDownloadComplete: Starts status: " + status );
        if(status == DownloadStatus.OK){
            mPhotoList = new ArrayList<>();
        }
        try{
            JSONObject jsonData = new JSONObject(data);
            JSONArray itemsArray = jsonData.getJSONArray("items");

            for(int i=0;i<itemsArray.length();i++){
                JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                String title = jsonPhoto.getString("title");
                String author = jsonPhoto.getString("author");
                String authorId = jsonPhoto.getString("author_id");
                String tags = jsonPhoto.getString("tags");

                JSONObject jsonMedia = jsonPhoto.getJSONObject("media");
                String photoURL = jsonMedia.getString("m");

                //The Java String replaceFirst() method replaces the first substring 'regex' found that matches the given argument substring
                String link = photoURL.replaceFirst("_m.","_b.");

                Photo photoObject = new Photo(title,author,authorId,link,tags,photoURL);
                mPhotoList.add(photoObject);

                Log.d(TAG, "OnDownloadComplete: " + photoObject.toString());
            }
        }catch(JSONException jsone){
                jsone.printStackTrace();
            Log.e(TAG, "OnDownloadComplete: Error Processing JSON data" + jsone.getMessage() );
            status = DownloadStatus.FAILED_OR_EMPTY;
        }

        if(runningOnSameThread && mCallBack != null){
            // now inform the caller that processing is done - possibly returning null if there
            // was an error
            mCallBack.OnDataAvailable(mPhotoList,status);
        }

        Log.d(TAG, "OnDownloadComplete: ends");

    }
}























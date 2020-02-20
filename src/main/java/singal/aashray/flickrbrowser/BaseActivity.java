package singal.aashray.flickrbrowser;

import android.util.Log;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    static final String FLICKR_QUERY = "FLICKR_QUERY";
    static final String PHOTO_TRANSFER ="PHOTO_TRANSFER";


    void activateToolbar(boolean enableHome){
        Log.d(TAG, "activateToolbar: Starts");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar==null)
        {
            Toolbar toolbar =  findViewById(R.id.toolbar);
            if(toolbar!=null){
                setSupportActionBar(toolbar);
                actionBar = getSupportActionBar();
            }
        }
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(enableHome);
        }
    }


}

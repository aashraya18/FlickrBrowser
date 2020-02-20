package singal.aashray.flickrbrowser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

class FlickrRecyclerViewAdapter extends RecyclerView.Adapter<FlickrRecyclerViewAdapter.FlickrImageViewHolder>{
    private static final String TAG = "FlickrRecyclerViewAdapt";
    private List<Photo> mPhoto;
    private Context mContext;

    public FlickrRecyclerViewAdapter(List<Photo> photo, Context context) {
        mPhoto = photo;
        mContext = context;
    }

    @NonNull
    @Override
    public FlickrImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a view
        Log.d(TAG, "onCreateViewHolder: new view requested");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.browse,parent,false);
        return new FlickrImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlickrImageViewHolder holder, int position) {
        // Called by layout manager when it wants new data in an existing row
        if(mPhoto == null || (mPhoto.size()==0)){
            holder.thumbnail.setImageResource(R.drawable.placeholder);
            holder.title.setText(R.string.empty_photo);
        }else {
            Photo photoItem = mPhoto.get(position);
            Log.d(TAG, "onBindViewHolder: " + photoItem.getTitle() + "--->" + position);
            Picasso.get().load(photoItem.getImage())
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.thumbnail);
            holder.title.setText(photoItem.getTitle());
        }
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: called");
        return ((mPhoto != null ) && (mPhoto.size()!=0)? mPhoto.size():1);
        // 1 is chosen because we wan to display default record with placeholder image and default image
    }

    void loadNewData(List<Photo> newPhotos){
        mPhoto = newPhotos;
        notifyDataSetChanged();
    }

    public Photo getPhoto(int position){
        return ((mPhoto != null ) && (mPhoto.size()!=0)? mPhoto.get(position):null);
    }

    static class FlickrImageViewHolder extends RecyclerView.ViewHolder{
        private static final String TAG = "FlickrImageViewHolder";
        ImageView thumbnail = null;
        TextView title = null;

        public FlickrImageViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "FlickrImageViewHolder: starts");
            this.thumbnail = itemView.findViewById(R.id.thumbnail);
            this.title = itemView.findViewById(R.id.title);

        }
    }

}

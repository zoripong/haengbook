package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Model.FirebaseImage;
import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;
import kr.hs.emirim.uuuuri.haegbook.R;

import static android.content.Context.WINDOW_SERVICE;

public class ImageDetailRecyclerAdapter extends RecyclerView.Adapter<ImageDetailRecyclerAdapter.ImageViewHolder> {
    private final String TAG = "ImageDetailRecyclerAdapter";

    Context context;
    Activity nowActivity;

    List<GalleryImage> items;
    List<String> mComments;

    public ImageDetailRecyclerAdapter(Context context, Activity nowActivity, List<GalleryImage> items){
        this.context = context;
        this.nowActivity = nowActivity;
        this.items = items;
        mComments = new ArrayList<>(items.size());
        for(int i=0; i<items.size(); i++){
            mComments.add(i, "");
        }
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_detail, parent, false);
        return new ImageViewHolder(v);
    }

    public ArrayList<FirebaseImage> getImageList(String date){
        ArrayList<FirebaseImage> firebaseImages = new ArrayList<>();
        for(int i = 0; i<getItemCount(); i++){
            Log.e(TAG, "Path : "+items.get(i).getImgPath());
            Log.e(TAG, "Comment : "+ mComments.get(i));

            /*
            * param : String imageComment, String imageURI, String date
            * */
            firebaseImages.add(new FirebaseImage(mComments.get(i), items.get(i).getImgPath(), date));

        }
        return firebaseImages;

    }
    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        GalleryImage item = items.get(position);

        Point pt = new Point();
        nowActivity.getWindowManager().getDefaultDisplay().getSize(pt);
        ((WindowManager) nowActivity.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(pt);
        int width = pt.x;

        Glide.with(context)
                .load(item.getImgPath())
                .asBitmap()
                .override(width, width)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)// for remote images
                .placeholder(R.drawable.loading)
                .into(holder.imageView);

        holder.commentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mComments.set(position, holder.commentEt.getText().toString());
            }
        });

    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        EditText commentEt;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageview);
            commentEt = itemView.findViewById(R.id.comment_et);
        }
    }
}


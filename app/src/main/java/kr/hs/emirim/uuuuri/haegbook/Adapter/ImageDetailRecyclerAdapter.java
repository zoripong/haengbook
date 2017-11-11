package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Model.GalleryImage;
import kr.hs.emirim.uuuuri.haegbook.R;

import static android.content.Context.WINDOW_SERVICE;

public class ImageDetailRecyclerAdapter extends RecyclerView.Adapter<ImageDetailRecyclerAdapter.ImageViewHolder> {
    Context context;
    Activity nowActivity;

    List<GalleryImage> items;

    public ImageDetailRecyclerAdapter(Context context, Activity nowActivity, List<GalleryImage> items){
        this.context = context;
        this.nowActivity = nowActivity;
        this.items = items;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_detail, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        GalleryImage item = items.get(position);

        Point pt = new Point();
        nowActivity.getWindowManager().getDefaultDisplay().getSize(pt);
        ((WindowManager) nowActivity.getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getSize(pt);
        int height = pt.x;
        int width = pt.y;


        Glide.with(context)
                .load(item.getImgPath())
                .asBitmap()
                .override(width, width)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.drawable.loading)
                .into(holder.imageView);

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


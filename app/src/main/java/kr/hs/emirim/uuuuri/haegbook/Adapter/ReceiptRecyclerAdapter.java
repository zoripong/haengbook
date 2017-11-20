package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Interface.TravelDetailTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.R;

import static kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType.CULTURE;
import static kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType.ETC;
import static kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType.FOOD;
import static kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType.GIFT;
import static kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType.SHOPPING;
import static kr.hs.emirim.uuuuri.haegbook.Interface.ReceiptType.TRAFFIC;

/**
 * Created by 유리 on 2017-09-14.
 */

public class ReceiptRecyclerAdapter extends RecyclerView.Adapter<ReceiptRecyclerAdapter.ReceiptViewHolder> {
    Context context;
    Activity nowActivity;

    ArrayList<Receipt> items;

    private FirebaseDatabase mDatabase;

    SharedPreferenceManager spm;

    public ReceiptRecyclerAdapter(Context context, Activity nowActivity, ArrayList<Receipt> items){
        this.context = context;
        this.nowActivity = nowActivity;
        this.items = items;
    }

    @Override
    public ReceiptViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);
        return new ReceiptViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ReceiptViewHolder holder, final int position) {
        spm = new SharedPreferenceManager((Activity) context);

        final Receipt item = items.get(position);
        holder.titleTv.setText(item.getTitle());
        holder.amountTv.setText(String.valueOf(item.getAmount()));
        holder.typeTv.setText(String.valueOf(item.getType()));
        holder.memoTv.setText(item.getMemo());
        holder.dateTv.setText(item.getDate());

        switch (item.getType()){
            case FOOD:
                holder.typeIv.setImageResource(R.drawable.barcode_food);
                break;
            case TRAFFIC:
                holder.typeIv.setImageResource(R.drawable.barcode_traffic);
                break;
            case SHOPPING:
                holder.typeIv.setImageResource(R.drawable.barcode_shopping);
                break;
            case GIFT:
                holder.typeIv.setImageResource(R.drawable.barcode_gift);
                break;
            case CULTURE:
                holder.typeIv.setImageResource(R.drawable.barcode_culture);
                break;
            case ETC:
                holder.typeIv.setImageResource(R.drawable.barcode_etc);
                break;
        }
        holder.deleteIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog deleteDialog = new Dialog(context, R.style.MyDialog);
                deleteDialog.setContentView(R.layout.dialog_delete);
                deleteDialog.findViewById(R.id.delete_btn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, items.size());

                        deleteReceipt(item.getKey());
                        deleteDialog.dismiss();

                    }
                });
                deleteDialog.show();


            }
        });
        holder.typeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, item.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        //TODO DEBUG PUBLISHING 일때 GONE
        if(spm.retrieveString(TravelDetailTag.IS_PUBLISHING_TAG)!=null)
            holder.deleteIv.setVisibility(View.GONE);
    }

    private void deleteReceipt(String key){

        mDatabase = FirebaseDatabase.getInstance();
        String bookCode =  spm.retrieveString(TravelDetailTag.CARD_BOOK_CODE_TAG);
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+bookCode+"/Content/Receipt/"+key);
        receiptRef.removeValue();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ReceiptViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView amountTv;
        TextView typeTv;
        TextView memoTv;
        TextView dateTv;
        ImageView typeIv;
        ImageView deleteIv;

        public ReceiptViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title_tv);
            amountTv = itemView.findViewById(R.id.amount_tv);
            typeTv = itemView.findViewById(R.id.type_tv);
            memoTv = itemView.findViewById(R.id.memo_tv);
            dateTv = itemView.findViewById(R.id.date_tv);
            typeIv = itemView.findViewById(R.id.type_iv);
            deleteIv = itemView.findViewById(R.id.delete_iv);
        }
    }
}



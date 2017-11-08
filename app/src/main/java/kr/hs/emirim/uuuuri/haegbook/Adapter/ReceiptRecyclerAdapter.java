package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import kr.hs.emirim.uuuuri.haegbook.Model.Receipt;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by 유리 on 2017-09-14.
 */

public class ReceiptRecyclerAdapter extends RecyclerView.Adapter<ReceiptRecyclerAdapter.ReceiptViewHolder> {
    Context context;
    Activity nowActivity;

    ArrayList<Receipt> items;


    public ReceiptRecyclerAdapter(Context context, Activity nowActivity){
        this.context = context;
        this.nowActivity = nowActivity;
    }

    @Override
    public ReceiptViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receipt, parent, false);
        return new ReceiptViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ReceiptViewHolder holder, int position) {
        Receipt item = items.get(position);
        holder.titleTv.setText(item.getTitle());
        holder.amountTv.setText(item.getAmount());
        holder.typeTv.setText(item.getType());
        holder.memoTv.setText(item.getMemo());
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

        public ReceiptViewHolder(View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.title_tv);
            amountTv = itemView.findViewById(R.id.amount_tv);
            typeTv = itemView.findViewById(R.id.type_tv);
            memoTv = itemView.findViewById(R.id.memo_tv);
        }
    }


}



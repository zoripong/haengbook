package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import java.util.HashMap;
import java.util.Map;

import kr.hs.emirim.uuuuri.haegbook.Interface.CurrencyTag;
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

                        deleteReceipt(item.getKey(),item.getType(),item.getAmount());
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

    private void deleteReceipt(String key, int type, String amount){
        ProgressDialog updateDialog = new ProgressDialog(context);

        updateDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        updateDialog.setMessage("로딩중입니다..");
        updateDialog.setCancelable(false);
        updateDialog.show();

        mDatabase = FirebaseDatabase.getInstance();
        String bookCode =  spm.retrieveString(TravelDetailTag.CARD_BOOK_CODE_TAG);
        Float restMoney = spm.retrieveFloat(TravelDetailTag.REST_MONEY_TAG);

        final DatabaseReference amountRef = mDatabase.getReference("TravelMoney/"+bookCode);

        Map<String, Object> amountUpdates = new HashMap<String, Object>();
        Map<String, Object> typeUpdates = new HashMap<String, Object>();
        char lastAmount = amount.charAt(amount.length() - 1);
        Float receiptAmount = Float.parseFloat(amount.replaceAll("[^0-9]", ""));

        if(lastAmount != '\uFFE6'){
            float rate = spm.retrieveFloat(CurrencyTag.CHOOSE_CURRENCY_TAG);
            receiptAmount= (float) (Math.round(receiptAmount / rate * 1000) / 1000.0) ;
        }
        float typeMoney= 0;
        switch (type){
            case 0://음식
                typeMoney = spm.retrieveFloat(TravelDetailTag.FOOD_MONEY_TAG);
                break;
            case 1:
                typeMoney = spm.retrieveFloat(TravelDetailTag.TRAFFIC_MONEY_TAG);
                break;
            case 2:
                typeMoney = spm.retrieveFloat(TravelDetailTag.SHOPPING_MONEY_TAG);
                break;
            case 3:
                typeMoney = spm.retrieveFloat(TravelDetailTag.GIFT_MONEY_TAG);
                break;
            case 4:
                typeMoney = spm.retrieveFloat(TravelDetailTag.CULTURE_MONEY_TAG);
                break;
            case 5:
                typeMoney = spm.retrieveFloat(TravelDetailTag.ETC_MONEY_TAG);
                break;
        }

        amountUpdates.put("restKorea", new Float(restMoney+receiptAmount));
        typeUpdates.put(String.valueOf(type+1),new Float(typeMoney - receiptAmount));
        amountRef.child("Total").updateChildren(amountUpdates);
        amountRef.child("Money").updateChildren(typeUpdates);



        //delete
        final DatabaseReference receiptRef = mDatabase.getReference("BookInfo/"+bookCode+"/Content/Receipt/"+key);
        receiptRef.removeValue();

        updateDialog.dismiss();

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



package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Model.PurchaseAmount;
import kr.hs.emirim.uuuuri.haegbook.R;

/**
 * Created by doori on 2017-11-19.
 */

public class WholePurchaseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
public static final int HEADER = 0;
public static final int CHILD = 1;

private List<Item> data;
private Activity mNowActivity;

public WholePurchaseListAdapter(Activity mNowActivity, List<Item> data) {
        this.mNowActivity = mNowActivity;
        this.data = data;
        }


@Override
public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (type) {
        case HEADER:
        view = inflater.inflate(R.layout.item_purchase_list_header, parent, false);
        ListHeaderViewHolder header = new ListHeaderViewHolder(view);
        return header;
        case CHILD:
        view = inflater.inflate(R.layout.item_purchase_list_child, parent, false);
        ListChildViewHolder child = new ListChildViewHolder(view);
        return child;
        }

        return null;
        }

public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
final Item item = data.get(position);
        switch (item.type) {
        case HEADER:
final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
        itemController.refferalItem = item;
        itemController.header_title.setText(item.text);
        if (item.invisibleChildren == null) {
        itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
        } else {
        itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
        }
        itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        if (item.invisibleChildren == null) {
        item.invisibleChildren = new ArrayList<Item>();
        int count = 0;
        int pos = data.indexOf(itemController.refferalItem);
        while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
        item.invisibleChildren.add(data.remove(pos + 1));
        count++;
        }
        notifyItemRangeRemoved(pos + 1, count);
        itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
        } else {
        int pos = data.indexOf(itemController.refferalItem);
        int index = pos + 1;
        for (Item i : item.invisibleChildren) {
        data.add(index, i);
        index++;
        }
        notifyItemRangeInserted(pos + 1, index - pos - 1);
        itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
        item.invisibleChildren = null;
        }
        }
        });
        break;
        case CHILD:
            ListChildViewHolder childViewHolder = (ListChildViewHolder)holder;
            childViewHolder.typeTv.setText(data.get(position).purchase.getTypeName());
            childViewHolder.foreignAmountTv.setText(String.valueOf(data.get(position).purchase.getForeignAmount()));
            childViewHolder.koreaAmountTv.setText(String.valueOf(data.get(position).purchase.getKoreaAmount()));
        break;
        }
        }

@Override
public int getItemViewType(int position) {
        return data.get(position).type;
        }

@Override
public int getItemCount() {
        return data.size();
        }

private class ListHeaderViewHolder extends RecyclerView.ViewHolder {
    TextView header_title;
    ImageView btn_expand_toggle;
    Item refferalItem;

    public ListHeaderViewHolder(View itemView) {
        super(itemView);
        header_title = (TextView) itemView.findViewById(R.id.header_title);
        btn_expand_toggle = (ImageView) itemView.findViewById(R.id.btn_expand_toggle);
    }
}

private class ListChildViewHolder extends RecyclerView.ViewHolder{
    LinearLayout root;
    TextView typeTv;
    TextView foreignAmountTv;
    TextView koreaAmountTv;

    public ListChildViewHolder(View itemView) {
        super(itemView);
        root = itemView.findViewById(R.id.root);
        typeTv = itemView.findViewById(R.id.type_tv);
        foreignAmountTv= itemView.findViewById(R.id.foreign_amount_tv);
        koreaAmountTv = itemView.findViewById(R.id.korea_amount_tv);
    }
}
public static class Item {
    public int type;
    public String text;
    public PurchaseAmount purchase;

    public List<Item> invisibleChildren;

    public Item() {
    }

    public Item(int type, PurchaseAmount purchase) {
        this.type = type;
        this.purchase = purchase;
    }

    public Item(int type, String text){
        this.type = type;
        this.text = text;
    }
}
}

package kr.hs.emirim.uuuuri.haegbook.Adapter;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Interface.NotificationTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Notification.AlarmReceiver;
import kr.hs.emirim.uuuuri.haegbook.R;
import kr.hs.emirim.uuuuri.haegbook.Widget.FloatingViewService;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String FILE_PATH_EXTRA = "FILE PATH EXTRA";


    String TAG="ExpandableListAdapter";
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private Context mContext;

    private Intent mServiceIntent;


    private List<Item> data;

    Calendar cal;

    PermissionListener permissionlistener;

    private SharedPreferenceManager spm;


    public ExpandableListAdapter(List<Item> data) {


        this.data = data;
        cal = Calendar.getInstance();

        Log.e(TAG, cal.get(Calendar.YEAR)+"");
        Log.e(TAG, cal.get(Calendar.MONTH)+1+"");
        Log.e(TAG, cal.get(Calendar.DATE)+"");
        Log.e(TAG, cal.get(Calendar.HOUR_OF_DAY)+"");
        Log.e(TAG, cal.get(Calendar.MINUTE)+"");

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        mContext = parent.getContext();

        spm = new SharedPreferenceManager((Activity) mContext);


        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                mServiceIntent = new Intent(mContext, FloatingViewService.class);
                String path = spm.retrieveString(SharedPreferenceTag.DEFAULT_DIRECTORY);
                Log.e(TAG, path);
                mServiceIntent.putExtra(FILE_PATH_EXTRA, path);
                mContext.startService(mServiceIntent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(mContext, "권한이 없을 경우, 플로팅 위젯 기능을 사용 할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }


        };



        float dp = mContext.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);
        switch (type) {
            case HEADER:
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.list_setting_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                TextView itemTextView = new TextView(mContext);
                itemTextView.setPadding(subItemPaddingLeft, subItemPaddingTopAndBottom, 0, subItemPaddingTopAndBottom);
                itemTextView.setTextColor(0x88000000);
                itemTextView.setLayoutParams(
                        new ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));


                return new RecyclerView.ViewHolder(itemTextView) {
                };
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
                  //  itemController.floatingWidgetSwitch.setImageResource(R.mipmap.ic_launcher);
                } else {
                //    itemController.floatingWidgetSwitch.setImageResource(R.mipmap.ic_launcher);
                }
//


                itemController.setting_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                        Log.e("헤더",item.text);
                        Log.e("쳌", String.valueOf(isChecked));
                        switch (item.text) {
                            case "D-Day":
                                break;
                            case "Notification":
                                changeNotification(item,itemController,isChecked);
                                break;
                            case "카메라 플로팅 위젯":
                                changeCameraWidget(item,itemController,isChecked);
                                break;
                        }

                    }
                });
                break;
            case CHILD:
                SharedPreferenceManager spm=new SharedPreferenceManager((Activity) mContext);
                final TextView itemTextView = (TextView) holder.itemView;
                if(item.text.contains("시작")) {
                    itemTextView.setText(data.get(position).text + "        " + spm.retrieveString(NotificationTag.START_TIME_TAG));
                }else{
                    itemTextView.setText(data.get(position).text + "        " + spm.retrieveString(NotificationTag.FINISH_TIME_TAG));
                }
                itemTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog dialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int min) {


                                SharedPreferenceManager spm = new SharedPreferenceManager((Activity) mContext);
                                if(item.text.contains("시작")){
                                    spm.save(NotificationTag.START_TIME_TAG, hour+":"+min);
                                    Log.e("시작 시간",spm.retrieveString(NotificationTag.START_TIME_TAG));
                                    itemTextView.setText(data.get(position).text + "        " + spm.retrieveString(NotificationTag.START_TIME_TAG));

                                }else{
                                    spm.save(NotificationTag.FINISH_TIME_TAG, hour+":"+min);
                                    Log.e("종료 시간",spm.retrieveString(NotificationTag.FINISH_TIME_TAG));
                                    itemTextView.setText(data.get(position).text + "        " + spm.retrieveString(NotificationTag.FINISH_TIME_TAG));

                                    new AlarmReceiver(mContext).Alarm();

                                }
                            }
                        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

                        dialog.show();

                    }
                });
                break;
        }
    }

    public void changeCameraWidget(Item item,ListHeaderViewHolder itemController,boolean isChecked){
        if(isChecked) {
                    if(spm.retrieveBoolean(SharedPreferenceTag.IS_TRAVELING_TAG)) {

                        TedPermission.with(mContext)
                                .setPermissionListener(permissionlistener)
                                .setDeniedMessage("권한이 없을 경우, 플로팅 위젯 기능을 사용 할 수 없습니다.\n\nPlease turn on permissions at [Setting] > [Permission]")
                                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.SYSTEM_ALERT_WINDOW)
                                .check();
                    }else{
                        Toast.makeText(mContext, "여행중에만 이용가능한 서비스 입니다.", Toast.LENGTH_SHORT).show();
                        itemController.setting_switch.setChecked(false);
                    }
                }else{
                    if(mServiceIntent!=null) {
                        mContext.stopService(mServiceIntent);
                        mServiceIntent = null;
                    }
                }



    }
    private void changeNotification(Item item,ListHeaderViewHolder itemController,boolean isChecked){
        if(isChecked){
            if (item.invisibleChildren !=null){
                int pos = data.indexOf(itemController.refferalItem);
                int index = pos + 1;
                for (Item i : item.invisibleChildren) {
                    data.add(index, i);
                    index++;
                }
                notifyItemRangeInserted(pos + 1, index - pos - 1);
                //     itemController.floatingWidgetSwitch.setImageResource(R.mipmap.ic_launcher);
                item.invisibleChildren = null;
            }

        }else{
            item.invisibleChildren = new ArrayList<Item>();
            int count = 0;
            int pos = data.indexOf(itemController.refferalItem);
            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                item.invisibleChildren.add(data.remove(pos + 1));
                count++;
            }
            notifyItemRangeRemoved(pos + 1, count);
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

    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView header_title;
        public Switch setting_switch;
        public Item refferalItem;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_title = (TextView) itemView.findViewById(R.id.header_title);
            setting_switch = itemView.findViewById(R.id.child_switch);
        }
    }



    public static class Item {
        public int type;
        public String text;
        public List<Item> invisibleChildren;

        public Item() {
        }

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }
    }



}
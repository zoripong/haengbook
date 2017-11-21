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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.hs.emirim.uuuuri.haegbook.Interface.NotificationTag;
import kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag;
import kr.hs.emirim.uuuuri.haegbook.Layout.ScalableLayout;
import kr.hs.emirim.uuuuri.haegbook.Manager.SharedPreferenceManager;
import kr.hs.emirim.uuuuri.haegbook.Notification.NotificationAdapter;
import kr.hs.emirim.uuuuri.haegbook.R;
import kr.hs.emirim.uuuuri.haegbook.Widget.FloatingViewService;

import static kr.hs.emirim.uuuuri.haegbook.Interface.SharedPreferenceTag.STATE_SETTING_CAMERA;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String FILE_PATH_EXTRA = "FILE PATH EXTRA";


    String TAG="ExpandableListAdapter";
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    private Context mContext;

    private Intent mServiceIntent;

    private boolean isFirst = true;
    private List<Item> data;

    Calendar cal;

    PermissionListener permissionlistener;

    private SharedPreferenceManager spm;

    private boolean isNotification=true;

    private NotificationAdapter notificationAdapter;
    SimpleDateFormat dateTimeFormat;
    private String nowDate;

    private String setStartTime;
    private String setFinishTime;



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
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mContext = parent.getContext();

        notificationAdapter = new NotificationAdapter((Activity) mContext, mContext);
        dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        nowDate = cal.get(Calendar.YEAR)+"-"+ (cal.get(Calendar.MONTH))+"-"+cal.get(Calendar.DAY_OF_MONTH);

        spm = new SharedPreferenceManager((Activity) mContext);
        setStartTime = spm.retrieveString(NotificationTag.START_TIME_TAG);
        setFinishTime = spm.retrieveString(NotificationTag.FINISH_TIME_TAG);


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


        switch (type) {
            case HEADER:
                view = inflater.inflate(R.layout.item_setting_list_header, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);

                return header;
            case CHILD:
                view = inflater.inflate(R.layout.item_setting_list_child, parent, false);
                ListChildViewHolder child = new ListChildViewHolder(view);
                return child ;
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
                if(item.text.contains("Notification")){
                    //todo notification set
                    isNotification = spm.retrieveBoolean(NotificationTag.NOTIFICATION_SET_TAG);
                    itemController.setting_switch.setChecked(isNotification);


                }

                if(item.text.equals("카메라 플로팅 위젯")){
                    itemController.setting_switch.setChecked(spm.retrieveBoolean(SharedPreferenceTag.STATE_SETTING_CAMERA));
                    changeCameraWidget(item,itemController, spm.retrieveBoolean(SharedPreferenceTag.STATE_SETTING_CAMERA));
                }

                itemController.setting_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                        Log.e("헤더",item.text);
                        Log.e("쳌", String.valueOf(isChecked));
                        switch (item.text) {
                            case "D-Day":
                                break;
                            case "Notification":
                                if(isFirst && !isNotification) {
                                    changeNotification(item, itemController, false);
                                }
                                isFirst=false;

                                spm.save(NotificationTag.NOTIFICATION_SET_TAG,isChecked);
                                isNotification = spm.retrieveBoolean(NotificationTag.NOTIFICATION_SET_TAG);
                                changeNotification(item,itemController,isChecked);
                                break;
                            case "카메라 플로팅 위젯":
                                changeCameraWidget(item,itemController,isChecked);
                                spm.save(STATE_SETTING_CAMERA, isChecked);
                                break;
                        }

                    }
                });
                break;
            case CHILD:
                final ListChildViewHolder childViewHolder = (ListChildViewHolder) holder;
                if(!isNotification) {
                    childViewHolder.root.setVisibility(View.GONE);
                    LinearLayout childLayout = (LinearLayout) childViewHolder.root;
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ScalableLayout.LayoutParams.MATCH_PARENT, childLayout.getHeight());
                    params.height = 0;
                    childLayout.setLayoutParams(params);
                }
                else{
                        childViewHolder.root.setVisibility(View.VISIBLE);
                        LinearLayout childLayout = (LinearLayout) childViewHolder.root;
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                ScalableLayout.LayoutParams.MATCH_PARENT, ScalableLayout.LayoutParams.WRAP_CONTENT);
                        childLayout.setLayoutParams(params);

                }

                Log.e("이름", item.text);
                childViewHolder.dateSetTv.setText(item.text);
                if (item.text.contains("하루 시작")) {
                    Log.e("처음", "시작");
                    childViewHolder.dateTv.setText(spm.retrieveString(NotificationTag.START_TIME_TAG));
                } else {
                    Log.e("처음", "종료");
                    childViewHolder.dateTv.setText(spm.retrieveString(NotificationTag.FINISH_TIME_TAG));
                }
                childViewHolder.dateSetTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimeDialog(item,childViewHolder);

                    }
                });

                childViewHolder.root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showTimeDialog(item, childViewHolder);

                    }
                });

        }
    }

    private void showTimeDialog(final Item item, final ListChildViewHolder childViewHolder){
        TimePickerDialog dialog = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                Log.e("이름", item.text);

                if (item.text.contains("시작")) {
                    String startTime = hour + ":" + min;
                    if(checkTime(startTime,setFinishTime)) {
                        Toast.makeText(mContext, "시작 시간은 끝 시간보다 더 빠르게 설정해주세요.", Toast.LENGTH_SHORT).show();
                    }else {
                        spm.save(NotificationTag.START_TIME_TAG, hour + ":" + min);
                        setStartTime = spm.retrieveString(NotificationTag.START_TIME_TAG);

                        Log.e("시간", spm.retrieveString(NotificationTag.START_TIME_TAG));
                        childViewHolder.dateTv.setText(spm.retrieveString(NotificationTag.START_TIME_TAG));

                        notificationAdapter.setNotification(0, "제목", "시작 알림 메시지", hour, min);
                    }
                } else {
                    String finishTime = hour + ":" + min;
                    if(checkTime(setStartTime,finishTime)) {
                        Toast.makeText(mContext, "끝 시간은 시작 시간보다 더 느리게 설정해주세요.", Toast.LENGTH_SHORT).show();
                    }else {
                        spm.save(NotificationTag.FINISH_TIME_TAG, hour + ":" + min);
                        setFinishTime = spm.retrieveString(NotificationTag.FINISH_TIME_TAG);

                        Log.e("시간", spm.retrieveString(NotificationTag.FINISH_TIME_TAG));
                        childViewHolder.dateTv.setText(spm.retrieveString(NotificationTag.FINISH_TIME_TAG));
                        notificationAdapter.setNotification(1, "제목", "종료 알림 메시지", hour, min);

                    }
                }


                //new AlarmReceiver(mContext).Alarm();

            }
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

        dialog.show();

    }

    public boolean checkTime(String time,String compareTime) {

        try{
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            Date Date = format.parse(time);
            Date compareDate = format.parse(compareTime);

            long calDate = Date.getTime() - compareDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / ( 24*60*60*1000);
            if(calDateDays < 0) return false;//날짜 적당

        } catch (ParseException e) {
            Log.e("날짜 파싱에러","서로 타입이 맞지 않음.");
        }
        return true;//시작날짜가 끝나는 날짜보다 뒤에 있음

    }

    public void changeCameraWidget(Item item,ListHeaderViewHolder itemController,boolean isChecked) {
        if (isChecked) {
            if (spm.retrieveBoolean(SharedPreferenceTag.IS_TRAVELING_TAG)) {

                TedPermission.with(mContext)
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("권한이 없을 경우, 플로팅 위젯 기능을 사용 할 수 없습니다.\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.SYSTEM_ALERT_WINDOW)
                        .check();
            } else {
                Toast.makeText(mContext, "여행중에만 이용가능한 서비스 입니다.", Toast.LENGTH_SHORT).show();
                itemController.setting_switch.setChecked(false);
            }
        } else {
            if (mServiceIntent != null) {
                mContext.stopService(mServiceIntent);
                mServiceIntent = null;
            }
        }
    }

    private void changeNotification(Item item,ListHeaderViewHolder itemController,boolean isChecked){
        if(isChecked){
            //TODO 알람 SET
            String[] startTime = spm.retrieveString(NotificationTag.START_TIME_TAG).split(":");
            String[] finishTime = spm.retrieveString(NotificationTag.FINISH_TIME_TAG).split(":");

            notificationAdapter.setNotification(0,"제목","시작 알림 메시지",Integer.parseInt(startTime[0]),Integer.parseInt(startTime[1]));
            notificationAdapter.setNotification(1,"제목","종료 알림 메시지",Integer.parseInt(finishTime[0]),Integer.parseInt(finishTime[1]));

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
            //TODO 알람 취소
            notificationAdapter.cancelNotification(0);
            notificationAdapter.cancelNotification(1);

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
    private class ListChildViewHolder extends RecyclerView.ViewHolder{
        LinearLayout root;
        TextView dateSetTv;
        TextView dateTv;

        public ListChildViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            dateSetTv = itemView.findViewById(R.id.date_set_tv);
            dateTv = itemView.findViewById(R.id.date_tv);
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
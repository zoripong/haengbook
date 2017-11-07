package kr.hs.emirim.uuuuri.haegbook.Layout;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 유리 on 2017-11-04.
 */
public class CustomViewPager extends ViewPager {
    private boolean mIsEnabled;

    public CustomViewPager(Context context, AttributeSet attrs){
        super(context, attrs);
        mIsEnabled = true;
    }

    public CustomViewPager(Context context){
        super(context);
        mIsEnabled = true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(mIsEnabled)
            return super.onInterceptTouchEvent(ev);
        else
            return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(mIsEnabled)
            return super.onTouchEvent(ev);
        else
            return false;
    }

    public void setPagingEnabled(boolean enabled){
        this.mIsEnabled = enabled;
    }
}

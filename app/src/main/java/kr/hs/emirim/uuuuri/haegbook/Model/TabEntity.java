package kr.hs.emirim.uuuuri.haegbook.Model;

import com.flyco.tablayout.listener.CustomTabEntity;

// CustomTabEntity를 상속받아야함
public class TabEntity implements CustomTabEntity {
    public String title;

    public TabEntity(String title) {
        this.title = title;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return 0;
    }

    @Override
    public int getTabUnselectedIcon() {
        return 0;
    }

}

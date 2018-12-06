package launcher.astanite.com.astanite.data;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.util.Objects;


public class AppInfo implements Comparable<AppInfo> {

    public String label;
    public Drawable icon;
    public Intent launchIntent;
    public String packageName;
    public boolean isChecked = false;

    @Override
    public int compareTo(AppInfo appInfo) {
        return this.label.compareTo(appInfo.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppInfo)) return false;
        AppInfo appInfo = (AppInfo) o;
        return isChecked == appInfo.isChecked &&
                Objects.equals(label, appInfo.label) &&
                Objects.equals(icon, appInfo.icon) &&
                Objects.equals(launchIntent, appInfo.launchIntent) &&
                Objects.equals(packageName, appInfo.packageName);
    }

}

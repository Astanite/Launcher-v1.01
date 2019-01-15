package launcher.astanite.com.astanite.data;

import android.app.Application;

public class MyApplication extends Application {
    private int settingsmode;

    public int getSettingsmode() {
        return settingsmode;
    }

    public void setSettingsmode(int settingsmode) {
        this.settingsmode = settingsmode;
    }
}

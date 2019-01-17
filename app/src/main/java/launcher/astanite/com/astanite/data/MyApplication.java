package launcher.astanite.com.astanite.data;

import android.app.Application;

public class MyApplication extends Application {
    private int settingsmode;
    private boolean  penaltymode = false;

    public int getSettingsmode() {
        return settingsmode;
    }

    public void setSettingsmode(int settingsmode) {
        this.settingsmode = settingsmode;
    }

    public boolean getPenaltymode() { return penaltymode; };

    public void setPenaltymode(boolean penaltymode) {
        this.penaltymode = penaltymode;
    }
}

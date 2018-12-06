package launcher.astanite.com.astanite.utils;

public class Constants {
    public static final String SHARED_PREFERENCES_NAME = "astanite-shared-preferences";

    public static final String KEY_CURRENT_MODE = "current-mode";
    public static final String KEY_FOCUS_APPS = "focus-apps";
    public static final String KEY_WORK_APPS = "work-apps";
    public static final String KEY_SLEEP_APPS = "sleep-apps";
    public static final String KEY_MY_MODE_APPS = "my-mode-apps";
    public static final String KEY_INTENTION = "intention";
    public static final String KEY_PENALTY = "penalty";
    public static final String KEY_MODE_TIME = "mode-time";
    public static final String KEY_MODE_TIME_WHEN_MODE_ENTERED = "time-when-mode-entered";
    public static final String KEY_CURRENT_MODE_NOTIFS = "mode-notifs";

    public static final int MODE_NONE = 0;
    public static final int MODE_FOCUS = 1;
    public static final int MODE_WORK = 2;
    public static final int MODE_SLEEP = 3;
    public static final int MY_MODE = 4;

    public static final int MODE_KEEP_NOTIFS = 0;
    public static final int MODE_DISMISS_NOTIFS = 1;

    public static final int FRAGMENT_MODES = 5;
    public static final int FRAGMENT_SETTINGS = 6;
    public static final int FRAGMENT_FLAGGED_APPS = 7;
    public static final int FRAGMENT_FLAGGED_CONTACTS = 8;

    public static final String ACTION_NOTIFICATION_SERVICE = "notif-service";
}

package launcher.astanite.com.astanite.ui;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

import kotlin.collections.IntIterator;
import launcher.astanite.com.astanite.utils.Constants;

public class BlockingAppService extends Service {

    private Handler handler = new Handler();
    private Runnable myrunnable;
    private SharedPreferences sp;
    //private List<String> focusModeApps;
    //private List<String> sleepModeApps;
    //private List<String> leisureModeApps;
    private List<String> setCurrentMode;
    int flag = 0;
    String prevapp = "Null";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("Service Created", "onCreate: ");
        Log.e("FlagValue", Integer.toString(flag));
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sp = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        int curr_mode = sp.getInt(Constants.KEY_CURRENT_MODE, Constants.MODE_NONE);
        setActiveModeButton(curr_mode);
        setCurrentMode.add("launcher.astanite.com.astanite");
        if (sp.getInt(Constants.KEY_CURRENT_MODE, Constants.MODE_NONE) != 0) {
            handler.postDelayed(myrunnable = new Runnable() {
                @Override
                public void run() {
                    printForegroundTask();
                    Log.e("screen_on", "loop");
                    handler.postDelayed(this, 800);
                }
            }, 800);
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        handler.removeCallbacks(myrunnable);
        super.onDestroy();
    }

    private void printForegroundTask() {
        String currentApp = "NULL";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Log.e("TAG", "yess: ");
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e("TAG", "Current App in foreground is: " + currentApp);

        if(currentApp.equals(prevapp))
        {
            flag++;
        }
        else {
            flag = 0;
        }

        prevapp = currentApp;

        if(flag <5)
        {
            Log.e("Check Triggered", "printForegroundTask: " );
            if (!setCurrentMode.contains(currentApp)) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("launcher.astanite.com.astanite");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }

            }
        }
    }

    public void setActiveModeButton ( int newMode){
        switch (newMode) {
            case Constants.MODE_FOCUS:
                setCurrentMode = new ArrayList<>(sp.getStringSet(Constants.KEY_FOCUS_APPS, new HashSet<>()));
                break;
            case Constants.MODE_SLEEP:
                setCurrentMode = new ArrayList<>(sp.getStringSet(Constants.KEY_SLEEP_APPS, new HashSet<>()));
                break;
            case Constants.MY_MODE:
                setCurrentMode = new ArrayList<>(sp.getStringSet(Constants.KEY_MY_MODE_APPS, new HashSet<>()));
                break;
            default:
                setCurrentMode = new ArrayList<>(sp.getStringSet(Constants.KEY_FOCUS_APPS, new HashSet<>()));
                break;
        }
    }

}

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

import launcher.astanite.com.astanite.utils.Constants;

public class BlockingAppService extends Service {

    private Handler handler = new Handler();
    private Runnable myrunnable;
    private SharedPreferences sp;
    private List<String> focusModeApps;
    private List<String> sleepModeApps;
    private List<String> leisureModeApps;
    private List<String> setCurrentMode;
    private ScreenOnOffReceiver mScreenReceiver;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("Service Created", "onCreate: ");
        registerScreenStatusReceiver();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sp = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        if (sp.getInt(Constants.KEY_CURRENT_MODE, Constants.MODE_NONE) != 0) {
            handler.postDelayed(myrunnable = new Runnable() {
                @Override
                public void run() {
                    printForegroundTask();
                    Log.e("screen_on", "loop");
                    handler.postDelayed(this, 2000);
                }
            }, 2000);
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        unregisterScreenStatusReceiver();
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
        focusModeApps = new ArrayList<>(sp.getStringSet(Constants.KEY_FOCUS_APPS, new HashSet<>()));
        sleepModeApps = new ArrayList<>(sp.getStringSet(Constants.KEY_SLEEP_APPS, new HashSet<>()));
        leisureModeApps = new ArrayList<>(sp.getStringSet(Constants.KEY_MY_MODE_APPS, new HashSet<>()));

        int curr_mode = sp.getInt(Constants.KEY_CURRENT_MODE, Constants.MODE_NONE);
        setActiveModeButton(curr_mode);
        setCurrentMode.add("launcher.astanite.com.astanite");

        int check_count = 0;
        for (String focus : setCurrentMode) {
            //if package name matches, no problem
            //else execute below code
            if (!currentApp.equals(focus)) {
                //increase the counter
                Log.i("app_not_allowed", focus);
                check_count++;
            } else
                break;                                         //current app matched focus Mode app
            if (check_count == setCurrentMode.size()) {
                Log.i("Block Current app ", currentApp); //blocking the current app by showing an activity
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("launcher.astanite.com.astanite");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                }
            }
        }
    }

    public void setActiveModeButton(int newMode) {
        switch (newMode) {
            case Constants.MODE_FOCUS:
                setCurrentMode = focusModeApps;
                break;
            case Constants.MODE_SLEEP:
                setCurrentMode = sleepModeApps;
                break;
            case Constants.MY_MODE:
                setCurrentMode = leisureModeApps;
                break;
            default:
                setCurrentMode = focusModeApps;
                break;
        }
    }

    private void registerScreenStatusReceiver() {
        mScreenReceiver = new ScreenOnOffReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(mScreenReceiver, filter);
    }

    private void unregisterScreenStatusReceiver() {
        try {
            if (mScreenReceiver != null) {
                unregisterReceiver(mScreenReceiver);
            }
        } catch (IllegalArgumentException ignored) {
        }
    }

    public class ScreenOnOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_OFF)) {
                Log.d("StackOverflow", "Screen Off");
                getBaseContext().stopService(new Intent(getBaseContext(), BlockingAppService.class));
            } else if (Objects.equals(intent.getAction(), Intent.ACTION_SCREEN_ON)) {
                Log.d("StackOverflow", "Screen On");
                getBaseContext().startService(new Intent(getBaseContext(), BlockingAppService.class));
            }
        }
    }
}

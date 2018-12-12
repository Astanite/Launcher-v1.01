package launcher.astanite.com.astanite.ui ;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.viewmodel.MainViewModel;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BlockingAppService extends Service {

    private Handler handler = new Handler();
    private Runnable myrunnable;
    private SharedPreferences sp ;
    private List<String> focusModeApps ;
    private List<String> sleepModeApps ;
    private List<String> leisureModeApps ;
    private List<String> setCurrentMode ;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("Service Created", "onCreate: " );
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.postDelayed(myrunnable = new Runnable() {
            @Override
            public void run() {
                printForegroundTask();
                handler.postDelayed(this, 1000);
            }
        }, 1000);
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
        sp = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
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
                setCurrentMode = focusModeApps ;
                break;
            case Constants.MODE_SLEEP:
                setCurrentMode = sleepModeApps;
                break;
            case Constants.MY_MODE:
                setCurrentMode = leisureModeApps ;
                break;
            default:
                setCurrentMode = focusModeApps ;
                break;
        }
    }

}

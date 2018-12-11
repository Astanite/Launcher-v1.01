package launcher.astanite.com.astanite.ui;

import android.app.Application;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import launcher.astanite.com.astanite.utils.Constants;

import static android.content.ContentValues.TAG;


public class BlockedAppChecker extends Application {
    private Handler handler = new Handler();
    private SharedPreferences sp;
    private long delta;
    private List<String> focusModeApps;

    @Override
    public void onCreate() {
        super.onCreate();

        sp = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        focusModeApps = new ArrayList<>(sp.getStringSet(Constants.KEY_FOCUS_APPS, new HashSet<>()));
        focusModeApps.add("launcher.astanite.com.astanite");
        focusModeApps.add("com.miui.securitycenter");
        focusModeApps.add("com.miui.home");
        focusModeApps.add("com.android.systemui");
        // retrieve the package name in every 10 seconds
        // compare it with the allowed apps in that mode(will get using key)
        // if allowed then ok
        // else Give a Toast that "This app is not allowed"

        sp = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        boolean isBlocked = sp.getBoolean("FocusApps", false);
        Log.d("isBlockedCheck", String.valueOf(isBlocked));
        setTimmings(); // will set the time of all 3 long parameters
        // checks whether has set the allowed apps and currently the user is in any mode or not
        if (isBlocked && delta < sp.getLong(Constants.KEY_MODE_TIME, 0L) && delta != 0) {

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    setTimmings();
                    printForegroundTask();
                    handler.postDelayed(this, 2000);
                }
            };
            //Start
            if (delta > sp.getLong(Constants.KEY_MODE_TIME, 0)) {
                handler.removeCallbacks((Runnable) this);
                handler.removeCallbacksAndMessages(runnable);         // stop checking
                isBlocked = false;
                sp
                        .edit()
                        .putBoolean("FocusApps", false)
                        .apply();
            }
            else
                handler.postDelayed(runnable, 2000);     // start checking again
        }
    }

    private void setTimmings() {
        long currentTime = System.currentTimeMillis();
        long enteredTime = sp.getLong(Constants.KEY_MODE_TIME_WHEN_MODE_ENTERED, 0L);
        delta = currentTime - enteredTime;
    }

    private void printForegroundTask() {
        String currentApp = "NULL";
        UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();
        List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
        if (appList != null && appList.size() > 0) {
            SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
            for (UsageStats usageStats : appList) {
                mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
            }
            if (!mySortedMap.isEmpty()) {
                currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
            }
        }

        Log.e(TAG, "Check_# " + currentApp);
        //run a loop and check if the app opened matches anything from allowed apps
        // if matches do nothing
        // if app opened does'nt matches Fire an intent to close it


        Log.v("current_app_info", currentApp);
        Log.v("allowed_app_info", String.valueOf(focusModeApps));

        int check_count = 0;
        for (String focus : focusModeApps) {
            //if package name matches, no problem
            //else execute below code
            if (!currentApp.equals(focus)) {
                //increase the counter
                Log.i("app_not_allowed", focus);
                check_count++;
            } else
                break;//current app matched focus Mode app
            if (check_count == focusModeApps.size()) {
                Log.i("Block Current app ", currentApp); //blocking the current app by showing an activity
                Intent dialogIntent = new Intent(getBaseContext(), PopupToBlockActivity.class);
                // If invoking from outside app activity, needs to include
                // New task flag.
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(dialogIntent);
            }
        }

    }
}

package launcher.astanite.com.astanite.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import launcher.astanite.com.astanite.data.AppInfo;
import launcher.astanite.com.astanite.utils.Block_All_Notification;
import launcher.astanite.com.astanite.utils.Constants;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private CompositeDisposable compositeDisposable;
    private SharedPreferences sharedPreferences;
    private MutableLiveData<List<AppInfo>> allApps;
    private MutableLiveData<Integer> currentMode;
    private MutableLiveData<String> currentIntention;

    public List<String> focusModeApps;
    public List<String> workModeApps;
    public List<String> sleepModeApps;
    public List<String> myModeApps;
    public MutableLiveData<List<AppInfo>> currentModeApps = new MutableLiveData<>();
    public MutableLiveData<Boolean> isAppDrawerOpen = new MutableLiveData<>();
    public MutableLiveData<Integer> penaltyScreenTriggeredForMode = new MutableLiveData<>();
    public boolean isPenaltyScreenOpen = false;

    public MainViewModel(Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        allApps = new MutableLiveData<>();
        currentIntention = new MutableLiveData<>();
        currentMode = new MutableLiveData<>();
        penaltyScreenTriggeredForMode.setValue(Constants.MODE_NONE);
        sharedPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        currentMode.setValue(sharedPreferences.getInt(Constants.KEY_CURRENT_MODE, Constants.MODE_NONE));
        currentIntention.setValue(sharedPreferences.getString(Constants.KEY_INTENTION, ""));

        focusModeApps = new ArrayList<>();
        workModeApps = new ArrayList<>();
        sleepModeApps = new ArrayList<>();
        myModeApps = new ArrayList<>();

        isAppDrawerOpen.setValue(false);
        compositeDisposable.add(Completable
                .fromAction(() -> {
                    focusModeApps.addAll(sharedPreferences.getStringSet(Constants.KEY_FOCUS_APPS, new HashSet<>()));
                    workModeApps.addAll(sharedPreferences.getStringSet(Constants.KEY_WORK_APPS, new HashSet<>()));
                    sleepModeApps.addAll(sharedPreferences.getStringSet(Constants.KEY_SLEEP_APPS, new HashSet<>()));
                    myModeApps.addAll(sharedPreferences.getStringSet(Constants.KEY_MY_MODE_APPS, new HashSet<>()));
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe());
    }

    public void getResolveInfoList(Context context) {
        Single<List<ResolveInfo>> resolveInfoSingle =
                Single.fromCallable(() -> {
                    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
                    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                    return context.getPackageManager().queryIntentActivities(mainIntent, 0);
                })
                        .subscribeOn(Schedulers.computation())
                        .subscribeOn(Schedulers.computation())
                        .doOnSuccess((newList) -> convertToAppInfo(newList));
        compositeDisposable.add(resolveInfoSingle.subscribe());
    }

    private void convertToAppInfo(List<ResolveInfo> resolveInfoList) {
        Single<List<AppInfo>> appInfoObservable = Observable.fromIterable(resolveInfoList)
                .filter(resolveInfo -> {
                    if (resolveInfo.activityInfo.packageName.equals("launcher.astanite.com.astanite"))
                        return false;
                    return true;
                })
                .map(resolveInfo -> {
                    AppInfo appInfo = new AppInfo();
                    PackageManager packageManager = getApplication().getPackageManager();
                    appInfo.label = resolveInfo.loadLabel(packageManager).toString();
                    appInfo.icon = resolveInfo.activityInfo.loadIcon(packageManager);
                    appInfo.packageName = resolveInfo.activityInfo.packageName;
                    appInfo.launchIntent = getApplication().getPackageManager().getLaunchIntentForPackage(resolveInfo.activityInfo.packageName);
                    return appInfo;
                })
                .sorted()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.computation())
                .toList()
                .doOnSuccess(appInfos -> {
                    allApps.postValue(appInfos);
                    currentModeApps.postValue(appInfos);
                });
        compositeDisposable.add(appInfoObservable.subscribe());
    }

    public void updateIntention(String intention) {
        sharedPreferences.edit()
                .putString(Constants.KEY_INTENTION, intention)
                .apply();
        currentIntention.postValue(intention);
    }

    public LiveData<String> getCurrentIntention() {
        return currentIntention;
    }

    public LiveData<Integer> getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int mode) {

        sharedPreferences
                .edit()
                .putInt(Constants.KEY_CURRENT_MODE, mode)
                .commit();

        Log.d(TAG, "Current mode updated to " + mode);
        String key;
        switch (mode) {
            case Constants.MODE_FOCUS:
                key = Constants.KEY_FOCUS_APPS;
                enableDND();
                break;
            case Constants.MODE_WORK:
                key = Constants.KEY_WORK_APPS;
                enableDND();
                break;
            case Constants.MODE_SLEEP:
                key = Constants.KEY_SLEEP_APPS;
                enableDND();
                break;
            case Constants.MY_MODE:
                key = Constants.KEY_MY_MODE_APPS;
                enableDND();
                break;
            default:
                disableDND();
                Log.d(TAG, "Exiting mode");
                key = null;
        }
        if (key == null) {
            currentModeApps.postValue(allApps.getValue());
        } else {
            Set<String> appSet = sharedPreferences.getStringSet(key, new HashSet<>());
            PackageManager packageManager = getApplication().getPackageManager();
            compositeDisposable.add(
                    Observable.fromIterable(appSet)
                            .map(packageName -> {
                                AppInfo appInfo = new AppInfo();
                                ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
                                appInfo.label = packageManager.getApplicationLabel(ai).toString();
                                appInfo.icon = packageManager.getApplicationIcon(ai);
                                appInfo.launchIntent = packageManager.getLaunchIntentForPackage(packageName);
                                appInfo.packageName = packageName;
                                return appInfo;
                            })
                            .toList()
                            .subscribeOn(Schedulers.computation())
                            .observeOn(Schedulers.computation())
                            .doOnSuccess(list -> currentModeApps.postValue(list))
                            .subscribe());
        }
        currentMode.postValue(mode);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    public void enableDND() {
        Log.d(TAG, "Enabling DND");
        Block_All_Notification.mode=1;
    }

    public void disableDND() {
        Log.d(TAG, "Disabling DND");
        Block_All_Notification.mode=0;
    }

    public void setPenalty(int penalty) {
        long pen = penalty * 60 * 1000; // Converting to milliseconds
        sharedPreferences
                .edit()
                .putLong(Constants.KEY_PENALTY, pen)
                .apply();
    }

    public void setModeTime(int hours, int minutes) {
        long time = (hours * 60 + minutes) * 60 * 1000; // Converting to milliseconds
        sharedPreferences
                .edit()
                .putLong(Constants.KEY_MODE_TIME, time)
                .putLong(Constants.KEY_MODE_TIME_WHEN_MODE_ENTERED, System.currentTimeMillis())
                .apply();
    }

    public long getTimeOfEnteringMode() {
        return sharedPreferences.getLong(Constants.KEY_MODE_TIME_WHEN_MODE_ENTERED, 0L);
    }

    public long getModeTime() {
        return sharedPreferences.getLong(Constants.KEY_MODE_TIME, 0L);
    }

    public long getPenalty() {
        return sharedPreferences.getLong(Constants.KEY_PENALTY, 0L);
    }
}

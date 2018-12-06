package launcher.astanite.com.astanite.ui.settings;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.HashSet;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import launcher.astanite.com.astanite.utils.Constants;
import launcher.astanite.com.astanite.data.AppInfo;

public class SettingsViewModel extends AndroidViewModel {

    private static final String TAG = SettingsViewModel.class.getSimpleName();

    private CompositeDisposable compositeDisposable;
    private MutableLiveData<List<AppInfo>> allApps;
    private SharedPreferences sharedPreferences;

    MutableLiveData<Integer> currentFragment;
    MutableLiveData<Integer> currentMode;

    public SettingsViewModel(Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
        sharedPreferences = application.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        allApps = new MutableLiveData<>();
        currentMode = new MutableLiveData<>();
        currentFragment = new MutableLiveData<>();
        currentFragment.setValue(0);
        getResolveInfoList(application);
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
                .doOnSuccess(appInfos -> allApps.postValue(appInfos));
        compositeDisposable.add(appInfoObservable.subscribe());
    }

    public LiveData<List<AppInfo>> getAllApps() {
        return allApps;
    }

    public void saveFlaggedApps(List<AppInfo> appsList) {
        Log.d(TAG, "Saving flagged apps");
        HashSet<String> appSet = new HashSet<>();
        for (AppInfo appinfo: appsList) {
            appSet.add(appinfo.packageName);
        }
        Log.d(TAG, "Calculated set size: " + appSet.size());
        String key;
        switch (currentMode.getValue()) {
            case Constants.MODE_FOCUS: key = Constants.KEY_FOCUS_APPS;
            break;
            case Constants.MODE_WORK: key = Constants.KEY_WORK_APPS;
            break;
            case Constants.MODE_SLEEP: key = Constants.KEY_SLEEP_APPS;
            break;
            case Constants.MY_MODE: key = Constants.KEY_MY_MODE_APPS;
            break;
            default: throw new IllegalArgumentException("Invalid mode given for saving apps");
        }
        Log.d(TAG, "Saving flagged apps for mode: " + key);
        sharedPreferences
                .edit()
                .putStringSet(key, appSet)
                .apply();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}

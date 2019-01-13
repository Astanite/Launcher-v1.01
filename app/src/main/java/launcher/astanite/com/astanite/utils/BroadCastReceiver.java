package launcher.astanite.com.astanite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.security.auth.login.LoginException;

import static android.content.Context.MODE_PRIVATE;

public class BroadCastReceiver extends BroadcastReceiver
{
    SendToHomeActivity mListener;
    ArrayList<String> dapp;
    HashSet<String> dnewapp;

    @SuppressWarnings("unused")
    public BroadCastReceiver()
    {
    }

    public BroadCastReceiver(SendToHomeActivity mListener)
    {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(mListener!=null)
            mListener.sendToHomeActivity();

        List<ResolveInfo> apps = getapps(context);

        updatedistractive(context, apps);
        updatefocus(context, apps);
        updateleisure(context, apps);
        updatesleep(context, apps);
        updatehome(context, apps);

    }
    public interface SendToHomeActivity
    {
        void sendToHomeActivity();
    }

    private List<ResolveInfo> getapps(Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        return pkgAppsList;
    }

    private void updatedistractive(Context context, List<ResolveInfo> apps)
    {


        dapp = new ArrayList<>();
        dnewapp = new HashSet<>();
        dapp.addAll(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_DISTRACTIVE_APPS, new HashSet<>()));

        for(int i =0; i<dapp.size(); i++)
        {
            boolean flag = false;
            for(int j =0; j<apps.size(); j++)
            {
                if(dapp.get(i).equals(apps.get(j).activityInfo.packageName))
                {
                    flag = true;
                }
            }

            if(flag)
            {
                dnewapp.add(dapp.get(i));
            }
        }

        Log.e("Old",Integer.toString(dapp.size()) );
        Log.e("New", Integer.toString(dnewapp.size()));
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putStringSet(Constants.KEY_DISTRACTIVE_APPS, dnewapp).apply();
    }

    private void updatefocus(Context context, List<ResolveInfo> apps)
    {


        ArrayList <String> dapp = new ArrayList<>();
        HashSet <String> dnewapp = new HashSet<>();
        dapp.addAll(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_FOCUS_APPS, new HashSet<>()));

        for(int i =0; i<dapp.size(); i++)
        {
            boolean flag = false;
            for(int j =0; j<apps.size(); j++)
            {
                if(dapp.get(i).equals(apps.get(j).activityInfo.packageName))
                {
                    flag = true;
                }
            }

            if(flag)
            {
                dnewapp.add(dapp.get(i));
            }
        }

        Log.e("Old",Integer.toString(dapp.size()) );
        Log.e("Apparentnew", Integer.toString(dnewapp.size()));
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putStringSet(Constants.KEY_FOCUS_APPS, dnewapp).apply();
        Log.e("New", Integer.toString(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_FOCUS_APPS, new HashSet<>()).size()));
    }

    private void updateleisure(Context context, List<ResolveInfo> apps)
    {


        ArrayList <String> dapp = new ArrayList<>();
        HashSet <String> dnewapp = new HashSet<>();
        dapp.addAll(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_MY_MODE_APPS, new HashSet<>()));

        for(int i =0; i<dapp.size(); i++)
        {
            boolean flag = false;
            for(int j =0; j<apps.size(); j++)
            {
                if(dapp.get(i).equals(apps.get(j).activityInfo.packageName))
                {
                    flag = true;
                }
            }

            if(flag)
            {
                dnewapp.add(dapp.get(i));
            }
        }

        Log.e("Old",Integer.toString(dapp.size()) );
        Log.e("Apparentnew", Integer.toString(dnewapp.size()));
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putStringSet(Constants.KEY_MY_MODE_APPS, dnewapp).apply();
        Log.e("New", Integer.toString(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_MY_MODE_APPS, new HashSet<>()).size()));
    }

    private void updatesleep(Context context, List<ResolveInfo> apps)
    {


        ArrayList <String> dapp = new ArrayList<>();
        HashSet <String> dnewapp = new HashSet<>();
        dapp.addAll(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_SLEEP_APPS, new HashSet<>()));

        for(int i =0; i<dapp.size(); i++)
        {
            boolean flag = false;
            for(int j =0; j<apps.size(); j++)
            {
                if(dapp.get(i).equals(apps.get(j).activityInfo.packageName))
                {
                    flag = true;
                }
            }

            if(flag)
            {
                dnewapp.add(dapp.get(i));
            }
        }

        Log.e("Old",Integer.toString(dapp.size()) );
        Log.e("Apparentnew", Integer.toString(dnewapp.size()));
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit().putStringSet(Constants.KEY_SLEEP_APPS, dnewapp).apply();
        Log.e("New", Integer.toString(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_SLEEP_APPS, new HashSet<>()).size()));
    }

    private void updatehome (Context context, List<ResolveInfo> apps)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor2 = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();

        int homeScreenApps2 = prefs.getInt("homeScreenApps",-1);

        for(int i =0; i<homeScreenApps2; i++)
        {
            boolean flag = false;
            String temp = prefs.getString("HomeApp" + Integer.toString(i+1), "");

            for(int j =0; j<apps.size(); j++)
            {
                if(temp.equals(apps.get(j).activityInfo.packageName))
                {
                    flag = true;
                }
            }

            if(flag == false)
            {
                editor2.putInt("homeScreenApps",homeScreenApps2 - 1);
                editor2.putString("removedPackageName",temp);
                editor2.putString("removedLabel", packtoapp(temp,context));
                editor2.apply();
            }
        }
    }

    public String packtoapp(String pname, Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( pname, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        return applicationName;
    }
}

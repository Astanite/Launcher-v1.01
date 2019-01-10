package launcher.astanite.com.astanite.ui;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import launcher.astanite.com.astanite.datatype.Analytes;
import launcher.astanite.com.astanite.datatype.Data;
import launcher.astanite.com.astanite.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

public class DataReceiver extends BroadcastReceiver {

    long  starttime = 0, endtime = 0;
    ArrayList<String> flagapp;
    int thisindex;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "onReceive: " );

        flagapp = new ArrayList<>();
        flagapp.addAll(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_DISTRACTIVE_APPS, new HashSet<>()));

        predict(context);
    }

    public void predict(Context context) {

        thisindex = getthisindex(context);

        long totaltime = 0;
        long flaggedtime = 0;
        UsageEvents.Event prop = new UsageEvents.Event();

        //Step 1: Store app installed in a list
        List<ResolveInfo> apps = getapps(context);

        //Step 2: Make a hashmap
        HashMap<String, ArrayList<Data>> store = new HashMap<String,ArrayList<Data>>();

        //Step 3: Make a key for everypackagename
        for(int i=0; i<apps.size();i++){
            if(store.get(apps.get(i).activityInfo.packageName) == null){
                ArrayList<Data> temp = new ArrayList<Data>();
                store.put(apps.get(i).activityInfo.packageName, temp);
            }
        }

        //Store sorted usage events in hashmap
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        endtime = System.currentTimeMillis();
        starttime = System.currentTimeMillis() - (1000 * 60 * 60 * 24);
        UsageEvents queryUsageEvents = mUsageStatsManager.queryEvents(starttime, endtime);

        while (queryUsageEvents.hasNextEvent())
        {
            queryUsageEvents.getNextEvent(prop);
            if(prop.getEventType()==UsageEvents.Event.MOVE_TO_BACKGROUND||prop.getEventType()==UsageEvents.Event.MOVE_TO_FOREGROUND)
            {
                if (store.get(prop.getPackageName()) != null) {
                    ArrayList<Data> temp1 = store.get(prop.getPackageName());
                    Data instance = new Data();
                    instance.time = prop.getTimeStamp();
                    if (prop.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND)
                        instance.type = true;
                    else {
                        if (prop.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND)
                            instance.type = false;
                    }
                    temp1.add(instance);
                }
            }

        }

        //Analytes is a new data type to store total time and packagename
        ArrayList<Analytes> analysis = new ArrayList<>();

        //Storing data in Analytes array
        for( String pname: store.keySet())
        {
            Analytes kool = new Analytes();
            kool.packname = pname;
            int flag =0;
            int i=0;
            long time = 0;

            for(; i<store.get(pname).size(); i++)
            {
                if(store.get(pname).get(i).type&&flag==0)
                {
                    time = time - store.get(pname).get(i).time;
                    flag=1;
                }
                else if (flag==1)
                {
                    time = time + store.get(pname).get(i).time;
                    flag=0;
                }
            }

            if(flag==1){
                time = time + store.get(pname).get(--i).time;
            }
            kool.timet = time/60000;
            analysis.add(kool);
        }

        //sorting the packages according to total time on the app
        sorting(analysis);

        //Summation of the applications
        for( int x=0; x<analysis.size(); x++)
        {

            Log.e(analysis.get(x).packname, Long.toString(analysis.get(x).timet));
            totaltime = totaltime + analysis.get(x).timet;
            if(flagapp!=null)
            {
                for( int y=0; y<flagapp.size(); y++)
                {
                    if(analysis.get(x).packname.equals(flagapp.get(y)))
                        flaggedtime=flaggedtime+analysis.get(x).timet;
                }
            }


        }

        thisindex++;

        float ttime = totaltime;
        ttime = ttime/60;

        float ftime = flaggedtime;
        ftime = ftime/60;


        savetotaltime(thisindex ,ttime, context);
        saveflagtime(thisindex, ftime, context);

        calculatescrore(context);

        Log.e("Index", Integer.toString(thisindex));
    }

    private void sorting(ArrayList<Analytes> analysis) {
        int n = analysis.size();
        Analytes temp;
        for (int i = n-1; i>=0; i--)
        {
            for (int j = i -1; j>=0; j--)
            {
                if (analysis.get(i).timet > analysis.get(j).timet)
                {
                    temp = analysis.get(i);
                    analysis.set(i,analysis.get(j));
                    analysis.set(j, temp);
                }
            }
        }
    }



    private List<ResolveInfo> getapps(Context context) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        return pkgAppsList;
    }

    //Function to get thisindex
    public int getthisindex(Context context)
    {
        SharedPreferences sharedpref = context.getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        int thisindex = sharedpref.getInt("this",0);
        return thisindex;
    }

    //Function to save totaltime for thisindex
    public void savetotaltime(int thisindex, float time, Context context)
    {
        SharedPreferences sharedpref = context.getSharedPreferences("datalysis", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt("this", thisindex);
        editor.putFloat(Integer.toString(thisindex) + 't', time);
        editor.apply();
    }

    //function to save flagtime for thisindex
    public void saveflagtime(int thisindex, float time, Context context )
    {
        SharedPreferences sharedpref = context.getSharedPreferences("datalysis", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpref.edit();
        editor.putInt("this", thisindex);
        editor.putFloat(Integer.toString(thisindex) + 'f', time);
        editor.apply();
    }

    public void calculatescrore(Context context)
    {
        SharedPreferences sharedpref = context.getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        int thisindex = sharedpref.getInt("this",0);
        int fcount = 0, tcount =0;
        float favg = 0, tavg =0;
        float fscore = 70,tscore=70;
        int fscorefinal,tscorefinal,scorefinal;
        for(int i=0; i<7; i++)
        {
            Float ftime = getflagtime(thisindex-i,context);
            if(ftime!=0)
            {
                favg = favg+ftime;
                fcount++;
            }

            Float ttime = gettotaltime(thisindex-i,context);
            if(ttime!=0)
            {
                tavg = tavg+ttime;
                tcount++;
            }
        }

        favg = favg/fcount;
        float fcurr = getflagtime(thisindex,context);

        if(favg==0)
        {
            fscore=50;
        }
        else
        {
            if(fcurr>favg)
                fscore=70-70*((fcurr-favg)/fcurr);
            else
                fscore=70+30*((favg-fcurr)/favg);
        }

        fscorefinal= Math.round(fscore);

        tavg = tavg/tcount;
        float tcurr = gettotaltime(thisindex,context);

        if(tavg==0)
        {
            tscore=50;
        }
        else
        {
            if(tcurr>tavg)
                tscore=70-70*((tcurr-tavg)/tcurr);
            else
                tscore=70+30*((tavg-tcurr)/tavg);
        }

        tscorefinal = Math.round(tscore);
        scorefinal = (fscorefinal +  tscorefinal)/2;
        SharedPreferences.Editor editor = sharedpref.edit();
        editor.apply();
        editor.putString("score", Integer.toString(scorefinal));
        Log.e("score:", Integer.toString(scorefinal));
        editor.apply();
    }

    public float getflagtime(int index, Context context)
    {
        SharedPreferences sharedpref = context.getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        float time = sharedpref.getFloat(Integer.toString(index)+'f',0);
        return time;
    }

    public float gettotaltime(int index, Context context)
    {
        SharedPreferences sharedpref = context.getSharedPreferences("datalysis", Context.MODE_PRIVATE);
        float time = sharedpref.getFloat(Integer.toString(index)+'t',0);
        return time;
    }
}

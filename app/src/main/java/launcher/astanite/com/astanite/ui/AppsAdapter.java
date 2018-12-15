package launcher.astanite.com.astanite.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.AppInfo;
import launcher.astanite.com.astanite.data.OnTimeUsedApps;
import launcher.astanite.com.astanite.utils.Constants;

import static android.content.Context.MODE_PRIVATE;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    private static final String TAG = AppsAdapter.class.getSimpleName();

    private List<AppInfo> appsList;
    private RequestManager glide;
    private Context context;
    private static int[] random ;
    private List<String> distractiveApps;

    AppsAdapter(List<AppInfo> AppsList, RequestManager glide, Context context) {
        this.appsList = AppsList;
        this.glide = glide;
        this.context = context;
    }

    private List<AppInfo> randomizeAppList(List<AppInfo> appsList) {
        List<AppInfo> newAppsList = new ArrayList<>(appsList.size());
        int i = 0;
        //this loop will run distractiveApps().size times

        while (i < distractiveApps.size()) {
            if (i == 0) newAppsList.addAll(appsList.subList(0, random[0]));
            else newAppsList.addAll(appsList.subList(random[i - 1], random[i]));
            //get info of all distractive apps and add it to a app info. add that app info to apps(list)
            AppInfo app = new AppInfo();
            try {
                app.icon = context.getPackageManager().getApplicationIcon(distractiveApps.get(i));
                app.label = (String) context.getApplicationContext()
                        .getPackageManager()
                        .getApplicationLabel(context.getPackageManager()
                                .getApplicationInfo(distractiveApps
                                        .get(i), PackageManager.GET_META_DATA));
                app.packageName = distractiveApps.get(i);
                app.launchIntent = context.getPackageManager().getLaunchIntentForPackage(distractiveApps.get(i));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            newAppsList.add(app);
            i++;
        }
        newAppsList.addAll(appsList.subList(random[random.length - 1], appsList.size()));
        return newAppsList;
    }

    private void generateRandomNum(int totalApps) {
        //generate n random variables. n equals total number of distractive apps. all random variable must be different
        //generate random number
        random = new int[distractiveApps.size()];
        do {
            for (int i = 0; i < random.length; i++) {
                random[i] = new Random().nextInt(totalApps-distractiveApps.size());
            }
            Arrays.sort(random);
        } while (containDuplicates());

        for (int i=0;i<distractiveApps.size();i++) Log.d("myrandom=", String.valueOf(random[i]));
    }

    static boolean containDuplicates() {
        // Creates an empty hashset
        HashSet<Integer> set = new HashSet<>();

        // Traverse the input array
        for (int i = 0; i < random.length; i++) {

            if (set.contains(random[i]))
                return true;

            // Add this item to hashset
            set.add(random[i]);

            if (i >= random.length)
                set.remove(random[i]);
        }
        return false;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.app_drawer_grid_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindValues(appsList.get(position), context);
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView appIconImageview;
        private TextView appNameTextview;
        private View appItem;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            appIconImageview = itemView.findViewById(R.id.appIconImageview);
            appNameTextview = itemView.findViewById(R.id.appNameTextview);
            appItem = itemView;
        }

        void bindValues(AppInfo app, Context context) {
            glide.load(app.icon).into(appIconImageview);
            appNameTextview.setText(app.label);
            appItem.setOnClickListener(view -> {
                context.startActivity(app.launchIntent);
                storeTimeApps(getAdapterPosition());
            });

            appItem.setOnLongClickListener(view -> {
                SharedPreferences.Editor editor = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).edit();
                editor.putString("packageName", app.packageName);
                editor.apply();
                ((Activity) context).registerForContextMenu(appItem);
                return false;
            });
        }
    }

    private void storeTimeApps(int position) {
        //when app is Clicked get package name and current time and store it in an array
        OnTimeUsedApps thisApp = new OnTimeUsedApps(appsList.get(position).packageName, System.currentTimeMillis());

        //get existing string from Shared Preference and convert it List of obj
        String storedApps = context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString("onTimeUsedApps", "");
        Gson gson = new Gson();
        Type type = new TypeToken<List<OnTimeUsedApps>>() {
        }.getType();
        List<OnTimeUsedApps> storedAppsList = new ArrayList<>();
        if (!storedApps.equals("")) storedAppsList = gson.fromJson(storedApps, type);

        //Add the currently clicked data to that list
        storedAppsList.add(thisApp);
        // update the list to Shared Preference again
        String jsonApps = gson.toJson(storedAppsList);
        Log.d("json_apps", jsonApps);
        context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE)
                .edit()
                .putString("onTimeUsedApps", jsonApps)
                .apply();
    }

    void updateAppsList(List<AppInfo> newList, int currentMode) {
        distractiveApps = new ArrayList<>();
        distractiveApps.addAll(context.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getStringSet(Constants.KEY_DISTRACTIVE_APPS, new HashSet<>()));
        if (!distractiveApps.isEmpty() && currentMode==Constants.MODE_NONE) {
            for (int i = 0; i < newList.size(); i++)
                Log.d("original_new_list", String.valueOf(newList.get(i).packageName) + " " + i);
            //get total distractive apps
            Log.d("distract_apps_unsorted", String.valueOf(distractiveApps));
            Map<String, String> sortedDistApps = getSortedApps();
            distractiveApps = new ArrayList<>(sortedDistApps.values());
            Log.d("distract_apps_sorted", String.valueOf(distractiveApps));
            //remove distractive apps
            List<AppInfo> AppList = new ArrayList<>();
            int counter = 0;
            for (int i = 0; i < newList.size(); i++) {
                if (!distractiveApps.get(counter).equals(newList.get(i).packageName)) {
                    Log.d("not_equalApps", String.valueOf(newList.get(i).packageName));
                    AppList.add(newList.get(i));

                } else {
                    Log.d("equalApps", String.valueOf(newList.get(i).packageName));
                    if (counter < distractiveApps.size() - 1) {
                        Log.d("_counter=", String.valueOf(counter));
                        counter++;
                    }
                }
            }
            newList = AppList;
            for (int i = 0; i < AppList.size(); i++)
                Log.d("RemovedAppList", AppList.get(i).packageName + " " + i);

            Log.d("distractiveApps", String.valueOf(distractiveApps));
            //generating random numbers to randomize distractive apps
            generateRandomNum(newList.size());
            this.appsList = randomizeAppList(newList);
            for (int i = 0; i < appsList.size(); i++) {
                if (i < 3) Log.d("distractive_random", String.valueOf(random[i]));
                Log.d("randomized_apps", String.valueOf(appsList.get(i).packageName) + "  " + i);
            }
        } else {
            this.appsList = newList;
        }

        notifyDataSetChanged();
    }

    private Map<String, String> getSortedApps() {
        HashMap<String, String> sortApps = new HashMap<>();
        for (String packageName : distractiveApps) {
            try {
                sortApps.put((String) context.getApplicationContext()
                        .getPackageManager()
                        .getApplicationLabel(context.getPackageManager()
                                .getApplicationInfo(packageName, PackageManager.GET_META_DATA)), packageName);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        Map<String, String> map = new TreeMap<>(sortApps);
        Log.d("Sort_Map", String.valueOf(map));
        return map;
    }
}

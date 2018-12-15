package launcher.astanite.com.astanite.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.List;

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
    List<OnTimeUsedApps> onTimeUsedAppsList;

    AppsAdapter(List<AppInfo> appsList, RequestManager glide, Context context) {
        this.appsList = appsList;
        this.glide = glide;
        this.context = context;
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
            onTimeUsedAppsList = new ArrayList<>();
        }

        void bindValues(AppInfo app, Context context) {
            glide.load(app.icon).into(appIconImageview);
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            appIconImageview.setColorFilter(filter);
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

    void updateAppsList(List<AppInfo> newList) {
        this.appsList = newList;
        notifyDataSetChanged();
    }
}

package launcher.astanite.com.astanite.ui.settings;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import launcher.astanite.com.astanite.R;
import launcher.astanite.com.astanite.data.AppInfo;

public class FlaggedAppsAdapter extends RecyclerView.Adapter<FlaggedAppsAdapter.ViewHolder> {

    private static final String TAG = FlaggedAppsAdapter.class.getSimpleName();

    private List<AppInfo> appsList;
    private RequestManager glide;

    public FlaggedAppsAdapter(List<AppInfo> appsList, RequestManager glide) {
        this.appsList = appsList;
        this.glide = glide;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.flagged_apps_grid_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppInfo app = this.appsList.get(position);
        holder.bind(app);
    }

    @Override
    public int getItemCount() {
        return this.appsList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView appNameTextview;
        private ImageView appIconImageView;
        private CheckBox appCheckbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            appNameTextview = itemView.findViewById(R.id.appNameTextview);
            appIconImageView = itemView.findViewById(R.id.appIconImageview);
            appCheckbox = itemView.findViewById(R.id.appCheckbox);
        }

        public void bind(AppInfo app) {
            appNameTextview.setText(app.label);
            glide.load(app.icon).into(appIconImageView);
            appCheckbox.setChecked(app.isChecked);
            appCheckbox.setOnCheckedChangeListener((compoundButton, state) -> appsList.get(getAdapterPosition()).isChecked = state);
        }
    }

    public List<AppInfo> getCheckedApps() {
        Log.d(TAG, "Finding checked apps");
        List<AppInfo> filteredList = new ArrayList<>();
        for (AppInfo app: this.appsList) {
            if (app.isChecked) filteredList.add(app);
        }
        Log.d(TAG, "Checked apps: " + filteredList.size());
        return filteredList;
    }

    public void updateList(List<AppInfo> appsList) {
        this.appsList = appsList;
        notifyDataSetChanged();
    }
}

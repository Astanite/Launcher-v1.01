package launcher.astanite.com.astanite.utils;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import launcher.astanite.com.astanite.data.AppInfo;

public class AppsDiffUtil extends DiffUtil.Callback {

    private List<AppInfo> oldList;
    private List<AppInfo> newList;

    public AppsDiffUtil(List<AppInfo> oldList, List<AppInfo> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).packageName.equals(newList.get(newItemPosition));
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
    }
}
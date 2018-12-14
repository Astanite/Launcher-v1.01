package launcher.astanite.com.astanite.data;

public class OnTimeUsedApps {
    private String packageName ;
    private long CurrentTimeMillis ;

    public OnTimeUsedApps(String packageName, long currentTimeMillis) {
        this.packageName = packageName;
        CurrentTimeMillis = currentTimeMillis;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}

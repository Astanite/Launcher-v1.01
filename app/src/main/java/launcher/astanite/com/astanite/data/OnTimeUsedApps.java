package launcher.astanite.com.astanite.data;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class OnTimeUsedApps {
    private String packageName ;
    private String currentTime;

    public OnTimeUsedApps(String packageName, long currentTimeMillis) {
        this.packageName = packageName;
        convertMilliseconds(currentTimeMillis);
    }

    private void convertMilliseconds(long currentTimeMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        currentTime = sdf.format(new Date(currentTimeMillis));
    }

    public String getAppOpenedTime() {
        return currentTime;
    }
    public String getPackageName() {
        return this.packageName;
    }


}

package launcher.astanite.com.astanite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadCastReceiver extends BroadcastReceiver
{
    SendToMainActivity mListener;

    @SuppressWarnings("unused")
    public BroadCastReceiver()
    {
    }

    public BroadCastReceiver(SendToMainActivity mListener)
    {
        this.mListener = mListener;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
            mListener.sendToMainActivity();
    }

    public interface SendToMainActivity
    {
        void sendToMainActivity();
    }
}

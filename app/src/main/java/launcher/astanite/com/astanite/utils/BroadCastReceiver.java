package launcher.astanite.com.astanite.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadCastReceiver extends BroadcastReceiver
{
    SendToHomeActivity mListener;

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
    }

    public interface SendToHomeActivity
    {
        void sendToHomeActivity();
    }
}

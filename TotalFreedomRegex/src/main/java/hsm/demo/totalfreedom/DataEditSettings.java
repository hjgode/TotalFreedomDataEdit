package hsm.demo.totalfreedom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by E841719 on 28.03.2017.
 */

public class DataEditSettings extends BroadcastReceiver {

    final static String TAG = "DataEditSettings: ";
    public DataEditSettings() {
        super();
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive intent "+ intent.toString());
    }
}

package ir.food.operatorAndroid.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class AvaReceiver extends BroadcastReceiver {

    public final void onReceive(final Context context, Intent intent) {
        int type = intent.getIntExtra(Keys.KEY_BROADCAST_TYPE, -1);
        String message = intent.getStringExtra(Keys.KEY_MESSAGE);

        switch (type) {
            case Keys.PUSH_RECEIVE:
                this.onPushReceived(context, message);
                break;
            case Keys.CONNECTED:
                this.onAvaConnected(context);
                break;
            case Keys.DISCONNECT:
            case Keys.AUTHORIZED_FAILED:
                this.onAuthorizeProblem(context);
                break;
            case 1:
                this.onGetLog(context, message);
                break;
            default:
                onAnomaly(context);
        }

    }


    public abstract void onPushReceived(Context context, String result);

    public void onAuthorizeProblem(Context context) {
    }

    public void onAvaDisconnected(Context context) {
    }

    public void onConnectionRefreshed(Context context) {
    }

    public void onAvaConnected(Context context) {
    }

    public void onAnomaly(Context context) {
    }

    public void onGetLog(Context context, String message) {
    }

}

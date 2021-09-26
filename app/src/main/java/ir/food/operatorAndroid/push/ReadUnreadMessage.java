package ir.food.operatorAndroid.push;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;

import ir.food.operatorAndroid.okHttp.RequestHelper;

public class ReadUnreadMessage {
    public void getUnreadPush(boolean force, Context context) {
        AvaPref avaPref = new AvaPref();
        if (avaPref.getMissingApiRequestTime() + 5000 > Calendar.getInstance().getTimeInMillis() && !force)
            return;
        avaPref.setMissingApiRequestTime(Calendar.getInstance().getTimeInMillis());
        if (avaPref.getMissingApiUrl() == null) return;

        RequestHelper.builder(avaPref.getMissingApiUrl())
                .addParam("projectId", avaPref.getProjectId())
                .addParam("userId", avaPref.getUserId())
                .returnInResponse(context)
                .listener(onGetMissingPush)
                .post();
    }

    RequestHelper.Callback onGetMissingPush = new RequestHelper.Callback() {
        @Override
        public void onResponse(Runnable reCall, Object... args) {
            try {
                JSONObject result = new JSONObject(args[0].toString());
                Context context = (Context) args[1];
                boolean status = result.getBoolean("status");
                if (status) {
                    JSONArray arrayMessage = result.getJSONArray("pushMessags");
                    for (int i = 0; i < arrayMessage.length(); i++) {
                        AvaReporter.Message(context, Keys.PUSH_RECEIVE, arrayMessage.getJSONObject(i).toString());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                AvaCrashReporter.send(e, 101);

            }
        }

        @Override
        public void onFailure(Runnable reCall, Exception e) {

        }
    };

}

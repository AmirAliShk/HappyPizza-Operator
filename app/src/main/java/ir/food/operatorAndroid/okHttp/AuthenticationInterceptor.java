package ir.food.operatorAndroid.okHttp;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ir.food.operatorAndroid.app.EndPoints;
import ir.food.operatorAndroid.app.MyApplication;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <p> add to your {@link RequestHelper} in OkHttpClient builder
 * <code><pre>
 * .addInterceptor(new AuthenticationInterceptor())
 * </pre></code>
 *
 * @output response
 * @auther Fatemeh Noori
 * @vertion 1.0.0
 * @since 12/22/2020
 */
public class AuthenticationInterceptor implements Interceptor {
    private static final String TAG = AuthenticationInterceptor.class.getSimpleName();

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        Request.Builder builder = request.newBuilder();
        String authorization = MyApplication.prefManager.getAuthorization();
        String idToken = MyApplication.prefManager.getIdToken();
        setAuthHeader(builder, authorization, idToken);
        request = builder.build();
        Response response = chain.proceed(request);
        if (response.code() == 401 || response.code() == 403 || response.code() == 402) {
            Log.w(TAG, "Request responses code: " + response.code());
            Log.w(TAG, "Request responses url: " + response.request().url());
//            synchronized (this) {
            boolean statusCode = refreshToken();
            if (statusCode) {
                setAuthHeader(builder, MyApplication.prefManager.getAuthorization(), MyApplication.prefManager.getIdToken());
                request = builder.build();
                Response responseRetry = chain.proceed(request);
                Log.i(TAG, "Request responses new url: " + responseRetry.request().url());
                return responseRetry;
            } else {
                JSONObject object = null;
                try {
                    object = new JSONObject(response.body().string());
                    object.put("refreshTokenError", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MediaType contentType = response.body().contentType();
                ResponseBody body = ResponseBody.create(contentType, object.toString());
                return response.newBuilder().body(body).build();
            }
//            } // synchronized
        } // response.code

        return response;
    }

    private void setAuthHeader(Request.Builder builder, String authorization, String idToken) {
        builder.header("authorization", authorization);
        builder.header("idToken", idToken);
    }

    public boolean refreshToken() {
        synchronized (this) {
            boolean statusCode = false;
            OkHttpClient client = new OkHttpClient.Builder().build();
            MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
            JSONObject json = new JSONObject();
            try {
                json.put("token", MyApplication.prefManager.getRefreshToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RequestBody body = RequestBody.create(jsonType, json.toString());
            Request request = new Request.Builder()
                    .url(EndPoints.REFRESH_TOKEN)
                    .post(body)
                    .build();

            Response response;

            try {
                response = client.newCall(request).execute();

                Log.i(TAG, "refreshToken : input " + json.toString());
                Log.i(TAG, "refreshToken : url " + request.url().toString());

                if (response != null) {
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonBody = new JSONObject(response.body().string());
                            boolean success = jsonBody.getBoolean("success");
                            String message = jsonBody.getString("message");

                            Log.i(TAG, "refreshToken : jsonBody " + jsonBody.toString());

                            if (success) {
                                JSONObject objData = jsonBody.getJSONObject("data");
                                String id_token = objData.getString("id_token");
                                String access_token = objData.getString("access_token");
                                MyApplication.prefManager.setAuthorization(access_token);
                                MyApplication.prefManager.setIdToken(id_token);
                                statusCode = true;
                            } else {
                                MyApplication.prefManager.setAuthorization("");
                                MyApplication.prefManager.setIdToken("");
                                statusCode = false;
                            }

                        } catch (JSONException e) {
                            e.getMessage();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return statusCode;
        }
    }
}

// server error response :
// {"message":"invalid signature","success":false} = getMessage, id_token is invalid

// {"message":"Format is Authorization: Bearer [token]","success":false} = token, /api/user/v1/tokenssss

// {"success":false,"message":".اطلاعات صحیح نمی باشد","data":{}} = token, refreshToken is wrong

// {"message":"No authorization token was found","success":false} = getMessages, refreshToken is wrong

// {"success":true,"message":"با موفقیت ایجاد شد","data":{"id_token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MTIzLCJ1c2VybmFtZSI6IjEyMzQiLCJpYXQiOjE2MDk0MTI1OTgsImV4cCI6MTYwOTQxMjg5OH0.kLo1w2p0V21P1i5Y1qHW7uNTJ3xjYoeDEJPLRgp1cbU","access_token":"Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL3R1cmJvdGF4aS5pciIsImF1ZCI6IlVzZXJzIiwiZXhwIjoxNjA5NDEyODk4LCJzY29wZSI6Im9wZXJhdG9yIiwic3ViIjoidHVyYm90YXhpIiwianRpIjoiRUM3M0NFRDg3NUNFRTcyNSIsImFsZyI6IkhTMjU2IiwiaWF0IjoxNjA5NDEyNTk4fQ.QqBXrACgx2d3O8HS1fXfidq-J9ONvTlXcSwRt-ZjkLA"}}
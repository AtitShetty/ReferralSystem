package shetty.atit.referralui;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Atit on 10/22/2017.
 */

public class RequestResponseSingleton {

    private static RequestResponseSingleton mInstance;

    private RequestQueue mRequestQueue;

    private static Context mCtx;


    private RequestResponseSingleton(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestResponseSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestResponseSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}

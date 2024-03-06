package com.example.terminaldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.wizarpos.wizarviewagentassistant.aidl.ISystemExtApi;

public class MainActivity extends AppCompatActivity {
    ISystemExtApi iSystemExtApi;
    private String TAG = "MainActivity";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        bindService();
        findViewById(R.id.btnSetScreenOff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (iSystemExtApi != null) {
                        boolean status = iSystemExtApi.setScreenOffTimeout(30000);
                        Log.i(TAG, "set screen off status:" + status);
                        status = iSystemExtApi.setTouchScreenWakeupValue("touch");
                        Log.i(TAG, "set touch screen wakeup status:" + status);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iSystemExtApi = ISystemExtApi.Stub.asInterface(iBinder);
            Log.i(TAG, "connect is success");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iSystemExtApi = null;
            Log.i(TAG, "connect is disconnect");
        }
    };

    /**
     * connect service
     */
    private void bindService() {
        Intent intent = new Intent();
        ComponentName comp = new ComponentName(
                "com.wizarpos.wizarviewagentassistant",
                "com.wizarpos.wizarviewagentassistant.SystemExtApiService");
        intent.setComponent(comp);
        boolean bindStatus = bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "bind service status:" + bindStatus);
    }

    /**
     * disconect service
     */
    private void unbindService() {
        if (connection != null) {
            unbindService(connection);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }
}
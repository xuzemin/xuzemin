
package com.ctv.settings.network.Listener;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import com.ctv.settings.network.utils.InitDataInfo;


public class LoadDataAsyncTask extends AsyncTask<Void, Integer, InitDataInfo> {
    DataFinishListener dataFinishListener;

    public void setFinishListener(DataFinishListener dataFinishListener) {
        this.dataFinishListener = dataFinishListener;
    }

    private final WeakReference<Context> mContextReference;

    private final WeakReference<ConnectivityListener> mConnectivityListenerRef;

    public LoadDataAsyncTask(Context context, ConnectivityListener listener) {
        this.mContextReference = new WeakReference<Context>(context);
        this.mConnectivityListenerRef = new WeakReference<ConnectivityListener>(listener);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected InitDataInfo doInBackground(Void... Void) {
        // TODO Auto-generated method stub
        if(mContextReference.get()!=null && mConnectivityListenerRef.get()!= null){
            return mConnectivityListenerRef.get().initViewNetworkData(mContextReference.get()) ;   
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(InitDataInfo results) {
        dataFinishListener.dataFinishSuccessfully(results);
    }

    public static interface DataFinishListener {
        void dataFinishSuccessfully(InitDataInfo data);
    }
}

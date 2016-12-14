package com.zhuoxin.asynctaskdemo;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;


public class MainActivity extends Activity {
    private ProgressBar mProgressBar = null;
    private Button myButton = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar) findViewById(R.id.Progressbar1);
        myButton = (Button) findViewById(R.id.button1);
        myButton.setOnClickListener(new BtnClickListener());
        Log.d("MainThread", "id:" + Thread.currentThread().getId() + " name:" + Thread.currentThread().getName()); //这里是UI主线程

    }

    private class BtnClickListener implements OnClickListener {


        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            new myAsync().execute();
        }

    }

    private class myAsync extends AsyncTask<Void, Integer, Void> {
        int duration = 0;
        int current = 0;

        @Override
        protected Void doInBackground(Void... params) {
            do {
                Log.d("handleMessage", "id:" + Thread.currentThread().getId() + " name:" + Thread.currentThread().getName());
                current += 10;
                try {
                    publishProgress(current); //这里的参数类型是 AsyncTask<Void, Integer, Void>中的Integer决定的，在onProgressUpdate中可以得到这个值去更新UI主线程，这里是异步线程
                    Thread.sleep(1000);
                    if (current >= 100) {
                        break;
                    }
                } catch (Exception e) {
                }
            } while (current <= 100);
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            System.out.println(values[0]);
            mProgressBar.setProgress(values[0]);
            Log.d("updateThread", "id:" + Thread.currentThread().getId() + " name:" + Thread.currentThread().getName());
        }
    }
}
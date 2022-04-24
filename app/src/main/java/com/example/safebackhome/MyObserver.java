package com.example.safebackhome;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class MyObserver implements DefaultLifecycleObserver {

    @Override
    public void onDestroy(LifecycleOwner owner) {

        Log.d("EXITAPP","onDestroy() has been called");
    }
    @Override
    public void onPause(LifecycleOwner owner) {

        Log.d("EXITAPP","onPause() has been called");
    }
    @Override
    public void onStart(LifecycleOwner owner) {

        Log.d("EXITAPP","onStart() has been called");
    }
}

//myLifecycleOwner.getLifecycle().addObserver(new MyObserver());

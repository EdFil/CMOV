package pt.ulisboa.tecnico.cmov.servicesimple;

import android.app.Application;

/**
 * Created by edgar on 02-03-2015.
 */
public class MyAppContext extends Application {

    private int mTimesStarted = 0;

    public void serviceStarted(){
        mTimesStarted++;
    }

    public int getTimesStarted(){ return mTimesStarted; }

}

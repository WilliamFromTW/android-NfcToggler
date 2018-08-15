package nfc.inmethod.nfctoggler;

import android.app.Application;
import android.content.Context;

public class ApplicationContextHelper extends Application {
    private static Context mContext;

    public void onCreate(){
        super.onCreate();
        this.mContext = this;
    }
    public static Context getContext(){
        return mContext;
    }
}

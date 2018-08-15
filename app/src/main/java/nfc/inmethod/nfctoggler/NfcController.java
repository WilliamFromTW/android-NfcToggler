package nfc.inmethod.nfctoggler;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class NfcController {
    private static final String TAG = "NfcController";
    private static boolean bGranted = false;
    public static boolean checkNfcEnableStatus(Context context) {
        return NfcAdapter.getDefaultAdapter (context).isEnabled();
    }

    public static boolean enableNfc(boolean isOn,Context context) {
        boolean result = false;
        grantPermission(context);
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null) {
            Class<?> NfcManagerClass;
            Method setNfcEnabled;
            try {
                NfcManagerClass = Class.forName(nfcAdapter.getClass().getName());
                setNfcEnabled = NfcManagerClass.getDeclaredMethod(isOn
                        ? "enable" : "disable");
                setNfcEnabled.setAccessible(true);
                result = (Boolean) setNfcEnabled.invoke(nfcAdapter);
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return result;
    }
    public static void grantPermission(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_SECURE_SETTINGS)
                != PackageManager.PERMISSION_GRANTED) {
            try {
                Process p = null;
                try {
                    p = Runtime.getRuntime().exec("su");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                try {
                    os.writeBytes("pm grant " + context.getPackageName() + " android.permission.WRITE_SECURE_SETTINGS \n");
                    os.writeBytes("exit\n");
                    os.flush();
                    p.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

package nfc.inmethod.nfctoggler

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import nfc.inmethod.nfctoggler.R.mipmap.ic_launcher
import android.content.Intent


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        val startServiceIntent = Intent(this, BootUpService::class.java)
        startService(startServiceIntent)
        NfcController.grantPermission(this);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("NFC Toggler")
                builder.setMessage("Version Name : " + this.packageManager.getPackageInfo(this.packageName, 0).versionName + "\nVersion Code : " + this.packageManager.getPackageInfo(this.packageName, 0).versionCode)
                builder.setNeutralButton("Ok", null)
                builder.setCancelable(true)
                val alert = builder.create()
                alert.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

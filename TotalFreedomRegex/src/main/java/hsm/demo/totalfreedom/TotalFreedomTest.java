package hsm.demo.totalfreedom;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static hsm.demo.totalfreedom.DataEdit.BROADCAST_ACTION;
import static hsm.demo.totalfreedom.DataEditUtils.getHexedString;

public class TotalFreedomTest extends AppCompatActivity {

    TextView textView;
    EditText editText1;
    Handler handler;
    final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE=42;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE=43;
    private static Context context;

    Button btnLiveTest;

    public static Context getAppContext() {
        return TotalFreedomTest.context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TotalFreedomTest.context = getApplicationContext();

        setContentView(R.layout.activity_total_freedom_test);

        //Version number
        ((TextView)findViewById(R.id.textViewVersion)).setText("v0.04");

        textView=(TextView)findViewById(R.id.textView2);
        textView.setHorizontallyScrolling(true);
        textView.setMovementMethod(new ScrollingMovementMethod());

        editText1=(EditText)findViewById(R.id.editText1);
        editText1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                // you can call or do what you want with your EditText here
                Intent _intent=new Intent(BROADCAST_ACTION);
                _intent.putExtra("text", getHexedString(s.toString()));
                getAppContext().sendBroadcast(_intent);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        Button btnShowList=(Button)findViewById(R.id.btnRulesList);
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i = new Intent(getApplicationContext(),RuleListActivity.class);
                //startActivity(i);
                showRulesList();
            }
        });

        //TODO: use in debug only
        Button btnTEST=(Button)findViewById(R.id.buttonTEST);
        btnTEST.setVisibility(View.GONE);
        btnTEST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyRegex.DoTests();
            }
        });

        //TODO: add live test option
        btnLiveTest=(Button)findViewById(R.id.btnLiveTestRule);
        btnLiveTest.setVisibility(View.GONE);
        btnLiveTest.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   DataEdit de=new DataEdit();
                   Intent intent=new Intent();//"hsm.demo.totalfreedom/.DataEdit"); // com.honeywell.decode.intent.action.EDIT_DATA
                   intent.setClassName("hsm.demo.totalfreedom", "DataEdit");
                   intent.setAction("com.honeywell.decode.intent.action.EDIT_DATA");
                   intent.putExtra("data",editText1.getText().toString());
                   intent.putExtra("aimId", "]C1");
                   byte[] buf=null;
                   try {
                       buf = editText1.getText().toString().getBytes("UTF-16");
                   }catch (UnsupportedEncodingException ex){}
                   intent.putExtra("dataBytes", buf);

                   de.onReceive(getAppContext(), intent);
               }
           }
        );

        if(Debug.isDebuggerConnected()) {
            btnTEST.setVisibility(View.VISIBLE);
            btnLiveTest.setVisibility(View.VISIBLE);
        }

        //verify permissions
        hasPermissions();
        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck!=PackageManager.PERMISSION_DENIED)
            Toast.makeText(this,"Need 'Read External Storage' permission!",Toast.LENGTH_LONG);
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck2!=PackageManager.PERMISSION_DENIED)
            Toast.makeText(this,"Need 'Write External Storage' permission!",Toast.LENGTH_LONG);
    }

    @Override
    protected void onResume(){
        super.onResume();
        registerReceiver(logReceiver, new IntentFilter(DataEdit.BROADCAST_ACTION));
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(logReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    void showRulesList(){
        Intent i = new Intent(getApplicationContext(),RuleListActivity.class);
        startActivity(i);
    }

    void showHelp(){
        HelpDialog helpDialog = new HelpDialog(this, "file:///android_asset/usage.html");
        helpDialog.setTitle("Usage");
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        helpDialog.show();
        helpDialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuitem_rules_list:
                showRulesList();
                return true;
            case R.id.menuitem_main_help:
                showHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver logReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };
    private void updateUI(Intent intent) {
        String text = intent.getStringExtra("text");
        textView.setText(textView.getText().toString() + "\n" + text);
    }

    void hasPermissions(){
// Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        return;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

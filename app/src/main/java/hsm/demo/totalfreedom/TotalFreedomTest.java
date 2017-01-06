package hsm.demo.totalfreedom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class TotalFreedomTest extends AppCompatActivity {

    TextView textView;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_freedom_test);
        textView=(TextView)findViewById(R.id.textView2);
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
    private BroadcastReceiver logReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };
    private void updateUI(Intent intent) {
        String text = intent.getStringExtra("text");
        textView.setText(textView.getText().toString() + "\n" + text);
    }}

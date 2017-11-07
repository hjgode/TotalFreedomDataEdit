package hsm.demo.totalfreedom;

import android.app.Dialog;

/**
 * Created by E841719 on 07.11.2017.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.MailTo;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HelpDialog extends Dialog {

    private static Context mContext = null;
    private String m_URL = "";

    public HelpDialog(Context context, String sUrl) {
        super(context);
        mContext = context;
        m_URL=sUrl;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.helpview);
        WebView help = (WebView)findViewById(R.id.helpView);

        help.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("mailto:")){
                    MailTo mt = MailTo.parse(url);
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] { mt.getTo()});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "AppHelp feedback");
                    mContext.startActivity(Intent.createChooser(intent, "Send feedback ..."));
                    return true;
                }
                else{
                    view.loadUrl(url);
                }
                return true;
            }
        });

        //String helpText = readRawTextFile(R.raw.help_contents);
        //help.loadData(helpText, "text/html; charset=utf-8", "utf-8");
        help.loadUrl(m_URL);// "file:///android_asset/usage.html");
    }

    private String readRawTextFile(int id) {
        InputStream inputStream = mContext.getResources().openRawResource(id);
        InputStreamReader in = new InputStreamReader(inputStream);
        BufferedReader buf = new BufferedReader(in);
        String line;
        StringBuilder text = new StringBuilder();
        try {
            while (( line = buf.readLine()) != null)
                text.append(line);
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }
}

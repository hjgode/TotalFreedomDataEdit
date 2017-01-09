package hsm.demo.totalfreedom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hjgode on 05.01.2017.
 */


public class DataEdit extends BroadcastReceiver {
    final String TAG = "BroadcastReceiver";
    final boolean myDebug = true;
    public static final String BROADCAST_ACTION = "hsm.demo.totalfreedom.displayevent";

    private final Handler handler = new Handler();
//    Intent myIntent;
//    Context myContext;
    StringBuilder logText = new StringBuilder();  //text to sedn to main activity

    void doLog(String s, Context context){
        Log.d(TAG, s);
        doUpdate(s, context);
    }

    void doUpdate(String s, Context context_) {
        class updateUI implements Runnable {
            String str;
            Context _context;
            updateUI(String s, Context c) {
                str = s;
                _context=c;
            }
            public void run() {
                Intent _intent=new Intent(BROADCAST_ACTION);
                _intent.putExtra("text", str);
                _context.sendBroadcast(_intent);
            }
        }
        Thread t = new Thread(new updateUI(s, context_));
        t.start();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String ScanResult = intent.getStringExtra("data");//Read the scan result from the Intent

//        this.myContext = context;//you can retrieve context from onReceive argument
//        this.myIntent = new Intent(BROADCAST_ACTION);
        logText.append(ScanResult);
        doLog(ScanResult, context);

        //return edited data as bandle
        Bundle bundle = new Bundle();
        String formattedOutput=ScanResult; //what to return

        if(myDebug) {
            //for debugging?
            byte[] byteData = intent.getByteArrayExtra("dataBytes");
            StringBuilder sb = new StringBuilder();
            for (byte b : byteData) {
                sb.append(String.format("0x%02x,", b));
            }
            Log.d(TAG, "dataBytes=" + sb.toString());
        }

        /*
        codeId b (java.lang.String)
        dataBytes [B@c9a8a48 ([B)
        data 10110 (java.lang.String)
        timestamp 2016-09-17T09:05:27.619+2:00 (java.lang.String)
        aimId ]A0 (java.lang.String)
        version 1 (java.lang.Integer)
        charset ISO-8859-1 (java.lang.String)
        */
//        Bundle bundle=intent.getExtras();
//        if (bundle != null) {
//            for (String key : bundle.keySet()) {
//                Object value = bundle.get(key);
//                Log.d(TAG, String.format("%s %s (%s)", key, value.toString(), value.getClass().getName()));
//            }
//        }

        String input=ScanResult;
        String aimID=intent.getStringExtra("aimId");
        doUpdate(String.format("ScanData IN: '%s'", removeUnicodeAndEscapeChars(input)),context);
        doUpdate(String.format("aimId='%s'",aimID),context);

        // read rules
        // a rule consist of two or three sections
        // sections are devided by '=>'
        // a two section rule defines the search pattern (regex) and the replacement pattern
        // a three section rule defines the Symbology (aimID) and then the search pattern and the replacement pattern
        // the processing of rules from top to down stops after a rule matches, except for the first or only char of
        // the aimID field is a '+'
        String[] sRules= new String[]{
//                "(.)(.)(.*)=>($1) $2-$3\n",
//                "]A0=>(.*)=>$1\n",
                "+=>\u001D=>FNC1",  //will not stop rule matching as aimID field starts with a '+'
                "(.*)=>$1\n"
        };

        //read rules from file
        ReadIniFile readIniFile=new ReadIniFile(context, "dataedit_regex.ini");
        sRules=readIniFile.getRules();
        if(sRules.length==0){
            //add one simple rule
            sRules=new String[]{"(.*)=>$1"};
            doLog("Using default rule",context);
        }

        //convert lines to rules
        List<rule> rules=new ArrayList<rule>();
        //split rule lines
        for (String s:sRules) {
            rules.add(new rule(s));
        }

        //go thru all rules unless rule matches
        StringBuilder buffer=new StringBuilder();
        Boolean bMatchedFound=false;
        for (rule r:rules) {
            if(r.valid){
                doLog(removeUnicodeAndEscapeChars(String.format("DataEdit processing rule: regex:'%s', replace:'%s', for input='%s'", r.regex, r.replace, ScanResult)),context);
                if(r.aimID.length()>0){
                    //rule uses aimId, so check if aimId matches
                    if(aimID.equals(r.aimID)){
                        if(doRegex(ScanResult, r.regex, r.replace, buffer)) {
                            bMatchedFound=true;
                            formattedOutput=buffer.toString();
                            doLog(String.format("Matched rule: %s", removeUnicodeAndEscapeChars(r.toString())),context);
                            if(r.stop)
                                break;
                            else{
                                doLog("'No stop rule' matched. Continue with next rule...", context);
                                ScanResult=formattedOutput;
                            }
                        }
                        else{
                            doLog(String.format("NO doRegex match for %s inside aimID", ScanResult),context);
                        }
                    }
                }
                else{
                    if(doRegex(ScanResult, r.regex, r.replace, buffer)) {
                        bMatchedFound=true;
                        formattedOutput=buffer.toString();
                        doLog(String.format("Matched rule: %s", r.toString()), context);
                        if(r.stop)
                            break;
                        else{
                            doLog("'No stop rule' matched. Continue with next rule...", context);
                            ScanResult=formattedOutput;
                        }
                    }
                    else{
                        doLog(String.format("NO doRegex match for %s outside aimID", ScanResult), context);
                    }
                }
            }// end of rule is valid
            else{
                doLog(String.format("DataEdit processing rule INVALID: aimId:'%s', regex:'%s', replace:'%s'", r.regex, r.replace), context);
            }
        }
        if(bMatchedFound)
            doLog(String.format("DataEdit replacement: %s=>%s", ScanResult, formattedOutput),context);
        else
            doLog(String.format("NO DataEdit replacement: %s=>%s", ScanResult, formattedOutput),context);

        //Return the Modified scan result string
        bundle.putString("data", formattedOutput);
        setResultExtras(bundle);
    }

    boolean doRegex(String sIN, String regex, String replacewith, StringBuilder sOut){
        String input=sIN;
        boolean bRet=true; //did we find a match?
        sOut.append(sIN);

        String myRegex="(.)(.)(.*)";
        myRegex=regex;

        boolean faulty=false;
        Pattern p=null;
        try {
            p = Pattern.compile(myRegex);
        }catch(UnknownFormatConversionException e){
            faulty=true;
            Log.e(TAG, "Pattern.Compile() exception: "+e.getMessage());
        }
        catch (Exception e){
            faulty=true;
            Log.e(TAG, "Pattern.Compile() exception: "+e.getMessage());
        }
        Matcher m=null;
        if(!faulty && p!=null) {
            try {
                m = p.matcher(input);
            }catch (Exception e){
                faulty=true;
                Log.e(TAG, "Pattern.matcher() exception: "+e.getMessage());
            }
        }
        if(faulty){
            //sOut=new StringBuilder(sIN);
//            bundle.putString("data", ScanResult); //return original data
//            setResultExtras(bundle);
            return false;
        }

        if(myDebug){
            int iCnt = 0;
            try{
                while(m.find()){
                    while(iCnt<=m.groupCount()) {
                        if(m.group(iCnt)!=null) {
                            Log.d(TAG, String.format("Match %i: %s", iCnt, m.group(iCnt)));
                        }
                        iCnt++;
                    }
                }
            }catch (Exception e){
                Log.e(TAG, "m.find() exception: "+e.getMessage());
            }
        }
        String replacementText = "($1) $2-$3\n"; // \\\\x0d will give output \x0d; \\x0d will output x0d NEED to use JAVA escape codes, ie '\n' for a new line
        replacementText=replacewith;
        String returnString="";
        try {
            returnString = m.replaceAll(replacementText);
            sOut.delete(0,sOut.length());
            sOut.append(returnString);
            bRet=true;
            Log.d(TAG, String.format("returnString= %s", returnString));
        }catch (Exception e){
            returnString=sIN;//return unchanged scan data
            Log.e(TAG, "Matcher.replaceAll() exception: "+e.getMessage());
        }

        return bRet;
    }

    class rule{
        public String aimID="";
        public String regex="";     // the regular expression defining what to search for
        public String replace="";   // the replacement pattern to return
        public boolean valid=false;
        public boolean stop=true;
        public rule(String sIn){
            String[] s=sIn.split("=>");
            if(s.length==3){
                if(s[0].startsWith("+")){
                    stop=false;
                    aimID=s[0].substring(1);
                }else {
                    aimID = s[0];
                }
                regex=s[1];
                replace=s[2];
                valid=true;
            }
            else if(s.length==2){
                aimID="";
                regex=s[0];
                replace=s[1];
                valid=true;
            }
        }
        @Override
        public String toString(){
            return "'" + aimID + "', '"+regex+ "', '"+replace+ "', "+valid;
        }
    }

    public static String removeUnicodeAndEscapeChars(String input) {
        StringBuilder buffer = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            if ((int) input.charAt(i) > 256) {
                buffer.append("\\u").append(Integer.toHexString((int) input.charAt(i)));
            } else {
                if((int)input.charAt(i)<0x20)
                    buffer.append("<x"+String.format("%02x",(int)input.charAt(i))+">");
                else
                    buffer.append(input.charAt(i));
            }
        }
        return buffer.toString();
    }
}

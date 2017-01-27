package hsm.demo.totalfreedom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
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
        //if nothing matches return nothing?
        formattedOutput="";

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
        doUpdate(String.format("ScanData IN: '%s'", DataEditUtils.getHexedString(input)),context);
        doUpdate(String.format("aimId='%s'",aimID),context);

        // read rules
        // a rule consist of two or three sections
        // sections are devided by '=>'
        // a two section rule defines the search pattern (regex) and the replacement pattern
        // a three section rule defines the Symbology (aimID) and then the search pattern and the replacement pattern
        // the processing of rules from top to down stops after a rule matches, except for the first or only char of
        // the aimID field is a '+'
        String[] sRules= new String[]{
                "# this is a comment",
                "# example 1",
                "([0-9]{6})=>M$1 ex1",
                "g=>([0-9]{6})=>M$1 ex1a",
                "(.*)\\s([0-9]{6})\\s(.*)=>M$2 ex1b",
                "# example 2",
                "([0-9]{3})([0-9]{2})([0-9]{4})=><SSN>$1-$2-$3</SSN> ex2",
                "# example 3",
                "...(.{13}).....=>$1 ex3",
                "# example 4",
                "([a-zA-Z]+) (\\w+)=>$2, $1 ex4",
                "# example 5",
                "g=>([0-9]{6})=>M$1 ex5",
                "# example 5b",
                "g=>(\\d{6})=>M$1 ex5b",
//                "]A0=>(.{2})(.{14})(.*)=>$2\n", //\n gives x0A
//                "]A0=>(.*)=>$1\n",
//                "+g=>\u001D=>FNC1",  //will not stop rule matching as aimID field starts with a '+', will a global search/replace
                "(.*)=>no match: $1\n"
        };
        //read rules from file
        ReadIniFile readIniFile=new ReadIniFile(context, "dataedit_regex.ini");
        String[] sRulesTest=readIniFile.getRules();

        if(
                Debug.isDebuggerConnected() ||
                        sRulesTest.length==0
                ) {
            //create a default rule file if there are no rules or if debugger is attached
            SaveToFile saveMe = new SaveToFile("dataedit_regex.ini", context);
            saveMe.saveToFile(sRules);
            doLog("Using default rules",context);
        }
        else{
            sRules=sRulesTest;
        }

        //convert lines to rules
        List<rule> rules=new ArrayList<rule>();
        //split rule lines
        for (String s:sRules) {
            rule theRule=new rule(s);
            if(theRule.regex.length()>0) //do not add empty rules
                rules.add(theRule);
        }

        //go thru all rules and see if rule matches
        StringBuilder buffer=new StringBuilder();
        Boolean bMatchedFound=false;
        for (rule r:rules) {
            doLog("Testing rule: "+r.toString(),context);
            if(r.valid){
                doLog(DataEditUtils.getHexedString(String.format("DataEdit processing rule: regex:'%s', replace:'%s', for input='%s'", r.regex, r.replace, ScanResult)),context);
                if(r.aimID.length()>0){
                    //rule uses aimId, so check if aimId matches
                    if(aimID.equals(r.aimID)){
                        doLog("aimID matches",context);
                        if(doRegex(ScanResult, r.regex, r.replace, buffer, r.global)) {
                            bMatchedFound=true;
                            formattedOutput=buffer.toString();
                            doLog(String.format("Matched rule: %s", DataEditUtils.getHexedString(r.toString())),context);
                            if(r.stop)
                                break;
                            else{
                                doLog("'No stop rule' matched. Continue with next rule...", context);
                                ScanResult=formattedOutput;
                            }
                        }
                        else{
                            doLog(String.format("aimID rule does not match", ScanResult),context);
                        }
                    }
                    else{
                        doLog(String.format("rule AimID does not match for: %s", ScanResult),context);
                        bMatchedFound=false;
                    }
                }
                else{
                    doLog("No aimID used in regex", context);
                    if(doRegex(ScanResult, r.regex, r.replace, buffer, r.global)) {
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
                        doLog(String.format("Regex did not match for %s", ScanResult), context);
                    }
                }//if AimID.length>0
            }// end of rule is valid
            else{
                doLog(String.format("Not a valid rule: %s", r.toString()), context);
            }
        }
        if(bMatchedFound)
            doLog(String.format("DataEdit replacement: %s=>%s", DataEditUtils.getJavaEscaped(ScanResult), DataEditUtils.getJavaEscaped(formattedOutput)),context);
        else {
            DataEditUtils.doBeepWarn();
            doLog(String.format("NO DataEdit replacement: %s=>%s", DataEditUtils.getJavaEscaped(ScanResult), DataEditUtils.getJavaEscaped(formattedOutput)), context);
        }
        //Return the Modified scan result string
        bundle.putString("data", formattedOutput);
        setResultExtras(bundle);
    }

    boolean doRegex(String sIN, String searchpattern, String replacematch, StringBuilder sOut, boolean global){
        String input=sIN;
        boolean bRet=false; //did we find a match?
        sOut.append(sIN);

        String myRegex="(.)(.)(.*)";
        myRegex=searchpattern;

        boolean faulty=false;
        Pattern p=null;
        //check the regex syntax, does it compile?
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
        replacementText= replacematch;
        String returnString="";

//        if(global && !Pattern.matches(searchpattern, input)) {
//            Log.d(TAG, "Pattern does not match!");
//            return false;
//        }
        try {
            if(m.matches() || global){ //redundant with Patter.matches above
                Log.d(TAG, String.format("m.matches()=%s, global=%s", m.matches(), global));
                if(global){
                    if(m.find()){
                        returnString = m.replaceAll((replacementText));
                        sOut.delete(0,sOut.length());
                        sOut.append(returnString);
                        bRet=true;
                        Log.d(TAG, String.format("returnString= %s", returnString));
                    }else{
                        //no global match found
                        sOut.delete(0, sOut.length());
                        sOut.append(""); //empty return
                        bRet=false;
                        Log.d(TAG, String.format("no match found for global regex %s", searchpattern));
                    }
                }
                else {
                    returnString = m.replaceAll(replacementText);
                    sOut.delete(0, sOut.length());
                    sOut.append(returnString);
                    bRet = true;
                    Log.d(TAG, String.format("returnString= %s", returnString));
                }//is global
            }
            else {
                Log.d(TAG, "rule does not match");
                bRet=false;
            }
        }catch (Exception e){
            returnString=sIN;//return unchanged scan data
            Log.e(TAG, "Matcher.replaceAll() exception: "+e.getMessage());
            bRet=false;
        }

        return bRet;
    }


}

package hsm.demo.totalfreedom;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UnknownFormatConversionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by E841719 on 07.02.2017.
 */

public class MyRegex {
    static String TAG="MyRegex::doRegex";
    static boolean myDebug=false;

    /*
    Test rules against Strings and verify result
     */
    static void DoTests(){
        String[][] testRulesData=new String[][]{
                {"(.*)=>$1", "abc 123"},
                {"(\\S+)(\\s+)(\\S+)=>$1-$3", "abc 123"}, //two 'words' separated by space, gives abc-123
                {"(\\(\\d\\))(.+)(\\(\\d\\))(\\d+)=>$1-$2-$3-$4", "(1)234(2)abc"}, //gives no match as last group of digits is no there
                {"(\\(\\d\\))(.+)(\\(\\d\\))(\\d+)=>$1-$2-$3-$4", "(1)234(2)567"}, //gives 1-234-2-567, shows how to look for braces
                {"g=>=>.fnc1.", "123\u001d456\u001d789"}, //replace all FNC1
                {"(\\d{3})([0-9]{3})(\\d\\d\\d)=>$1 $2 $3","123456789"}, //different ways to look for a count of digits
        };

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
                "g=>\u001d=>.fnc1.",
//                "]A0=>(.{2})(.{14})(.*)=>$2\n", //\n gives x0A
//                "]A0=>(.*)=>$1\n",
//                "+g=>\u001D=>FNC1",  //will not stop rule matching as aimID field starts with a '+', will a global search/replace
                "(.*)=>no match: $1\n"
        };

        StringBuilder result=new StringBuilder();
        String sTestData="", sAimID="";
        Log.d(TAG, "########################## TESTING #######################################");
        for (String[]testTupple:testRulesData) {
            result=new StringBuilder();
            DoTest(new String[]{testTupple[0]}, sAimID, testTupple[1], result);
            Log.d(TAG, "RESULT '"+DataEditUtils.getJavaEscaped(testTupple[1])+"' REPLACED BY '"+ DataEditUtils.getJavaEscaped(result.toString())+"'");
            Log.d(TAG, "--------------------------------------------------------------------------");
        }

        Log.d(TAG, "##########################   END   #######################################");
    }

    private static void DoTest(String[] sRules, String aimID, String scannedData, StringBuilder regExResult){
        //convert lines to rules
        List<rule> rules=new ArrayList<rule>();
        //split rule lines
        for (String s:sRules) {
            rule theRule=new rule(s);
            if(theRule.regex.length()>0) //do not add empty rules
                rules.add(theRule);
        }
        String formattedOutput="";

        //go thru all rules and see if rule matches
        StringBuilder buffer=new StringBuilder();
        Boolean bMatchedFound=false;
        for (rule r:rules) {
            Log.d(TAG, "Testing rule: "+r.toString());
            if(r.valid){
                Log.d(TAG, DataEditUtils.getHexedString(String.format("DataEdit processing rule: regex:'%s', replace:'%s', for input='%s'", r.regex, r.replace, scannedData)));
                if(r.aimID.length()>0){
                    //rule uses aimId, so check if aimId matches
                    if(aimID.equals(r.aimID)){
                        Log.d(TAG, "aimID matches");
                        if(MyRegex.doRegex(scannedData, r.regex, r.replace, buffer, r.global)) {
                            bMatchedFound=true;
                            formattedOutput=buffer.toString();
                            Log.d(TAG, String.format("Matched rule: %s", DataEditUtils.getHexedString(r.toString())));
                            if(r.stop)
                                break; //terminate for loop
                            else{
                                Log.d(TAG, "'No stop rule' matched. Continue with next rule...");
                                scannedData=formattedOutput;
                            }
                        }
                        else{
                            Log.d(TAG, String.format("aimID rule does not match", scannedData));
                        }
                    }
                    else{
                        Log.d(TAG, String.format("rule AimID does not match for: %s", scannedData));
                        bMatchedFound=false;
                    }
                }
                else{ //no AimID in rule
                    Log.d(TAG, "No aimID used in regex");
                    if(MyRegex.doRegex(scannedData, r.regex, r.replace, buffer, r.global)) {
                        bMatchedFound=true;
                        formattedOutput=buffer.toString();
                        Log.d(TAG, String.format("Matched rule: %s", r.toString()));
                        if(r.stop)
                            break;
                        else{
                            Log.d(TAG, "'No stop rule' matched. Continue with next rule...");
                            scannedData=formattedOutput;
                        }
                    }
                    else{
                        Log.d(TAG, String.format("Regex did not match for %s", scannedData));
                    }
                }//if AimID.length>0
            }// end of rule is valid
            else{
                Log.d(TAG, String.format("Not a valid rule: %s", r.toString()));
            }
        } //for loop end

        if(bMatchedFound)
            Log.d(TAG, String.format("DataEdit replacement: %s=>%s", DataEditUtils.getJavaEscaped(scannedData), DataEditUtils.getJavaEscaped(formattedOutput)));
        else {
            DataEditUtils.doBeepWarn();
            Log.d(TAG, String.format("NO DataEdit replacement: %s=>%s", DataEditUtils.getJavaEscaped(scannedData), DataEditUtils.getJavaEscaped(formattedOutput)));
        }
        //Return the Modified scan result string
        regExResult.append(formattedOutput);
    }

    
    public static boolean doRegex(String sIN, String searchpattern, String replacematch, StringBuilder sOut, boolean global){
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
                        Log.d(TAG, String.format("#### returnString= %s", returnString));
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
                    Log.d(TAG, String.format("#### returnString= %s", returnString));
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

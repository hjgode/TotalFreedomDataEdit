package hsm.demo.totalfreedom;

import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by hjgode on 13.01.2017.
 */

class rule implements Serializable{
    // a rule consist of two or three sections
    // sections are devided by '=>'
    // a two section rule defines the search pattern (regex) and the replacement pattern
    // a three section rule defines the Symbology (aimID) and then the search pattern and the replacement pattern
    // the processing of rules from top to down stops after a rule matches, except for the first or only char of
    // the aimID field is a '+'
    // if the first char of a the aimID field is 'g' or '+g' the find patter is treated as a simple search&replace rule
    /*
    examples:
        "# this is a comment",
        "test(.)(.)(.*)=>($1) $2-$3\n", //\n gives x0A
        "]A0=>(.*)=>$1\n",
        "+g=>\u001D=>FNC1",  //will not stop rule matching as aimID field starts with a '+', will a global search/replace
        "(.*)=>$1\n"

     */

    public String aimID="";         // var to store AimID of rule
    public String regex="";         // the regular expression defining what to search for
    public String replace="";       // the replacement pattern to return
    public boolean valid=false;     // internal to mark invalid rules
    public boolean stop=true;       // var to hold if rule is a 'no-stop-rule'
    public boolean global=false;    // match the pattern in sequence or do a search/replace

    public rule(String sIn){
        if(sIn.startsWith("#")){
            //this is a comment
            regex=sIn;
        }else{
            //no comment
            String[] s=sIn.split("=>");
            Log.d("rule read", "sIn="+sIn+", split.len="+s.length);
            if(s.length==3){
                if(s[0].startsWith("+")){
                    stop=false;
                    aimID=s[0].substring(1);
                }else {
                    aimID = s[0];
                }
                if(s[0].startsWith("g")){
                    global=true;
                    aimID=s[0].substring(1);
                }
                if(s[0].startsWith("+g")){
                    stop=false;
                    global=true;
                    aimID=s[0].substring(2);
                }

                regex=s[1];
                replace=s[2];
                valid=true;
            }
            else if(s.length==2){
                aimID="";
                if(s[0].startsWith("+g")) {
                    global = true;
                    if(s[0].length()>2)
                        aimID = s[0].substring(1);
                    regex=s[1];
                    replace="";
                    valid = true;
                    stop = false;
                }
                else if(s[0].startsWith("g")) {
                    global = true;
                    if(s[0].length()>1)
                        aimID = s[0].substring(1);
                    regex=s[1];
                    replace="";
                    valid = true;
                    stop = true;
                }
                else if(s[0].startsWith("+")) {
                    global = false;
                    if(s[0].length()>1)
                        aimID = s[0].substring(1);
                    regex=s[1];
                    replace="";
                    valid = true;
                    stop = false;
                }
                else {
                    regex = s[0];
                    replace = s[1];
                    valid = true;
                    global = false;
                    stop = true;
                }
            }
        }
        if(regex.length()==0)// || replace.length()==0)
            valid=false;
    }
    @Override
    public String toString(){
        return "'aimID: " + aimID + "', search: '"+DataEditUtils.getJavaEscaped(regex)+ "', replace: '"+DataEditUtils.getJavaEscaped(replace)+ "', valid: "+valid+", global: "+global;
    }

    public String getRawString(){
        StringBuilder sb=new StringBuilder();
        if(!stop)
            sb.append("+");
        if(global)
            sb.append("g");
        if(aimID.length()>0)
            sb.append(aimID);
        if(!stop || global || aimID.length()>0)
            sb.append("=>");
        if(regex.length()>0) {
            sb.append(regex);
            if(!regex.startsWith("#"))
                sb.append("=>");
        }
        sb.append(replace);

        return  sb.toString();
    }

}

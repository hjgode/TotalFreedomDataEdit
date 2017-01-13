package hsm.demo.totalfreedom;

/**
 * Created by hjgode on 13.01.2017.
 */

class rule{
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
    public String aimID="";
    public String regex="";     // the regular expression defining what to search for
    public String replace="";   // the replacement pattern to return
    public boolean valid=false;
    public boolean stop=true;
    public boolean global=false;    //match the pattern in sequence or do a search/replace

    public rule(String sIn){
        if(sIn.startsWith("#")){
            //this is a comment
            regex=sIn;
        }else{
            //no comment
            String[] s=sIn.split("=>");
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
                    global=true;
                    aimID=s[0].substring(2);
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
                global=false;
            }
        }
    }
    @Override
    public String toString(){
        return "'aimID: " + aimID + "', search: '"+DataEditUtils.getJavaEscaped(regex)+ "', replace: '"+DataEditUtils.getJavaEscaped(replace)+ "', valid: "+valid+", global: "+global;
    }
}
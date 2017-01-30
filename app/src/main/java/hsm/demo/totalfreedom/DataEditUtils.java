package hsm.demo.totalfreedom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by hjgode on 12.01.2017.
 */

public class DataEditUtils {

    /*
    public static final String unescapeJava(String input)
    Unescapes any Java literals found in the String. For example, it will turn a sequence of '\' and 'n' into a newline character, unless the '\' is preceded by another '\'.

    Parameters:
    input - the String to unescape, may be null
    Returns:
    a new unescaped String, null if null string input
     */
    static String getJavaEscaped(String s){
        return StringEscapeUtils.escapeJava(s);
    }
    /*
    public static final String escapeJava(String input)
    Escapes the characters in a String using Java String rules.

    Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.)

    So a tab becomes the characters '\\' and 't'.

    The only difference between Java strings and JavaScript strings is that in JavaScript, a single quote and forward-slash (/) are escaped.

    Example:

     input string: He didn't say, "Stop!"
     output string: He didn't say, \"Stop!\"

    Parameters:
    input - String to escape values in, may be null
    Returns:
    String with escaped values, null if null string input
     */
    static String getJavaUnescaped(String s){
        return StringEscapeUtils.unescapeJava(s);
    }

    public static String myCharset(){
        return "UTF-16";
    }
    public static String ruleDelimiter(){
        return ";\r\n";
    }

    public static void updateMTP(File _f, Context _context, String TAG){
        Log.d(TAG, "sending Boradcast about file change to MTP...");
        //make the file visible for PC USB attached MTP
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(_f));
        _context.sendBroadcast(intent);

    }

    public static File getDocumentsStorageDir(String TAG) {
        //Starting with Android 6 you need to set the permissions in Settings-Apps-TotalFreedom-Permissions
        // Get the directory for the user's public pictures directory.
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if (!file.mkdirs()) {
            Log.d(TAG, "Directory not created");
        }
        Log.d(TAG, "getDocumentsStorageDir() return with " + file.toString());
        return file;
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static String getHexedString(String input) {
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


    static byte backslash=(byte)0x5c;

    //[Obsolete]
    public static byte[] getBytesForRule(String s){
        ArrayList<Byte> bytes=new ArrayList<Byte>();
        bytes.add((byte) 0x09);
/*
\t	Insert a tab in the text at this point.
\b	Insert a backspace in the text at this point.
\n	Insert a newline in the text at this point.
\r	Insert a carriage return in the text at this point.
\f	Insert a formfeed in the text at this point.
\'	Insert a single quote character in the text at this point.
\"	Insert a double quote character in the text at this point.
\\	Insert a backslash character in the text at this point.
 */
        for (char c:s.toCharArray()) {
            if(c=='\t'){
                bytes.add(backslash);
                bytes.add((byte)'t');
            }
            else if(c=='\b'){
                bytes.add(backslash);
                bytes.add((byte)'b');
            }
            else if(c=='\n'){
                bytes.add(backslash);
                bytes.add((byte)'n');
            }
            else if(c=='\r'){
                bytes.add(backslash);
                bytes.add((byte)'r');
            }
            else if(c=='\f'){
                bytes.add(backslash);
                bytes.add((byte)'f');
            }
            else if(c=='\''){
                bytes.add(backslash);
                bytes.add((byte)'\'');
            }
            else if(c=='\"'){
                bytes.add(backslash);
                bytes.add((byte)'"');
            }
            else if(c=='\\'){
                bytes.add(backslash);
                bytes.add((byte)'\\');
            }
            else
                bytes.add((byte)c);
        }
        byte[] bytesRet = new byte[bytes.size()];
        int i=0;
        for (byte b:bytes) {
            bytesRet[i]=b;
            i++;
        }
        return bytesRet;
    }
    public static void doBeepWarn(){
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
    }

}

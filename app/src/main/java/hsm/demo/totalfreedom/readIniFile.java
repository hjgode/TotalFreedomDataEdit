package hsm.demo.totalfreedom;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by hjgode on 09.01.2017.
 */

public class ReadIniFile {
    String TAG = "raedIniFile";
    File _directory;
    File _file;// = new File(contextWrapper.getExternalFilesDir(null), "state.txt");
    String _filename;
    Context _context;
    ReadIniFile(Context context, String filename){
        _context=context;
        _filename=filename;
    }

    public String[] getRules(){
        ArrayList<String> _rulesList=new ArrayList<String>();
        try {
            Log.d(TAG, "testing external storage is writeable...");
            if(isExternalStorageWritable()) {
                Log.d(TAG, "external storage writeable");
                _directory = getDocumentsStorageDir();
                Log.d(TAG, "using directory '" + _directory.toString() + "', file='" + _filename.toString()+"'");
                _file = new File(_directory, _filename);
                if(_file.exists()){
                    LineNumberReader linereader=new LineNumberReader(new FileReader(_file));
                    String line="";
                    while ((line = linereader.readLine()) != null) {
                        // do something with the line you just read, e.g.
                        _rulesList.add(line);
                        Log.d(TAG, "read: "+line);
                    }
                }
                Log.d(TAG, "read "+_rulesList.size()+" rule lines");
            }
            else {
                Log.d(TAG, "external storage is NOT writeable!");
                //Toast.makeText(_context, "External storage is not writeable", Toast.LENGTH_LONG);
            }
        }catch(Exception e){
            Log.d("CSV: ", "Exception: " + e.getMessage());
        }

        String[] sReturn=new String[_rulesList.size()];
        for (int i=0; i<_rulesList.size(); i++) {
            sReturn[i]=_rulesList.get(i);
        }
        return sReturn;
    }

    public File getDocumentsStorageDir() {
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
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

}

package hsm.demo.totalfreedom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by E841719 on 09.01.2017.
 */

public class LogToFile {
    String TAG = "logToFile";
    File _directory;
    File _file;// = new File(contextWrapper.getExternalFilesDir(null), "state.txt");
    String _filename;
    Context _context;

    public LogToFile(Context context, String filename){
        _context=context;
        _filename=filename;
        try {
            Log.d(TAG, "testing external storage is writeable...");
            if(isExternalStorageWritable()) {
                Log.d(TAG, "external storage writeable");
                _directory = getDocumentsStorageDir();
                //_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);//  getExternalFilesDir(null);// filepath, Context.MODE_WORLD_READABLE + Context.MODE_APPEND);
                //directory = context.getDir(filepath, Context.MODE_WORLD_READABLE + Context.MODE_APPEND);
                Log.d(TAG, "using directory '" + _directory.toString() + "', file='" + _filename.toString()+"'");
                _file = new File(_directory, _filename);
                //if file already exists, MTP will not show an updated file, so we delete the old one first
                if(_file.exists()) {
                    _file.delete();
                    _file = new File(_directory, _filename);
                }
                updateMTP(_file);
            }
            else {
                Log.d(TAG, "external storage is NOT writeable!");
                Toast.makeText(_context, "External storage is not writeable", Toast.LENGTH_LONG);
            }
        }catch(Exception e){
            Log.d("CSV: ", "Exception: " + e.getMessage());
        }

    }

    public void writeLog(String s){
        try {
            Log.d(TAG, "...will write " + s);
            OutputStream os = new FileOutputStream(_file);
            os.write(s.getBytes(Charset.forName("UTF-8")));
            os.flush();
            os.close();
            updateMTP(_file);
        } catch (IOException e) {
            Log.d(TAG, "Error writing " + _file + ", " + e.getMessage());
        } finally {
        }
    }

    @Override
    protected void finalize (){
        try {
            super.finalize();

        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    void updateMTP(File _f){
        Log.d(TAG, "sending Boradcast about file change to MTP...");
        //make the file visible for PC USB attached MTP
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(_f));
        _context.sendBroadcast(intent);

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

}

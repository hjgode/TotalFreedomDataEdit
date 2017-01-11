package hsm.demo.totalfreedom;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

/**
 * Created by hjgode on 10.01.2017.
 */

public class SaveToFile {
    Context _context;
    String TAG ="SaveToFile";
    String _filename="saved.txt";
    File _directory;
    File _file;// = new File(contextWrapper.getExternalFilesDir(null), "state.txt");
    public SaveToFile(String filename, Context con){
        _context=con;
        _filename=filename;
    }

    boolean saveToFile(String[] strings){
        StringBuilder sb=new StringBuilder();
        for (String s:strings) {
            String t= Pattern.quote(s);
            sb.append(s+";\r\n");
        }
        return saveToFile(sb.toString());
    }

    boolean saveToFile(String string){
        boolean bRet=false;
        try {
            if(isExternalStorageWritable()) {
                Log.d(TAG, "external storage writeable");
                _directory = getDocumentsStorageDir();
                Log.d(TAG, "using directory '" + _directory.toString() + "', file='" + _filename.toString()+"'");
                _file = new File(_directory, _filename);
                FileOutputStream fileOututStream=new FileOutputStream(_file);
                FileChannel fileChannel=fileOututStream.getChannel();
                ByteBuffer byteBuffer = ByteBuffer.wrap(string.getBytes(Charset.forName("UTF-8")));
                fileChannel.write(byteBuffer);
                //byteBuffer.flip();
                fileOututStream.flush();
                fileChannel.close();
                fileChannel=null;
                updateMTP(_file);
                bRet=true;
            }
        }catch (Exception e){
            Log.d(TAG, "Exception: " + e.getMessage());
        }
        return bRet;
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
    void updateMTP(File _f){
        Log.d(TAG, "sending Boradcast about file change to MTP...");
        //make the file visible for PC USB attached MTP
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(_f));
        _context.sendBroadcast(intent);

    }}

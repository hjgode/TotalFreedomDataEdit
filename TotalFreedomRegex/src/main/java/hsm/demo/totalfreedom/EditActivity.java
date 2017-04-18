package hsm.demo.totalfreedom;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import static android.R.id.list;
import static android.R.id.switchInputMethod;
import static hsm.demo.totalfreedom.AimId.AimIds;

public class EditActivity extends AppCompatActivity
        //implements View.OnClickListener
        {

    rule theRule;
    final String TAG="EditActivity";
    int iPosition=-1;
    Spinner aimIdList;
    CheckBox chkGlobal, chkNoStop;
    EditText txtAimID, txtRegex, txtReplace;
    EditText currentEditText=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //To pass:
        //intent.putExtra("MyClass", obj);
        theRule = (rule) getIntent().getSerializableExtra("theRule");
        iPosition=getIntent().getIntExtra("position", -1);

        Log.d(TAG, "received rule: "+theRule.toString());

        chkGlobal=(CheckBox)findViewById(R.id.checkBox_Global);
        chkGlobal.setChecked(theRule.global);

        chkNoStop=(CheckBox)findViewById(R.id.checkBox_NoStop);
        chkNoStop.setChecked(!theRule.stop);

        txtAimID=(EditText) findViewById(R.id.editText_AimID);
        aimIdList=(Spinner)findViewById(R.id.aimIDlist);

        //set AimID to rule's AimID
        txtAimID.setText(theRule.aimID);
//        txtAimID.setOnClickListener(this);
        txtAimID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    currentEditText=txtAimID;
            }
        });

        //fill list
        ArrayAdapter<AimId> dataAdapter = new ArrayAdapter<AimId>(this, android.R.layout.simple_spinner_item, AimIds);
        dataAdapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
        aimIdList.setAdapter(dataAdapter);
        //already sets
        aimIdList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AimId a= AimIds[position];
                txtAimID.setText(a.aimid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //sroll to AimID in list if set
        String sAimID=txtAimID.getText().toString();
        if(sAimID.length()>0) {
            int iPos=-1;
            for (int x=0; x < AimIds.length; x++) {
                if(sAimID.equals(AimIds[x].aimid)) {
                    iPos=x;
                    break;
                }
            }
            if(iPos!=-1)
                aimIdList.setSelection(iPos);
        }

        txtRegex=(EditText)findViewById(R.id.editText_SearchFor);
        txtRegex.setText(DataEditUtils.getJavaEscaped(theRule.regex));
//        txtRegex.setOnClickListener(this);
        txtRegex.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                currentEditText=txtRegex;
            }
        });

        txtReplace=(EditText)findViewById(R.id.editText_ReplaceWith);
        txtReplace.setText(DataEditUtils.getJavaEscaped(theRule.replace));
//        txtReplace.setOnClickListener(this);
        txtReplace.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                currentEditText=txtReplace;
            }
        });

        Button btnOK = (Button)findViewById(R.id.button_Save);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theRule.global=((CheckBox)findViewById(R.id.checkBox_Global)).isChecked();
                theRule.stop=!((CheckBox)findViewById(R.id.checkBox_NoStop)).isChecked();
                theRule.aimID=((EditText)findViewById(R.id.editText_AimID)).getText().toString();

                String regexStr=txtRegex.getText().toString();
                //internally we use unescaped JAVA
                theRule.regex=DataEditUtils.getJavaUnescaped (regexStr);
                //theRule.regex=regexStr;

                String replaceStr=txtReplace.getText().toString();
                theRule.replace=DataEditUtils.getJavaUnescaped (replaceStr);
                //theRule.replace=replaceStr;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("theRule", theRule);
                resultIntent.putExtra("position", iPosition);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        Button btnCancel=(Button)findViewById(R.id.button_Cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, new Intent());
                finish();
            }
        });

    }


//    public void onClick(View v){
//        switch(v.getId()){
//            case R.id.editText_AimID:
//                currentEditText=(EditText)v;
//                break;
//            case R.id.editText_SearchFor:
//                currentEditText=(EditText)v;
//                break;
//            case R.id.editText_ReplaceWith:
//                currentEditText=(EditText)v;
//                break;
//        }
//    }

    //onClick has been defined in layout file
    public void perform_onInsertClick(View v){
        TextView tv= (TextView) v;
        switch(tv.getId()) {
            case R.id.textAnyChar:
                insertText(currentEditText, ".");
                break;
            case R.id.textAnyDigit:
                insertText(currentEditText, "\\d");
                break;
            case R.id.textZeroOrMany:
                insertText(currentEditText, "*");
                break;
            case R.id.textOneOrMany:
                insertText(currentEditText, "+");
                break;
            case R.id.textNTimes:
                insertText(currentEditText, "{n}");
                break;
            //Groups
            case R.id.textGroupAnyChar:
                insertText(currentEditText, "(.)");
                break;
            case R.id.textGroupAnyDigit:
                insertText(currentEditText, "(\\d)");
                break;
            case R.id.textGroupZeroOrMany:
                insertText(currentEditText, "(.*)");
                break;
            case R.id.textGroupOneOrMany:
                insertText(currentEditText, "(.+)");
                break;
            case R.id.textGrouppNTimes:
                insertText(currentEditText, "(.{n})");
                break;
        }
    }

    void insertText(EditText editText, String sInsert){
        int start=Math.max(editText.getSelectionStart(),0);
        int end=Math.max(editText.getSelectionEnd(),0);
        editText.getText().replace(
                Math.min(start,end),
                Math.max(start,end),
                sInsert,
                0,
                sInsert.length());
    }
}

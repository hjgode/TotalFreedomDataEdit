package hsm.demo.totalfreedom;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import static android.R.id.list;

public class EditActivity extends AppCompatActivity {

    rule theRule;
    final String TAG="EditActivity";
    int iPosition=-1;
    EditText editAim;
    Spinner aimIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        //To pass:
        //intent.putExtra("MyClass", obj);
        theRule = (rule) getIntent().getSerializableExtra("theRule");
        iPosition=getIntent().getIntExtra("position", -1);

        Log.d(TAG, "received rule: "+theRule.toString());

        ((CheckBox)findViewById(R.id.checkBox_Global)).setChecked(theRule.global);
        ((CheckBox)findViewById(R.id.checkBox_NoStop)).setChecked(!theRule.stop);

        editAim=(EditText) findViewById(R.id.editText_AimID);
        editAim.setText(theRule.aimID);

        ((EditText)findViewById(R.id.editText_SearchFor)).setText(DataEditUtils.getJavaEscaped(theRule.regex));
        ((EditText)findViewById(R.id.editText_ReplaceWith)).setText(DataEditUtils.getJavaEscaped(theRule.replace));

        Button btnOK = (Button)findViewById(R.id.button_Save);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                theRule.global=((CheckBox)findViewById(R.id.checkBox_Global)).isChecked();
                theRule.stop=!((CheckBox)findViewById(R.id.checkBox_NoStop)).isChecked();
                theRule.aimID=((EditText)findViewById(R.id.editText_AimID)).getText().toString();

                theRule.regex=DataEditUtils.getJavaUnescaped (((EditText)findViewById(R.id.editText_SearchFor)).getText().toString());
                theRule.replace=DataEditUtils.getJavaUnescaped (((EditText)findViewById(R.id.editText_ReplaceWith)).getText().toString());
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
        aimIdList=(Spinner)findViewById(R.id.aimIDlist);
        //fill list
        ArrayAdapter<AimId> dataAdapter = new ArrayAdapter<AimId>(this, android.R.layout.simple_spinner_item, AimId.AimIds);
        dataAdapter.setDropDownViewResource(R.layout.multiline_spinner_dropdown_item);
        aimIdList.setAdapter(dataAdapter);
        aimIdList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AimId a=AimId.AimIds[position];
                editAim.setText(a.aimid);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}

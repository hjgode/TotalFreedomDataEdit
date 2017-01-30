package hsm.demo.totalfreedom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.util.ArrayList;

public class RuleListActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    Button btnSave, btnAdd;
    ListView myListView;
    ArrayList<rule> rules=new ArrayList<>();
    CustomRuleAdapter ruleAdapter;
    ListView rulesListView;
    final String TAG="ListActivity";
    private static int pos=-1;
    final int UPDATE_RULE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ReadIniFile readIniFile = new ReadIniFile(this, "dataedit_regex.ini");
        String[] rStrings=readIniFile.getRules();
        for (String s:rStrings) {
            rules.add(new rule(s));
        }

        //buttons
        btnAdd=(Button)findViewById(R.id.btnAdd);
        btnSave =(Button)findViewById(R.id.btnSaveListActivity);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rule theRule=new rule("(.*)=>$1");
                rules.add(theRule);
                ruleAdapter.updateAdapter(rules);
                int newpos=ruleAdapter.getCount()-1;
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                i.putExtra("theRule", rules.get(newpos));
                i.putExtra("position", newpos);
                startActivityForResult(i, UPDATE_RULE_REQUEST);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges(getApplicationContext());
                finish();
            }
        });
        //rules ArrayList with data
        // instantiate the custom list adapter
        ruleAdapter=new CustomRuleAdapter(this, rules);
        // get the ListView and attach the adapter
        rulesListView=(ListView)findViewById(R.id.list_view_rules);
        rulesListView.setAdapter(ruleAdapter);


        rulesListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick (AdapterView < ? > adapter, View view, int position, long arg){
                    rule theRule=rules.get(position);
                    Log.d(TAG, "Clicked on "+theRule.regex+", "+theRule.replace+" at pos="+position);
                    pos=position;
                    showPopupMenu(view);
//                    Intent i = new Intent(getApplicationContext(), EditActivity.class);
//                    i.putExtra("theRule", theRule);
//                    i.putExtra("position", position);
//                    startActivityForResult(i, UPDATE_RULE_REQUEST);
//                    TextView v = (TextView) view.findViewById(R.id.textPatternMatchLookFor);
//                    Toast.makeText(getApplicationContext(), "selected Item Name is " + v.getText(), Toast.LENGTH_LONG).show();
                }
            }
        );
//        rulesListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                pos = position;
//                return false;
//            }
//        });
    }//onCreate

    void saveChanges(Context c){
        //
        SaveToFile saveMe = new SaveToFile("dataedit_regex.ini", this);
        saveMe.saveToFile(rules);
        Toast.makeText(getApplicationContext(), "Data saved", Toast.LENGTH_LONG);
    }

    public void showPopupMenu(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.rule_list_popup, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(this);
    }

    public boolean onMenuItemClick(MenuItem item){
        rule rule_tomove;
        rule rule_saved;
        switch(item.getItemId()){
            case R.id.menu_edit_rule:
//                Toast.makeText(getBaseContext(), "edit "+rules.get(pos), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                i.putExtra("theRule", rules.get(pos));
                i.putExtra("position", pos);
                startActivityForResult(i, UPDATE_RULE_REQUEST);
                return true;
            case R.id.menu_delete_rule:
                Toast.makeText(getBaseContext(), "delete "+rules.get(pos), Toast.LENGTH_SHORT).show();
                deleteRule(pos);
                return true;
            case R.id.menu_pushup_rule:
                Toast.makeText(getBaseContext(), "push up "+rules.get(pos), Toast.LENGTH_SHORT).show();
                moveRule(pos, -1);
                return true;
            case R.id.menu_pushdown_rule:
                Toast.makeText(getBaseContext(), "push down "+rules.get(pos), Toast.LENGTH_SHORT).show();
                moveRule(pos, +1);
                return true;
            case R.id.menu_dismiss_rule:
                return true;
            default: return false;
        }
    }

    // move rule up (+1) or down (-1)
    void moveRule(int old, int dir){
        if((old + dir < 0) ||
           (old + dir > rules.size()-1)) {
            return;
        }
        rule rule_tomove;
        rule rule_saved;
        rule_tomove=rules.get(pos);
        rule_saved=rules.get(pos+dir);
        rules.set(pos+dir, rule_tomove);
        rules.set(pos, rule_saved);
        ruleAdapter.updateAdapter(rules);
    }

    void deleteRule(final int position) {
        //ask before delete!?
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String title = getResources().getString(R.string.txtYesNoDialogTitle);
        String txtYes = getResources().getString(R.string.txtYesNoDialogYesButtonText);
        String txtNo = getResources().getString(R.string.txtYesNoDialogNoButtonText);
        String text = getResources().getString(R.string.txtYesNoDialogText);
        builder.setTitle(title);
        builder.setMessage(text + "\r\n" + rules.get(position));

        builder.setPositiveButton(txtYes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                rules.remove(position);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.txtMessageYesDeleted), Toast.LENGTH_LONG);
                ruleAdapter.updateAdapter(rules);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton(txtNo, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.txtMessageNoDelete), Toast.LENGTH_LONG);
                // Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == UPDATE_RULE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                rule r = (rule)data.getSerializableExtra("theRule");
                int p = data.getIntExtra("position", -1);
                Log.d(TAG, "onActivityResult: "+r.toString()+" pos="+p);

                //update the list
                rules.set(p,r);
                ruleAdapter.updateAdapter(rules);
            }
        }
    }}

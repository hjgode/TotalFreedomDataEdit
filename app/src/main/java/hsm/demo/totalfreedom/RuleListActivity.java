package hsm.demo.totalfreedom;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.R.attr.data;

public class RuleListActivity extends AppCompatActivity {
    ListView myListView;
    ArrayList<rule> rules=new ArrayList<>();
    CustomRuleAdapter ruleAdapter;
    final String TAG="ListActivity";

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

        //rules ArrayList with data
        // instantiate the custom list adapter
        ruleAdapter=new CustomRuleAdapter(this, rules);
        // get the ListView and attach the adapter
        ListView rulesList=(ListView)findViewById(R.id.list_view_rules);
        rulesList.setAdapter(ruleAdapter);


        rulesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick (AdapterView < ? > adapter, View view, int position, long arg){
                    rule theRule=rules.get(position);
                    Log.d(TAG, "Clicked on "+theRule.toString());
                    Intent i = new Intent(getApplicationContext(), EditActivity.class);
                    i.putExtra("theRule", theRule);
                    i.putExtra("position", position);
                    startActivityForResult(i, UPDATE_RULE_REQUEST);
//                    TextView v = (TextView) view.findViewById(R.id.textPatternMatchLookFor);
//                    Toast.makeText(getApplicationContext(), "selected Item Name is " + v.getText(), Toast.LENGTH_LONG).show();
                }
            }
        );
        rulesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                rule theRule=rules.get(position);
                Log.d(TAG, "Clicked on "+theRule.toString());
                Intent i = new Intent(getApplicationContext(), EditActivity.class);
                i.putExtra("theRule", theRule);
                i.putExtra("position", position);
                startActivityForResult(i, UPDATE_RULE_REQUEST);
//                    TextView v = (TextView) view.findViewById(R.id.textPatternMatchLookFor);
//                    Toast.makeText(getApplicationContext(), "selected Item Name is " + v.getText(), Toast.LENGTH_LONG).show();
                return false;
            }
        });
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

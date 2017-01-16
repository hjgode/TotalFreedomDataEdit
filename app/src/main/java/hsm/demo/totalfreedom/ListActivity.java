package hsm.demo.totalfreedom;

import android.support.annotation.RequiresPermission;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    ListView simpleList;
    ArrayList<rule> rules=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ReadIniFile readIniFile = new ReadIniFile(this, "dataedit_regex.ini");
        String[] rStrings=readIniFile.getRules();
        for (String s:rStrings) {
            rules.add(new rule(s));
        }

        try {
//            ArrayAdapter<rule> adapter=new ArrayAdapter<rule>(this, android.R.layout.simple_list_item_1, rules);
//            ListView listView1=(ListView)findViewById(R.id.listView1);
//            listView1.setAdapter(adapter);
//            simpleList.setAdapter(adapter);
//            RuleAdapter ruleAdapter = new RuleAdapter(this, rules);
//            simpleList = (ListView) findViewById(R.id.rulesListView);
//            //ruleListAdapter adapter=new ruleListAdapter(this, R.layout.rule_list_item, rules);
//            simpleList.setAdapter(ruleAdapter);
        }catch(Exception e){
            Log.d("Exception: ", e.getMessage());
        }
    }
}

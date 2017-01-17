package hsm.demo.totalfreedom;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ListView myListView;
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

        //rules ArrayList with data
        // instantiate the custom list adapter
        customRuleAdapter ruleAdapter=new customRuleAdapter(this, rules);
        // get the ListView and attach the adapter
        ListView rulesList=(ListView)findViewById(R.id.list_view_rules);
        rulesList.setAdapter(ruleAdapter);

        // Setup the data source
        ArrayList<Item> itemsArrayList=new ArrayList<>();
        itemsArrayList.add(new Item("name1","description1"));
        itemsArrayList.add(new Item("name2","description2"));// generateItemsList(); // calls function to get items list
        // instantiate the custom list adapter
        CustomListAdapter adapter = new CustomListAdapter(this, itemsArrayList);
        // get the ListView and attach the adapter
        ListView itemsListView  = (ListView) findViewById(R.id.list_view_items);
        itemsListView.setAdapter(adapter);


    }
}

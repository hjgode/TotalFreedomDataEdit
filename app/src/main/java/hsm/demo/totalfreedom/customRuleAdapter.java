package hsm.demo.totalfreedom;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hjgode on 16.01.2017.
 */

public class CustomRuleAdapter extends BaseAdapter {

    final String TAG="RuleAdapter";
    Context context;
    ArrayList<rule> rules;

    public CustomRuleAdapter(Context c, ArrayList<rule> items) {
        Log.d(TAG, "RuleAdapter init()...");
        context=c;
        rules = items;
    }

    public void updateAdapter(ArrayList<rule> newList){
        rules=newList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount () {
        Log.d(TAG, "getCount()="+rules.size());
        return rules.size();
    }

    @Override
    public long getItemId (int position) {
        Log.d(TAG, "getItemId(position) "+position);
        return position;
    }

    @Override
    public Object getItem (int position) {
        Log.d(TAG, "getItem() "+position);
        return rules.get(position);
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "getView()...");
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.rule_list_item, parent, false);
        }

        // get current item to be displayed
        rule currentItem = (rule) getItem(position);

        // get the TextView and checkBoxes for item name and item description
        CheckBox chkNoStop=(CheckBox)convertView.findViewById(R.id.chkNoStopRule);
        CheckBox chkGlobal=(CheckBox)convertView.findViewById(R.id.chkGlobalSR);

        TextView textViewAimID = (TextView)convertView.findViewById(R.id.textAimID);
        TextView textViewRegex = (TextView)convertView.findViewById(R.id.textPatternMatchLookFor);
        TextView textViewReplace = (TextView)convertView.findViewById(R.id.textReplacementPattern);

        //sets the text and checkboxes for item from the current item object
        chkNoStop.setChecked(!currentItem.stop);
        chkGlobal.setChecked(currentItem.global);

        textViewAimID.setText(currentItem.aimID);
        textViewRegex.setText(DataEditUtils.getJavaEscaped(currentItem.regex));
        textViewReplace.setText(DataEditUtils.getJavaEscaped(currentItem.replace));

        // returns the view for the current row
        return convertView;
    }
}

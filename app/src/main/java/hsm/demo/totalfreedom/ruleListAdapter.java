package hsm.demo.totalfreedom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by E841719 on 16.01.2017.
 */

public class ruleListAdapter extends ArrayAdapter<rule> {

    //private final rule[] _rules;
    ArrayList<rule> rulesList=new ArrayList<>();

    public ruleListAdapter(Context context, int textViewResourceID, ArrayList<rule> objects){
        super(context, textViewResourceID, objects);
        rulesList=objects;
    }
    @Override
    public int getCount() {
        return rulesList.size();
//        return super.getCount();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.rule_list_item, null);

        CheckBox checkBox=(CheckBox) v.findViewById(R.id.chkGlobalSR);
        checkBox.setChecked(rulesList.get(position).global);
        CheckBox checkBox1=(CheckBox) v.findViewById(R.id.chkNoStopRule);
        checkBox1.setChecked(rulesList.get(position).stop);

        TextView textView = (TextView) v.findViewById(R.id.textAimID);
        textView.setText(rulesList.get(position).aimID);

        TextView textView1 = (TextView) v.findViewById(R.id.textPatternMatchLookFor);
        textView1.setText(rulesList.get(position).regex);

        TextView textView2 = (TextView) v.findViewById(R.id.textReplacementPattern);
        textView2.setText(rulesList.get(position).replace);

        return v;

    }

}

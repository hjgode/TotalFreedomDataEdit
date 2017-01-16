package hsm.demo.totalfreedom;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by E841719 on 16.01.2017.
 */

public class RuleAdapter extends BaseAdapter {
    private List<rule> mItems;
    //private LayoutInflater mInflater;
    final String TAG="RuleAdapter";
    Context context;
    View listView;

    public RuleAdapter (Context c, List<rule> items) {
        Log.d(TAG, "RuleAdapter init()...");
        mItems = items;
        context=c;
        //Cache a reference to avoid looking it up on every getView() call
//        mInflater = LayoutInflater.from(c);
    }

    @Override
    public int getCount () {
        Log.d(TAG, "getCount()="+mItems.size());
        return mItems.size();
    }

    @Override
    public long getItemId (int position) {
        Log.d(TAG, "getItemId(position) "+position);
        return position;
    }

    @Override
    public Object getItem (int position) {
        Log.d(TAG, "getItem() "+position);
        return mItems.get(position);
    }

    @Override
    public View getView (int position, View view, ViewGroup parent) {
        Log.d(TAG, "getView()...");
        //If there's no recycled view, inflate one and tag each of the views
        //you'll want to modify later
        View row;
        //find the custom list item view
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);// .inflate (R.layout.rule_list_item, parent, false);
        if(view==null){
            listView=new View(context);
            listView=layoutInflater.inflate(R.layout.rule_list_item,parent,false);
        }

        //This assumes layout/row_left.xml includes a TextView with an id of "textview"
//        row.setTag (R.id.textAimID, convertView.findViewById(R.id.textAimID));

        rule item= (rule)this.getItem(position);
        //Retrieve the tagged view, get the item for that position, and
        //update the text
        TextView textView = (TextView) listView.findViewById(R.id.textAimID);

        String textItem = item.aimID;
        textView.setText(textItem);

        return view;
    }
}

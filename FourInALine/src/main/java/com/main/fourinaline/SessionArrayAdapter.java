package com.main.fourinaline;

import java.util.ArrayList;

import com.online.Session;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * http://www.vogella.com/articles/AndroidListView/article.html#listview_listviewexample
 * @author Richard GUERCI & Julien SIERRA
 *
 */
public class SessionArrayAdapter extends ArrayAdapter<Session> {
	private final Context context;
	private ArrayList<Session> values;

	public SessionArrayAdapter(Context context, ArrayList<Session> values) {
		super(context, R.layout.row_layout, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout, parent, false);
		
		TextView textView1 = (TextView) rowView.findViewById(R.id.label1);
		textView1.setText(values.get(position).getName());

		TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
		textView2.setText(" hosted by "+values.get(position).getPlayer2());

		return rowView;
	}
}

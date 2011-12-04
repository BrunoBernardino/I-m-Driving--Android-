package com.wozia.nophonezone;

import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;


public class PrivateGroup extends ListActivity {
	static DataHelper dh;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        dh = new DataHelper(getApplicationContext());
        
        List<String> pvtGroup = dh.getAllContacts();
        
        setListAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.list_pvt_grp, pvtGroup));

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);

        lv.setOnItemClickListener(new PvtUserClicked());

	}

	class PvtUserClicked implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//Toast.makeText(getApplicationContext(), ((TextView) view).getText() + " :: " + position, Toast.LENGTH_SHORT).show();
			setResult(Activity.RESULT_OK);
			SettingsActivity.selectedUsr = position;
			finish();

		}
	}

}
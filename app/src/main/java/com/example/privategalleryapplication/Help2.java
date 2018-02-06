package com.example.privategalleryapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adapter.ExpandableListAdapterMine;

public class Help2 extends AppCompatActivity {


    //String[] featuresArray = {"Import images from phone gallery to ‘My Private Gallery’","Export images from ‘My Private Gallery’ back to phone gallery","Change Shared Password"};
    ExpandableListAdapterMine listAdapter;
    ExpandableListAdapterMine listAdapter2;
    ExpandableListView expListView;
    ExpandableListView expListView2;
    List<String> listDataHeader;
    List<String> listDataHeader2;
    HashMap<String, List<String>> listDataChild;
    HashMap<String, List<String>> listDataChild2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help2);

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);
        prepareListData();
        listAdapter = new ExpandableListAdapterMine(this, listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView2 = (ExpandableListView) findViewById(R.id.expandableListView2);
        prepareListData2();
        listAdapter2 = new ExpandableListAdapterMine(this, listDataHeader2, listDataChild2);
        expListView2.setAdapter(listAdapter2);

//        ArrayAdapter adapter = new ArrayAdapter<String>(this,
//                R.layout.activity_listview, featuresArray);
//
//        ListView listView = (ListView) findViewById(R.id.listView);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(position==0){
//                    Toast.makeText(getApplicationContext(), "Select 'Import From Gallery' from the menu. Then select the images that you want to be loaded to from phone gallery to 'My Private Gallery'!",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        });
    }

    private void prepareListData(){
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Import images from phone gallery to ‘My Private Gallery’");
        listDataHeader.add("Export images from ‘My Private Gallery’ back to phone gallery");
        listDataHeader.add("Change Shared Password");

        List<String> optionA = new ArrayList<String>();
        optionA.add("Select 'Import From Gallery' from the menu. Then select the images that you want to be loaded to from phone gallery to 'My Private Gallery'!");

        List<String> optionB = new ArrayList<String>();
        optionB.add("Select the images that you want to move back to phone gallery by long pressing respective image icons.Then select 'Export To Gallery' from the menu");

        List<String> optionC = new ArrayList<String>();
        optionC.add("You can change your Shared Password by selecting 'Change Shared Password' from the menu.You'll be required to enter your current and new password");

        listDataChild.put(listDataHeader.get(0), optionA); // Header, Child data
        listDataChild.put(listDataHeader.get(1), optionB);
        listDataChild.put(listDataHeader.get(2), optionC);
    }

    private void prepareListData2(){
        listDataHeader2 = new ArrayList<String>();
        listDataChild2 = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader2.add("Show or hide locks on Secret Gallery images");
        listDataHeader2.add("Move images from Shared Gallery to Secret Gallery");
        listDataHeader2.add("Move images from Secret Gallery to Shared Gallery");
        listDataHeader2.add("Change Secret Password");

        List<String> optionA = new ArrayList<String>();
        optionA.add("You can hide or show the yellow locks that appear on images that are in Secret Gallery.Check or uncheck 'Show Locks' option in the menu to show or hide locks");

        List<String> optionB = new ArrayList<String>();
        optionB.add("Select the images that you want to move from Shared Gallery to Secret Gallery by long pressing respective image icons.Then select 'Move to Secret' from the menu");

        List<String> optionC = new ArrayList<String>();
        optionC.add("Select the images that you want to move from Secret Gallery to Shared Gallery by long pressing respective image icons.Then select 'Move to Shared' from the menu");

        List<String> optionD = new ArrayList<String>();
        optionD.add("You can change your Secret Password by selecting 'Change Secret Password' from the menu.You'll be required to enter your current and new password");


        listDataChild2.put(listDataHeader2.get(0), optionA); // Header, Child data
        listDataChild2.put(listDataHeader2.get(1), optionB);
        listDataChild2.put(listDataHeader2.get(2), optionC);
        listDataChild2.put(listDataHeader2.get(3), optionD);
    }
}

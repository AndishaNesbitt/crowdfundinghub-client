package edu.psu.bd.csse.crowdfundinghubclient;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import edu.psu.bd.csse.crowdfundinghubclient.model.Campaign;
import edu.psu.bd.csse.crowdfundinghubclient.model.DbHandler;

public class MainActivity extends AppCompatActivity {

    private static DbHandler db;
    private static String order = DbHandler.ORDER_BY_PERCENT_ASC;
    private static ViewPager mViewPager;
    private static CampaignBrowseFragment campaignBrowseFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //browseController = new BrowseController(this);
        db = new DbHandler(this, null, null, 1);

        // find our toolbar view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // replace the ActionBar with our toolbar
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout and integrate it with the ViewPager
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.optionAmtRaisedLow:
                order = DbHandler.ORDER_BY_AMT_RAISED_ASC;
                break;
            case R.id.optionAmtRaisedHigh:
                order = DbHandler.ORDER_BY_AMT_RAISED_DESC;
                break;
            case R.id.optionPercentCompleteLow:
                order = DbHandler.ORDER_BY_PERCENT_ASC;
                break;
            case R.id.optionPercentCompleteHigh:
                order = DbHandler.ORDER_BY_PERCENT_DESC;
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        mViewPager.getAdapter().notifyDataSetChanged();
        return true;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a CampaignBrowseFragment (defined as a static inner class below).

            campaignBrowseFragment = new CampaignBrowseFragment();
            Bundle args = new Bundle();
            // tell the fragment which type of campaigns to load for the tab section
            args.putInt("section_number", position + 1);
            // tell the fragment which order the campaigns should be listed
            args.putString("order_clause", order);
            // apply the arguments for the fragment
            campaignBrowseFragment.setArguments(args);

            return campaignBrowseFragment;
        }

        @Override
        public int getItemPosition(Object object) {
            // this method is called when notifyDataSetChange() is called
            // happens when a user changes the order of the campaigns via the options menu

            // get the current fragment in the section
            campaignBrowseFragment = (CampaignBrowseFragment) object;
            // reset the order_clause argument for the fragment to change the order of list
            campaignBrowseFragment.getArguments().putString("order_clause", order);

            // POSITION_NONE means that the Fragment must be recreated to apply changes
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab_title_earn);
                case 1:
                    return getString(R.string.tab_title_give);
                case 2:
                    return getString(R.string.tab_title_invest);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing the list view.
     */
    public static class CampaignBrowseFragment extends Fragment {

        // The fragment argument representing the section number for this fragment
        private static final String ARG_SECTION_NUMBER = "section_number";
        // The fragment argument representing the order clause to display campaigns
        private static final String ARG_ORDER_CLAUSE = "order_clause";

        // references the ListView for campaigns, inflated within our Browse Layout XML
        private static ListView campaignListView;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static CampaignBrowseFragment newInstance(int sectionNumber, String orderClause) {
            CampaignBrowseFragment fragment = new CampaignBrowseFragment();

            Bundle args = new Bundle();
            // pass section number so we know what campaigns to load into the list view
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString(ARG_ORDER_CLAUSE, orderClause);
            fragment.setArguments(args);

            return fragment;
        }

        public CampaignBrowseFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // inflate our browse view that contains a ListView for campaign listings
            View rootView = inflater.inflate(R.layout.fragment_campaign_browse, container, false);
            campaignListView = (ListView)rootView.findViewById(R.id.campaignListView);
            
            // get campaigns from the database for the tab section (query based on campaign type)
            final List<Campaign> campaigns = db.getCampaigns(getArguments().getInt(ARG_SECTION_NUMBER),
                    getArguments().getString(ARG_ORDER_CLAUSE));

            // use custom List adapter to populate ListView
            CampaignListAdapter adapter = new CampaignListAdapter(getContext(), campaigns);
            campaignListView.setAdapter(adapter);

            // ItemClickListener for when a user taps a list item
            campaignListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // get the selected item as a Campaign model
                    Campaign campaignSelected = (Campaign) parent.getAdapter().getItem(position);

                    // Create a new intent, we are starting an activity to view the selected campaign's webpage
                    Intent intent = new Intent(getContext(), WebActivity.class);
                    // Init a bundle so that we can pass the selected campaign's URL to the new activity
                    Bundle extras = new Bundle();
                    // add the campaign URL to the bundle with key, "url"
                    extras.putString("url", campaignSelected.getUrl());
                    intent.putExtras(extras);
                    // now start the WebActivity
                    startActivity(intent);
                }
            });

            // OnLongItemClickListener for when a user holds down on a campaign list item
            campaignListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Campaign campaignSelected = (Campaign) parent.getAdapter().getItem(position);

                    // create popup dialog that will appear with campaign details
                    final Dialog popupDialog = new Dialog(getContext());

                    popupDialog.setCancelable(true); // this dialog can be canceled with BACK key
                    popupDialog.setCanceledOnTouchOutside(true); // cancel when user touches outside dialog
                    //popupDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title bar
                    popupDialog.setTitle(campaignSelected.getTitle());

                    // set the popup's layout to our custom layout for campaign details
                    popupDialog.setContentView(R.layout.popup_stats_dialog);

                    // find and update popup dialog views
                    TextView percentComplete = (TextView) popupDialog.findViewById(R.id.percentComplete);
                    percentComplete.setText("Percent Complete: " + campaignSelected.getPercentComplete() + "%");

                    TextView amountRaised = (TextView) popupDialog.findViewById(R.id.amountRaised);
                    amountRaised.setText("Raised: $" + campaignSelected.getAmountRaised());

                    // show the dialog windows with selected campaign details
                    popupDialog.show();

                    return true;
                }
            });

            return rootView;
        }
    }
}

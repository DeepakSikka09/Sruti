package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.ToDoListViewModel;
import in.ecomexpress.sruti.utils.common_files.Constants;


// Eclipse wanted me to use a sparse array instead of my hashmaps, I just suppressed that suggestion
@SuppressLint("UseSparseArrays")
public class ExpListViewAdapterWithCheckbox extends BaseExpandableListAdapter {

    // Define activity context
    private Context mContext;

    /*
     * Here we have a Hashmap containing a String key
     * (can be Integer or other type but I was testing
     * with contacts so I used contact name as the key)
    */

    private static final String TAG = ExpListViewAdapterWithCheckbox.class.getName();
    private Context _context;
    public static final Integer MANIFEST_TYPE = 0, MANIFEST_STATUS = 1;
    ToDoListViewModel toDoListViewModel;
    List<Manifest_List> commonDRSListItemList = new ArrayList<>();

    private HashMap<String, List<String>> _listDataChild;


    // ArrayList that is what each key in the above
    // hashmap points to
    private ArrayList<String> _listDataHeader;

    // Hashmap for keeping track of our checkbox check states
    private final HashMap<Integer, boolean[]> mChildCheckStates;

    // Our getChildView & getGroupView use the viewholder patter
    // Here are the viewholders defined, the inner classes are
    // at the bottom
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    /*
          *  For the purpose of this document, I'm only using a single
     *	textview in the group (parent) and child, but you're limited only
     *	by your XML view for each group item :)
    */
    private String groupText;
    private String childText;

    /*  Here's the constructor we'll use to pass in our calling
     *  activity's context, group items, and child items
    */

    public ExpListViewAdapterWithCheckbox(Context context, ArrayList<String> listDataGroup,
                                          HashMap<String, List<String>> listDataChild, ToDoListViewModel toDoListViewModel, List<Manifest_List> mcommonDRSListItemList) {
        this._context = context;
        this._listDataHeader = listDataGroup;
        this._listDataChild = listDataChild;
        this.toDoListViewModel = toDoListViewModel;
        this.commonDRSListItemList = mcommonDRSListItemList;
        // Initialize our hashmap containing our check states here
        mChildCheckStates = new HashMap<Integer, boolean[]>();

    }


    @Override
    public int getGroupCount() {
        return _listDataHeader.size();
    }

    /*
     * This defaults to "public object getGroup" if you auto import the methods
     * I've always make a point to change it from "object" to whatever item
     * I passed through the constructor
    */
    @Override
    public String getGroup(int groupPosition) {
        return _listDataHeader.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        //  I passed a text string into an activity holding a getter/setter
        //  which I passed in through "ExpListGroupItems".
        //  Here is where I call the getter to get that text
        groupText = getGroup(groupPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandableview, null);


            // Initialize the GroupViewHolder defined at the bottom of this document
            groupViewHolder = new GroupViewHolder();

            groupViewHolder.mGroupText = convertView.findViewById(R.id.lblListHeader);

            convertView.setTag(groupViewHolder);

        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }

        groupViewHolder = (GroupViewHolder) convertView.getTag();
        groupViewHolder.mGroupText.setText(groupText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    /*
     * This defaults to "public object getChild" if you auto import the methods
     * I've always make a point to change it from "object" to whatever item
     * I passed through the constructor
    */
    @Override
    public String getChild(int groupPosition, int childPosition) {
        return _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final int mGroupPosition = groupPosition;
        final int mChildPosition = childPosition;

        //  I passed a text string into an activity holding a getter/setter
        //  which I passed in through "ExpListChildItems".
        //  Here is where I call the getter to get that text
        childText = getChild(mGroupPosition, mChildPosition);

        if (convertView == null) {

            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_item, null);

            childViewHolder = new ChildViewHolder();

            childViewHolder.mChildText = convertView
                    .findViewById(R.id.lblListItem);

            childViewHolder.mCheckBox = convertView
                    .findViewById(R.id.ischeck);

            convertView.setTag(R.layout.expandable_list_item, childViewHolder);

        } else {

            childViewHolder = (ChildViewHolder) convertView
                    .getTag(R.layout.expandable_list_item);
        }

        childViewHolder.mChildText.setText(childText);

        //Check for TYPE
        if (groupPosition == 0) {
            try {
                if (childText.equals(Constants.MANIFEST_RECCE)) {

                    //Check if fwd shipment is present
                    if (toDoListViewModel.recceTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.colorBackground);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                } else if (childText.equals(Constants.MANIFEST_SELLER)) {
                    //Check if rts shipment is present
                    if (toDoListViewModel.sellerTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.colorBackground);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                } else if (childText.equals(Constants.MANIFEST_WAREHOUSE)) {
                    Log.d(TAG, "getChildView: ");
                    //Check if rvp shipment is present
                    if (toDoListViewModel.warehouseTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.colorBackground);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                } else if (childText.equals(Constants.MANIFEST_RECCE_SELLER)) {
                    //Check if eds shipment is present
                    if (toDoListViewModel.recceSellerTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.colorBackground);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                } else if (childText.equals(Constants.MANIFEST_RECCE_WAREHOUSE)) {
                    //Check if eds shipment is present
                    if (toDoListViewModel.recceWarehouseTotalCount.get() == 0) {
                        convertView.setBackgroundResource(R.color.gray_ecom);
                        convertView.setEnabled(false);
                        childViewHolder.mCheckBox.setEnabled(false);
                    } else {
                        convertView.setBackgroundResource(R.color.colorBackground);
                        convertView.setEnabled(true);
                        childViewHolder.mCheckBox.setEnabled(true);
                    }
                }
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }

        //Check for Status
        else if (groupPosition == 1) {
            if (childText.equals(Constants.COMMITPENDING)) {
                //Check if pending shipment is present

                if (toDoListViewModel.totalAssignedCount.get().equalsIgnoreCase("0")) {
                    convertView.setBackgroundResource(R.color.gray_ecom);
                    convertView.setEnabled(false);
                    childViewHolder.mCheckBox.setEnabled(false);
                } else {
                    convertView.setBackgroundResource(R.color.colorBackground);
                    convertView.setEnabled(true);
                    childViewHolder.mCheckBox.setEnabled(true);
                }
            } else if (childText.equals(Constants.COMMITPICKED)) {
                //Check if successful shipment is present
                if (toDoListViewModel.totalPickedCount.get().equalsIgnoreCase("0")) {
                    convertView.setBackgroundResource(R.color.gray_ecom);
                    convertView.setEnabled(false);
                    childViewHolder.mCheckBox.setEnabled(false);
                } else {
                    convertView.setBackgroundResource(R.color.colorBackground);
                    convertView.setEnabled(true);
                    childViewHolder.mCheckBox.setEnabled(true);
                }
            } else if (childText.equals(Constants.COMMITFAILED)) {
                //Check if unsuccessful shipment is present
                if (toDoListViewModel.totalUnPickedCount.get().equalsIgnoreCase("0")) {
                    convertView.setBackgroundResource(R.color.gray_ecom);
                    convertView.setEnabled(false);
                    childViewHolder.mCheckBox.setEnabled(false);
                } else {
                    convertView.setBackgroundResource(R.color.colorBackground);
                    convertView.setEnabled(true);
                    childViewHolder.mCheckBox.setEnabled(true);
                }
            }

        }



        /*
         * You have to set the onCheckChangedListener to null
		 * before restoring check states because each call to
		 * "setChecked" is accompanied by a call to the
		 * onCheckChangedListener
		*/
        childViewHolder.mCheckBox.setOnCheckedChangeListener(null);

        if (mChildCheckStates.containsKey(mGroupPosition)) {
            /*
             * if the hashmap mChildCheckStates<Integer, Boolean[]> contains
			 * the value of the parent view (group) of this child (aka, the key),
			 * then retrive the boolean array getChecked[]
			*/
            boolean[] getChecked = mChildCheckStates.get(mGroupPosition);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(getChecked[mChildPosition]);

        } else {

			/*
             * if the hashmap mChildCheckStates<Integer, Boolean[]> does not
			 * contain the value of the parent view (group) of this child (aka, the key),
			 * (aka, the key), then initialize getChecked[] as a new boolean array
			 *  and set it's size to the total number of children associated with
			 *  the parent group
			*/
            boolean[] getChecked = new boolean[getChildrenCount(mGroupPosition)];

            // add getChecked[] to the mChildCheckStates hashmap using mGroupPosition as the key
            mChildCheckStates.put(mGroupPosition, getChecked);

            // set the check state of this position's checkbox based on the
            // boolean value of getChecked[position]
            childViewHolder.mCheckBox.setChecked(false);
        }

        childViewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {

                    boolean[] getChecked = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);

                } else {

                    boolean[] getChecked = mChildCheckStates.get(mGroupPosition);
                    getChecked[mChildPosition] = isChecked;
                    mChildCheckStates.put(mGroupPosition, getChecked);
                }
            }
        });

        return convertView;
    }

    public HashMap<Integer, boolean[]> getmyChildCheckStates() {
        return mChildCheckStates;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public void clearAllCheckbox() {

    }

    public final class GroupViewHolder {

        TextView mGroupText;
    }

    public final class ChildViewHolder {

        TextView mChildText;
        CheckBox mCheckBox;
    }


}
package in.ecomexpress.sruti.ui.dashboard.signature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

import in.ecomexpress.sruti.R;

public class CustomMobileListAdapter extends ArrayAdapter<MobileItem> {

    public CustomMobileListAdapter(Context context,
                                   ArrayList<MobileItem> mobileItemArrayList) {
        super(context, 0, mobileItemArrayList);
    }

    int selectedPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable
    View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable
    View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView,
                          ViewGroup parent) {
        // It is used to set our custom view.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_drop_down_item, parent, false);

        }

        TextView textViewName = convertView.findViewById(R.id.textView);
        ConstraintLayout container = convertView.findViewById(R.id.container);
        ImageView check_box = convertView.findViewById(R.id.check_box);
        MobileItem currentItem = getItem(position);
        if (selectedPosition == position) {
            check_box.setImageResource(R.drawable.item_selected);
        } else {
            check_box.setImageResource(R.drawable.item_unselected);
        }
        // It is used the name to the TextView when the
        // current item is not null.
        if (currentItem != null) {
            textViewName.setText(currentItem.getMobileNo());
        }
     /*   container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });

*/
        return convertView;
    }
}
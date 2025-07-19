package in.ecomexpress.sruti.ui.dashboard.todolist.todoactivitymodel;

import static in.ecomexpress.sruti.utils.AppConstants.CAMERA_REQUEST;
import static in.ecomexpress.sruti.utils.CommonUtils.deleteIMG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import in.ecomexpress.sruti.BR;
import in.ecomexpress.sruti.R;
import in.ecomexpress.sruti.background_service.GpsUtils;
import in.ecomexpress.sruti.databinding.RecciViewBinding;
import in.ecomexpress.sruti.di.ViewModelProviderRoom;
import in.ecomexpress.sruti.model.commitdata.Recci;
import in.ecomexpress.sruti.model.masterdata.AdditionalControl;
import in.ecomexpress.sruti.model.masterdata.General_Question;
import in.ecomexpress.sruti.model.menifestdata.Address;
import in.ecomexpress.sruti.model.menifestdata.Manifest_List;
import in.ecomexpress.sruti.model.menifestdata.Vender_Detail;
import in.ecomexpress.sruti.ui.base.BaseActivity;
import in.ecomexpress.sruti.ui.dashboard.seller.seller_activity_model.SellerActivity;
import in.ecomexpress.sruti.ui.dashboard.signature.SuccessFailActivity;
import in.ecomexpress.sruti.ui.dashboard.starttrip.ThreadGeneric;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_listener.IQrRecciView;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.RecciViewModel;
import in.ecomexpress.sruti.ui.dashboard.todolist.todo_item_viewmodel.SpinAdapter;
import in.ecomexpress.sruti.ui.dashboard.warehouse.warehouse_activity_model.WarehouseActivity;
import in.ecomexpress.sruti.utils.common_files.Constants;

/**
 * Created by 63091 on 22-08-2019.
 */

public class RecciQuestion extends BaseActivity<RecciViewBinding, RecciViewModel> implements View.OnClickListener, IQrRecciView {
    @Inject
    ViewModelProviderRoom viewModelProviderRoom;
    RecciViewModel recciViewModel;
    RecciViewBinding recciViewBinding;
    private ArrayList<View> dynamicView = new ArrayList<>();
    private List<General_Question> general_questions;
    private List<General_Question> general_questions_created_View = new ArrayList<>();
    private String imageFilePath = "";
    private ImageView camera_capture;
    private String manifest_type;
    private String location_type;
    private long pickup_location_id;
    private Manifest_List manifestList;
    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean isGPS = false;
    private long manifest_no;
    private String custmer_name;
    String addressData, city,pincode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.recciViewBinding = getViewDataBinding();
        recciViewModel.setNavigator(this);

        recciViewBinding.submit.setOnClickListener(this);
        recciViewBinding.recciback.setOnClickListener(this);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("data"))
            manifestList = getIntent().getParcelableExtra("data");
        custmer_name=getIntent().getExtras().getString("custmer_name");
        Vender_Detail vender_detail = getIntent().getExtras().getParcelable("vender_detail");
        manifest_type = getIntent().getExtras().getString("manifest_type");
        location_type = getIntent().getExtras().getString("location_type");
        pickup_location_id = getIntent().getExtras().getLong("pickup_location_id");
        manifest_no = getIntent().getExtras().getLong("manifest_no");
        List<Integer> question_ids = (ArrayList<Integer>) getIntent().getSerializableExtra("question_ids");
        System.out.println("vender_detail  " + vender_detail.getLocationCode());
        recciViewBinding.venderName.setText(vender_detail.getLocationName());
        Address address = vender_detail.getAddress();
        String addst = notNull(address.getLine1()) + "," + notNull(address.getLine2()) + "," + notNull(address.getLine3()) + "," + notNull(address.getCity());


        addressData = notNull(address.getLine1()) + "," + notNull(address.getLine2()) + "," + notNull(address.getLine3());
        city = address.getCity();
        pincode = address.getPincode();

        recciViewBinding.venderAddress.setText("Address :" + addst);
        if (question_ids != null && question_ids.size() > 0) {
            recciViewModel.getGeneral_question(question_ids).observe(this, general_questions -> {
                if (general_questions != null && general_questions.size() > 0) {
                    this.general_questions = general_questions;
                    inflateView(general_questions, recciViewBinding.parentView);
                } else {
                    recciViewBinding.submit.setVisibility(View.GONE);
                }
            });
        } else {
            Toast.makeText(this, "no question ids", Toast.LENGTH_SHORT).show();
        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000);
        locationRequest.setFastestInterval(2 * 1000);

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;

            }
        });


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
                    }
                }
            }
        };
        getLocation();
    }

    @Override
    protected String getScreenName() {
        return "Recci Questions Screen";
    }

    String notNull(String vl) {
        return TextUtils.isEmpty(vl) ? "" : vl;
    }

    @Override
    public RecciViewModel getViewModel() {
        recciViewModel = ViewModelProviders.of(this, viewModelProviderRoom).get(RecciViewModel.class);
        return recciViewModel;
    }

    @Override
    public int getBindingVariable() {
        return BR.recciViewmodel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.recci_view;
    }

    private void inflateView(List<General_Question> ob, LinearLayout parentView) {
        LayoutInflater layoutInflater = getLayoutInflater();

        for (General_Question general_question : ob) {
            if (general_question.getAns_type().equals("INPUT")) {
                //if (general_question.getType().equals("INPUT") && general_question.getAns_type().equals("STRING"))
                View view = layoutInflater.inflate(R.layout.recci_edittext, parentView, false);
                TextView recci_question = view.findViewById(R.id.questiondes);
                Spanned mandatory = null;
                if (general_question.getOption().equalsIgnoreCase("M")) {
                    mandatory = Html.fromHtml(general_question.getQtag() + "<font color='#ea3737'> *</font>");
                } else {
                    mandatory = Html.fromHtml(general_question.getQtag());
                }
                recci_question.setText(mandatory);
                EditText recci_editview = view.findViewById(R.id.recci_editview);
                recci_editview.setInputType(InputType.TYPE_CLASS_TEXT);
                dynamicView.add(view);
                general_questions_created_View.add(general_question);
                parentView.addView(view);
            } else if (general_question.getAns_type().equals("DROPDOWN")) {
                //if (general_question.getType().equals("SELECTION") && general_question.getAns_type().equals("STRING"))
                try {
                    View view = layoutInflater.inflate(R.layout.recci_selectionview, parentView, false);
                    Spinner spinner = view.findViewById(R.id.spinner_selection);
                    TextView recci_question = view.findViewById(R.id.recci_question);
                    Spanned mandatory = null;
                    if (general_question.getOption().equalsIgnoreCase("M")) {
                        mandatory = Html.fromHtml(general_question.getQtag() + "<font color='#ea3737'> *</font>");
                    } else {
                        mandatory = Html.fromHtml(general_question.getQtag());
                    }

                    recci_question.setText(mandatory);
                    JSONArray jsonArray = new JSONArray(general_question.getValue_array().get(0));
                    ArrayList<String> aa = new Gson().fromJson(jsonArray.toString(), new TypeToken<List<String>>() {
                    }.getType());
                    aa.add(0, "---SELECT---");
                    aa.removeAll(Collections.singleton(null));
                    if (aa != null) {
                        SpinAdapter adapter = new SpinAdapter(RecciQuestion.this, android.R.layout.simple_spinner_item, aa);
                        spinner.setAdapter(adapter);
                    }
                    dynamicView.add(view);
                    general_questions_created_View.add(general_question);
                    parentView.addView(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (general_question.getAns_type().equals("SELECTION")) {//SELECTION
                //if (general_question.getType().equals("INPUT") && general_question.getAns_type().equals("RADIO"))
                View view = layoutInflater.inflate(R.layout.recci_radioview, parentView, false);
                if (general_question.getAdditional_control() != null) {
                    Gson gson = new Gson();
                    AdditionalControl additionalControl = gson.fromJson(general_question.getAdditional_control(), AdditionalControl.class);
                    RadioGroup radio_group = view.findViewById(R.id.radio_group);
                    EditText recci_editview = view.findViewById(R.id.recci_editview);
                    recci_editview.setHint(additionalControl.getWatermark());
                    radio_group.setOnCheckedChangeListener((radioGroup, i) -> {
                        switch (i) {
                            case R.id.yes:
                                if (additionalControl.getDisplay_condition().equalsIgnoreCase("yes")) {
                                    recci_editview.setVisibility(View.VISIBLE);
                                } else {
                                    recci_editview.setVisibility(View.GONE);
                                }

                                break;
                            case R.id.no:
                                if (additionalControl.getDisplay_condition().equalsIgnoreCase("no")) {
                                    recci_editview.setVisibility(View.VISIBLE);
                                } else {
                                    recci_editview.setVisibility(View.GONE);
                                }
                                break;
                        }
                    });
                }
                TextView recci_question = view.findViewById(R.id.questiondes);
                Spanned mandatory = null;
                if (general_question.getOption().equalsIgnoreCase("M")) {
                    mandatory = Html.fromHtml(general_question.getQtag() + "<font color='#ea3737'> *</font>");
                } else {
                    mandatory = Html.fromHtml(general_question.getQtag());
                }
                recci_question.setText(mandatory);

                dynamicView.add(view);
                general_questions_created_View.add(general_question);
                parentView.addView(view);
            } else if (general_question.getAns_type().equals("TIME_DURATION")) {
                //if (general_question.getType().equals("INPUT") && general_question.getAns_type().equals("TIME_DURATION"))
                View view = layoutInflater.inflate(R.layout.recci_timeduration, parentView, false);
                TextView recci_question = view.findViewById(R.id.questiondes);
                Spanned mandatory = null;
                if (general_question.getOption().equalsIgnoreCase("M")) {
                    mandatory = Html.fromHtml(general_question.getQtag() + "<font color='#ea3737'> *</font>");
                } else {
                    mandatory = Html.fromHtml(general_question.getQtag());
                }
                recci_question.setText(mandatory);

                TextView startTime = view.findViewById(R.id.startTime);
                TextView endTime = view.findViewById(R.id.endTime);
                TextView st_time = view.findViewById(R.id.st_time);
                TextView endtime = view.findViewById(R.id.endtime);

                startTime.setOnClickListener(view1 -> {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(RecciQuestion.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            st_time.setText(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                });
                endTime.setOnClickListener(view1 -> {
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(RecciQuestion.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            endtime.setText(selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);//Yes 24 hour time
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                });

                dynamicView.add(view);
                general_questions_created_View.add(general_question);
                parentView.addView(view);
            } else if (general_question.getType().equals("MULTISELECTION")) {//&& general_question.getAns_type().equals("CHECKBOX")
               /* View view = layoutInflater.inflate(R.layout.recci_checkboxview, parentView, false);
                TextView recci_question = view.findViewById(R.id.questiondes);
                Spanned mandatory = null;
                if (general_question.getOption().equalsIgnoreCase("M")) {
                    mandatory = Html.fromHtml(general_question.getQtag() + "<font color='#ea3737'> *</font>");
                } else {
                    mandatory = Html.fromHtml(general_question.getQtag());
                }

                recci_question.setText(mandatory);

                dynamicView.add(view);
                general_questions_created_View.add(general_question);
                parentView.addView(view);*/
            }

        }
    }

    boolean validation() {
        int position = 0;
        for (General_Question gq : general_questions_created_View) {
            if (gq.getAns_type().equals("SELECTION")) {
                //if (gq.getType().equals("INPUT") && gq.getAns_type().equals("RADIO"))
                View view = dynamicView.get(position);
                EditText recci_editview = view.findViewById(R.id.recci_editview);
                RadioButton yes = view.findViewById(R.id.yes);
                RadioButton no = view.findViewById(R.id.no);
                if (gq.getOption().equals("M")) {
                    if (!(yes.isChecked() || no.isChecked())) {
                        Toast.makeText(this, "Please choose required data. ", Toast.LENGTH_SHORT).show();
                        return false;
                    } else if (gq.getAdditional_control() != null) {
                        Gson gson = new Gson();
                        AdditionalControl additionalControl = gson.fromJson(gq.getAdditional_control(), AdditionalControl.class);
                        if (additionalControl.getDisplay_condition().equalsIgnoreCase("yes") && yes.isChecked() && TextUtils.isEmpty(recci_editview.getText())) {
                            Toast.makeText(this, "Please enter required data. ", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (additionalControl.getDisplay_condition().equalsIgnoreCase("no") && no.isChecked() && TextUtils.isEmpty(recci_editview.getText())) {
                            Toast.makeText(this, "Please enter required data. ", Toast.LENGTH_SHORT).show();
                            return false;
                        }
//                (gq.getOption().equals("M") && TextUtils.isEmpty(recci_editview.getText().toString()))

                    }
                }

            } else if (gq.getAns_type().equals("INPUT")) {
                //if (gq.getType().equals("INPUT") && gq.getAns_type().equals("INTEGER"))
                View view = dynamicView.get(position);
                EditText recci_editview = view.findViewById(R.id.recci_editview);
                if (gq.getOption().equals("M") && TextUtils.isEmpty(recci_editview.getText())) {
                    Toast.makeText(this, "Please enter required data. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (gq.getAns_type().equals("DROPDOWN")) {
                View view = dynamicView.get(position);
                Spinner selection = view.findViewById(R.id.spinner_selection);
                if (selection != null && gq.getOption().equals("M") && selection.getSelectedItem().toString().equals("---SELECT---")) {
                    Toast.makeText(this, "Please select required data. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else if (gq.getAns_type().equals("TIME_DURATION")) {
                View view = dynamicView.get(position);
                TextView st_time = view.findViewById(R.id.st_time);
                TextView endtime = view.findViewById(R.id.endtime);
                if (gq.getOption().equals("M") && TextUtils.isEmpty(st_time.getText())) {
                    Toast.makeText(this, "Please select start time. ", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (gq.getOption().equals("M") && TextUtils.isEmpty(endtime.getText())) {
                    Toast.makeText(this, "Please select end time. ", Toast.LENGTH_SHORT).show();
                    return false;
                } else {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
                        Date inTime = sdf.parse(st_time.getText().toString());
                        Date outTime = sdf.parse(endtime.getText().toString());
                        if (!isTimeAfter(inTime, outTime)) {
                            Toast.makeText(this, "End time should be greater than start time. ", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else if (gq.getType().equals("MULTISELECTION")) {

            }
            /*else if (gq.getType().equals("MULTISELECTION") && gq.getAns_type().equals("CHECKBOX")) {
                View view = dynamicView.get(position);
                CheckBox malegender = (CheckBox) view.findViewById(R.id.malegender);
                CheckBox femalegender = (CheckBox) view.findViewById(R.id.femalegender);

                if (!(malegender.isChecked() || femalegender.isChecked())) {
                    Toast.makeText(this, "Please choose required data. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else if (gq.getType().equals("INPUT") && gq.getAns_type().equals("INTEGER")) {
                View view = dynamicView.get(position);
                EditText recci_editview = view.findViewById(R.id.recci_editview);
                if (gq.getOption().equals("M") && TextUtils.isEmpty(recci_editview.getText())) {
                    Toast.makeText(this, "Please enter required data. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else if (gq.getType().equals("IMG") && gq.getAns_type().equals("BOOLEAN")) {
                // View view = dynamicView.get(position);
                if (gq.getOption().equals("M") && TextUtils.isEmpty(imageFilePath)) {
                    Toast.makeText(this, "Please capture location photo. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }*/

            /*Increament*/
            position++;
        }
        return true;
    }

    boolean isTimeAfter(Date startTime, Date endTime) {
        //Same way you can check with after() method also.
        return !endTime.before(startTime);
    }

    ArrayList<Recci> getValue() {
        ArrayList<Recci> value_data = new ArrayList<>();
        int position = 0;
        for (General_Question gq : general_questions_created_View) {
            if (gq.getAns_type().equals("SELECTION")) {
                //if (gq.getType().equals("INPUT") && gq.getAns_type().equals("RADIO"))
                View view = dynamicView.get(position);
                EditText recci_editview = view.findViewById(R.id.recci_editview);
                RadioButton yes = view.findViewById(R.id.yes);
                RadioButton no = view.findViewById(R.id.no);
                Recci recci = new Recci();
                String dd = yes.isChecked() ? yes.getText().toString() : no.isChecked() ? no.getText().toString() : "";
                recci.setAnswer(dd);
                recci.setQuestion_id(gq.getId());
                recci.setComment(recci_editview.getText().toString());
                value_data.add(recci);

            } else if (gq.getAns_type().equals("INPUT")) {
                //if (gq.getType().equals("INPUT") && gq.getAns_type().equals("INTEGER"))
                View view = dynamicView.get(position);
                EditText recci_editview = view.findViewById(R.id.recci_editview);
                Recci recci = new Recci();
                recci.setAnswer(recci_editview.getText().toString());
                recci.setQuestion_id(gq.getId());
                recci.setComment("");
                value_data.add(recci);


            } else if (gq.getAns_type().equals("DROPDOWN")) {
                View view = dynamicView.get(position);
                Spinner selection = view.findViewById(R.id.spinner_selection);
                Recci recci = new Recci();
                recci.setAnswer(selection.getSelectedItem().toString());
                recci.setQuestion_id(gq.getId());
                recci.setComment("");
                value_data.add(recci);

            } else if (gq.getAns_type().equals("TIME_DURATION")) {
                View view = dynamicView.get(position);
                TextView st_time = view.findViewById(R.id.st_time);
                TextView endtime = view.findViewById(R.id.endtime);
                Recci recci = new Recci();
                recci.setAnswer(st_time.getText().toString() + "-" + endtime.getText().toString());
                recci.setQuestion_id(gq.getId());
                recci.setComment("");
                value_data.add(recci);
            } else if (gq.getType().equals("MULTISELECTION")) {

            }
            /*else if (gq.getType().equals("MULTISELECTION") && gq.getAns_type().equals("CHECKBOX")) {
                View view = dynamicView.get(position);
                CheckBox malegender = (CheckBox) view.findViewById(R.id.malegender);
                CheckBox femalegender = (CheckBox) view.findViewById(R.id.femalegender);

                if (!(malegender.isChecked() || femalegender.isChecked())) {
                    Toast.makeText(this, "Please choose required data. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else if (gq.getType().equals("INPUT") && gq.getAns_type().equals("INTEGER")) {
                View view = dynamicView.get(position);
                EditText recci_editview = view.findViewById(R.id.recci_editview);
                if (gq.getOption().equals("M") && TextUtils.isEmpty(recci_editview.getText())) {
                    Toast.makeText(this, "Please enter required data. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
            else if (gq.getType().equals("IMG") && gq.getAns_type().equals("BOOLEAN")) {
                // View view = dynamicView.get(position);
                if (gq.getOption().equals("M") && TextUtils.isEmpty(imageFilePath)) {
                    Toast.makeText(this, "Please capture location photo. ", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }*/

            /*Increament*/
            position++;
        }
        return value_data;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:

                if (validation()) {
                    if (manifest_type.equalsIgnoreCase("R")) {
                        ArrayList<Recci> postvalue = getValue();

                        recciViewModel.createCommitPacketNew(postvalue, pickup_location_id, wayLatitude, wayLongitude);

                    } else if (manifest_type.equalsIgnoreCase("PR") && location_type.equalsIgnoreCase("Warehouse")) {
                        ArrayList<Recci> postvalue = getValue();
                        Intent recci_qution = new Intent(RecciQuestion.this, WarehouseActivity.class);
                        recci_qution.putExtra("RECCIQ", postvalue);
                        recci_qution.putExtra("pickup_location_id", pickup_location_id);
                        recci_qution.putExtra("wayLatitude", wayLatitude);
                        recci_qution.putExtra("wayLongitude", wayLongitude);
                        recci_qution.putExtra("data", manifestList);
                        recci_qution.putExtra("manifest_no", manifest_no);
                        startActivity(recci_qution);
                        RecciQuestion.this.finish();
                    } else if (manifest_type.equalsIgnoreCase("PR") && location_type.equalsIgnoreCase("Seller")) {
                        ArrayList<Recci> postvalue = getValue();
                        Intent recci_qution = new Intent(RecciQuestion.this, SellerActivity.class);
                        recci_qution.putExtra("RECCIQ", postvalue);
                        recci_qution.putExtra("pickup_location_id", pickup_location_id);
                        recci_qution.putExtra("wayLatitude", wayLatitude);
                        recci_qution.putExtra("wayLongitude", wayLongitude);
                        recci_qution.putExtra("data", manifestList);
                        recci_qution.putExtra("manifest_no", manifest_no);
                        startActivity(recci_qution);
                        RecciQuestion.this.finish();
                    }
                }
                break;
            case R.id.recciback:
                RecciQuestion.this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            System.out.println("CAMERA" + imageFilePath);

            deleteIMG(RecciQuestion.this);

//            start_trip_images.get(position).setImage_path(imageFilePath);
//            start_trip_adapter.updateImage(start_trip_images);
            if (camera_capture != null)
                Glide.with(this).load(imageFilePath).into(camera_capture);
        } else if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_CANCELED) {
            imageFilePath = "";
//            start_trip_images.get(position).setImage_path(imageFilePath);
//            start_trip_adapter.updateImage(start_trip_images);
//            mRunIdViewModel.setUriPath(imageFilePath);
        } else if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.GPS_REQUEST) {
                isGPS = true;
            }
        }
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(RecciQuestion.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(RecciQuestion.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(RecciQuestion.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    Constants.LOCATION_REQUEST);

        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(RecciQuestion.this, location -> {
                if (location != null) {
                    wayLatitude = location.getLatitude();
                    wayLongitude = location.getLongitude();
                } else {
                    mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                }
            });

        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mFusedLocationClient.getLastLocation().addOnSuccessListener(RecciQuestion.this, location -> {
                        if (location != null) {
                            wayLatitude = location.getLatitude();
                            wayLongitude = location.getLongitude();
                        } else {
                            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        }
                    });
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    @Override
    public void nextScreen(String manifest_no, String sFileBody) {
        try {
            FileOutputStream fileout = openFileOutput(String.valueOf(manifest_no), MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(sFileBody);
            outputWriter.close();

            String filePath = getFilesDir().getAbsolutePath() + "/" + manifest_no;

            ThreadGeneric.executeCall(() -> {
                recciViewModel.updateFileUrl(filePath, manifest_no);
            });
            Intent recci_qution = new Intent(RecciQuestion.this, SuccessFailActivity.class);
            recci_qution.putExtra("screen_validation", "success");
            recci_qution.putExtra("pickup_location_id", pickup_location_id);
            recci_qution.putExtra("customer_name", custmer_name);
            recci_qution.putExtra("address", addressData);
            recci_qution.putExtra("city", city);
            recci_qution.putExtra("pincode",pincode);
            startActivity(recci_qution);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

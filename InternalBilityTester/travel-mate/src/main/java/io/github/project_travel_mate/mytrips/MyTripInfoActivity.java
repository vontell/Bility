package io.github.project_travel_mate.mytrips;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import adapters.NestedListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.github.project_travel_mate.FullScreenImage;
import io.github.project_travel_mate.R;
import io.github.project_travel_mate.destinations.description.FinalCityInfoActivity;
import io.github.project_travel_mate.friend.FriendsProfileActivity;
import objects.City;
import objects.Trip;
import objects.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.TravelmateSnackbars;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static utils.Constants.API_LINK_V2;
import static utils.Constants.EXTRA_MESSAGE_FRIEND_ID;
import static utils.Constants.EXTRA_MESSAGE_PART_OF_TRIP;
import static utils.Constants.EXTRA_MESSAGE_TRIP_OBJECT;
import static utils.Constants.SHARE_PROFILE_URI;
import static utils.Constants.SHARE_TRIP_TRIP_ID_QUERY;
import static utils.Constants.USER_EMAIL;
import static utils.Constants.USER_TOKEN;

public class MyTripInfoActivity extends AppCompatActivity implements TravelmateSnackbars {

    @BindView(R.id.city_image)
    ImageView cityImageView;
    @BindView(R.id.city_name)
    TextView cityName;
    @BindView(R.id.trip_start_date)
    TextView tripDate;
    @BindView(R.id.add_new_friend)
    TextView addNewFriend;
    @BindView(R.id.friend_list)
    NestedListView listView;
    @BindView(R.id.friend_email)
    AutoCompleteTextView friendEmail;
    @BindView(R.id.trip_name)
    EditText tripName;
    @BindView(R.id.friend_title)
    TextView friendTitle;
    @BindView(R.id.plus_icon)
    ImageView showIcon;
    @BindView(R.id.edit_trip_icon)
    ImageView editTrip;
    @BindView(R.id.public_trip_message)
    TextView tripPublicMessage;
    @BindView(R.id.ispublic_toggleButton)
    ToggleButton publicToggleButton;
    @BindView(R.id.know_more)
    FloatingActionButton details;
    @BindView(R.id.animation_view)
    LottieAnimationView animationView;
    @BindView(R.id.layout)
    LinearLayout layout;
    @BindView(R.id.no_friend_title)
    TextView noFriendTitle;
    @BindView(R.id.trip_name_progress_bar)
    ProgressBar tripNameProgressBar;
    @BindView(R.id.addme_to_trip)
    LinearLayout addMeToTrip;
    @BindView(R.id.public_trip_layout)
    RelativeLayout publicPrivateInfo;

    private String mFriendId = null;
    private String mFriendDeleteId = null;
    private String mNameYet;
    private String mToken;
    private Trip mTrip;
    private Handler mHandler;
    private boolean mIsClicked = false;
    private boolean mIsTripNameEdited = false;
    private MaterialDialog mDialog;
    private MyTripFriendNameAdapter mAdapter;
    boolean isUserPartofTrip;
    private String mUserEmail;
    boolean isTripPublic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trip_info);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        mTrip = (Trip) intent.getSerializableExtra(EXTRA_MESSAGE_TRIP_OBJECT);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = sharedPreferences.getString(USER_TOKEN, null);
        mUserEmail = sharedPreferences.getString(USER_EMAIL, null);
        if (mTrip.getImage() != null && !mTrip.getImage().isEmpty())
            Picasso.with(this).load(mTrip.getImage()).error(R.drawable.placeholder_image)
                    .placeholder(R.drawable.placeholder_image).into(cityImageView);
        showIcon.setVisibility(GONE);
        editTrip.setVisibility(GONE);
        mHandler = new Handler(Looper.getMainLooper());
        friendEmail.clearFocus();
        friendEmail.setThreshold(1);
        setTitle(mTrip.getName());

        publicToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTripPrivacy();
            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSingleTrip();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editTrip.setOnClickListener(v -> {
            if (!mIsTripNameEdited) {
                //if edit trip is clicked before editing the name
                editTrip.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_black_24dp));
                tripName.setFocusableInTouchMode(true);
                tripName.setCursorVisible(true);
                tripName.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(inputMethodManager)
                        .showSoftInput(tripName, InputMethodManager.SHOW_IMPLICIT);
                mIsTripNameEdited = true;
            } else {
                //clicking edit trip after editing the trip name
                if (tripName.getText().length() != 0) {
                    editTrip.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_black_24dp));
                    tripName.setFocusableInTouchMode(false);
                    tripName.setCursorVisible(false);
                    mIsTripNameEdited = false;
                    View view = getCurrentFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
                    updateTripName();
                } else {
                    TravelmateSnackbars.createSnackBar(findViewById(R.id.activityMyTripInfo), R.string.cannot_edit,
                            Snackbar.LENGTH_SHORT).show();
                }
            }
        });

    }

    @OnClick(R.id.city_image)
    void cityImageClicked() {
        Intent fullScreenIntent = FullScreenImage.getStartIntent(MyTripInfoActivity.this,
                mTrip.getImage(), mTrip.getName());
        startActivity(fullScreenIntent);
    }

    @OnTextChanged(R.id.friend_email)
    void onTextChanged() {
        mNameYet = friendEmail.getText().toString();
        if (!mNameYet.contains(" ") && mNameYet.length() % 3 == 0) {
            friendAutoComplete();
        }
    }

    @OnClick(R.id.add_new_friend)
    void onClick() {
        if (mFriendId == null) {
            TravelmateSnackbars.createSnackBar(findViewById(R.id.activityMyTripInfo),
                    getString(R.string.no_friend_selected),
                    Snackbar.LENGTH_LONG).show();
        } else {
            addFriend();
        }
    }

    private void getSingleTrip() {

        animationView.setVisibility(VISIBLE);
        animationView.playAnimation();

        // to fetch mCity names
        String uri = API_LINK_V2 + "get-trip/" + mTrip.getId();

        Log.v("EXECUTING", uri);

        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();

        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(Call call, final Response response) {

                mHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject ob;
                        try {
                            final String res = Objects.requireNonNull(response.body()).string();
                            ob = new JSONObject(res);
                            String title = ob.getString("trip_name");
                            String start = ob.getString("start_date_tx");
                            String end = ob.optString("end_date", null);
                            String city = ob.getJSONObject("city").getString("city_name");
                            boolean isPublic = ob.getBoolean("is_public");
                            details.setVisibility(VISIBLE);
                            details.setOnClickListener(view -> {
                                details.setEnabled(false);
                                getCity(city);
                            });
                            cityName.setText(city + " " + getString(R.string.trip_info_city_date_separater));
                            tripName.setText(title);
                            final Calendar cal = Calendar.getInstance();
                            cal.setTimeInMillis(Long.parseLong(start) * 1000);
                            final String timeString =
                                    new SimpleDateFormat("dd MMM''yy", Locale.US).format(cal.getTime());
                            tripDate.setText(timeString);
                            isTripPublic = isPublic;

                            updateFriendList();
                            animationView.setVisibility(GONE);
                        } catch (JSONException | IOException | NullPointerException e) {
                            e.printStackTrace();
                            networkError();
                        }
                    } else {
                        networkError();
                    }
                    tripName.setVisibility(VISIBLE);
                    cityImageView.setVisibility(VISIBLE);
                    mFriendId = null;
                });
            }
        });
    }

    private void getCity(String cityname) {
        // to fetch city names
        String uri = API_LINK_V2 + "get-city-by-name/" + cityname;
        Log.v("EXECUTING", uri);

        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();
        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(Call call, final Response response) {

                mHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONArray arr;
                        City city = null;
                        try {
                            arr = new JSONArray(Objects.requireNonNull(response.body()).string());
                            Log.v("RESPONSE : ", arr.toString());
                            try {
                                city = new City(arr.getJSONObject(0).getString("id"),
                                        arr.getJSONObject(0).getString("image"),
                                        arr.getJSONObject(0).getString("city_name"),
                                        arr.getJSONObject(0).getInt("facts_count"),
                                        R.color.sienna,
                                        "Know More", "View on Map", "Fun Facts", "City Trends");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = FinalCityInfoActivity.getStartIntent(MyTripInfoActivity.this, city);
                            startActivity(intent);
                            details.setEnabled(true);

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            networkError();
                            Log.e("ERROR", "Message : " + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trip_info_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_remove_trip:
                removeTrip();
                return true;
            case R.id.share_trip:
                shareTrip();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void shareTrip() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        Uri uri = Uri.parse(SHARE_PROFILE_URI)
                .buildUpon()
                .appendQueryParameter(SHARE_TRIP_TRIP_ID_QUERY, mTrip.getId())
                .build();

        Log.v("trip url", uri + "");

        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_trip_text) + " " + uri);
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.share_chooser)));
        } catch (android.content.ActivityNotFoundException ex) {
            TravelmateSnackbars.createSnackBar(findViewById(R.id.layout), R.string.snackbar_no_share_app,
                    Snackbar.LENGTH_LONG).show();
        }
    }

    private void removeTrip() {
        //set AlertDialog before removing trip
        ContextThemeWrapper crt = new ContextThemeWrapper(this, R.style.AlertDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(crt);
        builder.setMessage(R.string.remove_trip_message)
                .setPositiveButton(R.string.positive_button,
                        (dialog, which) -> {
                            mDialog = new MaterialDialog.Builder(MyTripInfoActivity.this)
                                    .title(R.string.app_name)
                                    .content(R.string.progress_wait)
                                    .progress(true, 0)
                                    .show();

                            String uri;
                            uri = API_LINK_V2 + "remove-user-from-trip/" + mTrip.getId();
                            Log.v("EXECUTING", uri);

                            //Set up client
                            OkHttpClient client = new OkHttpClient();
                            //Execute request
                            Request request = new Request.Builder()
                                    .header("Authorization", "Token " + mToken)
                                    .url(uri)
                                    .build();
                            //Setup callback
                            client.newCall(request).enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.e("Request Failed", "Message : " + e.getMessage());
                                }

                                @Override
                                public void onResponse(Call call, final Response response) throws IOException {
                                    final String res = Objects.requireNonNull(response.body()).string();
                                    mHandler.post(() -> {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(MyTripInfoActivity.this,
                                                    R.string.remove_trip_success, Toast.LENGTH_SHORT).show();
                                            finish();

                                        } else {
                                            Toast.makeText(MyTripInfoActivity.this,
                                                    res, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    mDialog.dismiss();
                                }
                            });
                        })
                .setNegativeButton(android.R.string.cancel,
                        (dialog, which) -> {
                        });
        builder.create().show();
    }


    private void friendAutoComplete() {

        if (mNameYet.trim().equals(""))
            return;

        String uri = API_LINK_V2 + "get-user/" + mNameYet.trim();
        Log.v("EXECUTING", uri);

        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        final Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();
        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(Call call, final Response response) {

                mHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {

                        JSONArray arr;
                        final ArrayList<String> id, email;
                        try {
                            String result = response.body().string();
                            Log.e("RES", result);
                            if (response.body() == null)
                                return;
                            arr = new JSONArray(result);

                            id = new ArrayList<>();
                            email = new ArrayList<>();
                            for (int i = 0; i < arr.length(); i++) {
                                try {
                                    id.add(arr.getJSONObject(i).getString("id"));
                                    email.add(arr.getJSONObject(i).getString("username"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e("ERROR ", "Message : " + e.getMessage());
                                }
                            }
                            ArrayAdapter<String> dataAdapter =
                                    new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, email);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            friendEmail.setAdapter(dataAdapter);
                            friendEmail.setOnItemClickListener((arg0, arg1, arg2, arg3) -> mFriendId = id.get(arg2));
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            networkError();
                            Log.e("ERROR", "Message : " + e.getMessage());
                        }
                    } else {
                        networkError();
                    }
                });

            }
        });
    }

    private void addFriend() {

        final MaterialDialog mDialog = new MaterialDialog.Builder(MyTripInfoActivity.this)
                .title(R.string.app_name)
                .content(R.string.progress_wait)
                .progress(true, 0)
                .show();

        String uri = API_LINK_V2 + "add-friend-to-trip/" + mTrip.getId() + "/" + mFriendId;

        Log.v("EXECUTING", uri);

        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();
        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(Call call, final Response response) {
                mHandler.post(() -> {
                    if (response.isSuccessful()) {
                        TravelmateSnackbars.createSnackBar(findViewById(R.id.activityMyTripInfo),
                                getString(R.string.friend_added), Snackbar.LENGTH_LONG).show();
                        updateFriendList();
                        friendEmail.setText(null);
                    } else {
                        networkError();
                    }
                    mDialog.dismiss();
                });
            }
        });
    }

    private void updateFriendList() {
        List<User> tripFriends = new ArrayList<>();
        String uri = API_LINK_V2 + "get-trip/" + mTrip.getId();

        Log.v("EXECUTING", uri);

        //Set up client
        OkHttpClient client = new OkHttpClient();
        //Execute request
        Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();

        //Setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {

                final String res = Objects.requireNonNull(response.body()).string();
                mHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {

                        JSONObject ob;
                        try {
                            ob = new JSONObject(res);
                            JSONArray usersArray = ob.getJSONArray("users");

                            isUserPartofTrip = false;
                            for (int i = 0; i < usersArray.length(); i++) {

                                JSONObject jsonObject = usersArray.getJSONObject(i);
                                String friendFirstName = jsonObject.getString("first_name");
                                String friendLastName = jsonObject.getString("last_name");
                                String friendImage = jsonObject.getString("image");
                                String friendJoinedOn = jsonObject.getString("date_joined");
                                String friendUserName = jsonObject.getString("username");
                                String friendStatus = jsonObject.getString("status");
                                int friendId = jsonObject.getInt("id");

                                if (friendUserName.equals(mUserEmail)) {
                                    isUserPartofTrip = true;
                                    continue;
                                }

                                mFriendDeleteId = String.valueOf(friendId);
                                tripFriends.add(new User(friendUserName, friendFirstName, friendLastName, friendId,
                                        friendImage, friendJoinedOn, friendStatus));
                            }


                            if (tripFriends.size() == 0) {
                                addNewFriend.setVisibility(GONE);
                                friendEmail.setVisibility(GONE);
                                noFriendTitle.setVisibility(VISIBLE);
                                noFriendTitle.setTypeface(null, Typeface.BOLD);
                                String mystring = getString(R.string.friends_title);
                                SpannableString content = new SpannableString(mystring);
                                content.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
                                noFriendTitle.setText(content);
                                noFriendTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f);

                                addNewFriend.setVisibility(VISIBLE);
                                friendEmail.setVisibility(VISIBLE);
                                addMeToTrip.setVisibility(GONE);
                                publicPrivateInfo.setVisibility(VISIBLE);

                                if (isTripPublic) {
                                    tripPublicMessage.setText(R.string.trip_public_message);
                                    publicToggleButton.setChecked(true);
                                } else {
                                    tripPublicMessage.setText(R.string.trip_private_message);
                                    publicToggleButton.setChecked(false);
                                }

                            } else {


                                friendTitle.setText(R.string.friends_show_title);
                                friendTitle.setVisibility(VISIBLE);
                                showIcon.setVisibility(VISIBLE);

                                if (isUserPartofTrip) { //user is part of the trip
                                    addNewFriend.setVisibility(VISIBLE);
                                    friendEmail.setVisibility(VISIBLE);
                                    addMeToTrip.setVisibility(GONE);
                                    publicPrivateInfo.setVisibility(VISIBLE);

                                    if (isTripPublic) {
                                        tripPublicMessage.setText(R.string.trip_public_message);
                                        publicToggleButton.setChecked(true);
                                    } else {
                                        tripPublicMessage.setText(R.string.trip_private_message);
                                        publicToggleButton.setChecked(false);
                                    }

                                } else {
                                    //user is not a part of trip
                                    addMeToTrip.setVisibility(VISIBLE);
                                    addNewFriend.setVisibility(GONE);
                                    friendEmail.setVisibility(GONE);
                                }


                                if (mIsClicked) {
                                    mAdapter = new MyTripFriendNameAdapter(
                                            MyTripInfoActivity.this, tripFriends, mTrip, mFriendDeleteId);
                                    listView.setAdapter(null);
                                    listView.setAdapter(mAdapter);
                                }
                                showIcon.setOnClickListener(v -> {

                                    mAdapter = new MyTripFriendNameAdapter(
                                            MyTripInfoActivity.this, tripFriends, mTrip, mFriendDeleteId);
                                    if (!mIsClicked) {
                                        listView.setAdapter(mAdapter);
                                        showIcon.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                                        mIsClicked = true;
                                    } else {
                                        listView.setAdapter(null);
                                        showIcon.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                                        mIsClicked = false;
                                    }
                                });
                                listView.setOnItemClickListener((parent, view, position, id) -> {
                                    Intent intent = new Intent(MyTripInfoActivity.this,
                                            FriendsProfileActivity.class);
                                    intent.putExtra(EXTRA_MESSAGE_FRIEND_ID, tripFriends.get(position).getId());
                                    intent.putExtra(EXTRA_MESSAGE_TRIP_OBJECT, mTrip);
                                    startActivity(intent);
                                });
                            }
                        } catch (JSONException e) {
                            networkError();
                            e.printStackTrace();
                        }
                    } else {
                        networkError();
                    }
                });
            }
        });
    }

    private void updateTripName() {

        runOnUiThread(() -> {
            tripName.setVisibility(View.INVISIBLE);
            tripNameProgressBar.setVisibility(VISIBLE);
            editTrip.setVisibility(GONE);
        });
        String editedTripName = String.valueOf(tripName.getText());
        String uri = API_LINK_V2 + "update-trip-name/" + mTrip.getId() + "/" + editedTripName;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();
        //setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String res = Objects.requireNonNull(response.body()).string();
                    mHandler.post(() -> {
                        if (response.isSuccessful()) {
                            TravelmateSnackbars.createSnackBar(findViewById(R.id.activityMyTripInfo),
                                    R.string.trip_name_updated, Snackbar.LENGTH_SHORT).show();
                        } else {
                            TravelmateSnackbars.createSnackBar(findViewById(R.id.activityMyTripInfo),
                                    res, Snackbar.LENGTH_LONG).show();
                        }
                    });
                } else {
                    networkError();
                }
                runOnUiThread(() -> {
                    tripName.setVisibility(VISIBLE);
                    tripNameProgressBar.setVisibility(View.GONE);
                    editTrip.setVisibility(VISIBLE);
                });
            }
        });
    }

    private void updateTripPrivacy() {

        String uri = "";
        if (publicToggleButton.isChecked()) {
            uri = API_LINK_V2 + "update-trip-public/" + mTrip.getId();
        } else {
            uri = API_LINK_V2 + "update-trip-private/" + mTrip.getId();
        }

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .header("Authorization", "Token " + mToken)
                .url(uri)
                .build();
        //setup callback
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Request Failed", "Message : " + e.getMessage());
                mHandler.post(() -> networkError());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    final String res = Objects.requireNonNull(response.body()).string();
                    if (response.isSuccessful()) {
                        runOnUiThread(() -> {
                            if (publicToggleButton.isChecked()) {
                                tripPublicMessage.setText(R.string.trip_public_message);
                            } else {
                                tripPublicMessage.setText(R.string.trip_private_message);
                            }
                        });
                    }
                } else {
                    networkError();
                }
            }
        });
    }

    /**
     * Plays the network lost animation in the view
     */
    private void networkError() {
        layout.setVisibility(View.INVISIBLE);
        animationView.setVisibility(VISIBLE);
        animationView.setAnimation(R.raw.network_lost);
        animationView.playAnimation();
    }

    public static Intent getStartIntent(Context context, Trip trip) {
        Intent intent = new Intent(context, MyTripInfoActivity.class);
        intent.putExtra(EXTRA_MESSAGE_TRIP_OBJECT, trip);
        return intent;
    }

    public static Intent getStartIntent(Context context, Trip trip, boolean isPartoftrip) {
        Intent intent = new Intent(context, MyTripInfoActivity.class);
        intent.putExtra(EXTRA_MESSAGE_TRIP_OBJECT, trip);
        intent.putExtra(EXTRA_MESSAGE_PART_OF_TRIP, isPartoftrip);
        return intent;
    }
}
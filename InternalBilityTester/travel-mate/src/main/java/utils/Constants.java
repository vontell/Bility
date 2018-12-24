package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by swati on 11/10/15.
 */
public class Constants {
    public static final String IS_ADDED_INDB = "is_added_in_db";
    public static final String USER_NAME = "user_name";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_STATUS = "user_status";
    public static final String USER_TOKEN = "user_token";
    public static final String USER_DATE_JOINED = "user_date_joined";
    public static final String USER_IMAGE = "user_image";
    public static final String USER_ID = "user_id";
    public static final String OTHER_USER_ID = "other_user_id";
    public static final String READ_NOTIF_STATUS = "read_notificatons_status";

    public static final String EVENT_IMG = "event_name";
    public static final String EVENT_NAME = "event_img";
    public static final String IMAGE_NO = "number";

    public static final String LAST_CACHE_TIME = "last_cache";

    public static final String API_LINK_V2 = "https://project-travel-mate.herokuapp.com/api/";
    public static final String AUTHORIZATION = "Authorization";

    // TODO:: replace placeholders with actual values
    //Cloudinary information
    public static final String CLOUDINARY_CLOUD_NAME = "sample_cloud";
    public static final String CLOUDINARY_API_KEY = "sample_api_key";
    public static final String CLOUDINARY_API_SECRET = "sample_api_secret";


    public static final List<String> BASE_TASKS = new ArrayList<String>() {
        {
            add("Bags");
            add("Keys");
            add("Charger");
            add("Earphones");
            add("Clothes");
            add("Food");
            add("Tickets");
        }
    };

    // For passing within intents / fragments
    public static final String EXTRA_MESSAGE_TYPE = "type_";
    public static final String EXTRA_MESSAGE_CITY_OBJECT = "cityobject_";
    public static final String EXTRA_MESSAGE_FUNFACT_OBJECT = "funfactobject_";
    public static final String EXTRA_MESSAGE_TRIP_OBJECT = "tripobject_";
    public static final String EXTRA_MESSAGE_FRIEND_ID = "tripfriendid_";
    public static final String EXTRA_MESSAGE_IMAGE_URI = "profileimageuri_";
    public static final String EXTRA_MESSAGE_USER_FULLNAME = "userfullname_";
    public static final String EXTRA_MESSAGE_CITY_NAME = "citynickname_";
    public static final String EXTRA_MESSAGE_CITY_ID = "cityid_";
    public static final String EXTRA_MESSAGE_CALLED_FROM_UTILITIES = "iscalledfromutilties_";
    public static final String EXTRA_MESSAGE_HASHTAG_NAME = "hashtagname_";
    public static final String EXTRA_MESSAGE_PART_OF_TRIP = "ispartoftrip_";

    // Here API
    public static final List<String> HERE_API_MODES = new ArrayList<String>() {
        {
            add("eat-drink");
            add("going-out,leisure-outdoor");
            add("sights-museums");
            add("transport");
            add("shopping");
            add("petrol-station");
            add("atm-bank-exchange");
            add("hospital-health-care-facility");
        }
    };

    //spotlight shown count
    public static final String SPOTLIGHT_SHOW_COUNT = "spotlight_";

    //share profile strings
    public static final String SHARE_PROFILE_URI = "http://project-travel-mate.github.io/Travel-Mate";
    public static final String SHARE_PROFILE_USER_ID_QUERY = "user";
    public static final String SHARE_TRIP_TRIP_ID_QUERY = "trip";

    //QRCode constants
    public static final int QR_CODE_WIDTH = 200;
    public static final int QR_CODE_HEIGHT = 200;

    //Weather forecast constants
    public static final String CURRENT_TEMP = "TEMP";
    public static final int NUM_DAYS = 7;

    // for email verification
    public static final int VERIFICATION_REQUEST_CODE = 400;
  
    // Quotes
    public static final String QUOTES_API_LINK = "https://raw.githubusercontent.com/btford/philosobot/master/quotes/";
    public static final String QUOTES_SHOW_DAILY = "show_daily_quotes";
    public static final String QUOTES_LAUNCH_COUNT = "number_of_times_launched";
    public static final String QUOTES_FIRST_LAUNCH_DATE = "date_of_first_launch";
    public static final boolean QUOTES_SHOW_ON_FIRST_LOAD = true;
    // Min number of days between quotes
    public static final int DAYS_UNTIL_SHOW_QUOTE = 1;
    //Min number of launches before Quotes
    public static final int LAUNCHES_UNTIL_SHOW_QUOTE = 1;

}
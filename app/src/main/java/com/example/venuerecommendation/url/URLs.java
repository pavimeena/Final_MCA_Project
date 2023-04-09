package com.example.venuerecommendation.url;

import com.example.venuerecommendation.activity.IpAddress;

/**
 * Created by Belal on 9/5/2017.
 */

public class URLs {

    private static final String ROOT_URL = "http://"+IpAddress.Ip_Address+"/VenueRecommender/Api.php?apicall=";
    public static final String DATA_URL = "http://"+IpAddress.Ip_Address+"/VenueRecommender/ViewGroup.php?uid=";
    public static final String CENTER_POINT = "http://"+IpAddress.Ip_Address+"/VenueRecommender/loc.php";
    public static final String COUNT = "http://"+IpAddress.Ip_Address+"/VenueRecommender/count.php?";
    public static final String TAGS="http://"+IpAddress.Ip_Address+"/VenueRecommender/allloc.php";
    public static final String NEARBY_EVENT="http://"+IpAddress.Ip_Address+"/VenueRecommender/select.php";
    public static final String EVENT="http://"+IpAddress.Ip_Address+"/VenueRecommender/event.php";
    public static final String URL_REGISTER = ROOT_URL + "signup";
    public static final String URL_LOGIN= ROOT_URL + "login";
    public static final String URL_GROUP= ROOT_URL + "group";
    public static final String URL_TAGS= ROOT_URL + "tags";

}

package com.wojteklisowski.planator;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wojteklisowski.planator.database.AppDatabase;
import com.wojteklisowski.planator.entities.NearbyPlace;
import com.wojteklisowski.planator.interfaces.OnPlacesAvailable;
import com.wojteklisowski.planator.parsers.NearbyJsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, List, List> {
    private static final String TAG = "GetNearbyPlaces";
    public OnPlacesAvailable delegate = null; // dodac do konstruktora

    private String mRawPlacesData;
    private GoogleMap mMap;
    private ArrayList<Marker> mMarkerArray = new ArrayList<Marker>();
    private ArrayList<NearbyPlace> nearbyPlaceArrayList;
    private ArrayList<NearbyPlace> mVisited = new ArrayList<>();
    private String[] mUrl;
    private String mWayPoints = "";
    private boolean mManualMode;


    @Override
    protected List<String> doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        mUrl = (String[]) objects[1];
        mManualMode = (boolean) objects[2];
        Context context = (Context) objects[3];
        List<String> jsonList = new ArrayList<>();
        AppDatabase database = AppDatabase.getDatabase(context);
        mVisited = (ArrayList<NearbyPlace>) database.nearbyPlaceDao().loadAllVisitedPlaces(true);

        //TODO do testów:
        mRawPlacesData = "{\n" +
                "   \"html_attributions\" : [],\n" +
                "  \n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 51.0578124,\n" +
                "               \"lng\" : 20.7055183\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.05984490000002,\n" +
                "                  \"lng\" : 20.70677512989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.05713489999999,\n" +
                "                  \"lng\" : 20.70407547010727\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"4a2b27a6fe48e9c2c94a242649744db50eb278fa\",\n" +
                "         \"name\" : \"Rezerwat Świnia Góra\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2322,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/107017268429158941935/photos\\\"\\u003ePiotr Czerski Pstrąg\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAq_R2Bb9xMuuUI2UZihJtASWq4X_iB-dd-SgwRWdP4qe-n-ImTxA9RgmnmVib00qLnVxgcwWJIYFD2VxFZs7rO_-f11TkFP5zDDSXoBCih57Kq9KaVdn3fo1LVj7UPBTnEhCuvdheKIcNVuMkqG7MmWCgGhRq7zHke-MXtrGktMwchSHA6g1kVw\",\n" +
                "               \"width\" : 4128\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJJbnCM14wGEcRN1ksTwjGDjY\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"3P54+46 Świnia Góra\",\n" +
                "            \"global_code\" : \"9G323P54+46\"\n" +
                "         },\n" +
                "         \"rating\" : 4.5,\n" +
                "         \"reference\" : \"ChIJJbnCM14wGEcRN1ksTwjGDjY\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Świnia Góra, Bliżyn\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.8611224,\n" +
                "               \"lng\" : 20.6175605\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.86257027989272,\n" +
                "                  \"lng\" : 20.61945132989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.85987062010727,\n" +
                "                  \"lng\" : 20.61675167010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"7a2a08f0a1209b767f1875ce884eb627d3579593\",\n" +
                "         \"name\" : \"Kadzielnia\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 4000,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/114860223429916697545/photos\\\"\\u003eWaldemar Kołba\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAQFmOQcx6sLAugVZJmbRnOL-S8uL86VORJVS0NvGspHA3Vgn09J4v0n4AHE8euSAFhQNZ6ovz4Iqf7dZUQxw0h6BcQhovkLxgKZzzRQlrLXDrbD6aesxx3g7dLZVTY00DEhDsbBa6W_boeTX4EGj2VpPBGhSoU6t_bZRlIWTnPA1nxhqqbguTUQ\",\n" +
                "               \"width\" : 6016\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJM2W8CfUnGEcRYlGzIjPh2a0\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"VJ69+C2 Kielce\",\n" +
                "            \"global_code\" : \"9G22VJ69+C2\"\n" +
                "         },\n" +
                "         \"rating\" : 4.7,\n" +
                "         \"reference\" : \"ChIJM2W8CfUnGEcRYlGzIjPh2a0\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"aleja Legionów, Kielce\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.8545151,\n" +
                "               \"lng\" : 20.6452738\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.85586492989272,\n" +
                "                  \"lng\" : 20.64662362989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.85316527010727,\n" +
                "                  \"lng\" : 20.64392397010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"a93d1cb9cd322658399797b7b07cc941f8c7e071\",\n" +
                "         \"name\" : \"Rezerwat Geologiczny Wietrznia im. Zbigniewa Rubinowskiego\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 3024,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/117365972323742959261/photos\\\"\\u003eTomasz Kochman\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAACdvaOvHw4yqIqyI0oOT5upJ8-VkdX5jODFl1cVipwzjMdZoBQztTH_zJd_1poHZEAgCIcPQr-IexFbYmKn9ZE1hyWM2xQbPKVxvOowjfdFpJQYq4iiIZRJd4xxCV_j9fEhA3tozeBn5Naroz7fEWH6TTGhRJHBXms9hsM4mT1u3ltILlzLUIog\",\n" +
                "               \"width\" : 4032\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJeRAxX4cnGEcRDEvc4sovK8M\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"VJ3W+R4 Kielce\",\n" +
                "            \"global_code\" : \"9G22VJ3W+R4\"\n" +
                "         },\n" +
                "         \"rating\" : 4.7,\n" +
                "         \"reference\" : \"ChIJeRAxX4cnGEcRDEvc4sovK8M\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"50°51'20.0\\\"N 20°38'28., 0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.9982541,\n" +
                "               \"lng\" : 20.8691851\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.99960392989273,\n" +
                "                  \"lng\" : 20.87053492989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.99690427010728,\n" +
                "                  \"lng\" : 20.86783527010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"80d2836168244cb4b00af50a0a7891c6c1bcf687\",\n" +
                "         \"name\" : \"Kamień Michniowski\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2160,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/113044245996091545888/photos\\\"\\u003eLongin Fikcyjny\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAXn2L2NLm3VYOkOXGFL2_i4H4YAxqKYeWv5mqUUwgj6lHeRj6F2cPRAreKO8-danHTFMACWPH6W-3X9ZgvB_EIuZI_CImsETMkiEOenu8qCSD0KEg5q8LuihCAVgYBT_DEhAWeYYtpUyOR64Q4Oh89KcRGhRxmt-ayEgpHJYWpk1f7f88-zAUYQ\",\n" +
                "               \"width\" : 3840\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJ_Y9r68U8GEcRzLPrrhts28Q\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"XVX9+8M Suchedniów\",\n" +
                "            \"global_code\" : \"9G22XVX9+8M\"\n" +
                "         },\n" +
                "         \"rating\" : 4.7,\n" +
                "         \"reference\" : \"ChIJ_Y9r68U8GEcRzLPrrhts28Q\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Michniów\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.847222,\n" +
                "               \"lng\" : 20.358889\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.84829254999998,\n" +
                "                  \"lng\" : 20.3599493\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.84401035000001,\n" +
                "                  \"lng\" : 20.3557081\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"3713b412fd55c593aa1a364c3874027f795162f7\",\n" +
                "         \"name\" : \"Góra Hukka\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 1152,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115231948761203424320/photos\\\"\\u003epawel bujak\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAzgh4_om6--fFvuLto2xqzTNWPJGrjqYV4ZtyICrFFPJk3GsFGRifQ65t2Mg-2Tm2uFI4Lfiau4WLzZGGXzOeP4r_yolLTvjo0aM3TpJm6b5_9qFuVPO8p64Sr9p1jQToEhB4eeuor2F4aaeKfJD3MOkHGhRNkfDPiKZTOvWqRpPfDLiXsWwX-Q\",\n" +
                "               \"width\" : 2048\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJnbYqnLyBF0cRhiMBFd_TU9U\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"R9W5+VH Miedzianka\",\n" +
                "            \"global_code\" : \"9G22R9W5+VH\"\n" +
                "         },\n" +
                "         \"rating\" : 4.8,\n" +
                "         \"reference\" : \"ChIJnbYqnLyBF0cRhiMBFd_TU9U\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"50°50'50.0\\\"N 20°21'32., 0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 51.0165355,\n" +
                "               \"lng\" : 20.9702612\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.01825587989272,\n" +
                "                  \"lng\" : 20.97124392989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.01555622010728,\n" +
                "                  \"lng\" : 20.96854427010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"59080a2f1b830095b3fdafd8c8237c6bf5c5c304\",\n" +
                "         \"name\" : \"Rezerwat Wykus\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2448,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115273731700438721691/photos\\\"\\u003ePaweł Sawicki\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAATlDICKWM_3dSj1GTVdXKxsb_fz8lkypH-ZKq14Oo-S2LAX_UKmVvjFHpr3zywgt8RQVdK6h8finf5HbfpFKlWXW6OOLVNGtkekq4pN3KEG-t_RXjaQRSumoNJ_EyJbfFEhC7wAyZiai30qjaVnUe4MKsGhSpfW-zuo6E_PrPe3UgcNUdm7d6hQ\",\n" +
                "               \"width\" : 3264\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJvSLXRIE9GEcREdJsc82oq9o\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"2X8C+J4 Suchedniów\",\n" +
                "            \"global_code\" : \"9G322X8C+J4\"\n" +
                "         },\n" +
                "         \"rating\" : 4.8,\n" +
                "         \"reference\" : \"ChIJvSLXRIE9GEcREdJsc82oq9o\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"26-010\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.75,\n" +
                "               \"lng\" : 20.85\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.75134982989272,\n" +
                "                  \"lng\" : 20.85134982989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.74865017010728,\n" +
                "                  \"lng\" : 20.84865017010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"743ee94092d22e35681f94cc0f7b7d83280fe9c5\",\n" +
                "         \"name\" : \"Białe Ługi\",\n" +
                "         \"place_id\" : \"ChIJKUKto2f0F0cR6AdWZ-BE5KM\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"QR2X+2X Wymyslow\",\n" +
                "            \"global_code\" : \"9G22QR2X+2X\"\n" +
                "         },\n" +
                "         \"rating\" : 0,\n" +
                "         \"reference\" : \"ChIJKUKto2f0F0cR6AdWZ-BE5KM\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Daleszyce\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.972778,\n" +
                "               \"lng\" : 20.386389\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.9747133,\n" +
                "                  \"lng\" : 20.3901969\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.9669721,\n" +
                "                  \"lng\" : 20.3851197\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"9002605b6bcc7e9c59a69a9816b4f4b0ca621fbc\",\n" +
                "         \"name\" : \"Rezerwat geologiczno-leśny Perzowa Góra\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 1536,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115519946859413221661/photos\\\"\\u003eMałgosia N.\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAA0jIlwMwhF1A0PywLUQSUDAAoVmRc01dfUDF7L6dwGOLC6gj09rA87HhWudxE9iY6Z3B3dNmkN6ujge19ZCKRmYdl4UQc8dFCCkVoYdPyM3Qg_tNP5tHNga4fgLYPx4hCEhD6f2eCPGz533dwqlAFsBC_GhS9jYoiHe21VXWSXQ8ewh_nJKkobg\",\n" +
                "               \"width\" : 2048\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJdwhtKrrUGUcRAEyuxqa5l88\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"X9FP+4H Hucisko\",\n" +
                "            \"global_code\" : \"9G22X9FP+4H\"\n" +
                "         },\n" +
                "         \"rating\" : 4.7,\n" +
                "         \"reference\" : \"ChIJdwhtKrrUGUcRAEyuxqa5l88\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"50°58'22.0\\\"N 20°23'11., 0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.886643,\n" +
                "               \"lng\" : 20.5879778\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.88751017989271,\n" +
                "                  \"lng\" : 20.58975742989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.88481052010727,\n" +
                "                  \"lng\" : 20.58705777010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"2742602315831ecb3002b37664321b009bed96e4\",\n" +
                "         \"name\" : \"Rezerwat Skalny Ślichowice\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 1836,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/106543626182757131236/photos\\\"\\u003eA Google User\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAARvwpxiBhkQkABa1Nl9tYtCM6AxldUZCpux1NsElP-Wv5u_lwPTVwXfxfOVWHvl-pLjx4lCG8XyBhmjlmn1Ce3kDQVJTsdTqrA8x23YZDL0Rmid1_MbjNdBjmeQnK1lnDEhDoUOMVxJ6R4hVtMqGJMU2SGhQB5I7iil-LRD6L8MEkyvyWbAT9Ig\",\n" +
                "               \"width\" : 3264\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJZYxo1BIoGEcR0zLFkpJDqJk\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"VHPQ+M5 Kielce\",\n" +
                "            \"global_code\" : \"9G22VHPQ+M5\"\n" +
                "         },\n" +
                "         \"rating\" : 4.5,\n" +
                "         \"reference\" : \"ChIJZYxo1BIoGEcR0zLFkpJDqJk\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Kazimierza Wielkiego, Kielce\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 51.129906,\n" +
                "               \"lng\" : 20.4123432\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.13133072989272,\n" +
                "                  \"lng\" : 20.4163137\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.12863107010728,\n" +
                "                  \"lng\" : 20.4110197\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"e75a426975e7df1f9ffceb1161ece4d2e3a85664\",\n" +
                "         \"name\" : \"Pomnik Przyrody Skałki \\\"Piekło\\\"\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2336,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/109779378965476169254/photos\\\"\\u003eWaldemar Zaborski\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAA9GkoaCNk77eWyH68QQBOFChckKFpfZw5a4aw_1EG6PMIZf44fRMY6Wa3FHZUfkr6_XG1C1Z7hlY85ly4kH6mhwJgqkTD-CHR2L1p_PwtK-aMm2c62R50zQwP0zwF0nxREhAji7OdMVcILI655YPUsYZSGhTrFFWWSSOwlZUWo_LLfFMfB_qGFQ\",\n" +
                "               \"width\" : 4160\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJt4nPEjvMGUcR_WPpZqNGyJo\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"4CH6+XW Piekło\",\n" +
                "            \"global_code\" : \"9G324CH6+XW\"\n" +
                "         },\n" +
                "         \"rating\" : 4.1,\n" +
                "         \"reference\" : \"ChIJt4nPEjvMGUcR_WPpZqNGyJo\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Końskie\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.8444941,\n" +
                "               \"lng\" : 20.5729616\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.84584392989272,\n" +
                "                  \"lng\" : 20.57431142989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.84314427010728,\n" +
                "                  \"lng\" : 20.57161177010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"1ba18367cc92a748f161ba1c66f189f35615843a\",\n" +
                "         \"name\" : \"Rezerwat Biesak-Białogon\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2988,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/100335817227954339263/photos\\\"\\u003eDariusz Mazurek\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAWzPPy3Jge9vpsK6IM6zzR2YIFFBgLMrPempmDwz_hPiD6t13CLMlVexdt6ecRjuBmI5_JOTqK0pRNrIw3LPVxsyEmipRzC08UCNzbwzvRsKhG43giVMQY_qnCvi2_TQkEhAKNubkdsrkpvaF9TSgwYmfGhT00RprVQ0uRbFuWG5YzNT6rZ_dGg\",\n" +
                "               \"width\" : 5312\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJqf3Ku9-HF0cRVx0BK4vxBEs\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"RHVF+Q5 Kielce\",\n" +
                "            \"global_code\" : \"9G22RHVF+Q5\"\n" +
                "         },\n" +
                "         \"rating\" : 4.6,\n" +
                "         \"reference\" : \"ChIJqf3Ku9-HF0cRVx0BK4vxBEs\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Kielce\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 51.146944,\n" +
                "               \"lng\" : 20.6625\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.14805635,\n" +
                "                  \"lng\" : 20.67126135\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.14360695,\n" +
                "                  \"lng\" : 20.65957955\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"1953ce536175e80f424cb265cb9114adcfedb232\",\n" +
                "         \"name\" : \"Gagaty Sołtykowskie\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 3456,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/109972474990381029321/photos\\\"\\u003eJan Jelonek\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAUMxGedBliIuMqor6om07qffHhE3oNFTrHuLmxFiB_XMWUme5EflxapL4AUc0OefApSGwj13mlHOdyXHjqPeONbwTV-EKs1dImDbBzdxh3DwAZ88AYSTxukyhiE08KYWfEhAsjTQR67eJwlPa3Hz0bRgsGhQsEjbhywcRq2jbxCiQULn5iM8VHg\",\n" +
                "               \"width\" : 5184\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJj8e4DC80GEcRr3EWnqbwPP4\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"4MW6+QX Wólka Plebańska\",\n" +
                "            \"global_code\" : \"9G324MW6+QX\"\n" +
                "         },\n" +
                "         \"rating\" : 4.7,\n" +
                "         \"reference\" : \"ChIJj8e4DC80GEcRr3EWnqbwPP4\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"51°08'49.0\\\"N 20°39'45., 0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.9624512,\n" +
                "               \"lng\" : 20.4753902\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.96399730000001,\n" +
                "                  \"lng\" : 20.47745935\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.95781289999999,\n" +
                "                  \"lng\" : 20.46918275\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"02c8ccb6b9c00e206189a70d8a0f78f8caecd66f\",\n" +
                "         \"name\" : \"Barania Góra\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 3488,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/106695965991598788941/photos\\\"\\u003eMaciej Majchrzak\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAXBeDhx8H7H41pEEY0t2ffsPc6hjBi1nIPWP4KqJ2J2A1jtqGYdYYPC3hS3Web0s0zRnfIZz0ucTgFbLXKiy4po59C-6vXvNUXbSdhZKKgyxf5g6EvbaYDeXA1IFtDlZ4EhA7mtgdO9eqxpzNc3fT1psLGhRjL2nbFC1M8DFf4Y4IcmkHCOO0MA\",\n" +
                "               \"width\" : 3488\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJWbyssYQrGEcRUTJS8lpn3Hw\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"XF6G+X5 Oblęgorek\",\n" +
                "            \"global_code\" : \"9G22XF6G+X5\"\n" +
                "         },\n" +
                "         \"rating\" : 4.3,\n" +
                "         \"reference\" : \"ChIJWbyssYQrGEcRUTJS8lpn3Hw\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Oblęgorek\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.7402778,\n" +
                "               \"lng\" : 20.6538889\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.74213047989272,\n" +
                "                  \"lng\" : 20.6561204\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.73943082010727,\n" +
                "                  \"lng\" : 20.64719440000001\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"82133adb8a42292f963a2a5b134eb5d06578c5ad\",\n" +
                "         \"name\" : \"Radomice\",\n" +
                "         \"place_id\" : \"ChIJTeNRHjyMF0cRsGNUYRwr4b0\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"PMR3+4H Łabędziów\",\n" +
                "            \"global_code\" : \"9G22PMR3+4H\"\n" +
                "         },\n" +
                "         \"rating\" : 3.7,\n" +
                "         \"reference\" : \"ChIJTeNRHjyMF0cRsGNUYRwr4b0\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"50°44'25.0\\\"N 20°39'14., 0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.799167,\n" +
                "               \"lng\" : 20.450833\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.80051682989272,\n" +
                "                  \"lng\" : 20.45218282989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.79781717010727,\n" +
                "                  \"lng\" : 20.44948317010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"726aec92992fa90af813ceea66fb2e8c344eb327\",\n" +
                "         \"name\" : \"Góra Rzepka\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2448,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/103269216376215432250/photos\\\"\\u003eŁukasz Andrzej\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAk1wWOIEmVkXkEd-hhos4XvBsscFOBbPZZ7vgIv5qVB6OOFzsez-fNbhuo0XCg3u2bgKs5xpycOylOPQFkf5HIkPv-n9pDFY4xg6cuLdRYpX070u0G7k08-M4Ic5ES7hpEhB45ggDdyneV1f6VnKfUvuaGhTUMY8-lYduSlrOePMbFBSD7YS5gA\",\n" +
                "               \"width\" : 3264\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJmbmRuSaEF0cRWyjZDd9hoBo\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"QFX2+M8 Chęciny\",\n" +
                "            \"global_code\" : \"9G22QFX2+M8\"\n" +
                "         },\n" +
                "         \"rating\" : 4.9,\n" +
                "         \"reference\" : \"ChIJmbmRuSaEF0cRWyjZDd9hoBo\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"50°47'57.0\\\"N 20°27'03., 0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.9691667,\n" +
                "               \"lng\" : 20.6905556\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.97051652989272,\n" +
                "                  \"lng\" : 20.69190542989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.96781687010728,\n" +
                "                  \"lng\" : 20.68920577010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"789bd1b499188020da2cd705f85b429214b10025\",\n" +
                "         \"name\" : \"Rezerwat przyrody Zachełmie\",\n" +
                "         \"opening_hours\" : {\n" +
                "            \"open_now\" : true\n" +
                "         },\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 3072,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/108379612667432473875/photos\\\"\\u003eKrzysztof Kaminski\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAJ1ezAy0ZEw69qxnxnWkkQsBJGRAxuEl58A3eSGvwaUfAISPZlwcmenvN8VC9mEA4i-aJdpO0VpmAp22btIXLuWvoiRLPhEGHaSNNm0NBNxkHU7l7lAixbEGHaFEkXn7cEhDRWlptFB_306ZKy1P_tl7YGhRXVChzMdXvBoDtUi3Utir6BYSUhQ\",\n" +
                "               \"width\" : 4096\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJHXKAGJslGEcRwr3NIFzBPFw\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"XM9R+M6 Zachełmie\",\n" +
                "            \"global_code\" : \"9G22XM9R+M6\"\n" +
                "         },\n" +
                "         \"rating\" : 4.8,\n" +
                "         \"reference\" : \"ChIJHXKAGJslGEcRwr3NIFzBPFw\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"Belno\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.9594408,\n" +
                "               \"lng\" : 20.7206301\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.96139692989272,\n" +
                "                  \"lng\" : 20.72133134999999\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.95869727010727,\n" +
                "                  \"lng\" : 20.71852635\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"d9c508611c22bd399d2632d7ec8132098ab27b15\",\n" +
                "         \"name\" : \"Rezerwat Barcza\",\n" +
                "         \"opening_hours\" : {},\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2620,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/109126249294960698786/photos\\\"\\u003ePedro Fernandes\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAA0kt4LpzgJxHbqcHlr9FszusbWV0J3fxKs1e4R-IX__XkjKXvF1ZlBXXoxnWgErmByj_1mAc0jVYayb5-kjNIM3HRzlQMyDhEkwOSN26BdAqnXd2w_VZayA2gY629KycEhB-24oDkzPgiyBHoJVT0duoGhTee6hbfnX1shPmf8XrrYB8StWAuQ\",\n" +
                "               \"width\" : 4656\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJgzUawrElGEcRwZKG3UtxPgs\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"XP5C+Q7 Masłów Drugi\",\n" +
                "            \"global_code\" : \"9G22XP5C+Q7\"\n" +
                "         },\n" +
                "         \"rating\" : 4.7,\n" +
                "         \"reference\" : \"ChIJgzUawrElGEcRwZKG3UtxPgs\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.968056,\n" +
                "               \"lng\" : 20.576944\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.96958097989272,\n" +
                "                  \"lng\" : 20.57859047989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.96688132010728,\n" +
                "                  \"lng\" : 20.57589082010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"1ca7439f93280c5592847093532dfd24acdf85e9\",\n" +
                "         \"name\" : \"Kręgi Kamienne\",\n" +
                "         \"opening_hours\" : {},\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 3024,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/116115823909096567666/photos\\\"\\u003ePrzemyslaw Wolak\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAaKlbrF44dSy_frPps050K4E3JXfo1-x0uZ--uUcjdmMiUqfjvZXiJKBZJGAFXFHE7jksspn5rS5xAT8vmgAuYiF7JOI5rWZBYp8NCVH8iCAJAt6-X1ZctL5NwAN668pIEhCiClBQFChjotEgkoHd0Ci3GhQkEr2c9Vz8oJv-BnznVfYhO7xayQ\",\n" +
                "               \"width\" : 4032\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJbWaji8YuGEcRpR9BDud6Tk8\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"XH9G+6Q Tumlin-Podgród\",\n" +
                "            \"global_code\" : \"9G22XH9G+6Q\"\n" +
                "         },\n" +
                "         \"rating\" : 4.3,\n" +
                "         \"reference\" : \"ChIJbWaji8YuGEcRpR9BDud6Tk8\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"50°58'05.0\\\"N 20°34'37., 0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 51.0675,\n" +
                "               \"lng\" : 20.567222\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.06870577989271,\n" +
                "                  \"lng\" : 20.5718885\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.06600612010727,\n" +
                "                  \"lng\" : 20.5656665\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"41605a158853f5d60ddad4d7610b2eb46461f340\",\n" +
                "         \"name\" : \"Górna Krasna\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2457,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/115413418253249005075/photos\\\"\\u003eGrzegorz Pięta\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAZsMIKUYNTDHCutnK3kNpUKcp3W_3tsxpOoaFDWrkQ3XCnBDSKLjsXwSntkxco9510riwmm3966U68_tORvDvXbZikTykiDwwvhZEG7BQUyggLf6Cgp-tojihMeh3ub4EEhAHAG52GsOs-zk74MIzOAfoGhSHmC-1cCe0xgOoJGyFezudT3TAwA\",\n" +
                "               \"width\" : 3276\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJpZkUVUIyGEcRcICs2ReIpTY\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"3H88+XV Luta\",\n" +
                "            \"global_code\" : \"9G323H88+XV\"\n" +
                "         },\n" +
                "         \"rating\" : 4.6,\n" +
                "         \"reference\" : \"ChIJpZkUVUIyGEcRcICs2ReIpTY\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"0\"\n" +
                "      },\n" +
                "      {\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 50.771389,\n" +
                "               \"lng\" : 20.8955559\n" +
                "            },\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 50.77273882989272,\n" +
                "                  \"lng\" : 20.89690572989272\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 50.77003917010727,\n" +
                "                  \"lng\" : 20.89420607010728\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"icon\" : \"https://maps.gstatic.com/mapfiles/place_api/icons/generic_recreational-71.png\",\n" +
                "         \"id\" : \"a84fb9374e8dfdf2a6f9589b9d3f8f8f190af564\",\n" +
                "         \"name\" : \"Cisów im. prof. Zygmunta Czubińskiego\",\n" +
                "         \"photos\" : [\n" +
                "            {\n" +
                "               \"height\" : 2322,\n" +
                "               \"html_attributions\" : [\n" +
                "                  \"\\u003ca href=\\\"https://maps.google.com/maps/contrib/113246103238427104178/photos\\\"\\u003eAndrzej\\u003c/a\\u003e\"\n" +
                "               ],\n" +
                "               \"photo_reference\" : \"CmRaAAAAXiJsX_-j2C_WUUjCKTExpHCzsoUSGFEcjUgEl6W5ApZT_ho_DK0Q9bnho5v0cQ84phhoPC0q9KT3N5updCkvAp17U68b-UNEX1hyQk0Qge-J0VSfe-RaxJasjRHD3tT_EhAXu-YtDa8c8IM-gHpTatVBGhRJcKfRHwvx8Ldj2Q95lEzjRtUr7Q\",\n" +
                "               \"width\" : 4128\n" +
                "            }\n" +
                "         ],\n" +
                "         \"place_id\" : \"ChIJyzy3tc71F0cR_ST_PJb4Mpo\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"QVCW+H6 Cisów\",\n" +
                "            \"global_code\" : \"9G22QVCW+H6\"\n" +
                "         },\n" +
                "         \"rating\" : 5,\n" +
                "         \"reference\" : \"ChIJyzy3tc71F0cR_ST_PJb4Mpo\",\n" +
                "         \"scope\" : \"GOOGLE\",\n" +
                "         \"types\" : [ \"park\", \"point_of_interest\", \"establishment\" ],\n" +
                "         \"vicinity\" : \"50°46'17.0\\\"N 20°53'44., 0\"\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"dada\"\n" +
                "}";
//        jsonList.add(mRawPlacesData); // do testów

        for (int i = 0; i < mUrl.length; i++) {
            GetRawData getRawData = new GetRawData();
            mRawPlacesData = getRawData.readUrl(mUrl[i]);
            jsonList.add(mRawPlacesData);

            for (; ; ) {
                try {
                    JSONObject jsonObject = new JSONObject(mRawPlacesData);
                    if (jsonObject.has("next_page_token")) {
                        GetRawData getData = new GetRawData();
                        String token = jsonObject.getString("next_page_token");
                        Log.d(TAG, " contain next_page_token " + token);

                        Thread.sleep(2000);
                        mRawPlacesData = getData.readUrl(buildURL(token));
                        jsonList.add(mRawPlacesData);
                        Log.d(TAG, " next places " + mRawPlacesData);
                    } else break;
                } catch (JSONException e) {
                    Log.e(TAG, "JSONException " + e.getMessage());
                } catch (InterruptedException e) {
                    Log.e(TAG, "InterruptedException " + e.getMessage());
                }
            }
        }

        return jsonList;
    }

    @Override
    protected void onPostExecute(List list) {
        ArrayList<NearbyPlace> nearbyPlaceList;
        NearbyJsonParser parser = new NearbyJsonParser();
        nearbyPlaceList = parser.parse(list);
        if (nearbyPlaceList == null){
            delegate.onPlacesAvailable(null, null, null);
        }else {
            showNearbyPlaces(nearbyPlaceList);
            delegate.onPlacesAvailable(mWayPoints, mMarkerArray, nearbyPlaceArrayList);
        }
    }

    private void showNearbyPlaces(ArrayList<NearbyPlace> nearbyPlaceList) {
        Log.d(TAG, "showNearbyPlaces: found " + nearbyPlaceList.size() + " places");
        nearbyPlaceArrayList = new ArrayList<>();
        int counter = 0; // licznik znacznikow
        for (int i = 0; i < nearbyPlaceList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            NearbyPlace nearbyPlace = nearbyPlaceList.get(i);
            boolean isVisited = visited(nearbyPlace);
            if (nearbyPlace.getRating() >= 4.5 && !isVisited) {
                markerOptions.position(nearbyPlace.getLocation())
                        .title(nearbyPlace.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker))
                        .alpha(0.8f)
                        .snippet("Średnia ocena " + nearbyPlace.getRating());
//                        .snippet("Okolica: " + nearbyPlace.getVicinity() + " Ocena " + nearbyPlace.getRating());


                Marker marker = mMap.addMarker(markerOptions);
                if (!mManualMode) {
                    marker.setTag(counter);
                }
                mMarkerArray.add(marker);
                Log.d(TAG, "showNearbyPlaces: rating " + nearbyPlace.getRating());

                nearbyPlaceArrayList.add(nearbyPlace);
                mWayPoints += nearbyPlace.getLocation().latitude + "," + nearbyPlace.getLocation().longitude + "|";
                counter++;
                if (!mManualMode) {
                    if (nearbyPlaceArrayList.size() >= 19)
                        break;
                }
            }
        }
        Log.d(TAG, "showNearbyPlaces: waypoints before substring " + mWayPoints);
        mWayPoints = mWayPoints.substring(0, mWayPoints.length() - 1);
        Log.d(TAG, "showNearbyPlaces: waypoints after substring " + mWayPoints);
    }

    private String buildURL(String nextPageToken) {
        StringBuilder url = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?pagetoken=");
        url.append(nextPageToken);
        url.append("&key=AIzaSyCGO8Y-5XFNrPEApOGPbJluQfa68kh4IWo");

        return url.toString();
    }

    private boolean visited(NearbyPlace place) {
        for (NearbyPlace pl : mVisited) {
            if (pl.getName().equals(place.getName()))
                return true;
        }
        return false;
    }

}

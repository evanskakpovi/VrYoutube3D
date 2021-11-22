package com.ekm.youtubevr3dvideosprod;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

import tools.data;

/**
 * Created by work on 3/13/15.
 */
public class _tasks {
    public Context _this;

    String url;
    final int maxInt = 30;
    final String max = "maxResults=" + maxInt;
    final String searchUrl = "https://www.googleapis.com/youtube/v3/search?";
    final String channelsUrl = "https://www.googleapis.com/youtube/v3/channels?part=contentDetails&mine=true";
    final String playlistItem = "https://www.googleapis.com/youtube/v3/playlistItems?";
    final String and = "&";
    final String term = "q=";
    final String iDplay = "ids=";
    static String MyAcessTokenData = "access_token";
    final String key = "key=AIzaSyA6Sp0Jo0PdZmY0VYXwDSGsTk16yHcjEYA";
    final String part = "part=snippet";
    final String channelId = "channelId=";
    final String playlistId = "playlistId=";
    final String pageToken = "pageToken=";
    final String extras = "videoDimension=3d&type=video,channel,playlist";


    private boolean scroll;
    ArrayList<String> pages;
    List<data> myData;
    public static List<data> myDataAll;
    ArrayList<String> ids;
    ArrayList<Integer> idsInt;
    String accountName;
    private SharedPreferences pref;

    public _tasks(Context _this){
    this._this = _this;
}


}

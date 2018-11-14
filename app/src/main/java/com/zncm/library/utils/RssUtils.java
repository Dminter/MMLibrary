package com.zncm.library.utils;

import android.util.Log;
import android.util.Xml;

import com.zncm.library.utils.saxrssreader.RssItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * by jiaomx on 2018/8/1 14:02
 */
public class RssUtils {


    public static ArrayList<RssItem>  parseFeed(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        ArrayList<RssItem> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);
            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if (name == null)
                    continue;

                if (eventType == XmlPullParser.END_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if (name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }


                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }


                if (isItem){
                    if (name.equalsIgnoreCase("title")) {
                        title = result;
                    } else if (name.equalsIgnoreCase("link")) {
                        link = result;
                    } else if (name.equalsIgnoreCase("description")) {
                        description = result;
                    }

                }

                if (title != null && link != null && description != null) {
                    if (isItem) {
                        RssItem item = new RssItem(title, link, description);
                        items.add(item);
                    }
                    XUtil.debug("RssItem:"+title+" "+link+" "+description);
                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
                XUtil.debug("");
            }

            return items;
        } finally {
            inputStream.close();
        }
    }


}

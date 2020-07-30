package com.ctv.settings.about.Bean;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AboutTvXml {

    public static List<AboutTvInfo> parseFile(File pfile) throws XmlPullParserException,
            IOException {
        List<AboutTvInfo> infoList = null;
        AboutTvInfo info = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new FileReader(pfile));
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    infoList = new ArrayList<AboutTvInfo>();
                    break;
                case XmlPullParser.START_TAG:
                    if ("info".equals(parser.getName())) {
                        info = new AboutTvInfo();
                    } else if ("key".equals(parser.getName())) {
                        info.setAboutString(parser.nextText());
                    } else if ("value".equals(parser.getName())) {
                        info.setAboutValue(parser.nextText());
                    } else if ("visibility".equals(parser.getName())) {
                        info.setVisibility(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if ("info".equals(parser.getName())) {
                        infoList.add(info);
                    }
                    break;
            }
            event = parser.next();
        }
        return infoList;
    }
}

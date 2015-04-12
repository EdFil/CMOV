package com.lab.cmov.exi_3_marshallingandun;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {

    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            // ignore namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readCurrent(parser);
        } finally {
            in.close();
        }
    }

    private List readCurrent(XmlPullParser parser) throws XmlPullParserException, IOException {
        List currents = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "current");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("wind")) {
                currents.add(readWind(parser));
            } else {
                skip(parser);
            }
        }
        return currents;
    }

    public static class Current {
        public final String speed;

        private Current(String speed) {
            this.speed = speed;
        }
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Current readWind(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "wind");
        String speed = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("speed")) {
                speed = readSpeed(parser);
//            } else if (name.equals("summary")) {
//                summary = readSummary(parser);
//            } else if (name.equals("link")) {
//                link = readLink(parser);
//            } else {
//                skip(parser);
            }
        }
        return new Current(speed);
    }

    // Processes title tags in the feed.
    private String readSpeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "speed");
        String speed = readAttributeValue(parser);

        try {
            parser.require(XmlPullParser.END_TAG, ns, "speed");
        } catch (Exception e) {
            Log.i("SPEED: " , "No ending tag on Speed");
        }
        return speed;
    }

    // For the tags title and summary, extracts their text values.
    private String readAttributeValue(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = result = parser.getAttributeValue(1);
        parser.nextTag();
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}

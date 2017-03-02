package com.zncm.library.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;

import java.net.URISyntaxException;

public class UriUtils {

    public static Intent parseIntentUri(String uri, int flags) throws URISyntaxException {
        int i = 0;
        try {
            final boolean androidApp = uri.startsWith("android-app:");

            if ((flags & (Intent.URI_INTENT_SCHEME | Intent.URI_ANDROID_APP_SCHEME)) != 0) {
                if (!uri.startsWith("intent:") && !androidApp) {
                    Intent intent = new Intent();
                    try {
                        intent.setData(Uri.parse(uri));
                    } catch (IllegalArgumentException e) {
                        throw new URISyntaxException(uri, e.getMessage());
                    }
                    return intent;
                }
            }

            i = uri.lastIndexOf("#");
            if (i == -1) {
                if (!androidApp) {
                    return new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                }

            } else if (!uri.startsWith("#Intent;", i)) {
                if (!androidApp) {
                    throw new URISyntaxException(uri, "format Intent URI Error");
                } else {
                    i = -1;
                }
            }

            Intent intent = new Intent();
            Intent baseIntent = intent;
            boolean explicitAction = false;
            boolean inSelector = false;

            String scheme = null;
            String data;
            if (i >= 0) {
                data = uri.substring(0, i);
                i += 8;
            } else {
                data = uri;
            }

            while (i >= 0 && !uri.startsWith("end", i)) {
                int eq = uri.indexOf('=', i);
                if (eq < 0) eq = i - 1;
                int semi = uri.indexOf(';', i);
                String value = eq < semi ? Uri.decode(uri.substring(eq + 1, semi)) : "";

                if (uri.startsWith("action=", i)) {
                    intent.setAction(value);
                    if (!inSelector) {
                        explicitAction = true;
                    }
                } else if (uri.startsWith("category=", i)) {
                    intent.addCategory(value);
                } else if (uri.startsWith("launchFlags=", i)) {
                    intent.addFlags(Integer.decode(value));
                } else if (uri.startsWith("package=", i)) {
                    intent.setPackage(value);
                } else if (uri.startsWith("component=", i)) {
                    intent.setComponent(ComponentName.unflattenFromString(value));
                } else if (uri.startsWith("scheme=", i)) {
                    if (inSelector) {
                        intent.setData(Uri.parse(value + ":"));
                    } else {
                        scheme = value;
                    }
                } else if (uri.startsWith("sourceBounds=", i)) {
                    intent.setSourceBounds(Rect.unflattenFromString(value));
                } else if (semi == (i + 3) && uri.startsWith("SEL", i)) {
                    intent = new Intent();
                    inSelector = true;
                } else {
                    String key = Uri.decode(uri.substring(i + 2, eq));
                    if (uri.startsWith("S.", i)) {
                        intent.putExtra(key, value);
                    } else if (uri.startsWith("B.", i)) {
                        intent.putExtra(key, Boolean.parseBoolean(value));
                    } else if (uri.startsWith("b.", i)) {
                        intent.putExtra(key, Byte.parseByte(value));
                    } else if (uri.startsWith("c.", i)) {
                        intent.putExtra(key, value.charAt(0));
                    } else if (uri.startsWith("d.", i)) {
                        intent.putExtra(key, Double.parseDouble(value));
                    } else if (uri.startsWith("f.", i)) {
                        intent.putExtra(key, Float.parseFloat(value));
                    } else if (uri.startsWith("i.", i)) {
                        intent.putExtra(key, Integer.parseInt(value));
                    } else if (uri.startsWith("l.", i)) {
                        intent.putExtra(key, Long.parseLong(value));
                    } else if (uri.startsWith("s.", i)) {
                        intent.putExtra(key, Short.parseShort(value));
                    } else throw new URISyntaxException(uri, "unknown EXTRA type", i);
                }

                i = semi + 1;
            }

            if (inSelector) {
                intent = baseIntent;
            }

            if (data.startsWith("intent:")) {
                data = data.substring(7);
                if (scheme != null) {
                    data = scheme + ':' + data;
                }
            } else if (data.startsWith("android-app:")) {
                if (data.charAt(12) == '/' && data.charAt(13) == '/') {
                    int end = data.indexOf('/', 14);
                    if (end < 0) {
                        intent.setPackage(data.substring(14));
                        if (!explicitAction) {
                            intent.setAction(Intent.ACTION_MAIN);
                        }
                        data = "";
                    } else {
                        String authority = null;
                        intent.setPackage(data.substring(14, end));
                        int newEnd;
                        if ((end + 1) < data.length()) {
                            if ((newEnd = data.indexOf('/', end + 1)) >= 0) {
                                scheme = data.substring(end + 1, newEnd);
                                end = newEnd;
                                if (end < data.length() && (newEnd = data.indexOf('/', end + 1)) >= 0) {
                                    authority = data.substring(end + 1, newEnd);
                                    end = newEnd;
                                }
                            } else {
                                scheme = data.substring(end + 1);
                            }
                        }
                        if (scheme == null) {
                            if (!explicitAction) {
                                intent.setAction(Intent.ACTION_MAIN);
                            }
                            data = "";
                        } else if (authority == null) {
                            data = scheme + ":";
                        } else {
                            data = scheme + "://" + authority + data.substring(end);
                        }
                    }
                } else {
                    data = "";
                }
            }

            if (data.length() > 0) {
                try {
                    intent.setData(Uri.parse(data));
                } catch (IllegalArgumentException e) {
                    throw new URISyntaxException(uri, e.getMessage());
                }
            }

            return intent;

        } catch (IndexOutOfBoundsException e) {
            throw new URISyntaxException(uri, "illegal Intent URI format", i);
        }
    }
}
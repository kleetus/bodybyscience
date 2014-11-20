package org.kleetus.bodybyscience;


import java.util.Locale;

public class LocaleManager {
    private static LocaleManager ourInstance = new LocaleManager();

    public static LocaleManager getInstance() {
        return ourInstance;
    }

    private boolean useMetric = false;

    private LocaleManager() {

        setLocale();

    }

    public boolean useMetric() {

        return useMetric;

    }

    private void setLocale() {

        String countryCode = Locale.getDefault().getCountry();
        switch (countryCode) {
            case "US":
                useMetric = false;
                break;
            case "LR":
                useMetric = false;
                break;
            case "MM":
                useMetric = false;
                break;
            default:
                useMetric = true;
        }

    }

}

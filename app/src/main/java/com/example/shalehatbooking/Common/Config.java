package com.example.shalehatbooking.Common;

public class Config {
    public static final String PAYPAL_CLIENT_ID="ARzu05a5AMrZnkrid9OaVdjNblYM88lL1WKWT-YrFq2UI8SCFhRC2_7wG-1tg19noHtg4ItRnjYJTEUV";

    public static String getPriceString(int priceInt) {
        switch (priceInt) {
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
            default:
                return "$$$";
        }
    }
}

package com.barclaycardus.armageddon;

/**
 * Created by Junaid on 11-Jun-16.
 */
public class QueryResponse {

    public static final String PLEASE_TRY_AGAIN = "Please try again";

    public static String getResponse() {
        if(response!=null){
            return response;
        }
            return PLEASE_TRY_AGAIN;
    }

    public static  void setResponse(String response1) {
        response = response1;
    }

    public static void reset(){
        response=PLEASE_TRY_AGAIN;
    }

    static String response;
}

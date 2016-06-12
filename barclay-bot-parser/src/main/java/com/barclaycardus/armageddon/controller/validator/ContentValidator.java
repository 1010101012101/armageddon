package com.barclaycardus.armageddon.controller.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Junaid on 12-Jun-16.
 */
public class ContentValidator {

    public static String[] phonePatterns = new String[]{"\\d{10}", "\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}", "\\d{3}-\\d{3}-\\d{4}\\s(x|(ext))\\d{3,5}", "\\(\\d{3}\\)-\\d{3}-\\d{4}"};

    public static String addressPattern = ".*[0-9]{5}(?:-[0-9]{4})?$";

    public static String emailPattern = "[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

    public static void main(String[] args){
        containsEmail("my email as abc@sasa.com");
    }
    public static String containsAddress(String value)
    {
        if(value.indexOf("to")>0){
            value = value.substring(value.indexOf(" to ") + 4);
        }
        Pattern p = Pattern.compile(addressPattern, Pattern.DOTALL);
        Matcher m = p.matcher(value);

        while(m.find()) {
            String address = m.group(0).replaceAll("[\\n\\r]+", "");
            return address;
        }
        return "";
    }
    public static Boolean validateAddress(String value)
    {
        if(value.indexOf("to")>0){
            value = value.substring(value.indexOf(" to ") + 4);
        }
        return value.matches(addressPattern);
    }

    public static String containsPhone(String value)
    {
        for(String phonePattern : phonePatterns){
            String phoneNo = containsPhone(value, phonePattern);
            if(!"".equals(phoneNo)){
                return phoneNo;
            }
        }
        return "";
    }
    public static String containsPhone(String value, String regex)
    {
        Pattern p = Pattern.compile(regex, Pattern.DOTALL);
        Matcher m = p.matcher(value);

        while(m.find()) {
            String phoneNo = m.group(0).replaceAll("[\\n\\r]+", "");
            return phoneNo;
        }
        return "";
    }

    public static String containsEmail(String value)
    {
        Pattern p = Pattern.compile(emailPattern, Pattern.DOTALL);
        Matcher m = p.matcher(value);

        while(m.find()) {
            // filter newline
            String email = m.group(0).replaceAll("[\\n\\r]+", "");
            return email;
        }
        return "";
    }

    public static Boolean validateEmail(String value)
    {
        return value.matches( "^" + emailPattern + "$");
    }

    public static Boolean validatePhone(String phoneNo)
    {
        for(String phonePattern : phonePatterns){
            if (phoneNo.matches(phonePattern)) {return true;}
        }
        return false;
    }
}

//package com.mudrahome.MHLMS.Services;
//
//import android.util.Log;
//import android.view.textclassifier.ConversationActions;
//
//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;
//
//public class SMSService {
//
//    private String ACCOUNT_SID, AUTH_TOKEN;
//
//    public void SMSService() {
//        ACCOUNT_SID = "ACd26896fff1a8bc25eb88503dcecc2e58";
//        AUTH_TOKEN = "2179a4f911834e502c7b28a6a2291572";
//    }
//
//    public void sendSMS() {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//        Message message = Message.creator(
//                new PhoneNumber(System.getenv("+919654902642")),
//                new PhoneNumber("+12056513825"),
//                "sjrfhbvksehrbvklehbvkledb").create();
//
//        Log.d("SMS service", message.getSid());
//    }
//}

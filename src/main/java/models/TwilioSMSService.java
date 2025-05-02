package models;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class TwilioSMSService {

    public static final String ACCOUNT_SID = "ACe9c27b92d1a17c90b553aaf9d521df51"; // remplace par le tien
    public static final String AUTH_TOKEN = "0ab734bd8747ad877e24627bb7e75d48"; // remplace par le tien
    public static final String TWILIO_PHONE_NUMBER = "+13527247782"; // ton numéro Twilio

    static {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    public static void sendSMS(String to, String messageBody) {
        Message message = Message.creator(
                new PhoneNumber(to),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                messageBody
        ).create();

        System.out.println("Message envoyé avec SID : " + message.getSid());
    }
}


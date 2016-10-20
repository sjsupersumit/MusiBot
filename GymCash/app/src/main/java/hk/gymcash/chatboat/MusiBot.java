package hk.gymcash.chatboat;

import java.io.InputStream;

/**
 * Created by mahender.yadav on 10/20/2016.
 */

public class MusiBot {

    private static Bot bot;

    public  String startChat(String message) {

        String response = bot.send(message);

        StringBuilder stringBuilder = new StringBuilder();
        if (response.length() > 0) {
            addText(response, stringBuilder);
        }

        // display new state message
        addText(bot.getMessage(), stringBuilder);

        return  stringBuilder.toString();


    }

    private static void addText(String response, StringBuilder stringBuilder) {
        stringBuilder.append(response).append("\n");

    }

    public String startChat(InputStream inputStream) {

        // construct a data parser
        DataParser dp = new DataParser(inputStream);

        // construct new bot with level 0 as default and given data parser
        bot = new Bot("0", dp);

       return      bot.getMessage();


    }
}

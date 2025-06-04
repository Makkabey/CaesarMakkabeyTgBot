package org.example.bot;

import java.util.Map;
import java.util.HashMap;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBot extends TelegramLongPollingBot {

    private final Map<String, String> userState = new HashMap<>();
    private final Map<String, String> textToEncrypt = new HashMap<>();
    private final Map<String, Integer> userLang = new HashMap<>();

    @Override
    public String getBotUsername() {
        return "CaesarMakkabeyBot";
    }

    @Override
    public String getBotToken() {
        return "8010726285:AAG6RsobIZf1rxup9jiEtfM4IfbZYFrEfsM";
    }

    @Override
    public void onUpdateReceived(Update update) {

        String chatID = update.getMessage().getChatId().toString();
        String text = update.getMessage().getText();
        String state = userState.getOrDefault(chatID, "");
        int lang = userLang.getOrDefault(chatID, 1); // по умолчанию английский
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatID);

        if (text.equalsIgnoreCase("/start")) {
            sendMessage.setText("Hello. You use a CaesarMakkabeyBot that can encrypt and decrypt Caesar cipher.\nIf you want to change language\n______________\n/english\n______________\n/russian\n______________\n/hebrew\n______________\nIf your language is OK, send \n/langconfirmed");
            userState.put(chatID, "waiting_for_shift");
        }

        if (text.equalsIgnoreCase("/english")) {
            userLang.put(chatID, 1);
            sendMessage.setText("Your language is english\n What do you want to do?\n Encrypt     Decrypt\n/encrypt    /decrypt");
            userState.remove(chatID);
        } else if (text.equalsIgnoreCase("/langconfirmed")) {

        } else if (text.equalsIgnoreCase("/russian")) {
            userLang.put(chatID, 2);
            sendMessage.setText("Ваш язык - русский\nЧто вы хотите?\nзашифровать     расшифровать\n/encrypt            /decrypt");
            userState.remove(chatID);
        } else if (text.equalsIgnoreCase("/hebrew")) {
            userLang.put(chatID, 3);
            sendMessage.setText("שופה שלך עיברית.מה את_ה רוצה \nלהצפין      לפענח\n/decrypt       encrypt");
            userState.remove(chatID);
        }

        // english code
        if (lang == 1) {
            char[] EnglishBigLetters = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                    'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
            };
            char[] EnglishSmallLetters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u','v', 'w', 'x', 'y', 'z'
            };

            if (text.equalsIgnoreCase("/encrypt")) {
                userState.put(chatID, "waiting_for_encrypt_text");
                sendMessage.setText("Write your text to encrypt (in on message)");
            } else if (state.equals("waiting_for_encrypt_text")) {
                textToEncrypt.put(chatID, text);
                userState.put(chatID, "waiting_for_encrypt_shift");
                sendMessage.setText("Write your movement (count)");
            } else if (state.equals("waiting_for_encrypt_shift")) {
                try {
                    int movement = Integer.parseInt(text);
                    String texttocrypt = textToEncrypt.getOrDefault(chatID, "");
                    String encryptedtext = EnglishEncrypt(texttocrypt, movement, EnglishBigLetters, EnglishSmallLetters);
                    sendMessage.setText("Encrypted text:\n" + encryptedtext);
                } catch (NumberFormatException e) {
                    sendMessage.setText("Error: movement must be a count.");
                }
                userState.remove(chatID);
            } else if (text.equalsIgnoreCase("/decrypt")) {
                userState.put(chatID, "waiting_for_decrypt_text");
                sendMessage.setText("Write text for decrypt (in one message)");
            } else if (state.equals("waiting_for_decrypt_text")) {
                String[] decrypted = EnglishDecrypt(text, EnglishBigLetters, EnglishSmallLetters);
                StringBuilder sb = new StringBuilder("Variants of decrypting:\n");
                for (int i = 0; i < decrypted.length; i++) {
                    sb.append(i).append(": ").append(decrypted[i]).append("\n");
                }
                sendMessage.setText(sb.toString());
                userState.remove(chatID);
            }
        }

        // russian code
        else if (lang == 2) {
            char[] RussianBigLetters = { 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й',
                    'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш',
                    'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я' };
            char[] RussianSmallLetters = { 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й',
                    'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш',
                    'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я' };

            if (text.equalsIgnoreCase("/encrypt")) {
                userState.put(chatID, "waiting_for_encrypt_text");
                sendMessage.setText("Напишите свой текст для шифровки (одним сообщением)");
            } else if (state.equals("waiting_for_encrypt_text")) {
                textToEncrypt.put(chatID, text);
                userState.put(chatID, "waiting_for_encrypt_shift");
                sendMessage.setText("Напишите желаемый сдвиг по алфавиту (числом)");
            } else if (state.equals("waiting_for_encrypt_shift")) {
                try {
                    int movement = Integer.parseInt(text);
                    String texttocrypt = textToEncrypt.getOrDefault(chatID, "");
                    String encryptedtext = RussianEncrypt(texttocrypt, movement, RussianBigLetters, RussianSmallLetters);
                    sendMessage.setText("Ваш зашифрованный текст:\n" + encryptedtext);
                } catch (NumberFormatException e) {
                    sendMessage.setText("Ошибка: введите число для сдвига.");
                }
                userState.remove(chatID);
            } else if (text.equalsIgnoreCase("/decrypt")) {
                userState.put(chatID, "waiting_for_decrypt_text");
                sendMessage.setText("Напишите зашифрованный текст (одним сообщением)");
            } else if (state.equals("waiting_for_decrypt_text")) {
                String[] decrypted = RussianDecrypt(text, RussianBigLetters, RussianSmallLetters);
                StringBuilder sb = new StringBuilder("Возможные варианты:\n");
                for (int i = 0; i < decrypted.length; i++) {
                    sb.append(i).append(": ").append(decrypted[i]).append("\n");
                }
                sendMessage.setText(sb.toString());
                userState.remove(chatID);
            }
        }

        if (lang==3){

            char[] HebrewLetters = {'א', 'ב', 'ג', 'ד', 'ה', 'ו', 'ז', 'ח', 'ט', 'י','כ', 'ך',
                    'ל', 'מ', 'ם', 'נ', 'ן', 'ס', 'ע', 'פ', 'ף', 'צ', 'ץ', 'ק', 'ר', 'ש', 'ת',
            };
            if (text.equalsIgnoreCase("/encrypt")) {
                userState.put(chatID, "waiting_for_encrypt_text");
                sendMessage.setText("תבתוב טקסט שלך(בהודעה 1)");
            } else if (state.equals("waiting_for_encrypt_text")) {
                textToEncrypt.put(chatID, text);
                userState.put(chatID, "waiting_for_encrypt_shift");
                sendMessage.setText("תכתוב תזוזה(מספר)");
            } else if (state.equals("waiting_for_encrypt_shift")) {
                try {
                    int movement = Integer.parseInt(text);
                    String texttocrypt = textToEncrypt.getOrDefault(chatID, "");
                    String encryptedtext = HebrewEncrypt(texttocrypt, movement, HebrewLetters);
                    sendMessage.setText("Encrypted text:\n" + encryptedtext);
                } catch (NumberFormatException e) {
                    sendMessage.setText("Error: movement must be a count.");
                }
                userState.remove(chatID);
            } else if (text.equalsIgnoreCase("/decrypt")) {
                userState.put(chatID, "waiting_for_decrypt_text");
                sendMessage.setText("Write text for decrypt (in one message)");
            } else if (state.equals("waiting_for_decrypt_text")) {
                String[] decrypted = HebrewDecrypt(text, HebrewLetters);
                StringBuilder sb = new StringBuilder("Variants of decrypting:\n");
                for (int i = 0; i < decrypted.length; i++) {
                    sb.append(i).append(": ").append(decrypted[i]).append("\n");
                }
                sendMessage.setText(sb.toString());
                userState.remove(chatID);
            }

        }


        try {
            this.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static String RussianEncrypt(String texttocrypt, int movement, char[] RussianBigLetters, char[] RussianSmallLetters) {

        int TextLenght = texttocrypt.length();
        char[] chartext = new char[TextLenght];

        for (int i = 0; i < TextLenght; i++) {
            if (Character.isUpperCase(texttocrypt.charAt(i))) {
                for (int j = 0; j < RussianBigLetters.length; j++) {
                    if (texttocrypt.charAt(i) == RussianBigLetters[j]) {
                        chartext[i] = RussianBigLetters[j];
                        break;
                    }
                }
            } else if (Character.isLowerCase(texttocrypt.charAt(i))) {
                for (int j = 0; j < RussianSmallLetters.length; j++) {
                    if (texttocrypt.charAt(i) == RussianSmallLetters[j]) {
                        chartext[i] = RussianSmallLetters[j];
                        break;
                    }
                }
            } else {
                chartext[i] = texttocrypt.charAt(i);
            }
        }

        char[] encryptedchartext = new char[chartext.length];

        for (int i = 0; i < chartext.length; i++) {
            if (Character.isUpperCase(chartext[i])) {
                for (int j = 0; j < RussianBigLetters.length; j++) {
                    if (chartext[i] == RussianBigLetters[j]) {
                        int newIndex = (j + movement) % RussianBigLetters.length;
                        encryptedchartext[i] = RussianBigLetters[newIndex];
                        break;
                    }
                }
            } else if (Character.isLowerCase(chartext[i])) {
                for (int j = 0; j < RussianSmallLetters.length; j++) {
                    if (chartext[i] == RussianSmallLetters[j]) {
                        int newIndex = (j + movement) % RussianSmallLetters.length;
                        encryptedchartext[i] = RussianSmallLetters[newIndex];
                        break;
                    }
                }
            } else {
                encryptedchartext[i] = chartext[i];
            }
        }

        return new String(encryptedchartext);
    }
    public static String[] RussianDecrypt(String texttodecrypt, char[] RussianBigLetters, char[] RussianSmallLetters){
        int TextLenght=texttodecrypt.length();
        char[] chartext=new char[TextLenght];
        //I do char array equals to text
        for(int i=0; i<TextLenght; i++){
            if(Character.isUpperCase(texttodecrypt.charAt(i))){
                for(int j=0; j<RussianBigLetters.length;j++){
                    if(texttodecrypt.charAt(i)==RussianBigLetters[j]){
                        chartext[i] = RussianBigLetters[j];
                        j=RussianBigLetters.length-1;
                    }
                }
            }
            else if(Character.isLowerCase(texttodecrypt.charAt(i))){
                for(int j=0; j<RussianSmallLetters.length;j++){
                    if(texttodecrypt.charAt(i)==RussianSmallLetters[j]){
                        chartext[i] = RussianSmallLetters[j];
                        j=RussianSmallLetters.length-1;
                    }
                }
            }
            else{
                chartext[i]=texttodecrypt.charAt(i);
            }
        }
        String[] DecryptedVars=new String[RussianSmallLetters.length];
        for(int q=0;q< RussianBigLetters.length;q++){
            char[] decryptedchartext = new char[chartext.length];
            for(int i=0;i<chartext.length;i++){
                if(Character.isUpperCase(chartext[i])){
                    for(int j=0;j<RussianBigLetters.length;j++){
                        if(chartext[i]==RussianBigLetters[j]){
                            if(j+q>=RussianBigLetters.length){
                                int NewIndex = j+q-RussianBigLetters.length;
                                decryptedchartext[i]=RussianBigLetters[NewIndex];
                            }
                            else{
                                decryptedchartext[i]=RussianBigLetters[j+q];
                            }
                        }
                    }
                }
                else if(Character.isLowerCase(chartext[i])){
                    for(int j=0;j<RussianSmallLetters.length;j++){
                        if(chartext[i]==RussianSmallLetters[j]){
                            if(j+q>=RussianSmallLetters.length){
                                int NewIndex = j+q-RussianSmallLetters.length;
                                decryptedchartext[i]=RussianSmallLetters[NewIndex];
                            }
                            else{
                                decryptedchartext[i]=RussianSmallLetters[j+q];
                            }
                        }
                    }
                }
                else{
                    decryptedchartext[i]=chartext[i];
                }
            }
            DecryptedVars[q]= new String(decryptedchartext);
        }
        return(DecryptedVars);
    }

    public static String EnglishEncrypt(String texttocrypt, int movement, char[] EnglishBigLetters, char[] EnglishSmallLetters) {

        int TextLenght = texttocrypt.length();
        char[] chartext = new char[TextLenght];

        for (int i = 0; i < TextLenght; i++) {
            if (Character.isUpperCase(texttocrypt.charAt(i))) {
                for (int j = 0; j < EnglishBigLetters.length; j++) {
                    if (texttocrypt.charAt(i) == EnglishBigLetters[j]) {
                        chartext[i] = EnglishBigLetters[j];
                        break;
                    }
                }
            } else if (Character.isLowerCase(texttocrypt.charAt(i))) {
                for (int j = 0; j < EnglishSmallLetters.length; j++) {
                    if (texttocrypt.charAt(i) == EnglishSmallLetters[j]) {
                        chartext[i] = EnglishSmallLetters[j];
                        break;
                    }
                }
            } else {
                chartext[i] = texttocrypt.charAt(i);
            }
        }

        char[] encryptedchartext = new char[chartext.length];

        for (int i = 0; i < chartext.length; i++) {
            if (Character.isUpperCase(chartext[i])) {
                for (int j = 0; j < EnglishBigLetters.length; j++) {
                    if (chartext[i] == EnglishBigLetters[j]) {
                        int newIndex = (j + movement) % EnglishBigLetters.length;
                        encryptedchartext[i] = EnglishBigLetters[newIndex];
                        break;
                    }
                }
            } else if (Character.isLowerCase(chartext[i])) {
                for (int j = 0; j < EnglishSmallLetters.length; j++) {
                    if (chartext[i] == EnglishSmallLetters[j]) {
                        int newIndex = (j + movement) % EnglishSmallLetters.length;
                        encryptedchartext[i] = EnglishSmallLetters[newIndex];
                        break;
                    }
                }
            } else {
                encryptedchartext[i] = chartext[i];
            }
        }

        return new String(encryptedchartext);
    }
    public static String[] EnglishDecrypt(String texttodecrypt, char[] EnglishBigLetters, char[] EnglishSmallLetters){
        int TextLenght=texttodecrypt.length();
        char[] chartext=new char[TextLenght];
        //I do char array equals to text
        for(int i=0; i<TextLenght; i++){
            if(Character.isUpperCase(texttodecrypt.charAt(i))){
                for(int j=0; j<EnglishBigLetters.length;j++){
                    if(texttodecrypt.charAt(i)==EnglishBigLetters[j]){
                        chartext[i] = EnglishBigLetters[j];
                        j=EnglishBigLetters.length-1;
                    }
                }
            }
            else if(Character.isLowerCase(texttodecrypt.charAt(i))){
                for(int j=0; j<EnglishSmallLetters.length;j++){
                    if(texttodecrypt.charAt(i)==EnglishSmallLetters[j]){
                        chartext[i] = EnglishSmallLetters[j];
                        j=EnglishSmallLetters.length-1;
                    }
                }
            }
            else{
                chartext[i]=texttodecrypt.charAt(i);
            }
        }
        String[] DecryptedVars=new String[EnglishSmallLetters.length];
        for(int q=0;q< EnglishBigLetters.length;q++){
            char[] decryptedchartext = new char[chartext.length];
            for(int i=0;i<chartext.length;i++){
                if(Character.isUpperCase(chartext[i])){
                    for(int j=0;j<EnglishBigLetters.length;j++){
                        if(chartext[i]==EnglishBigLetters[j]){
                            if(j+q>=EnglishBigLetters.length){
                                int NewIndex = j+q-EnglishBigLetters.length;
                                decryptedchartext[i]=EnglishBigLetters[NewIndex];
                            }
                            else{
                                decryptedchartext[i]=EnglishBigLetters[j+q];
                            }
                        }
                    }
                }
                else if(Character.isLowerCase(chartext[i])){
                    for(int j=0;j<EnglishSmallLetters.length;j++){
                        if(chartext[i]==EnglishSmallLetters[j]){
                            if(j+q>=EnglishSmallLetters.length){
                                int NewIndex = j+q-EnglishSmallLetters.length;
                                decryptedchartext[i]=EnglishSmallLetters[NewIndex];
                            }
                            else{
                                decryptedchartext[i]=EnglishSmallLetters[j+q];
                            }
                        }
                    }
                }
                else{
                    decryptedchartext[i]=chartext[i];
                }
            }
            DecryptedVars[q]= new String(decryptedchartext);
        }
        return(DecryptedVars);
    }

    public static String HebrewEncrypt(String texttocrypt, int movement, char[] HebrewLetters){
        int TextLenght = texttocrypt.length();
        char[] chartext = new char[TextLenght];

        for(int i=0; i<TextLenght;i++){
            for(int j=0;j<HebrewLetters.length;j++){
                if(texttocrypt.charAt(i) == HebrewLetters[j]){chartext[i]=HebrewLetters[j];}
            }
            if(chartext[i]==' '){chartext[i]=texttocrypt.charAt(i);}
        }
        char[] encryptedchartext = new char[chartext.length];

        for (int i = 0; i < chartext.length; i++) {
            for (int j = 0; j < HebrewLetters.length; j++) {
                if (chartext[i] == HebrewLetters[j]) {
                    int newIndex = (j + movement) % HebrewLetters.length;
                    encryptedchartext[i] = HebrewLetters[newIndex];
                    break;
                }
            }
           if(encryptedchartext[i]==' '){encryptedchartext[i]=chartext[i];}
        }
        String EncryptedText = new String(encryptedchartext);
        return EncryptedText;
    }
    public static String[] HebrewDecrypt(String text, char[] HebrewLetters){
        int TextLenght = text.length();
        char[] chartext = new char[TextLenght];

        for(int i=0; i<TextLenght;i++){
            for(int j=0;j<HebrewLetters.length;j++){
                if(text.charAt(i) == HebrewLetters[j]){chartext[i]=HebrewLetters[j];}
            }
            if(chartext[i]==' '){chartext[i]=text.charAt(i);}
        }
        String[] DecryptedVars=new String[HebrewLetters.length];
        for(int q=0;q< HebrewLetters.length;q++) {
            char[] decryptedchartext = new char[chartext.length];
            for(int i=0; i<chartext.length;i++){
                for(int j=0;j<HebrewLetters.length;j++){
                    if(chartext[i]==HebrewLetters[j]) {
                        if((i==11||i==14||i==16||i==20||i==22)&&chartext[i+1]==' '){
                            i-=1;
                            if (j + q >= HebrewLetters.length) {
                                int NewIndex = j + q - HebrewLetters.length;
                                decryptedchartext[i] = HebrewLetters[NewIndex];
                            } else {
                                decryptedchartext[i] = HebrewLetters[j + q];
                            }
                        }
                        i+=1;
                    }
                }
                if(decryptedchartext[i]==' '){decryptedchartext[i]=chartext[i];}
            }
        }
        return DecryptedVars;
    }
}


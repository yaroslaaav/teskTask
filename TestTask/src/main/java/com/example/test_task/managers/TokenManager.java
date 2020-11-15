package com.example.test_task.managers;


import com.example.test_task.exception.AuthException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TokenManager {
    private static TokenManager instance=new TokenManager(); //инициализация объекта класса Singleton
    private List<String> tokens = new LinkedList<>();       //инициализация списка хранящего токены
    AccountManager accountManager= new AccountManager();
    static List<String> accounts;
    {
        try {
            accounts = accountManager.getAllAccounts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private TokenManager() {
    }
    public static TokenManager getInstance(){       //возвращение единственного экземпляра класса
        return instance;
    }       //возврат единственного объекта класса
    public boolean checkToken(String token) throws IOException {        //проверка токена на наличие в списке выданных токенов

        String SECRET_KEY = "mySecretKey";                              //заданный секретный ключ
        Claims claims = Jwts.parser()                                   //декодирование токена
                .setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
                .parseClaimsJws(token).getBody();
        String name=claims.getSubject();                                //получение имени аккаунта закодированного в токене
        if(accountManager.findNameDuplicate(name)){                     //поиск такого имени аккаунта в хранилище аккаунтов
            Date dateNow = new Date();                                  //получение настоящей даты
            if (dateNow.getTime()<claims.getExpiration().getTime())     //проверка времени жизнм токена
                return true;
        }
        tokens.remove(token);
        return false;


    }

    //генерация токенов
    public String generatedToken(String userName, Date currentTime) throws AuthException {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;           //указание аклгоритма кодирование токена

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("mySecretKey");   //преобразование к формату base64
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());           // задание ключа

        String token = Jwts.builder().setId("test")                                                       //создание токена
                .setSubject(userName)                                                                     //кладем в токен имя аккаунта
                .setIssuedAt(new Date(currentTime.getTime()))                                             //кладем время создания
                .setExpiration(new Date(currentTime.getTime() + 1800000))                                 //задаем время жизни
                .signWith(signatureAlgorithm, signingKey).compact();                                      //кодируем и преобразуем в строку

        tokens.add(token);                                                                                  //добавляем в хранилище токенов
        return token;
    }

}

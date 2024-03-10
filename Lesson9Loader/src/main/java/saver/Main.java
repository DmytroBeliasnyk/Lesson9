package saver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Main {
    private static final Gson gson = new GsonBuilder().create();
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
    private static final EntityManager em = emf.createEntityManager();

    public static void main(String[] args) {
        SimpleDateFormat formater = new SimpleDateFormat("dd.MM.yyyy");
        Calendar date = new GregorianCalendar(2024, Calendar.JANUARY, 1);

        String spec = "";
        Response response = null;
        CurrencyUSD currencyUSD = null;
        while (date.get(Calendar.MONTH) <= Calendar.FEBRUARY) {
            spec = "https://api.privatbank.ua/p24api/exchange_rates?date=" +
                    formater.format(date.getTime());
            try {
                response = getResponse(spec, gson);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            for (Object obj : response.getExchangeRate()) {
                if (obj.toString().indexOf("USD") >= 0) {
                    currencyUSD = gson.fromJson(obj.toString(), CurrencyUSD.class);
                    currencyUSD.setDate(response.getDate());
                    break;
                }
            }
            em.getTransaction().begin();
            em.persist(currencyUSD);
            try {
                em.getTransaction().commit();
            } catch (Exception e) {
                em.getTransaction().rollback();
            }
            if (date.getActualMaximum(Calendar.DATE) != date.get(Calendar.DATE)) {
                date.roll(Calendar.DATE, true);
            } else {
                date.roll(Calendar.MONTH, true);
                date.set(Calendar.DATE, 1);
            }
        }
    }

    public static Response getResponse(String spec, Gson gson) throws IOException {
        URL url = new URL(spec);
        URLConnection conn = url.openConnection();

        Response response = null;
        String res = "";
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            res = br.readLine();
            response = gson.fromJson(res, Response.class);
        }
        return response;
    }
}







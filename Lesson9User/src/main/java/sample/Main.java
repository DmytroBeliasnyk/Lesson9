package sample;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class Main {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("JPA");
    private static final EntityManager em = emf.createEntityManager();

    public static void main(String[] args) {
        Calendar calendar1 = new GregorianCalendar(2024, Calendar.JANUARY, 5);
        Calendar calendar2 = new GregorianCalendar(2024, Calendar.FEBRUARY, 5);

        System.out.println(getExchangeRateByDate(calendar1));

        Map<String, Double> averageRates = getAverageRates(calendar1, calendar2);
        for (String k : averageRates.keySet()) {
            System.out.println(k + " = " + averageRates.get(k));
        }

        Map<String, List<Double>> minMaxRates = getMinAndMaxRates(calendar1, calendar2);
        for (String k : minMaxRates.keySet()) {
            System.out.println(k + " = " + minMaxRates.get(k));
        }
    }

    public static CurrencyUSD getExchangeRateByDate(Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String formatedDate = sdf.format(date.getTime());

        TypedQuery<CurrencyUSD> query = em.createQuery("SELECT c FROM CurrencyUSD c " +
                "WHERE date=:requestedDate", CurrencyUSD.class);
        query.setParameter("requestedDate", formatedDate);

        CurrencyUSD result = query.getSingleResult();
        if (result == null)
            throw new NoResultException();

        return result;
    }

    public static Map<String, Double> getAverageRates(Calendar startDate, Calendar endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String formatedStartDate = sdf.format(startDate.getTime());
        String formatedEndDate = sdf.format(endDate.getTime());

        TypedQuery<Double> purchaseQuery = em.createQuery("SELECT AVG(rate.purchaseRate) " +
                "FROM CurrencyUSD rate WHERE date BETWEEN :start AND :end", Double.class);
        purchaseQuery.setParameter("start", formatedStartDate);
        purchaseQuery.setParameter("end", formatedEndDate);

        TypedQuery<Double> saleQuery = em.createQuery("SELECT AVG(rate.saleRate) " +
                "FROM CurrencyUSD rate  WHERE date BETWEEN :start AND :end", Double.class);
        saleQuery.setParameter("start", formatedStartDate);
        saleQuery.setParameter("end", formatedEndDate);

        Double purchaseRate = purchaseQuery.getSingleResult();
        Double saleRate = saleQuery.getSingleResult();
        if (purchaseRate == null || saleRate == null)
            throw new NoResultException();

        return Map.of("Average purchase rate", purchaseRate, "Average sale rate", saleRate);
    }

    public static Map<String, List<Double>> getMinAndMaxRates(Calendar startDate, Calendar endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String formatedStartDate = sdf.format(startDate.getTime());
        String formatedEndDate = sdf.format(endDate.getTime());

        TypedQuery<Double> minPurchaseQuery = em.createQuery("SELECT MIN (rate.purchaseRate) " +
                "FROM CurrencyUSD rate WHERE date BETWEEN :start AND :end", Double.class);
        minPurchaseQuery.setParameter("start", formatedStartDate);
        minPurchaseQuery.setParameter("end", formatedEndDate);
        TypedQuery<Double> maxPurchaseQuery = em.createQuery("SELECT MAX (rate.purchaseRate) " +
                "FROM CurrencyUSD rate WHERE date BETWEEN :start AND :end", Double.class);
        maxPurchaseQuery.setParameter("start", formatedStartDate);
        maxPurchaseQuery.setParameter("end", formatedEndDate);

        TypedQuery<Double> minSaleQuery = em.createQuery("SELECT MIN (rate.saleRate) " +
                "FROM CurrencyUSD rate WHERE date BETWEEN :start AND :end", Double.class);
        minSaleQuery.setParameter("start", formatedStartDate);
        minSaleQuery.setParameter("end", formatedEndDate);
        TypedQuery<Double> maxSaleQuery = em.createQuery("SELECT MAX (rate.saleRate) " +
                "FROM CurrencyUSD rate WHERE date BETWEEN :start AND :end", Double.class);
        maxSaleQuery.setParameter("start", formatedStartDate);
        maxSaleQuery.setParameter("end", formatedEndDate);

        Double minPurchaseRate = minPurchaseQuery.getSingleResult();
        Double minSaleRate = minSaleQuery.getSingleResult();
        Double maxPurchaseRate = maxPurchaseQuery.getSingleResult();
        Double maxSaleRate = maxSaleQuery.getSingleResult();

        return Map.of("Results for purchase rate", List.of(minPurchaseRate, maxPurchaseRate),
                "Results for sale rate", List.of(minSaleRate, maxSaleRate));
    }
}


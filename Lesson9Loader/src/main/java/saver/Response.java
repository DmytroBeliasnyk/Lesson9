
package saver;

import java.util.Arrays;

public class Response {
    private String date;
    private Object[] exchangeRate;

    public Response(String date, String[] exchangeRate) {
        this.date = date;
        this.exchangeRate = exchangeRate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Object[] getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Object[] exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String toString() {
        return "Response{" +
                "date='" + date + '\'' +
                ", exchangeRate=" + Arrays.toString(exchangeRate) +
                '}';
    }
}

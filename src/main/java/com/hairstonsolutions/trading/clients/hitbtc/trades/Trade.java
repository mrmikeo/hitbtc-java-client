package com.hairstonsolutions.trading.clients.hitbtc.trades;

import lombok.Data;
import lombok.ToString;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@Data
public class Trade {

    private long id;
    private String clientOrderId;
    private long orderId;
    private String symbol;
    private String side;
    private String quantity;
    private String price;
    private String fee;
    private String timestamp;

    public long getId() {
        return id;
    }

    public String getClientOrderId() {
        return clientOrderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSide() {
        return side;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getPrice() {
        return price;
    }

    public String getFee() {
        return fee;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @ToString.Include
    public String getTotalCost() {
        float fQuantity = Float.parseFloat(quantity);
        float fPrice = Float.parseFloat(price);
        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        float totalCost = fQuantity * fPrice;

        return df.format(totalCost);
    }

    public static String getAveragePrice(List<Trade> tradeReport) {
        if ( tradeReport.size() == 0)
            return "0.0";

        DecimalFormat df = new DecimalFormat("##.##");
        df.setRoundingMode(RoundingMode.HALF_UP);

        float sum = 0f;
        for ( Trade trades : tradeReport ) {
            sum = sum + Float.parseFloat(trades.getPrice());
        }

        return df.format(sum / tradeReport.size());
    }
}


/*
HISTORY TRADES: /history/trades
  {
    "id": 0,
    "clientOrderId": "string",
    "orderId": 0,
    "symbol": "string",
    "side": "sell",
    "quantity": "string",
    "fee": "string",
    "price": "string",
    "timestamp": "2019-10-05T20:18:54.426Z"
  }

  HISTORY ORDER TRADES: /history/order/{id}/trades
    {
    "id": 0,
    "clientOrderId": "string",
    "orderId": 0,
    "symbol": "string",
    "side": "sell",
    "quantity": "string",
    "fee": "string",
    "price": "string",
    "timestamp": "2019-10-05T20:17:27.687Z"
  }
 */
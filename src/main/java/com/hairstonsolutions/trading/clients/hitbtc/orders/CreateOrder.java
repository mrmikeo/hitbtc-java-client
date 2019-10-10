package com.hairstonsolutions.trading.clients.hitbtc.orders;

import com.hairstonsolutions.trading.clients.hitbtc.attributes.Side;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.TimeInForce;
import com.hairstonsolutions.trading.clients.hitbtc.attributes.TradeType;

/*
    {
  "symbol": "BTCUSD",
  "side": "buy",
  "type": "limit",
  "timeInForce": "GTC",
  "price": "10055.16",
  "quantity": "0.0001",
  "postOnly": false
}
*/

public class CreateOrder {
    private String symbol;
    private Side side;
    private TradeType tradeType;
    private TimeInForce timeInForce;
    private String price;
    private String quantity;
    private boolean postOnly;


    public CreateOrder() {
        this.symbol = "BTCUSD";
        this.side = new Side(Side.BUY);
        this.tradeType = new TradeType(TradeType.LIMIT);
        this.timeInForce = new TimeInForce(TimeInForce.GTC_GOOD_TILL_CANCELLED);
        this.price = "10000.00";
        this.quantity = "0.0001";
        this.postOnly = false;
    }

    public CreateOrder(String symbol, String side, String tradeType, String timeInForce, String price, String quantity, boolean postOnly) {
        this.symbol = symbol;
        this.side = new Side(side);
        this.tradeType = new TradeType(tradeType);
        this.timeInForce = new TimeInForce(timeInForce);
        this.price = price;
        this.quantity = quantity;
        this.postOnly = postOnly;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSide() {
        return side.getSide();
    }

    public String getTradeType() {
        return tradeType.getType();
    }

    public String getTimeInForce() {
        return timeInForce.getForce();
    }

    public String getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public boolean isPostOnly() {
        return postOnly;
    }
}

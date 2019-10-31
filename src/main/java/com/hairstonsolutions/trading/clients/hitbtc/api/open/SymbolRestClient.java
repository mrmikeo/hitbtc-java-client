package com.hairstonsolutions.trading.clients.hitbtc.api.open;

import com.hairstonsolutions.trading.clients.hitbtc.Symbol;
import com.hairstonsolutions.trading.clients.hitbtc.api.HitBtcAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class SymbolRestClient {

    Logger LOG = LoggerFactory.getLogger(SymbolRestClient.class);

    private static final String RESOURCE_PATH = "/public/symbol";

    private String REQUEST_URI;
    private RestTemplate restTemplate;

    public SymbolRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.REQUEST_URI = HitBtcAPI.BaseUrl + RESOURCE_PATH;
    }

    public ResponseEntity<Symbol> getForEntity(String symbol) {
        ResponseEntity<Symbol> entity = restTemplate.getForEntity(REQUEST_URI + "/{symbol}",
                                                                    Symbol.class, symbol);
        LOG.info(String.format("Status Code: %s", entity.getStatusCode()));
        LOG.info(String.format("Return values: %s", entity.toString()));

        return entity;
    }

    public Symbol getSymbol(String id) {
        ResponseEntity<Symbol> entity = restTemplate.getForEntity(REQUEST_URI + "/{id}",
                Symbol.class, id);
        LOG.info(String.format("Status Code: %s", entity.getStatusCode()));
        LOG.info(String.format("Return values: %s", entity.toString()));

        return entity.getBody();
    }

}
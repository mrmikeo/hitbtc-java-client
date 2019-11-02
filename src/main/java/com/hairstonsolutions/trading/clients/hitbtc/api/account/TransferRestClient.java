package com.hairstonsolutions.trading.clients.hitbtc.api.account;

import com.hairstonsolutions.trading.clients.hitbtc.account.Direction;
import com.hairstonsolutions.trading.clients.hitbtc.account.TransferResponse;
import com.hairstonsolutions.trading.clients.hitbtc.api.HitBtcAPI;
import com.oracle.tools.packager.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class TransferRestClient {

    Logger LOG = LoggerFactory.getLogger(TransferRestClient.class);
    private static final String RESOURCE_PATH = "/account/transfer";

    private String REQUEST_URI = HitBtcAPI.BaseUrl + RESOURCE_PATH;
    private HitBtcAPI hitBtcAPI;
    private RestTemplate restTemplate;

    public TransferRestClient(RestTemplate restTemplate, HitBtcAPI hitBtcAPI) {
        this.restTemplate = restTemplate;
        this.hitBtcAPI = hitBtcAPI;
    }

    public ResponseEntity<TransferResponse> transferExecution(String currency, String direction, String amount) {
        String encodedCredentials = hitBtcAPI.getEncodedCredentials();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("currency", currency);
        map.add("type", direction);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity<TransferResponse> responseEntity = restTemplate.exchange(REQUEST_URI, HttpMethod.POST, request, TransferResponse.class);

        LOG.info(String.format("Status Code: %s", responseEntity.getStatusCode()));
        LOG.info(String.format("Return Values: %s", responseEntity.toString()));

        return responseEntity;
    }

    public static void moveToTrading(HitBtcAPI hitBtcAPI, String currency, String amount) {
        String direction = Direction.TO_TRADING;

        transfer(hitBtcAPI, currency, amount, direction);
    }

    public static void moveToMain(HitBtcAPI hitBtcAPI, String currency, String amount) {
        String direction = Direction.TO_MAIN_BANK;

        transfer(hitBtcAPI, currency, amount, direction);
    }

    private static void transfer(HitBtcAPI hitBtcAPI, String currency, String amount, String direction) {
        TransferRestClient transferRestClient = new TransferRestClient(new RestTemplate(), hitBtcAPI);

        ResponseEntity<TransferResponse> responseEntity = transferRestClient.transferExecution(currency, direction, amount);

        Log.info(responseEntity.getStatusCode().toString());

        TransferResponse transferResponse = responseEntity.getBody();
        Log.info(transferResponse.toString());
    }

}
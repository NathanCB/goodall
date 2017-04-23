package com.goodall.controllers;

import com.goodall.entities.geocodes.Geocode;
import com.goodall.entities.nasaurl.NasaImagery;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ApiCtl {

    private final static String APIKEY = "AIzaSyCTNiSGsl475_XqhD6mDf0oa-RiHi68WuQ";
    private final static String NASA_KEY = "eUKTxqEpAbx6V3IEv7BTpZDl5rw3reu7EWluZhzT";
    // working example url request with street only address
    // https://maps.googleapis.com/maps/api/geocode/json?address=2221w21stst&AIzaSyCTNiSGsl475_XqhD6mDf0oa-RiHi68WuQ
    // build a string request for latitude/longitude lookup
    // https://maps.googleapis.com/maps/api/geocode/outputFormat?parameters

    //glitch static url api
    private final static String glitchApi = "http://hitode909.appspot.com/glitch/api?uri=";
    private final static String glitchApi2 = "http://hitode909.appspot.com/glitch/api2?uri=";
    public static String defaultImgUrl = "https://dncache-mauganscorp.netdna-ssl.com/thumbseg/453/453966-bigthumbnail.jpg";

    public String makeGeocodeRequest(String address){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = "https://maps.googleapis.com/maps/api/geocode/" + "json?address=" + address + "&" + APIKEY;

        RestTemplate template = new RestTemplate();
        ResponseEntity<Geocode> geocode = template.exchange(url, HttpMethod.GET, entity, Geocode.class);
        String lat = geocode.getBody().getLat();
        String lng = geocode.getBody().getLng();
        String coordinates = lat + ", " + lng;

        return coordinates;
    }
    // https://api.nasa.gov/planetary/earth/imagery?lon=100.75&lat=1.5&date=2014-02-01&cloud_score=True&api_key=DEMO_KEY
    public String getNasaImageUrl(String coordinates){
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String[] values = coordinates.split(",\\s*");
        String lat = values[0];
        String lon = values[1];
        String requestUrl = "https://api.nasa.gov/planetary/earth/imagery?lon="+ lon + "&lat=" + lat + "&api_key=" + NASA_KEY;

        RestTemplate template = new RestTemplate();
        ResponseEntity<NasaImagery> nasaImagery = template.exchange(requestUrl, HttpMethod.GET, entity, NasaImagery.class);
        String nasaImageUrl = nasaImagery.getBody().getUrl();

        return nasaImageUrl;
    }

    public String getGlitchImageUrl(String url){
        String glitchUrl;
        if(url != null || !url.contains("")) {
            glitchUrl = glitchApi + url;
        }else{
            glitchUrl = defaultImgUrl;
        }
        return glitchUrl;
    }
}

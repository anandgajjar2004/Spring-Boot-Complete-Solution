package com.microservices.currencyexchangeservice.currencyconversionservice.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.microservices.currencyexchangeservice.currencyconversionservice.domain.CurrencyConversionBean;
import com.microservices.currencyexchangeservice.currencyconversionservice.feign.CurrencyExchangeServiceProxy;

@RestController
public class ConversionConversionController {
	
	@Autowired
	private Environment environmemt;
	
    @Autowired
    RestTemplate restTemplate;
	
	@Autowired
	private CurrencyExchangeServiceProxy proxy;
	
	@GetMapping("/currency-converter/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrency(@PathVariable String from,@PathVariable  String to,@PathVariable  BigDecimal quantity) {		
		Map<String, String> uriVariable = new HashMap<String, String>();
		uriVariable.put("from", from);
		uriVariable.put("to", to);
		CurrencyConversionBean response = restTemplate.getForObject("http://currency-exchange-service/currency-exchange/from/{from}/to/{to}", CurrencyConversionBean.class, uriVariable);
		
		
		CurrencyConversionBean currencyConversionBean =  new CurrencyConversionBean(response.getId(), from, to,response.getConversionMultiple(), quantity,(quantity.multiply(response.getConversionMultiple())));
		currencyConversionBean.setPort(response.getPort());		
		return currencyConversionBean;
	}
	
	@GetMapping("/currency-converter-feign/from/{from}/to/{to}/quantity/{quantity}")
	public CurrencyConversionBean convertCurrencyFeign(@PathVariable String from,@PathVariable  String to,@PathVariable  BigDecimal quantity) {		
		CurrencyConversionBean response = proxy.retrieveExchangeValue(from, to);		
		CurrencyConversionBean currencyConversionBean =  new CurrencyConversionBean(response.getId(), from, to,response.getConversionMultiple(), quantity,(quantity.multiply(response.getConversionMultiple())));
		currencyConversionBean.setPort(response.getPort());
		return currencyConversionBean;        
	}
}
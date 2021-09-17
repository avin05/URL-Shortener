package com.avinash.first.controller;

import com.avinash.first.service.UrlService;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.avinash.first.model.Url;
import com.avinash.first.model.UrlDto;
import com.avinash.first.model.UrlErrorResponseDto;
import com.avinash.first.model.UrlResponseDto;

@RestController
public class UrlShortenController {
	@Autowired
	private UrlService urlService;
	
	@PostMapping("/generate")
	public ResponseEntity<?> generateShortLink(@RequestBody UrlDto urlDto){
		Url urlToRet = urlService.generateShortLink(urlDto);
		
		if(urlToRet!=null) {
			UrlResponseDto urlResponseDto = new UrlResponseDto();
			urlResponseDto.setOriginalUrl(urlToRet.getOriginalUrl());
			urlResponseDto.setExpirationDate(urlToRet.getExpirationDate());
			urlResponseDto.setShortLink(urlToRet.getShortLink());
			return new ResponseEntity<UrlResponseDto>(urlResponseDto,HttpStatus.OK);
			
		}else {
			UrlErrorResponseDto urlErrorresponseDto = new UrlErrorResponseDto();
			urlErrorresponseDto.setStatus("404");
			urlErrorresponseDto.setError("There was an error processing your request, please try again");
			return new ResponseEntity<UrlErrorResponseDto>(urlErrorresponseDto,HttpStatus.OK);
		}
		
	}
	
	@GetMapping("/{shortLink}")
	public ResponseEntity<?> redirectToOriginalUrl(@PathVariable String shortLink,HttpServletResponse response) throws IOException{
		if(StringUtils.isEmpty(shortLink)) {
			UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
			urlErrorResponseDto.setError("Invalid Url");
			urlErrorResponseDto.setStatus("400");
			return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
		}
		Url urlToRet = urlService.getEncodedUrl(shortLink);
			
		if(urlToRet == null) {
			UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
			urlErrorResponseDto.setError("Url does not exist or ot might have expired");
			urlErrorResponseDto.setStatus("400");
			return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
		}
		
		if(urlToRet.getExpirationDate().isBefore(LocalDateTime.now())) {
			urlService.deleteShortLink(urlToRet);
			UrlErrorResponseDto urlErrorResponseDto = new UrlErrorResponseDto();
			urlErrorResponseDto.setError("Url expired");
			urlErrorResponseDto.setStatus("200");
			return new ResponseEntity<UrlErrorResponseDto>(urlErrorResponseDto,HttpStatus.OK);
		}
		
		
		response.sendRedirect(urlToRet.getOriginalUrl());
		return null;
		
	}

}

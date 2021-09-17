package com.avinash.first.service;
import org.springframework.stereotype.Service;

import com.avinash.first.model.Url;
import com.avinash.first.model.UrlDto;

@Service
public interface UrlService {
	public Url generateShortLink(UrlDto urlDto);
	public Url persistShortLink(Url url);
	public Url getEncodedUrl(String url);
	public void deleteShortLink(Url url);

}






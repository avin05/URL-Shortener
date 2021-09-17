package com.avinash.first.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.avinash.first.model.*;

@Repository
public interface UrlRepository extends JpaRepository<Url,Long> {
	public Url findByShortLink(String shortLink);

}

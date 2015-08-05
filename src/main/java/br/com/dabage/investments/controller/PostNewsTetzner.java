package br.com.dabage.investments.controller;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import br.com.dabage.investments.news.NewsTO;

public class PostNewsTetzner {
	private final RestTemplate template = new RestTemplate();

	public void postNews(NewsTO newsTO) {
		String url = "http://tetzner.wordpress.com/wp-comments-post.php";
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("highlander_comment_nonce", "96554302fb");
		map.add("_wp_http_referer", "/");
		map.add("hc_post_as", "guest");
		map.add("comment", "Final de maio e ainda nao sairam todos os relatorios. \nEsses estagiarios estao mesmo de sacanagem.");
		map.add("email", "dogaum@gmail.com");
		map.add("author", "Douglas Araujo");
		map.add("comment_post_ID", "672");
		map.add("comment_parent", "0");
		template.postForLocation(url, map);
	}
}

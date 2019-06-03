package com.github.oliverpavey.projectsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix="finder")
@Data
public class FinderConfig {
	
	private String root;
	private String exclude;
	
	public String[] getRoots() {
		return root.split(",");
	}
	
	public String[] getExcludes() {
		return exclude.split(",");
	}
}

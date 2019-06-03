package com.github.oliverpavey.projectsearch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjectSearchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ProjectSearchApplication.class, args);
	}
	
	@Autowired
	FolderFinder folderFinder;
	
	@Autowired
	JetBrainsFinder jetBrainsFinder;

	@Override
	public void run(String... args) throws Exception {
		folderFinder.walk();
		jetBrainsFinder.walk();
	}

}

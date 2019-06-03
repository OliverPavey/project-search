package com.github.oliverpavey.projectsearch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Component
@Log
public class GitInfoReader {

	private final String GIT_HOME = "C:/Program Files/Git";
	private final String GIT = "/mingw64/bin/git";
	private final String GIT_EXE = GIT_HOME + GIT + ".exe";
	
	private Optional<String> gitResponse(Path path, String command) {
		
		try {
			List<String> commandAndArgs = new ArrayList<>();
			commandAndArgs.add(GIT_EXE);
			commandAndArgs.addAll(Arrays.asList(command.substring(4).split(" ")));
			ProcessBuilder pb = new ProcessBuilder(commandAndArgs);
			pb.directory(path.toFile());
			Process process = pb.start();
			
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
			  sb.append(line);
			}
			return sb.length()==0 ? Optional.empty() : Optional.of( sb.toString() );
			
		} catch (IOException e) {
			String msg = String.format("Could not retrieve value from GIT (running '%s' in '%s').", command, path); 
			log.log(Level.WARNING, msg, e);
			return Optional.empty();
		}
	}

	public Optional<String> getUser(Path path) {
		final String COMMAND = "git config user.name";
		return gitResponse(path, COMMAND);
	}
	
	public Optional<String> getEmail(Path path) {
		final String COMMAND = "git config user.email";
		return gitResponse(path, COMMAND);
	}
	
	public Optional<String> getOrigin(Path path) {
		final String COMMAND = "git config --get remote.origin.url";
		return gitResponse(path, COMMAND);
	}
	
}

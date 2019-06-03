package com.github.oliverpavey.projectsearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

@Component
@Log
public class WorkspaceInfoReader {

	// C:/Users/user/eclipse-workspace-2019-03/.metadata/.plugins/org.eclipse.core.resources/.projects/lab-3-server
	
	private File dotProjects(Path workspace) {
		return new File( new File( new File( new File( 
				workspace.toFile(), ".metadata"), ".plugins"), "org.eclipse.core.resources"), ".projects");
	}
	
	private File dotLocation(Path workspace, String project) {
		return new File( new File( dotProjects(workspace) , project ), ".location" );
	}

	public List<String> projects(Path workspace) {
		List<String> projects = new ArrayList<>();
		File dotProjects = dotProjects(workspace);
		if (dotProjects.exists() && dotProjects.isDirectory()) {
			for (File dotProjectFolder : dotProjects.listFiles(f->f.isDirectory())) {
				projects.add(dotProjectFolder.getName());
			}
		}
		return projects;
	}

	private String bytesToString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			if (b==0||(b>=32&&b<=127))
				sb.append((char)b);
			else
				sb.append((char)0);
		}
		return sb.toString();
	}
	
	private String extractCodeLocation(File dotLocation) {
		try {
			byte[] fileContent = Files.readAllBytes(dotLocation.toPath());
			String content = bytesToString(fileContent);
			Pattern p = Pattern.compile("^.*file:/(.*?)\0.*$");
			Matcher m = p.matcher(content);
			if (m.matches()) {
				return m.group(1);
			}
			return null;
			
		} catch (IOException e) {
			String message = String.format("Could not extract code location for %s", 
					dotLocation == null ? "null" : dotLocation.getAbsolutePath());
			log.log(Level.WARNING, message, e);
			return e.getClass().getName();
		}
	}
	
	private String cleanFolderPath(String location) {
		File cleaner = new File(location);
		return cleaner.getAbsolutePath();
	}

	public String projectLocation(Path workspace, String project) {
		
		File dotLocation = dotLocation(workspace, project);
		if (dotLocation.exists())
			return cleanFolderPath( extractCodeLocation( dotLocation ) );
			
		File workspaceProjectLocation = new File( workspace.toFile() , project );
		if (workspaceProjectLocation.exists() && workspaceProjectLocation.isDirectory())
			return cleanFolderPath( workspaceProjectLocation.getAbsolutePath() );
		
		return "PROJECT NOT FOUND";
	}
}

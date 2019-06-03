package com.github.oliverpavey.projectsearch;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.java.Log;

/**
 * Finder for Eclipse IDEs (and projects) and for GIT maintained projects
 * 
 * 
 * @author Oliver Pavey
 *
 */
@Component
@Log
public class FolderFinder extends SimpleFileVisitor<Path> {

	
	@Autowired
	FinderConfig finderConfig;
	
	@Autowired
	GitInfoReader gitInfoReader;
	
	@Autowired
	WorkspaceInfoReader workspaceInfoReader;
	
	public void walk() throws IOException {
		for (String root : finderConfig.getRoots()) {
			Files.walkFileTree(new File(root).toPath(), this);
		}
	}
	
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		
		final String name = dir.toFile().getName();
		final String fullname = dir.toFile().getAbsolutePath();
		
		for (String exclude : finderConfig.getExcludes()) {
			if (exclude.equalsIgnoreCase(fullname)) {
				return FileVisitResult.SKIP_SUBTREE;
			}
		}
		if (name.equalsIgnoreCase(".git")) {
			System.out.printf("GIT Repository: %s%n\t[%s] %s%n\t%s%n", 
					dir.getParent(),
					gitInfoReader.getUser(dir).orElse("<none>"),
					gitInfoReader.getEmail(dir).orElse("<none>"),
					gitInfoReader.getOrigin(dir).orElse("<local-repositiory>"));
			return FileVisitResult.SKIP_SUBTREE;
		}
		if (name.equalsIgnoreCase(".metadata")) {
			if (dir.toFile().listFiles((d,n)->n.equals("version.ini")).length > 0) {
				System.out.printf("Eclipse Workspace: %s%n", dir.getParent());
				for (String projectName : workspaceInfoReader.projects(dir.getParent())) {
					System.out.printf("\tProject: %s [%s]%n", projectName, 
							workspaceInfoReader.projectLocation(dir.getParent(), projectName));
				}
			}
		}
		return FileVisitResult.CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path dir, IOException exc) throws IOException {
		String message = String.format("Folder: %s - Error: %s%n", dir, exc.getClass().getSimpleName());
		log.log(Level.FINE, message, exc);
		return FileVisitResult.SKIP_SUBTREE;
	}
	
}

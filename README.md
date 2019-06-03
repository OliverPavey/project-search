# project-search

This is a tool to search local harddrive(s) for software development work.

> Status: Alpha.

It searches for:
- Work under GIT control
- Work in Eclipse workspaces
- Work referenced from Eclipse workspaces
- Work referenced from IntelliJ
- Work referenced from Android Studio

The output is written to the console.

## Build instructions

```
mvn clean package
```

## Exceution instructions

```
java -jar target/project-search-0.0.1-SNAPSHOT.jar
```

## Future plans

The next steps for the project will be to 
- Add code to collate lists of project folders, GIT repositories, and associated IDEs, and to cross-reference these.
- To export these resluts into an HTML page.
- To build in a simple javascript filter (so projects can be searched) to the exported HTML page.
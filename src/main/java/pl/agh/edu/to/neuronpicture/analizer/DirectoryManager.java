package pl.agh.edu.to.neuronpicture.analizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

public class DirectoryManager {

	public static final Logger LOGGER = Logger.getAnonymousLogger();
	private static final String EXCEPTION_1 = "An exception was thrown";


	private Path workingDir;
	private String src = "/home/domino/workspace/Analizer/myphoto";
	
	public DirectoryManager(){
		workingDir = Paths.get(src);
	}
	
	public void prepareDirectory(Image img){
		Path s = Paths.get(img.getImgFolderPath().toString() + "/" + img.getImgTitle());
		File source = new File(s.toString());
		File dest = new File(workingDir.toString());
		try {
		    FileUtils.copyFileToDirectory(source, dest);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, EXCEPTION_1, e);
		}
		
	}
	
	public void cleanDirectory(Image img){
		Path path = Paths.get(workingDir.toString()+"/"+ img.getImgTitle());
		try {
		    Files.delete(path);
		} catch (NoSuchFileException x) {
		    LOGGER.severe(String.format("%s: no such file or directory%n", path));
			LOGGER.log(Level.SEVERE, EXCEPTION_1, x);
		} catch (DirectoryNotEmptyException x) {
			LOGGER.severe(String.format("%s not empty%n", path));
			LOGGER.log(Level.SEVERE, EXCEPTION_1, x);
		} catch (IOException x) {
			LOGGER.log(Level.SEVERE, EXCEPTION_1, x);
		}
	}
	
	public Path getWorkingDir(){
		return workingDir;
	}
	
	public void setWorkingDir(Path p){
		workingDir = p;
	}
	
}

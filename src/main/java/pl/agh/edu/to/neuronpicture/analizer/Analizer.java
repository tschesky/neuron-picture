package pl.agh.edu.to.neuronpicture.analizer;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Analizer {

	public static final Logger LOGGER = Logger.getAnonymousLogger();
    private static final String EXCEPTION_1 = "An exception was thrown";

    private Image img;
    private Path workingDir;
    private String description;
    private static Analizer singleton = new Analizer( );

    private Analizer(){
        workingDir = Paths.get("/home/domino/workspace/Analizer/myphoto");
    }

	public Path getWorkingDir() {
		return workingDir;
	}
	public void setWorkingDir(Path workingDir) {
		this.workingDir = workingDir;
	}
    public static Analizer getInstance( ) {
      return singleton;
   }

	protected String execCmd() throws java.io.IOException {
		Runtime rt = Runtime.getRuntime();
		Process pr = rt.exec("th eval.lua -model ./model_id1-501-1448236541.t7_cpu.t7 -image_folder ./myphoto -num_images 1 -gpuid -1");
        java.io.InputStream is = pr.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String val;
        if (s.hasNext()) {
            val = s.next();
        }
        else {
            val = "";
        }
        return val;
	}

	protected void prepareDirectory(){
		Path s = Paths.get(img.getImgFolderPath().toString() + "/" + img.getImgTitle());
		File source = new File(s.toString());
		File dest = new File(workingDir.toString());
		try {
		    FileUtils.copyFileToDirectory(source, dest);
		} catch (IOException e) {
		    LOGGER.log(Level.SEVERE, EXCEPTION_1, e);
		}

	}

	protected void cleanDirectory(){
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
	
	public void processImg() throws IOException{
		
		String output = this.execCmd();
		String regex = "(image 1: )([a-zA-Z_0-9 ]+)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(output);
		while(m.find()){
			img.setDescription(m.group(2));
		}
	}
	
	public String getDescription(Image image) throws IOException{
		this.img = image;
		this.prepareDirectory();
		this.processImg();
		this.cleanDirectory();
		return img.getDescription();
	}
	
	public String getDescriptionTest(Image image) throws IOException{
	    String result = image.getImgTitle();
		return String.format("snow is falling %s", result);
	}
	
}
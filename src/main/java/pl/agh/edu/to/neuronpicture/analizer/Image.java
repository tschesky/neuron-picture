package pl.agh.edu.to.neuronpicture.analizer;
import java.nio.file.Path;

public class Image {
	private Path imgFolderPath;		//path to directory	with imgs
	private String imgTitle;		//img title : "image.jpg"
	private String description;	
	
	public Image(Path p, String t){
		imgFolderPath = p;
		imgTitle = t;
	}

	public Path getImgFolderPath() {
		return imgFolderPath;
	}

	public void setImgFolderPath(Path imgFolderPath) {
		this.imgFolderPath = imgFolderPath;
	}

	public String getImgTitle() {
		return imgTitle;
	}

	public void setImgTitle(String imgTitle) {
		this.imgTitle = imgTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
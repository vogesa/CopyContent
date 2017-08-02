package p;

import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CopyContent {

	private String sourceFolder;
	private String destinationFolder;


	public CopyContent(String sourceFolder, String destinationFolder) {
		this.sourceFolder = sourceFolder;
		this.destinationFolder = destinationFolder;
	}
	
	

	
	public static void main(String args[]) {
		String sourceFolder = "/Volumes/Fotos/Export";
		String destinationFolder = "/Users/anvoges/Desktop/FotosFromShare";
		if (args.length == 2) {
			sourceFolder = args[0];
			destinationFolder = args[1];
		}
		try {
			new CopyContent(sourceFolder, destinationFolder).executeCopy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	String longDate = "[0-3][0-9]\\s[a-zA-Z]+\\s[0-9]{4}";
	String longDatePattern = "^" + longDate;
	private String shortDate = "[12][0-9]{3}";
	String shortDatePattern = "^" + shortDate ;
	String placePattern = "(^.+),\\s(" + longDate+")";
	String yearOnly = "^[12][0-9]{3}";
	String monthOnly = "^[01][0-9]";
	String dayOnly = "^[0-3][0-9]";


	public void executeCopy() throws ParseException, IOException {
		SimpleDateFormat sourceFormat =  new SimpleDateFormat("dd MMM yyyy");
		SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
		File source = new File(sourceFolder);
		if (source.exists()) {
			File destination = new File(destinationFolder);
			if (!destination.exists()) {
				destination.mkdirs();
			}
			File[] listFiles = source.listFiles();
			for (File sourceFolderSubFile : listFiles) {
				if (sourceFolderSubFile.isDirectory()) {
					String stringFileDate = "";
					String placeName = "";
					if (sourceFolderSubFile.getName().matches(longDatePattern)) {
						stringFileDate = sourceFolderSubFile.getName();
					} else if (sourceFolderSubFile.getName().matches(placePattern)) {
						stringFileDate = sourceFolderSubFile.getName().replaceAll(placePattern, "$2");
						placeName = sourceFolderSubFile.getName().replaceAll(placePattern, "$1");
					} else if (sourceFolderSubFile.getName().matches(shortDatePattern)) {
						
					} else {
						System.err.println(sourceFolderSubFile.getName());
					}
					if (!stringFileDate.equals("")) {
						System.out.println(sourceFolderSubFile.getName());
						Date theDate = sourceFormat.parse(stringFileDate);
						String newFolder = targetFormat.format(theDate);
						if (!placeName.equals("")) {
							newFolder += " - " + placeName;
						}
						System.out.println(newFolder);
						File[] sourceContent = sourceFolderSubFile.listFiles();
						File newDestination = new File(destinationFolder + "/" + newFolder);
						newDestination.mkdirs();
						
						for (File file : sourceContent) {
							if (file.isFile()) {
								if (file.getName().matches("\\.\\_.*") || file.getName().equals(".DS_Store")) {
									System.err.println("\tApple File");
								}
								else {
									Files.copy(file.getAbsoluteFile().toPath(), new File(newDestination.getAbsolutePath() + "/" + file.getName()).toPath(), REPLACE_EXISTING);
								}
							}
							else {
								System.err.println(file.getName());
							}
						}
					}
				}
				else {
					System.err.println(sourceFolderSubFile.getName());
				}
			}
		}
		
	}
}

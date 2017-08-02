package p;

import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.file.FileMetadataDirectory;

public class CopyUsingMetadata {

	private String destinationFolder;

	public CopyUsingMetadata(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}

	
	//  2017 07 25 
	//  java.lang.Exception: /Volumes/Fotos/Export/01 February 2014/IMG_5307.JPG:/Volumes/Fotos/Export/01 February 2014/IMG_5307.JPG -> /Users/anvoges/Desktop/FotosFromShare2/2014-02-01/IMG_5307.JPG: Input/output error
	
	public static void main(String args[]) {
		// String sourceFolder = "/Volumes/Fotos";
		String sourceFolder = "/Volumes/Fotos";
		String destinationFolder = "/Users/anvoges/Desktop/FotosFromShare2";
		if (args.length == 2) {
			sourceFolder = args[0];
			destinationFolder = args[1];
		}
		try {
			new CopyUsingMetadata(destinationFolder).executeCopy(sourceFolder);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	String longDate = "[0-3][0-9]\\s[a-zA-Z]+\\s[0-9]{4}";
	String longDatePattern = "^" + longDate;
	String placePattern = "(^.+),\\s(" + longDate + ")";
	String ddMMyyyyFile = "(^[0-3][0-9])([01][0-9])([12][0-9]{3}).*";
	String yyyyMMddFile = "(^[12][0-9]{3})([01][0-9])([0-3][0-9]).*";
	String yearOnly = "^[12][0-9]{3}";
	String monthOnly = "^[01][0-9]";
	String dayOnly = "^[0-3][0-9]";

	public void executeCopy(String sourceFolder) throws Exception {
		File source = new File(sourceFolder);
		if (source.exists()) {
			File destination = new File(destinationFolder);
			if (!destination.exists()) {
				destination.mkdirs();
			}
			File[] sourceFolderContent = source.listFiles();
			for (File sourceFile : sourceFolderContent) {
				if (sourceFile.isDirectory()) {
					executeCopy(sourceFile.getAbsolutePath());
				} else if (sourceFile.isFile()) {
					saveFile(sourceFile, sourceFile.getName());
					// String theSource =
					// yearFile.getAbsoluteFile().toPath().toString();
					// String theDest = destination.getAbsolutePath() + "/" +
					// yearFile.getName();
					// System.out.println(theSource + " -> " + theDest);
					// Files.copy(new File(theSource).toPath(), new
					// File(theDest).toPath(), REPLACE_EXISTING);
				} else {
					System.err.println(sourceFile.getName());
				}
			}
		}

	}

	public void saveFile(File fromFile, String toFileName) throws Exception {
		try {
			if (fromFile.getName().equals(".DS_Store") || fromFile.getName().equals("._.DS_Store")
					|| fromFile.getName().endsWith(".ini") || fromFile.getName().startsWith("._")) {
				fromFile.delete();
				return;
			}
			if (fromFile.getName().endsWith(".sh")) {
				return;
			}
			String fromFileName = fromFile.getName();
			String fromFilePath = fromFile.getParent();
			MyDate fileDate = getFileDateFromMetadata(fromFile);
			if (fileDate == null) {
				fileDate = getFileDateFromFileName(fromFileName);
				if (fileDate == null) {
					fileDate = getFileDateFromSourcePath(fromFilePath);
				}
			}
			if (fileDate == null) {
				
				System.err.println("Could not get date for file "
						+ fromFile.getAbsolutePath());
				throw new Exception();
				//return;
			}
			String description = getDescriptionFromSource(fromFile);
			String finalDest = destinationFolder;
			finalDest += "/" + fileDate.toString();
			if (!StringUtil.isNull(description)) {
				finalDest += " - " + description;
			}
			String finalFile = finalDest + "/" + toFileName;
			System.out.println(fromFile.toString() + "->" + finalFile);
			new File(finalDest).mkdirs();
			Files.copy(fromFile.toPath(), new File(finalFile).toPath(),
					REPLACE_EXISTING);
		} catch (Exception e) {
			throw new Exception(fromFile.getAbsolutePath() + ":" + e.getMessage(), e);
		}
	}

	private String getDescriptionFromSource(File fromFile) {
		String description = "";
		String[] split = fromFile.getAbsolutePath().split("/");
		for (String string : split) {
			if (string.matches(placePattern)) {
				description = string.replaceAll(placePattern, "$1");
				break;
			}
		}
		return description;
	}

	private MyDate getFileDateFromSourcePath(String fromFilePath)
			throws ParseException {
		MyDate myDate = null;
		SimpleDateFormat sourceFormat = new SimpleDateFormat("dd MMM yyyy");
		Date date = null;
		String[] split = fromFilePath.split("/");
		int index = 0;
		for (String string : split) {
			if (string.matches(longDatePattern)) {
				date = sourceFormat.parse(string);
				myDate = new MyDate(date);
				break;
			} else if (string.matches(placePattern)) {
				String dateStr = string.replaceAll(placePattern, "$2");
				date = sourceFormat.parse(dateStr);
				myDate = new MyDate(date);
				break;
			} else if (string.matches(yearOnly)) {
				if (split[index+1].matches(monthOnly) && split[index+2].matches(dayOnly)) {
					myDate = new MyDate(string, split[index+1], split[index+2]);
					break;
				}
			}
			index++;
		}
		return myDate;
	}

	private MyDate getFileDateFromFileName(String fromFileName) {
		MyDate myDate = null;
		String year = null;
		String month = null;
		String day = null;
		if (fromFileName.matches(yyyyMMddFile)) {
			year = fromFileName.replaceAll(yyyyMMddFile, "$1");
			month = fromFileName.replaceAll(yyyyMMddFile, "$2");
			day = fromFileName.replaceAll(yyyyMMddFile, "$3");
		} else if (fromFileName.matches(ddMMyyyyFile)) {
			year = fromFileName.replaceAll(ddMMyyyyFile, "$3");
			month = fromFileName.replaceAll(ddMMyyyyFile, "$2");
			day = fromFileName.replaceAll(ddMMyyyyFile, "$1");
		}
		if (year != null) {
			myDate = new MyDate(year, month, day);
		}
		return myDate;
	}

	private MyDate getFileDateFromMetadata(File fromFile)
			throws ImageProcessingException, IOException {
		MyDate myDate = null;
		if (fromFile.getName().toLowerCase().endsWith(".mov") || fromFile.getName().toLowerCase().endsWith(".mp4") || fromFile.getName().toUpperCase().endsWith(".AAE")) {
			return null;
		}
		Metadata metadata = ImageMetadataReader.readMetadata(fromFile);
		ExifIFD0Directory dir = metadata
				.getFirstDirectoryOfType(ExifIFD0Directory.class);
		if (dir == null) {
			FileMetadataDirectory dir2 = metadata.getFirstDirectoryOfType(FileMetadataDirectory.class);
			Date date = dir2.getDate(3);
			if (date != null) {
				myDate = new MyDate(date);
			}
		}
		else {
			Date date = dir.getDate(306);
			if (date != null) {
				myDate = new MyDate(date);
			}
		}
		return myDate;
	}

}

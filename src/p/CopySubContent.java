package p;

import static java.nio.file.StandardCopyOption.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;

public class CopySubContent {

	private String sourceFolder;
	private String destinationFolder;


	public CopySubContent(String sourceFolder, String destinationFolder) {
		this.sourceFolder = sourceFolder;
		this.destinationFolder = destinationFolder;
	}
	
	

	
	public static void main(String args[]) {
		//String sourceFolder = "/Volumes/Fotos";
		String sourceFolder = "/Users/anvoges/Desktop/Fotos";
		String destinationFolder = "/Users/anvoges/Desktop/FotosFromShare2";
		if (args.length == 2) {
			sourceFolder = args[0];
			destinationFolder = args[1];
		}
		try {
			new CopySubContent(sourceFolder, destinationFolder).executeCopy();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	String ddMMyyyyFile = "(^[0-3][0-9])([01][0-9])([12][0-9]{3}).*";
	String yyyyMMddFile = "(^[12][0-9]{3})([01][0-9])([0-3][0-9]).*";
	String yearOnly = "^[12][0-9]{3}";
	String monthOnly = "^[01][0-9]";
	String dayOnly = "^[0-3][0-9]";


	public void executeCopy() throws Exception {
		File source = new File(sourceFolder);
		if (source.exists()) {
			File destination = new File(destinationFolder);
			if (!destination.exists()) {
				destination.mkdirs();
			}
			File[] sourceFolderContent = source.listFiles();
			for (File yearFile : sourceFolderContent) {
				if (yearFile.isDirectory()) {
					if (yearFile.getName().matches(yearOnly)) {
						String year = yearFile.getName();
						File[] monthDir = yearFile.listFiles();
						for (File monthFile : monthDir) {
							if (monthFile.isDirectory()) {
								if (monthFile.getName().matches(monthOnly)) {
									String month = monthFile.getName();
									File[] dayDir = monthFile.listFiles();
									for (File dayFile : dayDir) {
										//String theDestPath1 = destination.getAbsolutePath() + "/" + year + "-" + month;
										if (dayFile.isDirectory()) {
											String day = dayFile.getName();
											File[] images = dayFile.listFiles();
											for (File image : images) {
												//String theDestPath = theDestPath1 + "-" + day;
												if (image.isDirectory()) {
													File[] subfiles = image.listFiles();
													for (File file : subfiles) {
														if (file.isDirectory()) {
															System.err
																	.println(file.getAbsolutePath());
														}
														else {
															//String theSource = file.getAbsoluteFile().toPath().toString();
															//String theDest = theDestPath + "/" + file.getName();
															saveFile(file, year, month, day, file.getName());
															//System.out.println(theSource + " -> " + theDest);
															//new File(theDestPath).mkdirs();
															//Files.copy(new File(theSource).toPath(), new File(theDest).toPath(), REPLACE_EXISTING);
 															
														}
													}
												}
												else {
													//String theSource = image.getAbsoluteFile().toPath().toString();
													//String theDest = theDestPath + "/" + image.getName();
													//System.out.println(theSource + " -> " + theDest);
													saveFile(image, year, month, day, image.getName());
													//new File(theDestPath).mkdirs();
													//Files.copy(new File(theSource).toPath(), new File(theDest).toPath(), REPLACE_EXISTING);
												}
											}
										}
										else if (dayFile.isFile()) {
											//String theSource = dayFile.getAbsoluteFile().toPath().toString();
											//String theDest = theDestPath1 + "/" + dayFile.getName();
											saveFile(dayFile, year, month, null, dayFile.getName());
											//System.out.println(theSource + " -> " + theDest);
											//Files.copy(new File(theSource).toPath(), new File(theDest).toPath(), REPLACE_EXISTING);
										}
										else {
											System.err.println(dayFile.getAbsolutePath());
										}
									}
								}
								else {
									System.err.println(monthFile.getAbsolutePath());
								}
							}
							else if (monthFile.isFile()) {
								//String theSource = monthFile.getAbsoluteFile().toPath().toString();
								//String theDest = destination.getAbsolutePath() + "/" + year + "/" + monthFile.getName();
								//System.out.println(theSource + " -> " + theDest);
								saveFile(monthFile, year, null, null, monthFile.getName());
								//new File(destination.getAbsolutePath() + "/" + year).mkdirs();
								//Files.copy(new File(theSource).toPath(), new File(theDest).toPath(), REPLACE_EXISTING);
							}
							else {
								System.err.println(monthFile.getAbsolutePath());
							}
						}
					} else {
						System.err.println(yearFile.getAbsolutePath());
					}
				}
				else if (yearFile.isFile()) {
					saveFile(yearFile, null, null, null, yearFile.getName());
					//String theSource = yearFile.getAbsoluteFile().toPath().toString();
					//String theDest = destination.getAbsolutePath() + "/" + yearFile.getName();
					//System.out.println(theSource + " -> " + theDest);
					//Files.copy(new File(theSource).toPath(), new File(theDest).toPath(), REPLACE_EXISTING);
				}
				else {
					System.err.println(yearFile.getName());
				}
			}
		}
		
	}

	public void saveFile(File fromFile, String year, String month, String day, String toFileName) throws IOException, ImageProcessingException {
		if (fromFile.getName().equals(".DS_Store")) {
			return;
		}
		if (toFileName.matches(yyyyMMddFile)) {
			year = toFileName.replaceAll(yyyyMMddFile, "$1");
			month = toFileName.replaceAll(yyyyMMddFile, "$2");
			day = toFileName.replaceAll(yyyyMMddFile, "$3");
		}
		else if (toFileName.matches(ddMMyyyyFile)) {
			year = toFileName.replaceAll(ddMMyyyyFile, "$3");
			month = toFileName.replaceAll(ddMMyyyyFile, "$2");
			day = toFileName.replaceAll(ddMMyyyyFile, "$1");
		}
		String finalDest = destinationFolder;
		if (year != null) {
			finalDest += "/" + year;
		}
		if (month != null) {
			finalDest += "-" + month;
		}
		if (day != null) {
			finalDest += "-" + day;
		}
		String finalFile = finalDest + "/" + toFileName;
		System.out.println(fromFile.toString() + "->" + finalFile);
		//new File(finalDest).mkdirs();
		//Files.copy(fromFile.toPath(), new File(finalFile).toPath(), REPLACE_EXISTING);
		Metadata metadata = ImageMetadataReader.readMetadata(fromFile);
		Iterable<Directory> directories = metadata.getDirectories();
		for (Directory directory : directories) {
			System.out.println(directory.getName());
		}
	}
}

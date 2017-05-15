package GoogleMapsMiner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.apache.commons.lang3.StringEscapeUtils;

public class ScannerWithRegions {

	/**Takes a list of company names and converts it to a set of Company objects with those company names.
	 * @param fileName File name and address of a .txt file with a different company name on each line, e.g. "C:\\Users\\Username\\Folder\\filename.txt"
	 * @return ArrayList of Company objects, each with the field "ourName" set to the company name on file.
	 * @throws FileNotFoundException if there was a bad address and the file couldn't be found.
	 */
	public static ArrayList<Company> companiesFromFile(String fileName) throws FileNotFoundException{
		//empty ArrayList for results:
		ArrayList<Company> result = new ArrayList<Company>();

		//setup to read from file:
		FileReader fr = new FileReader(fileName);
		BufferedReader b = new BufferedReader(fr);
		Scanner s = new Scanner(b);

		//scan every new line of the file as a new company name:
		while(s.hasNextLine()){
			String nextCompany = s.nextLine();
//			System.out.println("Next line reads: "+nextCompany);
			String thisCompanyName = nextCompany.replaceAll(";","").replaceAll("\\s+", " ");
//			System.out.println("Searching for: "+thisCompanyName);
			Company thisCompany = new Company();
			thisCompany.setOurName(thisCompanyName);
			thisCompany.justName = nextCompany.split(";")[0].trim();
			result.add(thisCompany);
		}
		s.close();
		return result;
	}

	/** Removes , ' ( ) and . from a String.
	 * @param s String to be simplified
	 */
	public static String noFrills(String input){
		String s = input;
		String[] arr;

		arr = s.split("[',\\.\\(\\)]");
		s = "";
		for(String t : arr){
			s+=t;
		}
		return s;
	}

	/**Takes two Strings and returns a numerical measure of their similarity.
	 * @param a One String
	 * @param b The other String
	 * @return
	 */
	public static double stringsMatchRating(String aIn, String bIn){

		//get rid of punctuation:
		String a = noFrills(aIn);
		String b = noFrills(bIn);

		//check if they're exactly the same:
		if(a.equalsIgnoreCase(b)){
			return 1;
		}

		//start counting scores:
		double score = 0;

		//split A into individual words:
		ArrayList<String> aTokens = new ArrayList<String>();
		Scanner as = new Scanner (a);
		as.useDelimiter("[,.\\s]");
		while(as.hasNext()){
			String thisA = as.next();
			aTokens.add(thisA);
		}
		as.close();

		//split B into individual words:
		ArrayList<String> bTokens = new ArrayList<String>();
		Scanner bs = new Scanner (b);
		bs.useDelimiter("[,.\\s]");
		while(bs.hasNext()){
			String thisB = bs.next();
			bTokens.add(thisB);
		}
		bs.close();

		double mostWords;
		if(aTokens.size()<bTokens.size()){
			mostWords = bTokens.size();
		}
		else{
			mostWords = aTokens.size();
		}

		//go through each word of one string, and compare with each word of the other string:
		for(int i = 0; i<aTokens.size(); i++){
			String thisA = aTokens.get(i);
			for(int j = 0; j< bTokens.size(); j++){
				String thisB = bTokens.get(j);
				//if any 2 strings are identical, +1 to the score
				if(thisA.equalsIgnoreCase(thisB)){
					score+=1;
				}

				//common abbreviations and their meanings should be equivalent:
				else{
					if(thisA.equalsIgnoreCase("ltd")&&thisB.equalsIgnoreCase("limited")||thisB.equalsIgnoreCase("ltd")&&thisA.equalsIgnoreCase("limited")){
						score+=1;
					}
					if(thisA.equalsIgnoreCase("corp")&&thisB.equalsIgnoreCase("corporation")||thisB.equalsIgnoreCase("corp")&&thisA.equalsIgnoreCase("corporation")){
						score+=1;
					}
					if(thisA.equalsIgnoreCase("co")&&thisB.equalsIgnoreCase("company")||thisB.equalsIgnoreCase("co")&&thisA.equalsIgnoreCase("company")){
						score+=1;
					}
					if(thisA.equalsIgnoreCase("inc")&&thisB.equalsIgnoreCase("incorporated")||thisB.equalsIgnoreCase("inc")&&thisA.equalsIgnoreCase("incorporated")){
						score+=1;
					}
				}
			}
		}

		double rating = score/mostWords;		
		return rating;
	}

	/**Takes a string and replaces accented characters with the standard ASCII character.
	 * @param str String to be simplified
	 * @return String without accents
	 */
	public static String noAccent(String str){
		String r = StringEscapeUtils.unescapeHtml4(str);
		return r;
	}
	
	/**Moves a scanner forward until it reaches a particular String.
	 * Scanner stops right before the word, so that s.next() returns the argument String.
	 * @param s the scanner to be moved forward
	 * @param word the String which will stop the scanner
	 */
	public static void scanForString(Scanner s, String word){
		while(s.hasNext()){
			if(s.hasNext(word)){
				break;
			}
			else{
				s.next();
			}
		}
	}

	/**Takes a Company object, searches Google Maps for its name, and adds the information to that Company object,
	 * @param c The company to be Googled.
	 * @throws IOException if there's an issue with the URL (i.e. punctuation).
	 */
	public static void googleCompany(Company c) throws IOException{

		//first split the company name into individual strings:
		String thisFullName = c.ourName;
		String[] namePartsArray = thisFullName.split(" ");

		//now take each of the name parts and build a Google Maps search URL from it:
		StringBuilder urlStringBuilder = new StringBuilder("https://maps.google.com/?q=");
		for(int i = 0; i < namePartsArray.length; i++){
			String thisPart = namePartsArray[i];
			urlStringBuilder.append("+"+thisPart);
		}
		String urlString = urlStringBuilder.toString();
		URL u = new URL(urlString);

		//set up a scanner to read from URL:
		InputStream is = u.openStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader b = new BufferedReader(isr);
		Scanner s = new Scanner(b);

		//get the scanner to use , : ' { } as delimiters:
		s.useDelimiter("[,:'{}]+");

		//first see if there's a result at all:
		try{
			//scan through the page's source code to find the name tag of the top result:
			scanForString(s,"name");
			s.next(); //skip the String "name"
			String thisGoogleName = s.next();
			//this if block only evaluates if the name has an "&" in it, which shows up as "\x26" in hex.
			if(thisGoogleName.contains("\\x26")){
				String[] bothSides = thisGoogleName.split("x26");
				bothSides[0] = bothSides[0].substring(0, bothSides[0].length()-1);
				thisGoogleName = bothSides[0].concat("&"+bothSides[1]);
			}
			//this if block only evaluates if the name has a "'" in it, which shows up as "\x27" in hex.
			if(thisGoogleName.contains("\\x27")){
				String[] bothSides = thisGoogleName.split("x27");
				bothSides[0] = bothSides[0].substring(0, bothSides[0].length()-1);
				thisGoogleName = bothSides[0].concat("'"+bothSides[1]);
			}

			//check if top result matches what we were looking for to a reasonable extent:
			if(stringsMatchRating(thisGoogleName, thisFullName) < 0.5){
				NoSuchElementException e = new NoSuchElementException(); //will result the same way as there being no result at all
				throw e;
			}
			c.setGoogleName(thisGoogleName);

			//now that we know there's a result, get information from it:
			try{
				//now scan for the address tag of that result:
				scanForString(s,"addressLines");
				s.next(); //skip the String "addressLines"
				s.next(); //skip the String "["
				while(!s.hasNext("]")){
					String thisAddressLine = s.next().trim();
					
					//get rid of accents if necessary:
					if(thisAddressLine.contains("&#")){
						thisAddressLine = noAccent(thisAddressLine);
					}

					//check if company address in US:
					if(thisAddressLine.matches("\\D\\D\\s+\\d\\d\\d\\d\\d")){ //check for US address e.g. TX 75201
						c.setCountry("US");
						String state = thisAddressLine.substring(0, 2);
						String zipCode = thisAddressLine.substring(3,8);
						c.setCounty(state);
						c.setPostCode(zipCode);
					}

					//check if company address in UK:
					else if(thisAddressLine.contains("United Kingdom")){
						c.setCountry("UK");
						String lastAL = c.addressLines.get(c.addressLines.size()-1);	//grab region & post code from previous line
						c.addressLines.remove(c.addressLines.size()-1);					//remove previous line
						String[] arr = lastAL.split("\\s+");							//split last address line into region and post code
						int l = arr.length;
						String thisPostCode = arr[l-2].concat(" "+arr[l-1]);
						c.setPostCode(thisPostCode);
						String thisAL = "";
						for(int i = 0; i < l-2; i++){
							thisAL = thisAL.concat(" "+arr[i]);
						}
						c.addressLines.add(thisAL.trim());
					}
					
					//check if company address in Canada:
					else if(thisAddressLine.contains("Canada")){
						c.setCountry("CA");
						String lastAL = c.addressLines.get(c.addressLines.size()-1);	//grab region & post code from previous line
						c.addressLines.remove(c.addressLines.size()-1);					//remove previous line
						String[] arr = lastAL.split("\\s+");							//split last address line into region and post code
						int l = arr.length;
						String thisPostCode = arr[l-3].concat(" "+arr[l-2].concat(" "+arr[l-1]));
						c.setPostCode(thisPostCode);
						String thisAL = "";
						for(int i = 0; i < l-3; i++){
							thisAL = thisAL.concat(" "+arr[i]);
						}
						c.addressLines.add(thisAL.trim());
					}
					
					else{
						c.addressLines.add(thisAddressLine);
					}
				}

				//now scan for the phone number:
				scanForString(s,"number");
				s.next(); //skip the String "number"
				String thisNumber = s.next();
				c.setPhoneNumber(thisNumber);

				//now scan for the website:
				scanForString(s,"actual_url");
				s.next(); //skip the String "actual_url"
				s.next(); //skip the String "tp"
				String thisURL = s.next();
				String thisWebsite = thisURL.substring(2);
				if(thisWebsite.endsWith("/")){
					thisWebsite = thisWebsite.substring(0, thisWebsite.length()-1); //just get rid of the "/" at the end
				}
				c.setWebsite(thisWebsite);

				s.close();

			}
			catch(NoSuchElementException e){ //if there are no Google maps results, the company cannot be updated with Google results
				s.close();
			}

		}
		catch(NoSuchElementException e){ //if there are no Google maps results, mark the company as such
			c.setGoogleName("No_Google_Maps_Result_Found");
			s.close();
		}
	}

	/**Gives a PrintWriter that can be used to print to a file.
	 * @param fileName Address and name of file to be edited/created, e.g. "C:\\Users\\Username\\Folder\\filename.txt"
	 * @return A PrintWriter object that can be used to print to the file
	 * @throws IOException If fileName is a bad address and can't be found
	 */
	public static PrintWriter printToFile(String fileName) throws IOException{
		File outputfile = new File(fileName);
		FileWriter fw = new FileWriter(outputfile);
		BufferedWriter b = new BufferedWriter(fw);
		PrintWriter pw =  new PrintWriter(b);
		return pw;
	}

	//MAIN METHOD:
	public static void main(String[] args) throws Exception{
		try{

			//set up files and writers:
			String companyListFileName = "/path/to/file/Companies_Regions.txt";
			ArrayList<Company> companies = companiesFromFile(companyListFileName);
			String companyResultsFileName = "/path/to/file/Companies_Regions_FoundAddress.txt";
			PrintWriter outPW = printToFile(companyResultsFileName);
			String noResultsFileName = "/path/to/file/Companies_Regions_NoAddress.txt";
			PrintWriter outNoPW = printToFile(noResultsFileName);

			//loop over all companies taken from input file:
			for(int i = 0; i < companies.size(); i++){
				try{
					Company thisCompany = companies.get(i);
					googleCompany(thisCompany);
					if(thisCompany.allData().contains("No_Google_Maps_Result_Found")){
//						outNoPW.println(thisCompany.allData());
//						outNoPW.flush();
					}
					else{
//						System.out.println(thisCompany.justName+"; "+thisCompany.allData());
						outPW.println(thisCompany.justName+"; "+thisCompany.allData());
						outPW.flush();
					}
				}
				catch (ArrayIndexOutOfBoundsException e){
					System.out.println("ArrayIndexOutOfBoundsException thrown. Carrying on regardless.");
				}
			}
		}
		catch(UnknownHostException e){
			System.out.println("UnknownHostException encountered, probably because your internet connection is bad.");
		}
		catch(IOException e){
			System.out.println("IOException encountered, probably because a company name made a bad URL.");
		}
		catch(Exception e){
			throw e;
		}
		System.out.println("Program finished");
	}
}

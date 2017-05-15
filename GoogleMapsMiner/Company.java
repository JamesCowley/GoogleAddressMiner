package GoogleMapsMiner;

import java.util.ArrayList;

public class Company {
	String ourName, googleName, county, postCode, country, phoneNumber, website;
	ArrayList<String> addressLines = new ArrayList<String>();
	String justName;
	
	//constructor:
	Company(){}
	
	//value setters:
	public void setOurName(String input){ourName = input;}
	public void setGoogleName(String input){googleName = input;}
	public void setPhoneNumber(String input){phoneNumber = input;}
	public void setWebsite(String input){website = input;}
	public void setCountry(String input){country = input;}
	public void setCounty(String input){county = input;}
	public void setPostCode(String input){postCode = input;}

	//toString method mainly for testing (deprecated):
	public String toString(){
		return "Given company name: "+ourName+"\r\nFound company with name: "+googleName+"\r\nAddress info found is: "+addressLines+"\r\nPhone number: "+phoneNumber+"\r\nWebsite: "+website;
	}
	
	//method to return just the website URL:
	public String justWebsite(){
		return ourName+"; "+googleName+"; "+website+";";
	}
	
	//method to return just the address:
	public String justAddressInfo(){
		return ourName+"; "+googleName+"; "+addressLines+"; "+county+"; "+postCode+"; "+country+";"+phoneNumber+";";
	}
	
	//method to return all the data:
	public String allData(){
		return ourName+"; "+googleName+"; "+addressLines+"; "+county+"; "+postCode+"; "+country+"; "+phoneNumber+"; "+website+"; ";
	}
}

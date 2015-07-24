package org.hadatac.hasneto.loader;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.poi.ss.usermodel.Cell;

public class ValueCellProcessing {
	
	boolean isFullURI(String str) {
		return str.startsWith("http");
	}
	
	boolean isAbbreviatedURI(String str) {
		if (!str.contains(":") || str.contains("//"))
			return false;
		if (str.substring(0,str.indexOf(':')).contains(" "))
			return false;
		return true;
	}
	
	/*
	 *  the method verifies if cellContent contains a set of URIs, which we call an object set. Returns true if 
	 *  the content is regarded to be an object set.
	 */
	boolean isObjectSet (String cellContent) {
		 // we need to tokanize the string and verify that the first token is an URI
	     StringTokenizer st = new StringTokenizer(cellContent,",");
	     
	     // the string needs to have at least two tokens
	     String firstToken, secondToken;
	     if (!st.hasMoreTokens()) {
	    	 return false;
	     }
	     firstToken = st.nextToken().trim();
	     if (!st.hasMoreTokens()) {
	    	 return false;
	     }
	     secondToken = st.nextToken().trim();
	     
	     // the first token (we could also test the second) needs to be an URI
	     return (isFullURI(firstToken) || isAbbreviatedURI(firstToken));
	}
	
	/* 
	 *  if the argument str starts with the URI of one of the name spaces registered in NameSpaces.table, the
	 *  URI gets replaced by the name space's abbreviation. Otherwise, the string is returned wrapper
	 *  around angular brackets.
	 */
	private String replaceNameSpace(String str) {
		String resp = str;
	    for (Map.Entry<String, NameSpace> entry : NameSpaces.table.entrySet()) {
	        String abbrev = entry.getKey().toString();
	        String nsString = entry.getValue().getName();
	        if (str.startsWith(nsString)) {
	        	//System.out.println("REPLACE: " + resp + " / " + abbrev);
	        	resp = str.replace(nsString, abbrev + ":");
	        	return resp; 
	        }
	    }
	    return "<" + str + ">";
	}	
	
	/* 
	 *  check if the namespace in str is in the namamespace list (NameSpaces.table). 
	 *  If not, it issues a warning message. A warning message is issue if the name 
	 *  space used in the argument str is not registered in NameSpaces.table.
	 */
	public void validateNameSpace(String str) {
		//System.out.println("Validating namespace <" + str + ">");
		if (str.indexOf(':') <= 0)
			return;
		String abbrev = "";
		String nsName = str.substring(0,(str.indexOf(':') + 1));
	    for (Map.Entry<String, NameSpace> entry : NameSpaces.table.entrySet()) {
	        abbrev = entry.getKey().toString() + ":";
	        if (abbrev.equals(nsName)) {
	        	return;
	        }
	    }
		System.out.println("# WARNING: NAMESPACE NOT DEFINED <" + nsName + ">");
		System.out.println(abbrev);
	return;
	}
	
	private String processSubjectValue(String subject) {
		if (isAbbreviatedURI(subject)) 
			validateNameSpace(subject);
		// no indentation or semicolon at the end of the string
		return (replaceNameSpace(subject) + "\n");	
	}
	
	private String processObjectValue(String object) {
		
		// if abbreviated URI, just print it
		if (isAbbreviatedURI(object)) { 
			validateNameSpace(object);
			//System.out.print(object);
			return object;
		}

		// if full URI, either abbreviated it or print it between angled brackets
		if (isFullURI(object)) {
			// either replace namespace with acronym or add angled brackets
			//System.out.print(replaceNameSpace(object));
			return replaceNameSpace(object);
		} 		
		
		// if not URI, print the object between quotes
		object = object.replace("\n", " ").replace("\r", " ").replace("\"", "''");
		//System.out.println("\"" + object + "\"");		
		return "\"" + object + "\"";
	}
	
	public String exec(Cell cell, Vector<String> predicates) {

		String clttl = "";
		String cellValue = cell.getStringCellValue();
		String predicate = predicates.get(cell.getColumnIndex());

		// cell has subject value
		if (predicate.equals("hasURI")) {
			clttl = clttl + processSubjectValue(cell.getStringCellValue());
			return clttl;
		}
		
		// cell has object value
		clttl = clttl + "   " + predicate + " ";
		if (isObjectSet(cellValue)) {
		     StringTokenizer st = new StringTokenizer(cellValue,",");
		     while (st.hasMoreTokens()) {
		         clttl = clttl + processObjectValue(st.nextToken().trim());
		         if (st.hasMoreTokens()) {
		        	 clttl = clttl + ", ";
		         }
		     }
		} else {
			clttl = clttl + processObjectValue(cellValue);
		}
		clttl = clttl + ";\n";
				
		return clttl;
	}

}

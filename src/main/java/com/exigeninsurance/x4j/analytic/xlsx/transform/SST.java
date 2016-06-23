/*
 * Copyright 2008-2013 Exigen Insurance Solutions, Inc. All Rights Reserved.
 *
*/


package com.exigeninsurance.x4j.analytic.xlsx.transform;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.model.SharedStringsTable;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTRst;


final public class SST{
	private static final byte[] XML_SPACE_PRESERVE = " xml:space=\"preserve\"".getBytes(StandardCharsets.UTF_8);
	private static final byte[] E = ">".getBytes(StandardCharsets.UTF_8);
	private static final byte[] SI_T = "<si><t".getBytes(StandardCharsets.UTF_8);
	private static final byte[] T_SI = "</t></si>\n".getBytes(StandardCharsets.UTF_8);

	private final UTF8OutputStream out;
	private final Map<String,Long> cache = new HashMap< String, Long>();
	private long count;
	private long position;
	private long unique;

	public SST(UTF8OutputStream out,SharedStringsTable strings) throws IOException{
		this.out = out;
		long cnt = 0;
		for( CTRst  item : strings.getItems()){
			if(item != null){
				cache.put(item.getT(),cnt);
				write(out,item.getT());
				cnt++;
			}
		}
		position = cnt;
		unique = strings.getUniqueCount();
		count = strings.getCount();

	}

	private void write(UTF8OutputStream out, String item) throws IOException {
		position++;

		out.write(SI_T);

		if(preserveSpaces(item)){
			out.write(XML_SPACE_PRESERVE);
		}

		out.write(E);

		if(item != null){
			out.writeUTF(forXML(item));
		}

		out.write(T_SI);
	}


	private boolean preserveSpaces(String item) {
		return item != null && (item.length() > item.trim().length());
	}


	public long add(String str) throws IOException{
		count++;
		Long index = cache.get(str);

		if(index != null){
			return index;
		}else{
			unique++;
			index =  position;
			cache.put(str,index);
			write(out,str);
			return index;

		}
	}


	public static String toXMLEment(String aText){
		if(aText.startsWith("${")){
			aText = aText.substring(2, aText.length() - 1 );
		}

		if(aText.startsWith("translation")){
			aText = aText.substring("translation.".length());
		}

		StringBuilder builder = new StringBuilder();
		char previus = 0;
		for(char c : aText.toCharArray()){
			if(previus > 0 && Character.isLowerCase(previus) && Character.isUpperCase(c)){
				builder.append('-');
				builder.append(Character.toLowerCase(c));
			}else if (!Character.isLetter(c)){
				if(previus > 0){
					builder.append('-');
				}
			}else {
				builder.append(Character.toLowerCase(c));
			}
			previus = c;
		}

		return builder.toString();
	}

	public static String forXML(String aText){
		if(aText == null){
			return "";
		}
		StringBuilder result = new StringBuilder();
		StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character =  iterator.current();

		while (character != CharacterIterator.DONE ){

			switch(character){

			case '<' :
				result.append("&lt;");
				break;
			case  '>' :
				result.append("&gt;");
				break;
			case '\"' :
				result.append("&quot;");
				break;
			case '\'' :
				result.append("&#039;");
				break;
			case '&' :
				result.append("&amp;");
				break;
			default:
				result.append(character);
				break;
			}

			character = iterator.next();

		}

		return result.toString();
	}

	public long getCount() {
		return count;
	}

	public long getUnique() {
		return unique;
	}

}
package templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import main.XMLEditor;

public class Photostory extends XMLEditor{
	
	Document doc;
	String screenContent;
	
	public Photostory(Document doc, String screenContent){
		super(doc,screenContent);		
		this.doc = doc;
		this.screenContent = screenContent;
	}
	
	public Document editXML(){

		ArrayList<String> cellList = splitContentIntoCells(screenContent); 
		
		editCells(cellList);
		
		editTriggerEvent(cellList.size());
		
		return doc;
	}

	private void editCells(ArrayList<String> cellList) {
		
		Node cellNode;
		CDATASection cdata;
		Node custom = doc.getElementsByTagName("custom").item(0);
		int cellNumber = 1;
	
		for(String cellContent : cellList){
			
			//get cell node
			cellNode = doc.getElementsByTagName("cell").item(cellNumber - 1);
			
			
			//clone node
			if(cellNode == null){
				cellNode = doc.getElementsByTagName("cell").item(0).cloneNode(true);
				custom.appendChild(cellNode);
				
				//edit text node attribute. NodeList because attribute has been cloned
				editAttribute(getNodeListById("text","cell1_text").item(1),
						"id", "cell" + cellNumber + "_text");
				
				//edit image node attribute
				editAttribute(getNodeListById("image","cell1_image").item(1),
						"id", "cell" + cellNumber + "_image");
				
				//create event for the trigger (starts from zeroth index)
				createCellEvent(cellNumber - 1);
			}
			
			//get text content
			String titleText = getCellContent(cellContent, "HEADLINE");
			String text = getCellContent(cellContent, "TEXT");
			String promptText = getCellContent(cellContent, "PROMPT");
			titleText = addClass(titleText,"headline");
			promptText = addClass(promptText,"prompt");
			text = titleText + "\n" + text + "\n" + promptText;
			cdata = doc.createCDATASection(text);
			
			
			//insert text
			Node textNode = getNodeById("text", "cell" + cellNumber + "_text");
			replaceText(textNode,cdata);
			
			
			//edit image path
			editImagePath("cell" + cellNumber + "_image", "_0" + cellNumber);
			
			//check asset
			checkImageAsset("_0" + cellNumber);

			cellNumber++;
		}
		
	}
	

	private void editTriggerEvent(int numberOfCells) {

		Node triggerNode = doc.getElementsByTagName("trigger").item(0);
		String triggerEventString = "";
		
		for(int i=0;i<numberOfCells;i++){
	
			triggerEventString += "cell" + i + ",";
		}
		
		//remove last comma off string
		triggerEventString = triggerEventString.substring(0, triggerEventString.length() - 1);
		
		editAttribute(triggerNode, "events", triggerEventString);
	}	
	
	private void createCellEvent(int cellNumber) {
		
		Node eventsNode = doc.getElementsByTagName("events").item(0);		
		Element event = doc.createElement("event");
		Attr eventAttr = doc.createAttribute("id");		
		eventAttr.setValue("cell" + cellNumber);
		event.setAttributeNode(eventAttr);
		eventsNode.appendChild(event);		
	}	

	private ArrayList<String> splitContentIntoCells(String s) {
		ArrayList<String> list = new ArrayList<String>();
		
		//split content based on frame number
		String[]arr = s.split("<p><b>\\d</b></p>");
		
		for(int i=0;i<arr.length;i++){

			list.add(arr[i]);
		}
		
		//first item is the page description
		list.remove(0); 
		
		return list;
	};
	
	
	private String getCellContent(String cellContent, String heading){
		
		//split content by paragraph breaks
		Iterator<String> it = new ArrayList<String>(Arrays.asList(cellContent.split("\\n"))).iterator();
		String text = "";
		while(it.hasNext()){
			String line = it.next();
			
			if(line.contains(heading) && isAHeading(line)){
				//add the subsequent lines to the text String
				//stops when it reaches a new heading (i.e. OPTIONS)
				while(it.hasNext()){ 
					line = it.next();					
					if(isAHeading(line)){
						break;
					}					
					text += "\n" + line;
				}
			}
		}
		text = text.replaceAll("(?m)^[ \t]*\r?\n", "");
		return text;
	}	

}

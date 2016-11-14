package templates;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import main.XMLEditor;

public class TextGraphic extends XMLEditor{

	Document doc;
	String screenContent;
	
	public TextGraphic(Document doc, String screenContent){
		this.doc = doc;
		this.screenContent = screenContent;
	}


	public Document editXML(){
	
		editIntroText();
		
		editQuoteText();
		
		//editImagePath(doc);
		
		return doc;
	}
	
	private void editQuoteText() {

		String quoteText = getHeadingContent(screenContent, "QUOTE TEXT");
		Node quote = getNodeById(doc,"text","quoteText");
		replaceText(quote,doc.createCDATASection(quoteText));		
	}


	private void editIntroText(){
		
		String title = getHeadingContent(screenContent,"TITLE");
		String introText = getHeadingContent(screenContent, "BODY TEXT");
		String prompt = getHeadingContent(screenContent,"PROMPT");
		title = addClass(title, "title");
		prompt = addClass(prompt,"prompt");
		introText = title + introText + prompt;

		Node text = getNodeById(doc, "text", "screentext");
		CDATASection cdata = doc.createCDATASection(introText);
		replaceText(text,cdata);		
	}

	private void editImagePath() {
		
		String imagePath = "lib/images/content/" + getFilePath() + ".jpg";
		CDATASection cdata = doc.createCDATASection(imagePath);
		Node image = getNodeById(doc,"image","image1");
		replaceText(image,cdata);
	};
	
	
	
}
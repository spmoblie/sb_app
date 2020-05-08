package com.songbao.sampo_b.utils;

import com.songbao.sampo_b.widgets.area.AreaEntity;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;


public class XmlParserHandler extends DefaultHandler {

	private AreaEntity areaEntity = new AreaEntity();
	private List<AreaEntity> areaList = new ArrayList<>();
	 	  
	public XmlParserHandler() {
		
	}

	public List<AreaEntity> getAreaDataList() {
		return areaList;
	}

	@Override
	public void startDocument() throws SAXException {
		// 当读到第一个开始标签的时候，会触发这个方法
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		// 当遇到开始标记的时候，调用这个方法
		/*if (qName.equals("Country")) {

		} else*/
		if (qName.equals("district")) {
			areaEntity = new AreaEntity();
			areaEntity.setName(attributes.getValue(0));
			areaEntity.setSign(attributes.getValue(1));
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// 遇到结束标记的时候，会调用这个方法
		if (qName.equals("district")) {
			areaList.add(areaEntity);
        }
        /*else if (qName.equals("Country")) {

		}*/
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}

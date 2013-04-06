package com.github.lsiu.hkrestaurants.importer;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.github.lsiu.experiment.eclipselink.model.Restaurant;

public class RestaurantDataImporter {
	
	private static final Logger log = LoggerFactory.getLogger(RestaurantDataImporter.class);

	@PersistenceContext
	private EntityManager em;

	public void importData(InputStream inStream) throws Exception {

		Element rootElement = getXmlRootElement(inStream);
		processData(rootElement);
	}

	private void processData(Element root) throws Exception {

		NodeList nl = root.getElementsByTagName("LP");
		
		final int PAGE_SIZE = 30;
		List<Restaurant> restaurantPage = new ArrayList<Restaurant>();

		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) n;
				String type = getTagValue("TYPE", e);
				String dist = getTagValue("DIST", e);
				String licno = getTagValue("LICNO", e);
				String name = getTagValue("SS", e);
				String adr = getTagValue("ADR", e);
				String info = getTagValue("INFO", e);
				
				log.debug("Restaurant Name={}, license No={}", name, licno);

				Restaurant rs = new Restaurant();
				rs.address = adr;
				rs.districtCode = Short.valueOf(dist);
				rs.infoCode = info;
				rs.licenseNo = licno;
				rs.name = name;
				rs.typeCode = type;
				
				restaurantPage.add(rs);
				
				if (restaurantPage.size() > PAGE_SIZE) {
					commitPage(restaurantPage);
					restaurantPage.clear();
				}
			}
		}
	}

	@Transactional
	private void commitPage(List<Restaurant> restaurantPage) {
		for (Restaurant r: restaurantPage) {
			Restaurant prev = em.find(Restaurant.class, r.licenseNo);
			if (prev == null) {
				log.info("Insert restaurant '{}'", r.name);
				em.persist(r);
			} else {
				Restaurant merged = em.merge(r);
				if (merged.getVersion() > r.getVersion())
					log.info("Restaurant '{}' updated to version {}.", merged.name, merged.getVersion());
			}
		}
	}

	private Element getXmlRootElement(InputStream inStream) throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource inSource = new InputSource(inStream);
		Document doc = db.parse(inSource);

		doc.getDocumentElement().normalize();
		return doc.getDocumentElement();
	}

	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
				.getChildNodes();

		Node nValue = (Node) nlList.item(0);
		if (nValue == null)
			return null;
		return nValue.getNodeValue();
	}

}

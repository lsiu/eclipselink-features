package com.github.lsiu.experiment.eclipselink;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestaurantEventAdaptor extends DescriptorEventAdapter implements
		DescriptorCustomizer {
	
	private static final Logger log = LoggerFactory.getLogger(RestaurantEventAdaptor.class);

	@Override
	public void customize(ClassDescriptor descriptor) throws Exception {
		log.debug("customize descriptor");
		descriptor.getEventManager().addListener(this);
	}
	
	

}

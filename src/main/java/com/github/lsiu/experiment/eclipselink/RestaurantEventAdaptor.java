package com.github.lsiu.experiment.eclipselink;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.history.HistoryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestaurantEventAdaptor extends DescriptorEventAdapter implements
		DescriptorCustomizer {

	private static final Logger log = LoggerFactory
			.getLogger(RestaurantEventAdaptor.class);

	@Override
	public void customize(ClassDescriptor descriptor) throws Exception {
		log.debug("customize descriptor");
		descriptor.getEventManager().addListener(this);
		HistoryPolicy policy = new HistoryPolicy();
		policy.addHistoryTableName("RESTAURANT_HIST");
		policy.addStartFieldName("START_DATE");
		policy.addEndFieldName("END_DATE");
		descriptor.setHistoryPolicy(policy);
	}

}

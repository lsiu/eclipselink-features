package com.github.lsiu.experiment.eclipselink.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Customizer;

import com.github.lsiu.experiment.eclipselink.RestaurantEventAdaptor;

@SuppressWarnings("serial")
@Entity
@Customizer(RestaurantEventAdaptor.class)
public class Restaurant implements Serializable {

	@Column
	public short districtCode;

	@Column(length = 256)
	public String name;

	@Column(length = 2)
	public String typeCode;

	@Id
	@Column(length = 10)
	public String licenseNo;

	@Column(length = 65535)
	public String address;

	@Column(length = 16)
	public String infoCode;

	@Version
	private Integer version;
	
	public Integer getVersion() {
		return version;
	}
}

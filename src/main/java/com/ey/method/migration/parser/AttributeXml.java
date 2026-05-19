package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class AttributeXml {

    @XmlAttribute(name = "name")
    private String name;

    @XmlValue
    private String value;

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}

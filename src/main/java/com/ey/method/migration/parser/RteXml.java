package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class RteXml {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "id")
    private String id;

    @XmlValue
    private String value;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}

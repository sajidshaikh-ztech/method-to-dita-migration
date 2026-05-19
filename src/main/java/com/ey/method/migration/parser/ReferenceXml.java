package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class ReferenceXml {

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "Element")
    private ElementDetailXml element;

    public String getName() {
        return name;
    }

    public ElementDetailXml getElement() {
        return element;
    }
}

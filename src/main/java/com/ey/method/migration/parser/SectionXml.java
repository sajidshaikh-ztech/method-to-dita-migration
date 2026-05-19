package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class SectionXml {

    @XmlAttribute(name = "name")
    private String name;

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "rte")
    private List<RteXml> rtes;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<RteXml> getRtes() {
        return rtes;
    }
}

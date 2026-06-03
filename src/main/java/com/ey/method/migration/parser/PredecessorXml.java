package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class PredecessorXml {
    @XmlAttribute(name = "type")
    private String type;

    @XmlAttribute(name = "index")
    private Integer index;

    public String getType() { return type; }
    public Integer getIndex() { return index; }
    
    public void setType(String type) { this.type = type; }
    public void setIndex(Integer index) { this.index = index; }
}

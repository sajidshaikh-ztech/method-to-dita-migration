package com.ey.method.migration.parser;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "Element")
@XmlAccessorType(XmlAccessType.FIELD)
public class ElementDetailXml {

    @XmlAttribute(name = "Url")
    private String url;

    @XmlAttribute(name = "Type")
    private String type;

    @XmlAttribute(name = "TypeName")
    private String typeName;

    @XmlAttribute(name = "DisplayName")
    private String displayName;

    @XmlAttribute(name = "Id")
    private String id;

    @XmlAttribute(name = "Name")
    private String name;

    @XmlElement(name = "attribute")
    private List<AttributeXml> attributes;

    @XmlElement(name = "reference")
    private List<ReferenceXml> references;

    @XmlElement(name = "section")
    private List<SectionXml> sections;

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<AttributeXml> getAttributes() {
        return attributes;
    }

    public List<ReferenceXml> getReferences() {
        return references;
    }

    public List<SectionXml> getSections() {
        return sections;
    }

    /**
     * Helper to find an attribute value by name, searching recursively in references.
     */
    public String getAttributeValue(String name) {
        if (attributes != null) {
            for (AttributeXml attr : attributes) {
                if (name.equals(attr.getName())) {
                    return attr.getValue();
                }
            }
        }
        if (references != null) {
            for (ReferenceXml ref : references) {
                if (ref.getElement() != null) {
                    String val = ref.getElement().getAttributeValue(name);
                    if (val != null) {
                        return val;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Helper to find an RTE value by name or id, searching recursively in references.
     */
    public String getRteValue(String nameOrId) {
        if (sections != null) {
            for (SectionXml sec : sections) {
                if (sec.getRtes() != null) {
                    for (RteXml rte : sec.getRtes()) {
                        if (nameOrId.equalsIgnoreCase(rte.getName()) || nameOrId.equalsIgnoreCase(rte.getId())) {
                            return rte.getValue();
                        }
                    }
                }
            }
        }
        if (references != null) {
            for (ReferenceXml ref : references) {
                if (ref.getElement() != null) {
                    String val = ref.getElement().getRteValue(nameOrId);
                    if (val != null) {
                        return val;
                    }
                }
            }
        }
        return null;
    }
}

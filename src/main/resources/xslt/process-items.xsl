<xsl:stylesheet version="2.0" 
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xs">

    <xsl:output method="xml" indent="yes"/>

    <xsl:param name="outputDir" select="'output'"/>

    <!-- Match the root element -->
    <xsl:template match="/">
        <migration-summary>
            <!-- Apply template to process-item elements of type "Phase", "Activity", or "Task Descriptor" and deduplicate by ID -->
            <xsl:for-each-group select="//process-item[@type='Phase' or @type='Activity' or @type='Task Descriptor']" group-by="@id">
                <xsl:apply-templates select="."/>
            </xsl:for-each-group>
        </migration-summary>
    </xsl:template>

    <!-- Transformation template for Phase and Activity process-items -->
    <xsl:template match="process-item[@type='Phase' or @type='Activity']">
        <!-- Generate a pseudo-GUID for the topic ID. (For true 128-bit random UUIDs, XSLT 3.0 or Java Extensions are required) -->
        <xsl:variable name="pseudoGuid" select="concat(
            substring(concat(generate-id(.), '1a2b3c4d'), 1, 8), '-',
            '4a1b-',
            '8c2d-',
            '9e3f-',
            substring(concat(generate-id(.), '5a6b7c8d9e0f'), 1, 12)
        )"/>

        <!-- Generate a separate DITA topic file for each process-item in a folder based on its type -->
        <xsl:result-document href="file://{$outputDir}/{lower-case(@type)}/{@id}.dita" method="xml" indent="yes" doctype-public="-//OASIS//DTD DITA Topic//EN" doctype-system="topic.dtd">
            <topic id="{$pseudoGuid}">
                <title><xsl:value-of select="@name"/></title>
                <prolog>
                    <data name="methodId" value="{@id}"/>
                    <data name="type" value="{@type}"/>
                    <data name="index" value="{@index}"/>
                </prolog>
                <body>
                    <xsl:if test="@brief-description != ''">
                        <section>
                            <title>Brief Description</title>
                            <!-- Disable output escaping in case brief-description contains HTML snippets from method -->
                            <p><xsl:value-of select="@brief-description" disable-output-escaping="yes"/></p>
                        </section>
                    </xsl:if>
                </body>
            </topic>
        </xsl:result-document>
    </xsl:template>

    <!-- Transformation template for Task Descriptor process-items -->
    <xsl:template match="process-item[@type='Task Descriptor']">
        <!-- Generate a pseudo-GUID for the topic ID -->
        <xsl:variable name="pseudoGuid" select="concat(
            substring(concat(generate-id(.), '1a2b3c4d'), 1, 8), '-',
            '4a1b-',
            '8c2d-',
            '9e3f-',
            substring(concat(generate-id(.), '5a6b7c8d9e0f'), 1, 12)
        )"/>

        <!-- Lookup the description from the related <task> element -->
        <xsl:variable name="relatedTaskId" select="@related-task"/>
        <xsl:variable name="taskDesc" select="//task[@id=$relatedTaskId]/@brief-description"/>

        <!-- Generate a separate DITA topic file in the 'task' folder -->
        <xsl:result-document href="file://{$outputDir}/task/{@id}.dita" method="xml" indent="yes" doctype-public="-//OASIS//DTD DITA Topic//EN" doctype-system="topic.dtd">
            <topic id="{$pseudoGuid}">
                <title><xsl:value-of select="@name"/></title>
                <prolog>
                    <data name="methodId" value="{@id}"/>
                    <data name="type" value="{@type}"/>
                    <data name="index" value="{@index}"/>
                </prolog>
                <body>
                    <xsl:if test="$taskDesc != ''">
                        <section>
                            <title>Brief Description</title>
                            <p><xsl:value-of select="$taskDesc" disable-output-escaping="yes"/></p>
                        </section>
                    </xsl:if>
                </body>
            </topic>
        </xsl:result-document>
    </xsl:template>

</xsl:stylesheet>

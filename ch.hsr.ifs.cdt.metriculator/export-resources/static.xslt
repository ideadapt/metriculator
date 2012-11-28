<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet PUBLIC "Unofficial XSLT 1.0 DTD" "http://www.w3.org/1999/11/xslt10.dtd">
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="/">
		<table>
			<thead>
				<xsl:call-template name="header" />
			</thead>
			<tbody>
				<xsl:apply-templates/>	
			</tbody>
		</table>
	</xsl:template>
	<xsl:template name="header">
		<xsl:for-each select="metriculator/meta/metrics/*">
			<th data-description="{@description}">
				<xsl:value-of select="local-name()" />
			</th>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="node">
		<tr class="{@type} indent-{count(ancestor::node)}">
			<td class="label">
				<xsl:value-of select="@label"/>
			</td>
			<xsl:apply-templates select="metrics/*"/>
		</tr>
		<xsl:apply-templates select="node"/>
	</xsl:template>	
	<xsl:template match="metrics/*">
		<td class="{local-name()}">
			<xsl:value-of select="."/>
		</td>
	</xsl:template>
</xsl:stylesheet>

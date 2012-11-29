<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet PUBLIC "Unofficial XSLT 1.0 DTD" "http://www.w3.org/1999/11/xslt10.dtd">
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="themes/{string(/metriculator/properties/theme/@name)}/style.css" />
			</head>
			<body>
				<table>
					<thead>
						<xsl:call-template name="header-cells"/>
					</thead>
					<tbody>
						<xsl:apply-templates/>
					</tbody>
				</table>
				<script src="themes/{string(/metriculator/properties/theme/@name)}/script.js" />
			</body>
		</html>
	</xsl:template>
	<xsl:template name="header-cells">
		<th>Label</th>
		<xsl:for-each select="metriculator/node[1]/metrics/*">
			<th data-description="{@description}" class="metric">
				<xsl:value-of select="local-name()"/>
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
		<td class="{local-name()} metric-value problem-{@problem-state}">
			<xsl:value-of select="."/>
		</td>
	</xsl:template>
</xsl:stylesheet>

<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet PUBLIC "Unofficial XSLT 1.0 DTD" "http://www.w3.org/1999/11/xslt10.dtd">
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" indent="yes" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&lt;!DOCTYPE html&gt;</xsl:text>
		<html>
			<head>
				<link rel="stylesheet" type="text/css" href="themes/{string(/metriculator/properties/theme/@name)}/style.css"/>
			</head>
			<body>
				<div id="preferences">
					<xsl:call-template name="preferences"/>
				</div>
				<div id="tree">
					<table>
						<thead>
							<tr>
								<xsl:call-template name="header-cells"/>
							</tr>
						</thead>
						<tbody>
							<xsl:call-template name="tree"/>
						</tbody>
					</table>
				</div>
				<script src="require-jquery.js" data-main="script" />
			</body>
		</html>
	</xsl:template>
	<xsl:template name="preferences">
		<table>
			<xsl:for-each select="metriculator/properties/preferences/*">
				<tr class="metric">
					<th colspan="2">
						<xsl:value-of select="@longname"/> (<xsl:value-of select="@shortname"/>)
					</th>
				</tr>
				<xsl:for-each select="problem/*[not(self::report_problems)]">
					<tr class="preference">
						<td>
							<xsl:value-of select="local-name()"/>
						</td>
						<td>
							<xsl:value-of select="."/>
						</td>
					</tr>
				</xsl:for-each>
			</xsl:for-each>
		</table>
	</xsl:template>
	<xsl:template name="header-cells">
		<th>Scope</th>
		<xsl:for-each select="metriculator/properties/preferences/*">
			<th data-longname="{@longname}" class="metric">
				<xsl:value-of select="@shortname"/>
			</th>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="tree">
		<xsl:for-each select="//node">
			<tr class="{@type} indent-{count(ancestor::node)}">
				<td class="label">
					<span><xsl:value-of select="@label"/></span>
				</td>
				<xsl:apply-templates select="metrics/*"/>
			</tr>
		</xsl:for-each>
	</xsl:template>
	<xsl:template match="metrics/*">
		<td class="{local-name()} value {@problem-state}">
			<xsl:value-of select="."/>
		</td>
	</xsl:template>
</xsl:stylesheet>

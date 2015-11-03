<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
	<html>
		<head>
			<style type="text/css">
				.button_NotPerformed { background-color: #D3D3D3; color: #000000; }
				.border_NotPerformed { background-color: #FFFFFF; color: #000000; border: 1px solid #D3D3D3; }
				.button_NotApplicable { background-color: #D3D3D3; color: #000000; }
				.border_NotApplicable { background-color: #FFFFFF; color: #000000; border: 1px solid #D3D3D3; }
				.button_Passed	{ background-color: #90EE90; color: #000000; }
				.border_Passed	{ background-color: #FFFFFF; color: #000000; border: 1px solid #90EE90; }
				.button_Failed { background-color: #FA8072; color: #000000; }
				.border_Failed { background-color: #FFFFFF; color: #000000; border: 1px solid #FA8072; }
			
				.details_header_passed { background-color: #90EE90; color: #000000; }
				.details_body_passed { background-color: #FFFFFF; color: #000000; border: 1px solid #90EE90; }
				.details_header_failed { background-color: #FA8072; color: #000000; }
				.details_body_failed { background-color: #FFFFFF; color: #000000; border: 1px solid #FA8072; }
			
				pre { white-space: pre-wrap; word-wrap: break-word; }
			</style>
		</head>
		<body>
			<h1>Overview</h1>
			<table align="center" style="table-layout:fixed; border: 0px;">
				<tr>
					<td style="width: 100"></td>
					<td></td>
					<td style="width: 200"></td>
				</tr>
				<xsl:for-each select="report/testcase">
					<tr>
						<td><xsl:attribute name="class">border_<xsl:value-of select="@result" /></xsl:attribute>
							<xsl:value-of select="@module" />
						</td>
						<td><xsl:attribute name="class">border_<xsl:value-of select="@result" /></xsl:attribute>
							<b><xsl:value-of select="@name" /></b>
						</td>
						<td align="center"><xsl:attribute name="class">button_<xsl:value-of select="@result" /></xsl:attribute>
							<b><xsl:value-of select="@result" /></b>
						</td>
					</tr>
				</xsl:for-each>
			</table>
			<h1>Details</h1>
			<xsl:for-each select="report/testcase[@result='Failed']">
					<h2><xsl:value-of select="@name" />&#160;(<xsl:value-of select="@module" />)</h2>
					<table align="center" style="table-layout:fixed; border: 0px;">
						<tr>
							<td style="width: 50"></td>
							<td style="width: 75"></td>
							<td></td>
						</tr>
						<xsl:for-each select="event[@result='failed']">
							<tr>
								<td colspan="3" align="center" class="details_header_failed">
									<b><i><xsl:value-of select="@result" /></i></b>
								</td>
							</tr>
							<tr>
								<td></td>
								<td class="details_body_failed">Mode</td>
								<td class="details_body_failed">
									<b><xsl:value-of select="@mode" /></b>&#160;in Module&#160;<i><xsl:value-of select="@module" /></i>&#160;(Timestamp:&#160;<xsl:value-of select="@timestamp" />)
								</td>
							</tr>
							<tr>
								<td></td>
								<td class="details_body_failed">Logfile</td>
								<td class="details_body_failed">
									<xsl:value-of select="@logfile" />
								</td>
							</tr>
							<tr>
								<td></td>
								<td class="details_body_failed">Message</td>
								<td class="details_body_failed">
									<pre><xsl:value-of select="." /></pre>
								</td>
							</tr>
						</xsl:for-each>
					</table>
			</xsl:for-each>
		</body>
	</html>
	</xsl:template>
</xsl:stylesheet>
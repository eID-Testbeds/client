<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
	<html>
		<head>
			<style type="text/css">
				.type_STATE_header { background-color: #D3D3D3; color: #000000; }
				.type_STATE_body { background-color: #FFFFFF; color: #000000; border: 1px solid #D3D3D3; }
				
				.type_CONFORMITY_header_passed { background-color: #90EE90; color: #000000; }
				.type_CONFORMITY_body_passed { background-color: #FFFFFF; color: #000000; border: 1px solid #90EE90; }
				.type_CONFORMITY_header_failed { background-color: #FA8072; color: #000000; }
				.type_CONFORMITY_body_failed { background-color: #FFFFFF; color: #000000; border: 1px solid #FA8072; }
				.type_CONFORMITY_header_undetermined { background-color: #FFFACD; color: #000000; }
				.type_CONFORMITY_body_undetermined { background-color: #FFFFFF; color: #000000; border: 1px solid #FFFACD; }
				
				.type_PROTOCOL_header { background-color: #87CEFA; color: #000000; }
				.type_PROTOCOL_body { background-color: #FFFFFF; color: #000000; border: 1px solid #87CEFA; }
				
				.type_ENVIRONMENT_header { background-color: #7FFFD4; color: #000000; }
				.type_ENVIRONMENT_body { background-color: #FFFFFF; color: #000000; border: 1px solid #7FFFD4; }
				
				.loglevel_FATAL { background-color: #000000; color: #FA8072; }
				.loglevel_ERROR { background-color: #FA8072; color: #000000; }
				.loglevel_WARN { background-color: #FFFACD; color: #000000; }
				.loglevel_INFO { background-color: #87CEFA; color: #000000; }
				.loglevel_DEBUG { background-color: #90EE90; color: #000000; }
				
				pre { white-space: pre-wrap; word-wrap: break-word; }
			</style>
		</head>
		<body>
			<table align="center" style="table-layout:fixed; border: 0px; width: 100%">
				<tr>
					<td style="width: 200"></td>
					<td colspan="2"></td>
					<td style="width: 75"></td>
				</tr>
				<xsl:for-each select="events/event">
					<xsl:variable name="type" select="@type" />
					<xsl:variable name="loglevel" select="@loglevel" />
					<xsl:variable name="header_style">type_<xsl:value-of select="$type" />_header<xsl:if test="$type = 'CONFORMITY'">_<xsl:value-of select="attribute[@key='result']/@value" /></xsl:if></xsl:variable>
					<xsl:variable name="body_style">type_<xsl:value-of select="$type" />_body<xsl:if test="$type = 'CONFORMITY'">_<xsl:value-of select="attribute[@key='result']/@value" /></xsl:if></xsl:variable>
					<tr>
						<td><xsl:attribute name="class"><xsl:value-of select="$header_style" /></xsl:attribute>
							<b><xsl:value-of select="$type" /></b>
						</td>
						<td><xsl:attribute name="class"><xsl:value-of select="$header_style" /></xsl:attribute>
							<xsl:value-of select="timestamp/@date" />&#160;<xsl:value-of select="timestamp/@time" />
						</td>
						<td align="right"><xsl:attribute name="class"><xsl:value-of select="$header_style" /></xsl:attribute>
							<i><b><xsl:value-of select="@module" /></b></i>
						</td>
						<td align="right"><xsl:attribute name="class">loglevel_<xsl:value-of select="$loglevel" /></xsl:attribute>
							<b><xsl:value-of select="$loglevel" /></b>
						</td>
					</tr>
					<xsl:choose>
						<xsl:when test="$type = 'STATE'">
						<tr>
							<td></td>
							<td colspan="2"></td>
							<td></td>
						</tr>
						</xsl:when>
						<xsl:when test="$type = 'CONFORMITY'">
						<tr>
							<td></td>
							<td colspan="2"><xsl:attribute name="class"><xsl:value-of select="$body_style" /></xsl:attribute>
								<b><xsl:value-of select="attribute[@key='result']/@value" /></b> (<xsl:value-of select="attribute[@key='mode']/@value" />)
							</td>
							<td></td>
						</tr>
						</xsl:when>
						<xsl:when test="$type = 'PROTOCOL'">
						<tr>
							<td align="right">
								<i><xsl:value-of select="attribute[@key='name']/@value" /></i>
							</td>
							<td colspan="2"><xsl:attribute name="class"><xsl:value-of select="$body_style" /></xsl:attribute>
								<xsl:choose>
									<xsl:when test="attribute[@key='direction']/@value = 'received'">
										<i><xsl:value-of select="attribute[@key='receiver']/@value" /></i>
										&#160;<b>&#8612;</b>&#160;
										<i><xsl:value-of select="attribute[@key='sender']/@value" /></i>
										&#160;(<xsl:value-of select="attribute[@key='direction']/@value" />)
									</xsl:when>
									<xsl:when test="attribute[@key='direction']/@value = 'sent'">
										<i><xsl:value-of select="attribute[@key='sender']/@value" /></i>
										&#160;<b>&#8614;</b>&#160;<i>
										<xsl:value-of select="attribute[@key='receiver']/@value" /></i>
										&#160;(<xsl:value-of select="attribute[@key='direction']/@value" />)
									</xsl:when>
								</xsl:choose>
							</td>
							<td></td>
						</tr>
						</xsl:when>
						<xsl:when test="$type = 'ENVIRONMENT'">
						<tr>
							<td></td>
							<td colspan="2"><xsl:attribute name="class"><xsl:value-of select="$body_style" /></xsl:attribute>
								<xsl:value-of select="attribute[@key='classification']/@value" />
							</td>
							<td></td>
						</tr>
						</xsl:when>
					</xsl:choose>
					<tr>
						<td></td>
						<td colspan="2"><xsl:attribute name="class"><xsl:value-of select="$body_style" /></xsl:attribute>
							<pre><xsl:value-of select="message" /></pre>
						</td>
						<td></td>
					</tr>
				</xsl:for-each>
			</table>
		</body>
	</html>
	</xsl:template>
</xsl:stylesheet>
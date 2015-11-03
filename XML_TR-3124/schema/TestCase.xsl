<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tr03124="http://bsi.bund.de/TR03124">

<xsl:template match="/">
  <html>
  <body>
  <h2><xsl:value-of select="tr03124:TestCase/tr03124:Title"/></h2>
  <h4>Version: <xsl:value-of select="tr03124:TestCase/tr03124:Version"/></h4>
  <h4><xsl:value-of select="tr03124:TestCase/tr03124:Purpose"/></h4>
  
  <xsl:if test="count(//comment())">
    <div style="background-color:#ff0000">XML Comments:
      <ul>
        <xsl:for-each select="//comment()">
          <li><xsl:value-of select="."/></li>
        </xsl:for-each>
      </ul>
    </div>
  </xsl:if>

  <xsl:if test="string(tr03124:TestCase/tr03124:Title) != concat('Test case ',tr03124:TestCase/@id)">
    <div style="background-color:#ff0000">Title and ID differ:
      <ul>
        <li><xsl:value-of select="tr03124:TestCase/tr03124:Title"/></li>
        <li><xsl:value-of select="tr03124:TestCase/@id"/></li>
      </ul>
    </div>
  </xsl:if>

  <div style="background-color:#eaf2a2">Profiles:
    <ul>
      <xsl:for-each select="tr03124:TestCase/tr03124:Profile">
        <li><xsl:value-of select="."/></li>
      </xsl:for-each>
    </ul>
  </div>

  <div style="background-color:#eaf282">References:
    <ul>
      <xsl:for-each select="tr03124:TestCase/tr03124:Reference">
        <li><xsl:value-of select="."/></li>
      </xsl:for-each>
    </ul>
  </div>

  <div style="background-color:#eaf2e2">Preconditions:
    <ul>
      <xsl:for-each select="tr03124:TestCase/tr03124:Precondition">
        <li><xsl:value-of select="."/></li>
      </xsl:for-each>
    </ul>
  </div>

    <table border="1">
      <tr bgcolor="#9acd32">
        <th>Command </th>
        <th>ExpectedResult</th>
      </tr>
      <xsl:for-each select="tr03124:TestCase/tr03124:TestStep">
      <tr>
        <td>
          <div style="background-color:#eaf2e2">
            <xsl:apply-templates select="tr03124:Command/tr03124:Text"/>
          </div>
          <ul>
          <xsl:for-each select="tr03124:Description">
            <li><xsl:value-of select="."/></li>
          </xsl:for-each>
          </ul>
        </td>
        <td>
          <ul>
          <xsl:for-each select="tr03124:ExpectedResult">
            <li><xsl:apply-templates select="tr03124:Text"/></li>
          </xsl:for-each>
          </ul>
        </td>
      </tr>
      </xsl:for-each>
    </table>
  </body>
  </html>
</xsl:template>

<xsl:template match="tr03124:Link">
<span style="color:#0000ff">[<xsl:value-of select="@target"/>]<xsl:apply-templates /></span>
</xsl:template>

</xsl:stylesheet>

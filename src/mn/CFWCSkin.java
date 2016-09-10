package mn;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import javax.imageio.ImageIO;

import eg.CFWMath;
import eg.IFWObject;
import eg.IFWSkin;

public class CFWCSkin implements IFWSkin {
	protected SFWTexture m_buffTex[];
	protected SFWMaterial m_sknMtrl;
	
	public CFWCSkin( Color colDifI, Color colAmbI, Color colSpcI, Color colEmiI, float fPwrI)	{
		m_sknMtrl = new SFWMaterial();
		m_sknMtrl.m_colDiffuse = colDifI;
		m_sknMtrl.m_colAmbient = colAmbI;
		m_sknMtrl.m_colSpecular = colSpcI;
		m_sknMtrl.m_colEmissive = colEmiI;
		m_sknMtrl.m_fPower = fPwrI;
		
		m_buffTex = new SFWTexture[s_iMaxTex];
	}
	
	public CFWCSkin( Color colDifI, float fPwrI)	{
		m_sknMtrl = new SFWMaterial();
		m_sknMtrl.m_colDiffuse = colDifI;
		m_sknMtrl.m_fPower = fPwrI;
		
		m_buffTex = new SFWTexture[s_iMaxTex];
	}
	
	/**
	 * +_+input diffuse texture will cover the diffuse color of material
	 * the render system will ignore, but other colors remain themselves
	 * @param strDifPathI
	 * @throws Exception 
	 */
	public CFWCSkin(String strDifPathI) throws Exception	{
		m_sknMtrl = new SFWMaterial();
		m_buffTex = new SFWTexture[s_iMaxTex];
		readDatFromFile( s_iTexDif, strDifPathI);
	}

	public void readDatFromFile( int iTexTypeI, String strPathI) throws Exception	{
		//1)data check
		if(!CFWMath.isBetweenTwoNum( iTexTypeI, 0, s_iMaxTex - 1, true))	{
			throw new Exception("W:out if the texture map");
		}
		File flImage = new File(strPathI);
		if(!flImage.isFile())	{
			throw new FileNotFoundException("W:file isn't exist");
		}
		//2)read image data from file
		m_buffTex[iTexTypeI] = new SFWTexture();
		m_buffTex[iTexTypeI].m_buffTex = ImageIO.read(flImage);
		m_buffTex[iTexTypeI].m_bRead = true;
		m_buffTex[iTexTypeI].m_strPath = strPathI;
		m_buffTex[iTexTypeI].m_iWidth = m_buffTex[iTexTypeI].m_buffTex.getWidth();
		m_buffTex[iTexTypeI].m_iHeight = m_buffTex[iTexTypeI].m_buffTex.getHeight();
		m_buffTex[iTexTypeI].m_fAlpha = 1.0f;
	}
	
	/**
	 * include algorithm of the mixed color
	 * +_+ 0<=iXI<1 AND 0<=iYI<1
	 * 
	 * C = Flog*(Diffuse*(Light) + Specular + Emissive)
	 * # Light = Light + Embossing1 + Embossing2 + ...
	 */
	public Color getColFromImg( float fXI, float fYI) throws Exception	{
		//1)data check
		if((fXI < 0 || fXI >= 1)
		|| (fYI < 0 || fYI >= 1))	{
			throw new Exception("W:wanted pixel is out of the image map!");
		}
		//2)make out every color of texture
		//diffuse color
		Color colMixDif = null;
		if(!m_buffTex[s_iTexDif].m_bRead)	{
			String strDifPath = m_buffTex[s_iTexDif].m_strPath;
			try	{
				readDatFromFile( s_iTexDif, strDifPath);
			}
			catch(Exception e)	{
				e.printStackTrace();
				colMixDif = m_sknMtrl.m_colDiffuse;
			}
		}
		
		if(m_buffTex[s_iTexDif].m_bRead)	{
			int iSclX = (int)(fXI*m_buffTex[s_iTexDif].m_iWidth);
			int iSclY = (int)(fYI*m_buffTex[s_iTexDif].m_iHeight);
			colMixDif = new Color(m_buffTex[s_iTexDif].m_buffTex.getRGB( iSclX, iSclY));
		}
		
		colMixDif = CFWMath.colMultiVal( colMixDif, m_buffTex[s_iTexDif].m_fAlpha);
		colMixDif = CFWMath.colMultiVal( colMixDif, 1 + m_sknMtrl.m_fPower);
		//@_@ light color(specular, shadow)
		//@_@ embossing
		//@_@ ...
		
		return(colMixDif);
	}
	
	public int getWidth(int iTexTypeI)	{
		if(!CFWMath.isBetweenTwoNum( iTexTypeI, 0, s_iMaxTex - 1, true))	{
			return(0);
		}
		return(m_buffTex[iTexTypeI].m_iWidth);
	}
	
	public int getHeight(int iTexTypeI)	{
		if(!CFWMath.isBetweenTwoNum( iTexTypeI, 0, s_iMaxTex - 1, true))	{
			return(0);
		}
		return(m_buffTex[iTexTypeI].m_iHeight);
	}
	
	public SFWMaterial getMaterial()	{
		return(this.m_sknMtrl);
	}

	public boolean equals(IFWObject objI) {
		if(!objI.getClass().equals(CFWCSkin.class))	{
			return false;
		}
		
		CFWCSkin sknTmp = (CFWCSkin)objI;
		if(this.m_buffTex.length != sknTmp.m_buffTex.length)	{
			return false;
		}
		
		boolean bRet = this.m_sknMtrl.equals(sknTmp.m_sknMtrl);
		for( int i = 0; i < this.m_buffTex.length; ++i)	{
			if(null == this.m_buffTex[i]
			|| null == sknTmp.m_buffTex[i])	{
				return false;
			}
			
			if(null == this.m_buffTex[i]
			&& null == sknTmp.m_buffTex[i])	{
				break;
			}
			bRet &= this.m_buffTex[i].equals(sknTmp.m_buffTex[i]);
		}
		return(bRet);
	}
}

package eg;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface IFWSkin extends IFWObject {
	static public final int s_iMaxTex = 8;
	static public final int s_iTexDif = 0;
	static public final int s_iTexEmb = 1;
	static public final int s_iTexLgt = 2;
	//@_@ 3~8
	static public final Color s_colIgnoreLgt = Color.black;//means this color will not be effected by light
	
	public class SFWMaterial	{
		public Color m_colDiffuse;
		public Color m_colAmbient;
		public Color m_colSpecular;
		public Color m_colEmissive;
		public float m_fPower;
		
		public SFWMaterial()	{
			m_colDiffuse = new Color(CFWMath.geneRandValue(
					Color.black.getRGB(), Color.white.getRGB()));
			m_colAmbient = s_colIgnoreLgt;
			m_colSpecular = Color.white;
			m_colEmissive = s_colIgnoreLgt;
			m_fPower = 0.01f;
		}
		
		public boolean equals(SFWMaterial mtrlI)	{
			return(this.m_colAmbient.equals(mtrlI.m_colAmbient)
				&& this.m_colDiffuse.equals(mtrlI.m_colDiffuse)
				&& this.m_colEmissive.equals(mtrlI.m_colEmissive)
				&& this.m_colSpecular.equals(mtrlI.m_colSpecular)
				&& this.m_fPower == mtrlI.m_fPower);
					
		}
	}
	public class SFWTexture	{
		public String m_strPath;
		public boolean m_bRead;//means the image data whether be read
		public BufferedImage m_buffTex;
		public float m_fAlpha;//0~1
		public int m_iWidth;
		public int m_iHeight;
		
		public SFWTexture()	{
			m_strPath = "";
			m_bRead = false;
			m_buffTex = null;
			m_fAlpha = 0.0f;
			m_iWidth = 0;
			m_iHeight = 0;
		}
		
		public boolean equals(SFWTexture texI)	{
			return(this.m_buffTex.equals(texI.m_buffTex)
				&& this.m_fAlpha == texI.m_fAlpha
				&& this.m_iHeight == texI.m_iHeight
				&& this.m_iWidth == texI.m_iWidth);
		}
	}
	
	public void readDatFromFile( int iTexTypeI, String strPathI) throws Exception;
	public Color getColFromImg( float fXI, float fYI) throws Exception;
	public int getWidth(int iTexTypeI);
	public int getHeight(int iTexTypeI);
	public SFWMaterial getMaterial();
}

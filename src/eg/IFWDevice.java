package eg;

import java.awt.Color;

public interface IFWDevice extends IFWObject {
	//default deepth
	static public final float s_fDefDepth = 0;
	static public final float s_fShdwDisp = 1;
	
	public class SFWLight implements IFWObject	{
		static public final int s_iLgtDir = 0;
		static public final int s_iLgtPll = 1;
		static public final int s_iLgtPoi = 3;
		
		public int m_iType;
		public Color m_colDif;
		public Color m_colSpc;
		public Color m_colAmb;
		public CFWPoint m_poiPos;
		public CFWVector m_vecDir;
		public float m_fRange;
		public float m_fFalloff;
		public float m_fAttenuation0;
		public float m_fAttenuation1;
		public float m_fAttenuation2;
		public float m_fTheta;
		public float m_fPhi;
		public int m_iSplLvl;
		
		public SFWLight()	{
			m_iType = s_iLgtDir;
			m_colDif = Color.white;
			m_colSpc = Color.white;
			m_colAmb = Color.black;
			m_poiPos = new CFWPoint();
			m_vecDir = new CFWVector( 0.0f, -1.0f, 0.0f);
			
			m_fRange = 5000.0f;
			m_fFalloff = 0.0f;
			m_fAttenuation0 = 1.0f;
			m_fAttenuation1 = 1.0f;
			m_fAttenuation2 = 1.0f;
			m_fTheta = 0.0f;
			m_fPhi = 0.0f;
			m_iSplLvl = -1;
		}
		
		public SFWLight(SFWLight lgtI)	{
			m_iType = lgtI.m_iType;
			m_colDif = lgtI.m_colDif;
			m_colSpc = lgtI.m_colSpc;
			m_colAmb = lgtI.m_colAmb;
			m_poiPos = lgtI.m_poiPos;
			m_vecDir = lgtI.m_vecDir;
			
			m_fRange = lgtI.m_fRange;
			m_fFalloff = lgtI.m_fFalloff;
			m_fAttenuation0 = lgtI.m_fAttenuation0;
			m_fAttenuation1 = lgtI.m_fAttenuation1;
			m_fAttenuation2 = lgtI.m_fAttenuation2;
			m_fTheta = lgtI.m_fTheta;
			m_fPhi = lgtI.m_fPhi;
			m_iSplLvl = lgtI.m_iSplLvl;
		}
		
		public SFWLight transfer(CFWMatrix matI)	{
			SFWLight lgtRet = new SFWLight(this);
			
			if(IFWDevice.SFWLight.s_iLgtDir == lgtRet.m_iType
			|| IFWDevice.SFWLight.s_iLgtPoi == lgtRet.m_iType)	{
				lgtRet.m_poiPos = matI.multiPoiLeft(lgtRet.m_poiPos);
			}
			if(IFWDevice.SFWLight.s_iLgtDir == lgtRet.m_iType
			|| IFWDevice.SFWLight.s_iLgtPll == lgtRet.m_iType)	{
				lgtRet.m_vecDir = matI.multiVecLeft(lgtRet.m_vecDir);
			}
			
			return(lgtRet);
		}

		public boolean equals(IFWObject objI) {
			if(!objI.getClass().equals(SFWLight.class))	{
				return(false);
			}
			
			SFWLight lgtTmp = (SFWLight)objI;
			return(m_iType == lgtTmp.m_iType
				&&  m_colDif.equals(lgtTmp.m_colDif)
				&&  m_colSpc.equals(lgtTmp.m_colSpc)
				&&  m_colAmb.equals(lgtTmp.m_colAmb)
				&&  m_poiPos.equals(lgtTmp.m_poiPos)
				&&  m_vecDir.equals(lgtTmp.m_vecDir)
				&&  m_fRange == lgtTmp.m_fRange
				&&  m_fFalloff == lgtTmp.m_fFalloff
				&&  m_fAttenuation0 == lgtTmp.m_fAttenuation0
				&&  m_fAttenuation1 == lgtTmp.m_fAttenuation1
				&&  m_fAttenuation2 == lgtTmp.m_fAttenuation2
				&&  m_fTheta == lgtTmp.m_fTheta
				&&  m_fPhi == lgtTmp.m_fPhi
				&&  m_iSplLvl == lgtTmp.m_iSplLvl);
		}
		
		public CFWCone getDirLgtCtaInner() throws Exception 	{
			if(s_iLgtDir != m_iType)	{
				throw new Exception("W:this light is not direction light!");
			}
			return(new CFWCone( m_poiPos, m_vecDir, m_fRange, m_fTheta));
		}
		
		public CFWCone getDirLgtCtaOutside() throws Exception	{
			if(s_iLgtDir != m_iType)	{
				throw new Exception("W:this light is not direction light!");
			}
			return(new CFWCone( m_poiPos, m_vecDir, m_fRange, m_fPhi));
		}
	}
	
	public void setBackGround(Color colBkI);
	public Color getBackGround();
	public void setBaseDev(Object objI);
	
	public void setBkBufSize( int iWidthI, int iHeightI);
	public int getBkBufWidth();
	public int getBkBufHeight();
	
	public int getRefreshRate();
	public Object getBaseDev();
	public void render();
	public void rester();
	public void clsBackBuf();
	
	public boolean checkPixelDeepth( int iXI, int iYI, float fDeepI);
	//public void setPixel( int iXI, int iYI, Color colSetI);
	public void setPixel( int iXI, int iYI, float fZI, Color colSetI);//deep
	public void setLine( int poiBegXI, int poiBegYI, float poiBegZI,
							int poiEndXI, int poiEndYI, float poiEndZI, Color colSetI) throws Exception;//deep
	public void setArrow( int poiBegXI, int poiBegYI, int poiEndXI, int poiEndYI, int iLenI, Color colSetI) throws Exception;
	public int setPixelSize(int iSzPixelI);
	
	public void setLight(String strNamLgtI);
	public void enableLgt( String strNamLgtI, boolean bEnableI);
	public boolean isLgtEnable(String strNamLgtI);
	public void buildShadow(CFWScene sceCurI);//+_+: light number change call the function
}

package eg;

import java.awt.Color;

import eg.IFWMesh.SFWFace;

public class CFWSysGui {
	static public float s_fLenAxis = 150.0f;
	static public float s_fLenVec = 30.0f;
	static public int s_iSzPixel = 1;
	
	static private CFWSysGui s_Instance = null;
	static public CFWSysGui getInstance() throws Exception	{
		if(null == s_Instance)	{
			throw new Exception("W:the gui system hasn't be initlized!");
		}
		
		return(s_Instance);
	}
	static public CFWSysGui getInstance( IFWDevice devCurI, IFWCamera camOperI) throws Exception	{
		if(null == s_Instance)	{
			if(null == devCurI && null == camOperI)	{
				throw new Exception("E:given device should be initlized!");
			}
			s_Instance = new CFWSysGui( devCurI, camOperI);
		}
		
		return(s_Instance);
	}
	
	private CFWSysGui( IFWDevice devCurI, IFWCamera camOperI)	{
		m_devCur = devCurI;
		m_camOper = camOperI;
		
		m_bDrawWldCod = true;
		m_bDrawPoiNor = false;
		m_bDrawRectCenLn = false;
		m_bDrawEdge = true;
	}
	
	protected IFWDevice m_devCur;
	protected IFWCamera m_camOper;
	
	private boolean m_bDrawWldCod;
	private boolean m_bDrawPoiNor;
	private boolean m_bDrawRectCenLn;
	private boolean m_bDrawEdge;
	
	public void drawWldCoord( CFWCoord codI, CFWMatrix matTrsfI)	{
		if(m_bDrawWldCod)	{
			CFWPoint poiCT = new CFWPoint();
			CFWPoint poiXT = new CFWPoint( s_fLenAxis, 0.0f, 0.0f);
			CFWPoint poiYT = new CFWPoint( 0.0f, s_fLenAxis, 0.0f);
			CFWPoint poiZT = new CFWPoint( 0.0f, 0.0f, s_fLenAxis);
			
			poiCT = matTrsfI.multiPoiLeft(poiCT);
			poiXT = matTrsfI.multiPoiLeft(poiXT);
			poiYT = matTrsfI.multiPoiLeft(poiYT);
			poiZT = matTrsfI.multiPoiLeft(poiZT);
			
			try	{
				m_devCur.setArrow( (int)CFWMath.regulateFloat(poiCT.m_fX),
								   (int)CFWMath.regulateFloat(poiCT.m_fY),
								   (int)CFWMath.regulateFloat(poiXT.m_fX),
								   (int)CFWMath.regulateFloat(poiXT.m_fY), 10, Color.red);
				m_devCur.setArrow( (int)CFWMath.regulateFloat(poiCT.m_fX),
								   (int)CFWMath.regulateFloat(poiCT.m_fY),
								   (int)CFWMath.regulateFloat(poiYT.m_fX),
								   (int)CFWMath.regulateFloat(poiYT.m_fY), 10, Color.green);
				m_devCur.setArrow( (int)CFWMath.regulateFloat(poiCT.m_fX),
								   (int)CFWMath.regulateFloat(poiCT.m_fY),
								   (int)CFWMath.regulateFloat(poiZT.m_fX),
								   (int)CFWMath.regulateFloat(poiZT.m_fY), 10, Color.blue);
			}
			catch(Exception e)	{
				e.printStackTrace();
			}
		}
	}
	
	public void drawEdge( CFWTriangle tglVwI, SFWFace fcI, CFWMatrix matPFI)	{
		if(m_bDrawEdge)	{
			//use negative color of the back ground color
			Color colDraw = Color.white;
			
			//check depth test
			try	{
				int iSzFnt = this.m_devCur.setPixelSize(s_iSzPixel);
				if(m_bDrawRectCenLn || fcI.m_bDrawAB)	{
					drawLine( tglVwI.m_poiA, tglVwI.m_poiB, matPFI, colDraw);
				}
				if(m_bDrawRectCenLn || fcI.m_bDrawBC)	{
					drawLine( tglVwI.m_poiB, tglVwI.m_poiC, matPFI, colDraw);
				}
				if(m_bDrawRectCenLn || fcI.m_bDrawCA)	{
					drawLine( tglVwI.m_poiC, tglVwI.m_poiA, matPFI, colDraw);
				}
				this.m_devCur.setPixelSize(iSzFnt);
			}
			catch(Exception e)	{
				e.printStackTrace();
			}
		}
	}
	
	protected void drawLine( CFWPoint poiBegI, CFWPoint poiEndI, CFWMatrix matTrafI, Color colI) throws Exception	{
		CFWSegLn lnBE = m_camOper.getVwCentra().cutLn( poiBegI, poiEndI);
		CFWPoint poiBScBE = CFWSysRender.doMultiProjFmt( lnBE.m_poiBeg, matTrafI);
		CFWPoint poiEScBE = CFWSysRender.doMultiProjFmt( lnBE.m_poiEnd, matTrafI);
		
			m_devCur.setLine( (int)poiBScBE.m_fX, (int)poiBScBE.m_fY, IFWDevice.s_fDefDepth,
					  		  (int)poiEScBE.m_fX, (int)poiEScBE.m_fY, IFWDevice.s_fDefDepth, colI);
	}
	
	public void drawPoiNor( SFWFace fcI, CFWMatrix matTrsfI)	{
		if(m_bDrawPoiNor)	{
			CFWVector vecA = fcI.m_verA.m_vecNor;
			CFWVector vecB = fcI.m_verB.m_vecNor;
			CFWVector vecC = fcI.m_verC.m_vecNor;
			
			CFWPoint poiA = fcI.m_verA.getLoc();
			CFWPoint poiB = fcI.m_verB.getLoc();
			CFWPoint poiC = fcI.m_verC.getLoc();
			
			CFWPoint poiTarA = poiA.plus(vecA.multi(s_fLenVec));
			CFWPoint poiTarB = poiB.plus(vecB.multi(s_fLenVec));
			CFWPoint poiTarC = poiC.plus(vecC.multi(s_fLenVec));
			
			poiA = matTrsfI.multiPoiLeft(poiA);
			poiB = matTrsfI.multiPoiLeft(poiB);
			poiC = matTrsfI.multiPoiLeft(poiC);
			poiTarA = matTrsfI.multiPoiLeft(poiTarA);
			poiTarB = matTrsfI.multiPoiLeft(poiTarB);
			poiTarC = matTrsfI.multiPoiLeft(poiTarC);
			
			Color colAxis = CFWMath.getNegCol(m_devCur.getBackGround());
			
			try	{
				m_devCur.setArrow( (int)CFWMath.regulateFloat(poiA.m_fX),
								   (int)CFWMath.regulateFloat(poiA.m_fY),
								   (int)CFWMath.regulateFloat(poiTarA.m_fX),
								   (int)CFWMath.regulateFloat(poiTarA.m_fY), 10, colAxis);
				m_devCur.setArrow( (int)CFWMath.regulateFloat(poiB.m_fX),
						   		   (int)CFWMath.regulateFloat(poiB.m_fY),
						   		   (int)CFWMath.regulateFloat(poiTarB.m_fX),
						   		   (int)CFWMath.regulateFloat(poiTarB.m_fY), 10, colAxis);
				m_devCur.setArrow( (int)CFWMath.regulateFloat(poiC.m_fX),
						   		   (int)CFWMath.regulateFloat(poiC.m_fY),
						   		   (int)CFWMath.regulateFloat(poiTarC.m_fX),
						   		   (int)CFWMath.regulateFloat(poiTarC.m_fY), 10, colAxis);
			}
			catch(Exception e)	{
				e.printStackTrace();
			}
		}
	}
	
	public boolean enableDiagonl()	{
		return(m_bDrawRectCenLn);
	}
}

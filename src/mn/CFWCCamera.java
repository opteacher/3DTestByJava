package mn;

import java.awt.Color;

import eg.CFWCoord;
import eg.CFWMath;
import eg.CFWMatrix;
import eg.CFWPoint;
import eg.CFWRect;
import eg.CFWVector;
import eg.IFWCamera;
import eg.IFWObject;

public class CFWCCamera implements IFWCamera {
	protected CFWPoint m_poiTar;
	protected CFWCoord m_codCam;
	protected CFWRect m_plnProj;//project plane
	protected int m_iVwWidth;
	protected int m_iVwHeight;
	private long[] m_aFaceSet;
	
	private CFWMatrix m_matView;
	private CFWMatrix m_matWld;
	private CFWMatrix m_matProj;
	protected float m_fDisScn;
	protected float m_fDisFar;
	protected float m_fDisCls;
	protected SFWVwCentra m_ctaVw;
	//protected float m_fAngVw;//location point to the view plane's angle
	
	public CFWCCamera()	{
		m_poiTar = new CFWPoint();
		m_codCam = new CFWCoord();
		m_codCam.tunLeftHand();
		m_plnProj = new CFWRect();
		m_fDisScn = 5.0f;
		m_fDisFar = 1000.0f;
		m_fDisCls = 1.0f;
	}
	
	public void lookAt( CFWPoint poiCtI, CFWPoint poiTarI)	{
		m_poiTar = poiTarI;
		lookAt( poiCtI, new CFWVector(m_poiTar.sub(poiCtI)));
	}
	
	public void lookAt( CFWPoint poiCtI, CFWVector vecLkI)	{
		m_codCam.m_poiCenter = poiCtI;
		vecLkI.nor();
		if(!vecLkI.isZeroVec())	{
			m_codCam.m_vecAxis[s_iLook] = vecLkI;
			regulate();
		}
	}
	
	protected void regulate()	{
		CFWVector vecYTmp = new CFWVector( 0.0f, 1.0f, 0.0f);
		if(1 == Math.abs(m_codCam.m_vecAxis[s_iLook].m_fY))
			vecYTmp = new CFWVector( 1.0f, 0.0f, 0.0f);
		m_codCam.m_vecAxis[s_iRight] = vecYTmp.cross(m_codCam.m_vecAxis[s_iLook]);
		m_codCam.m_vecAxis[s_iUp] = m_codCam.m_vecAxis[s_iLook].cross(m_codCam.m_vecAxis[s_iRight]);
		m_codCam.m_vecAxis[s_iUp].nor();
		m_codCam.m_vecAxis[s_iRight] = m_codCam.m_vecAxis[s_iUp].cross(m_codCam.m_vecAxis[s_iLook]);
		m_codCam.m_vecAxis[s_iRight].nor();
	}
	
	public void setProjectPlane( CFWPoint poiLTI, CFWPoint poiRBI) throws Exception	{
		if(poiLTI.m_fZ != poiRBI.m_fZ)	{
			throw new Exception("the four points of project plane are not in the same plane!");
		}
		m_plnProj = new CFWRect( poiLTI, poiRBI);
		m_fDisScn = poiLTI.m_fZ;
		m_fDisCls = m_fDisScn - 110;
		m_fDisFar = m_fDisCls + 1000;
		
		this.rebuildViewCentra();
	}
	
	public void rebuildViewCentra() throws Exception	{
		if(0 == m_fDisScn)	{
			throw new Exception("screen's deepth can't be zero, or the camera hasn't be initlized!");
		}
		
		float fDetClsScn = m_fDisCls/m_fDisScn;
		CFWRect rectCls = new CFWRect(
				m_plnProj.m_poiLT.multi(fDetClsScn),
				m_plnProj.m_poiRB.multi(fDetClsScn));
		
		float fDetFarScn = m_fDisFar/m_fDisScn;
		CFWRect rectFar = new CFWRect(
				m_plnProj.m_poiLT.multi(fDetFarScn),
				m_plnProj.m_poiRB.multi(fDetFarScn));
		
		//because the camera coord use a left hand coord, locations in view are negative
		rectCls.m_poiLT.m_fZ = -rectCls.m_poiLT.m_fZ;
		rectCls.m_poiRB.m_fZ = -rectCls.m_poiRB.m_fZ;
		rectFar.m_poiLT.m_fZ = -rectFar.m_poiLT.m_fZ;
		rectFar.m_poiRB.m_fZ = -rectFar.m_poiRB.m_fZ;
		this.m_ctaVw = new SFWVwCentra( rectCls, rectFar);
	}
	
	public void setViewRect( int iVwWidthI, int iVwHeightI)	{
		if(0 == iVwWidthI || 0 == iVwHeightI)	{
			System.out.println("the view W&H should be zero!");
			return;
		}
		
		m_iVwWidth = iVwWidthI;
		m_iVwHeight = iVwHeightI;
	}
	
	public void setFaceSet(long[] aFacesI)	{
		if(0 == aFacesI.length)	{
			System.out.println("face data is empty!");
			return;
		}
		
		m_aFaceSet = aFacesI;
	}
	
	public CFWMatrix view()	{
		m_matWld = m_codCam.transform(new CFWCoord());
		return(m_matWld);
	}

	public Color[][] deep()	{
		//1.data check
		int iProjWidth = (int)CFWMath.regulateFloat(m_plnProj.getWidth());
		int iProjHeight = (int)CFWMath.regulateFloat(m_plnProj.getHeight());
		if(0 == iProjWidth || 0 == iProjHeight)	{
			System.out.println("the project plane W&H should be given first");
			return null;
		}
		if(0 == m_aFaceSet.length)	{
			System.out.println("no face data!");
			return null;
		}
		//2.clear the faces that behind sight
		Color[][] aBufRet = new Color[iProjWidth][iProjHeight];
		
		return(aBufRet);
	}
	
	public double[][] light()	{
		//@_@
		return null;
	}
	
	public CFWMatrix project()	{
		//1.data check
		if(null == m_matProj)
			m_matProj = new CFWMatrix();
		else if(!m_matProj.isIdentityMat())
			m_matProj = m_matProj.getIdentityMat();
		if(0 == m_fDisScn)	{
			System.out.println("the distance between eye's location and project plane" +
					"should be given first!");
			return null;
		}
		//2.input data
		m_matProj.m_fVal[2][3] = 1/m_fDisScn;
		m_matProj.m_fVal[2][2] = 0.0f;
		m_matProj.m_fVal[3][3] = 0.0f;
		
		/*float fHalfWidth = m_plnProj.getHeight()/2;
		m_matProj.m_fVal[2][2] = (fHalfWidth*m_fDisFar) / (m_fDisScn*(m_fDisFar - m_fDisClose));
		m_matProj.m_fVal[2][3] = fHalfWidth/m_fDisScn;
		m_matProj.m_fVal[3][2] = (-fHalfWidth*m_fDisFar*m_fDisClose) / (m_fDisScn*(m_fDisFar - m_fDisClose));
		m_matProj.m_fVal[3][3] = 0.0f;*/
		
		return(m_matProj);
	}
	
	public CFWMatrix format()	{
		//1.data check
		if(0 == m_iVwWidth || 0 == m_iVwHeight)	{
			System.out.println("the view W&H should be given first!");
			return null;
		}
		float fProjWidth = m_plnProj.getWidth();
		float fProjHeight = m_plnProj.getHeight();
		if(0 == fProjWidth || 0 == fProjHeight)	{
			System.out.println("the project plane W&H should be given first");
			return null;
		}
		//2.size rejust
		m_matView = CFWMatrix.scale2D(
				m_iVwWidth/fProjWidth,
				m_iVwHeight/fProjHeight);
		m_matView = m_matView.resetDim(CFWMatrix.s_iDim3D);
		m_matView = m_matView.dotRight(CFWMatrix.transfer3D( m_iVwWidth/2, m_iVwHeight/2, 0));
		
		return(m_matView);
	}
	
	public CFWVector getVecAxis(int iAxisI)	{
		if(iAxisI < 0 || iAxisI >= m_codCam.m_vecAxis.length)	{
			System.out.println("error axis, can't get!");
			return null;
		}
		
		return(m_codCam.m_vecAxis[iAxisI]);
	}
	
	public float getDisOfProjPln()	{
		return(m_fDisScn);
	}
	
	public CFWPoint getLocEye()	{
		return(m_codCam.m_poiCenter);
	}

	public SFWVwCentra getVwCentra()	{
		return(this.m_ctaVw);
	}

	public boolean equals(IFWObject objI) {
		if(!objI.getClass().equals(CFWCCamera.class))	{
			return false;
		}
		
		CFWCCamera camTmp = (CFWCCamera)objI;
		return(this.m_codCam.equals(camTmp.m_codCam)
			&& this.m_fDisCls == camTmp.m_fDisCls
			&& this.m_fDisFar == camTmp.m_fDisFar
			&& this.m_fDisScn == camTmp.m_fDisScn
			&& this.m_iVwHeight == camTmp.m_iVwHeight
			&& this.m_iVwWidth == camTmp.m_iVwWidth
			&& this.m_plnProj.equals(camTmp.m_plnProj)
			&& this.m_poiTar.equals(camTmp.m_poiTar));
	}
}

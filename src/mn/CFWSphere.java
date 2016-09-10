package mn;

import java.awt.Color;
import java.util.Vector;

import eg.CFWMatrix;
import eg.CFWPoint;
import eg.CFWVector;
import eg.IFWMesh;
import eg.IFWMshSet;

public class CFWSphere extends CFWCMesh implements IFWMshSet {
	
	protected Vector<IFWMesh> m_vecFc;
	
	public CFWSphere()	{
		m_vecFc = new Vector<IFWMesh>();
	}
	
	public void create( CFWPoint poiCenI, float fRadiusI, int iNumSecI) throws Exception	{
		CFWPoint poiTop = new CFWPoint( poiCenI.m_fX, poiCenI.m_fY + fRadiusI, poiCenI.m_fZ);
		CFWPoint poiBtm = new CFWPoint( poiCenI.m_fX, poiCenI.m_fY - fRadiusI, poiCenI.m_fZ);
		
		float fAngSec = 180.0f/iNumSecI;
		CFWPoint[] aPoisFnt = new CFWPoint[iNumSecI - 1];
		CFWVector vecAxisY = new CFWVector( 0.0f, 1.0f, 0.0f);
		CFWVector vecAxisX = new CFWVector( 0.0f, 0.0f, -1.0f);
		CFWMatrix matRotY = CFWMatrix.rotate3D( fAngSec, vecAxisY);
		for( int i = 0; i < iNumSecI*2 + 1; ++i)	{
			
			if(i == 0)	{
				for( int j = 1; j < iNumSecI; ++j)	{
					CFWMatrix matRotX = CFWMatrix.rotate3D( fAngSec*j, vecAxisX);
					aPoisFnt[j - 1] = matRotX.multiPoiLeft(poiBtm);
				}
				continue;
			}
			
			CFWPoint[] aPoisCur = new CFWPoint[iNumSecI - 1];
			vecAxisX = matRotY.multiVecLeft(vecAxisX);
			for( int j = 1; j < iNumSecI; ++j)	{
				aPoisCur[j - 1] = matRotY.multiPoiLeft(aPoisFnt[j - 1]);
			}
			
			m_vecFc.add(CFWCMesh.createTriangle(
					new IFWMesh.SFWAttribute(aPoisFnt[0], Color.orange),
					new IFWMesh.SFWAttribute(aPoisCur[0], Color.orange),
					new IFWMesh.SFWAttribute(poiBtm, Color.orange)));
			for( int j = 0; j < iNumSecI - 2; ++j)	{
				m_vecFc.add(CFWCMesh.createRectangle(
						new IFWMesh.SFWAttribute(aPoisFnt[j], Color.orange),
						new IFWMesh.SFWAttribute(aPoisCur[j], Color.orange),
						new IFWMesh.SFWAttribute(aPoisFnt[j + 1], Color.orange),
						new IFWMesh.SFWAttribute(aPoisCur[j + 1], Color.orange)));
			}
			m_vecFc.add(CFWCMesh.createTriangle(
					new IFWMesh.SFWAttribute(poiTop, Color.orange),
					new IFWMesh.SFWAttribute(aPoisCur[iNumSecI - 2], Color.orange),
					new IFWMesh.SFWAttribute(aPoisFnt[iNumSecI - 2], Color.orange)));
			
			aPoisFnt = aPoisCur;
		}
		
		this.m_strSknDat = m_vecFc.get(0).getSknId();
		
		this.getEveFace();
		super.generAdjInfo();
		super.generPoiNor();
	}
	
	public Vector<SFWFace> getEveFace() {
		for( int i = 0; i < m_vecFc.size(); ++i)	{
			m_aFcLst.addAll(m_vecFc.get(i).getEveFace());
		}
		return(m_aFcLst);
	}

	public void setSkin(int iSubMshI, String strSknI) throws Exception {
		
	}
}

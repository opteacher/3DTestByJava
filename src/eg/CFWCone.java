package eg;

import java.util.Vector;

public class CFWCone extends CFWRigid {
	static public final int s_iRangePln = 0;
	protected CFWPoint m_poiOrg;
	protected float m_fRange;
	protected CFWVector m_vecDir;
	protected float m_fAngRad;
	
	public CFWCone()	{
		m_poiOrg = new CFWPoint();
		m_vecDir = new CFWVector();
		m_fRange = 0.0f;
		m_fAngRad = 0.0f;
	}
	
	public CFWCone(CFWCone conI)	{
		super.m_aPlnSet = new Vector<CFWPlane>(conI.m_aPlnSet);
		m_poiOrg = new CFWPoint(conI.m_poiOrg);
		m_vecDir = new CFWVector(conI.m_vecDir);
		m_fRange = conI.m_fRange;
		m_fAngRad = conI.m_fAngRad;
	}
	
	public CFWCone( CFWPoint poiOrgI, CFWVector vecDirI, float fRangeI, float fAngRadI)	{
		m_poiOrg = poiOrgI;
		m_fRange = fRangeI;
		m_vecDir = vecDirI;
		m_fAngRad = fAngRadI;
		
		if(!m_vecDir.isNorVec())	{
			m_vecDir.nor();
		}
		
		CFWPlane plnTar = new CFWPlane( m_poiOrg.plus(m_vecDir.multi(m_fRange)), m_vecDir.getNegVec());
		plnTar.mkFunType(m_poiOrg);
		this.insertPln( s_iRangePln, plnTar);
	}
	
	public boolean isInside(CFWPoint poiI)	{
		boolean bSuperInside = super.isInside(poiI);
		if(!bSuperInside)	{
			return false;
		}
		else if(m_poiOrg.isZeroPoi()
			&&	m_vecDir.isZeroVec()
			&&  m_fAngRad == 0)	{
			return(bSuperInside);
		}
		
		CFWVector vecOrgToPoi = new CFWVector( m_poiOrg, poiI);
		float fAngOrgPoi = vecOrgToPoi.angWithVec( m_vecDir, false);
		
		if(fAngOrgPoi > m_fAngRad)	{
			return false;
		}
		else	{
			return true;
		}
	}
}

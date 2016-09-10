package eg;

import java.util.Vector;

public class CFWShadowObj {
	static public final float s_fPamShdo = 0.85f;
	public class SFWInfoShadow	{
		public String m_strLgt;
		public CFWCone m_cone;
		
		public SFWInfoShadow( String strLgtI, CFWCone coneI)	{
			m_strLgt = strLgtI;
			m_cone = coneI;
		}
	}
	//String is the ID of Light in device
	protected Vector<SFWInfoShadow> m_aShadows;
	protected IFWMesh.SFWFace m_fcBelong;
	
	public CFWShadowObj()	{
		m_aShadows = new Vector<SFWInfoShadow>();
		m_fcBelong = null;
	}
	/**
	 * shadow object should build in the world coord
	 * @param strLgtI
	 * @param lgtI
	 * @param ctaVwI
	 * @param tglWldI: already transfered to the world
	 */
	public void buildShadowObj( String strLgtI, IFWMesh.SFWFace fcBelongI, IFWDevice.SFWLight lgtI,
			IFWCamera.SFWVwCentra ctaVwI, CFWTriangle tglWldI)	{
		m_fcBelong = fcBelongI;
		CFWCone ctaShadowObj = new CFWCone();
		switch(lgtI.m_iType)	{
		case IFWDevice.SFWLight.s_iLgtDir:	{
			try	{
				CFWCone conLgt = lgtI.getDirLgtCtaOutside();
				if(!conLgt.isInside(tglWldI.m_poiA)
				|| !conLgt.isInside(tglWldI.m_poiB)
				|| !conLgt.isInside(tglWldI.m_poiC))	{
					ctaShadowObj = new CFWCone(conLgt);
				}
			}
			catch(Exception e)	{
				e.printStackTrace();
				break;
			}
		}
		case IFWDevice.SFWLight.s_iLgtPoi:	{
			if(ctaVwI.isInside(tglWldI.m_poiA)
			|| ctaVwI.isInside(tglWldI.m_poiB)
			|| ctaVwI.isInside(tglWldI.m_poiC))	{
				tglWldI.mkFunType(lgtI.m_poiPos);
				tglWldI.tunOtherSide();
				tglWldI.enableEquals(false);
				ctaShadowObj.insertPln(tglWldI);
			}

			try	{
				CFWPlane plnLAB = new CFWPlane( lgtI.m_poiPos, tglWldI.m_poiA, tglWldI.m_poiB);
				CFWPlane plnLBC = new CFWPlane( lgtI.m_poiPos, tglWldI.m_poiB, tglWldI.m_poiC);
				CFWPlane plnLCA = new CFWPlane( lgtI.m_poiPos, tglWldI.m_poiC, tglWldI.m_poiA);
				
				plnLAB.mkFunType(tglWldI.m_poiC);
				plnLBC.mkFunType(tglWldI.m_poiA);
				plnLCA.mkFunType(tglWldI.m_poiB);
				
				ctaShadowObj.insertPln(plnLAB);
				ctaShadowObj.insertPln(plnLBC);
				ctaShadowObj.insertPln(plnLCA);
			}
			catch(Exception e)	{
				return;
			}
			
			CFWRay rayLA = new CFWRay( lgtI.m_poiPos, tglWldI.m_poiA);
			CFWRay rayLB = new CFWRay( lgtI.m_poiPos, tglWldI.m_poiB);
			CFWRay rayLC = new CFWRay( lgtI.m_poiPos, tglWldI.m_poiC);
			
			this.intersectVwCta( ctaVwI, rayLA, ctaShadowObj);
			this.intersectVwCta( ctaVwI, rayLB, ctaShadowObj);
			this.intersectVwCta( ctaVwI, rayLC, ctaShadowObj);

			break;
		}
		case IFWDevice.SFWLight.s_iLgtPll:	{
			if(ctaVwI.isInside(tglWldI.m_poiA)
			|| ctaVwI.isInside(tglWldI.m_poiB)
			|| ctaVwI.isInside(tglWldI.m_poiC))	{
				if(lgtI.m_vecDir.isSameDir_VerticalIn(tglWldI.m_vecNor))	{
					tglWldI.setFunType(IFWLineEqu.s_iBigger);
				}
				else	{
					tglWldI.setFunType(IFWLineEqu.s_iLess);
				}
				tglWldI.enableEquals(false);
				ctaShadowObj.insertPln(tglWldI);
			}
			
			CFWRay rayLA = new CFWRay( tglWldI.m_poiA, lgtI.m_vecDir);
			CFWRay rayLB = new CFWRay( tglWldI.m_poiB, lgtI.m_vecDir);
			CFWRay rayLC = new CFWRay( tglWldI.m_poiC, lgtI.m_vecDir);
			
			CFWPoint poiCrsLA = this.intersectVwCta( ctaVwI, rayLA, ctaShadowObj);
			CFWPoint poiCrsLB = this.intersectVwCta( ctaVwI, rayLB, ctaShadowObj);
			CFWPoint poiCrsLC = this.intersectVwCta( ctaVwI, rayLC, ctaShadowObj);
			
			try	{
				CFWPlane plnAB = new CFWPlane( tglWldI.m_poiA, tglWldI.m_poiB, poiCrsLA);
				CFWPlane plnBC = new CFWPlane( tglWldI.m_poiB, tglWldI.m_poiC, poiCrsLB);
				CFWPlane plnCA = new CFWPlane( tglWldI.m_poiC, tglWldI.m_poiA, poiCrsLC);
				
				plnAB.mkFunType(tglWldI.m_poiC);
				plnBC.mkFunType(tglWldI.m_poiA);
				plnCA.mkFunType(tglWldI.m_poiB);
				
				ctaShadowObj.insertPln(plnAB);
				ctaShadowObj.insertPln(plnBC);
				ctaShadowObj.insertPln(plnCA);
			}
			catch(Exception e)	{
				e.printStackTrace();
				break;
			}
		}
		}
		
		m_aShadows.add(new SFWInfoShadow( strLgtI, ctaShadowObj));
	}
	
	/**
	 * return first cross point
	 * @param ctaVwI
	 * @param rayI
	 * @param shadowO
	 * @return
	 */
	private CFWPoint intersectVwCta( IFWCamera.SFWVwCentra ctaVwI, CFWRay rayI, CFWCone shadowO)	{
		Vector<CFWPlane> aArroundPln = ctaVwI.m_aPlnSet;
		
		CFWPoint poiRet = null;//@_@ light is on one of the three point
		float fMinK = 0;
		for( int i = 0; i < aArroundPln.size(); ++i)	{
			try	{
				CFWPoint poiCrsTmp = rayI.intersectsPln(aArroundPln.get(i));
				shadowO.insertPln(aArroundPln.get(i));
				
				float fK = 0;
				if(0 != rayI.m_fX)	{
					fK = Math.abs((poiCrsTmp.m_fX - rayI.m_poiBeg.m_fX)/rayI.m_fX);
				}
				else if(0 != rayI.m_fY)	{
					fK = Math.abs((poiCrsTmp.m_fY - rayI.m_poiBeg.m_fY)/rayI.m_fY);
				}
				else if(0 != rayI.m_fZ)	{
					fK = Math.abs((poiCrsTmp.m_fZ - rayI.m_poiBeg.m_fZ)/rayI.m_fZ);
				}
				else	{
					throw new Exception();
				}
				
				if(0 == fMinK || fK < fMinK)	{
					poiRet = poiCrsTmp;
				}
			}
			catch(Exception e)	{
				continue;
			}
		}
		
		return(poiRet);
	}
	
	public IFWMesh.SFWFace getFcIndex()	{
		return(m_fcBelong);
	}
}

package eg;

import java.util.HashMap;
import java.util.Vector;

public interface IFWCamera extends IFWObject {
	
	public class SFWVwCentra extends CFWRigid	{
		static public final int s_iFront = 0;
		static public final int s_iBack = 1;
		static public final int s_iLeft = 2;
		static public final int s_iRight = 3;
		static public final int s_iTop = 4;
		static public final int s_iBottom = 5;
		
		public SFWVwCentra( CFWRect rectClsI, CFWRect rectFarI) throws Exception	{
			//1)data check
			if(rectClsI.m_poiLT.m_fZ != rectClsI.m_poiRB.m_fZ
			|| rectFarI.m_poiLT.m_fZ != rectFarI.m_poiRB.m_fZ
			|| (rectClsI.m_poiLT.m_fZ > 0 && rectFarI.m_poiLT.m_fZ > 0
				&& rectFarI.m_poiLT.m_fZ <= rectClsI.m_poiLT.m_fZ)
			|| (rectClsI.m_poiLT.m_fZ < 0 && rectFarI.m_poiLT.m_fZ < 0
				&& rectFarI.m_poiLT.m_fZ >= rectClsI.m_poiLT.m_fZ))	{
				throw new Exception("W:close and far plane's location are not correct!");
			}
			//2)make out eight points
			CFWPoint poiLTCls = rectClsI.m_poiLT;
			CFWPoint poiRBCls = rectClsI.m_poiRB;
			CFWPoint poiRTCls = new CFWPoint( poiRBCls.m_fX, poiLTCls.m_fY, poiLTCls.m_fZ);
			CFWPoint poiLBCls = new CFWPoint( poiLTCls.m_fX, poiRBCls.m_fY, poiRBCls.m_fZ);
			
			CFWPoint poiLTFar = rectFarI.m_poiLT;
			CFWPoint poiRBFar = rectFarI.m_poiRB;
			CFWPoint poiRTFar = new CFWPoint( poiRBFar.m_fX, poiLTFar.m_fY, poiLTFar.m_fZ);
			CFWPoint poiLBFar = new CFWPoint( poiLTFar.m_fX, poiRBFar.m_fY, poiRBFar.m_fZ);
			//3)make out six plane to array
			m_aPlnSet = new Vector<CFWPlane>();
			m_aPlnSet.add( s_iFront, new CFWPlane( poiLTCls, poiRBCls, poiRTCls));
			m_aPlnSet.add( s_iBack, new CFWPlane( poiLTFar, poiRBFar, poiRTFar));
			m_aPlnSet.add( s_iLeft, new CFWPlane( poiLTCls, poiLTFar, poiLBFar));
			m_aPlnSet.add( s_iRight, new CFWPlane( poiRBCls, poiRTCls, poiRTFar));
			m_aPlnSet.add( s_iTop, new CFWPlane( poiLTCls, poiLTFar, poiRTFar));
			m_aPlnSet.add( s_iBottom, new CFWPlane( poiRBCls, poiLBCls, poiLBFar));
			
			HashMap<Integer, CFWPoint> mpIndPoi = new HashMap<Integer, CFWPoint>();
			mpIndPoi.put( s_iFront, poiLTFar);
			mpIndPoi.put( s_iBack, poiLTCls);
			mpIndPoi.put( s_iLeft, poiRBCls);
			mpIndPoi.put( s_iRight, poiLBCls);
			mpIndPoi.put( s_iTop, poiRBCls);
			mpIndPoi.put( s_iBottom, poiRTCls);
			
			this.buildSpace(mpIndPoi);
		}
		
		public CFWSegLn cutLn( CFWPoint poiBegI, CFWPoint poiEndI)	{
			CFWPoint poiBeg = new CFWPoint(poiBegI);
			CFWPoint poiEnd = new CFWPoint(poiEndI);
			
			CFWRay rayEB = new CFWRay( poiEnd, poiBeg);	rayEB.nor();
			for( int i = 0; i < m_aPlnSet.size(); ++i)	{
				if(!m_aPlnSet.get(i).isFillToFun(
						poiBeg.m_fX, poiBeg.m_fY, poiBeg.m_fZ))	{
					try {
						poiBeg = rayEB.intersectsPln(m_aPlnSet.get(i));
						break;
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			
			CFWRay rayBE = new CFWRay( poiBeg, poiEnd);	rayBE.nor();
			for( int i = 0; i < m_aPlnSet.size(); ++i)	{
				if(!m_aPlnSet.get(i).isFillToFun(
						poiEnd.m_fX, poiEnd.m_fY, poiEnd.m_fZ))	{
					try	{
						poiEnd = rayBE.intersectsPln(m_aPlnSet.get(i));
						break;
					} catch(Exception e)	{
						e.printStackTrace();
						continue;
					}
				}
			}
			
			return(new CFWSegLn( poiBeg, poiEnd));
		}
	}
	
	static public final int s_iRight = 0;
	static public final int s_iUp = 1;
	static public final int s_iLook = 2;
	
	public CFWPoint getLocEye();
	public CFWVector getVecAxis(int iAxisI);
	public float getDisOfProjPln();
	/**
	 * +_+
	 * the function must be implemented before transform!
	 * the deepth(Z) should be given!
	 * @param poiLTI
	 * @param poiRBI
	 */
	public void setProjectPlane( CFWPoint poiLTI, CFWPoint poiRBI) throws Exception;
	public void rebuildViewCentra() throws Exception;
	/**
	 * +_+
	 * the function must be implemented before transform!
	 * @param iVwWidthI
	 * @param iVwHeightI
	 */
	public void setViewRect( int iVwWidthI, int iVwHeightI);
	public void setFaceSet(long[] aFacesI);
	public void lookAt( CFWPoint poiCtI, CFWVector vecLkI);
	/**
	 * view -> deepthTest -> light -> cut -> project -> format
	 * @return
	 */
	public CFWMatrix view();
	//public Color[][] deep();
	//public double[][] light();
	public SFWVwCentra getVwCentra();
	public CFWMatrix project();
	public CFWMatrix format();
}

package mn;

import java.awt.Color;
import java.util.Vector;

import eg.CFWMath;
import eg.CFWPoint;
import eg.IFWMesh;
import eg.IFWMshSet;

public class CFWBox  extends CFWCMesh implements IFWMshSet  {
	static public final int s_iFront = 0;
	static public final int s_iBack = 1;
	static public final int s_iLeft = 2;
	static public final int s_iRight = 3;
	static public final int s_iTop = 4;
	static public final int s_iBottom = 5;
	
	static public final int s_iLTF = 0;
	static public final int s_iRTF = 1;
	static public final int s_iRBF = 2;
	static public final int s_iLBF = 3;
	static public final int s_iRTB = 4;
	static public final int s_iLTB = 5;
	static public final int s_iRBB = 6;
	static public final int s_iLBB = 7;
	
	protected IFWMesh m_SixFc[];
	
	public CFWBox()	{
		m_SixFc = new IFWMesh[6];
	}
	
	public void create( CFWPoint poiLTFI, CFWPoint poiRBBI) throws Exception	{
		//1)generate points of box
		CFWPoint[] aPois = generPois( poiLTFI, poiRBBI);
		if(aPois.length != 8)	{
			throw new Exception("E:box's points generate error!");
		}
		
		//2)fill in
		IFWMesh[] aPlns = new IFWMesh[6];
		aPlns[s_iFront] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTF]),
				new IFWMesh.SFWAttribute( aPois[s_iRTF]),
				new IFWMesh.SFWAttribute( aPois[s_iLBF]),
				new IFWMesh.SFWAttribute( aPois[s_iRBF]));
		aPlns[s_iBack] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iRTB]),
				new IFWMesh.SFWAttribute( aPois[s_iLTB]),
				new IFWMesh.SFWAttribute( aPois[s_iRBB]),
				new IFWMesh.SFWAttribute( aPois[s_iLBB]));
		aPlns[s_iLeft] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTB]),
				new IFWMesh.SFWAttribute( aPois[s_iLTF]),
				new IFWMesh.SFWAttribute( aPois[s_iLBB]),
				new IFWMesh.SFWAttribute( aPois[s_iLBF]));
		aPlns[s_iRight] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iRTF]),
				new IFWMesh.SFWAttribute( aPois[s_iRTB]),
				new IFWMesh.SFWAttribute( aPois[s_iRBF]),
				new IFWMesh.SFWAttribute( aPois[s_iRBB]));
		aPlns[s_iTop] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTB]),
				new IFWMesh.SFWAttribute( aPois[s_iRTB]),
				new IFWMesh.SFWAttribute( aPois[s_iLTF]),
				new IFWMesh.SFWAttribute( aPois[s_iRTF]));
		aPlns[s_iBottom] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLBF]),
				new IFWMesh.SFWAttribute( aPois[s_iRBF]),
				new IFWMesh.SFWAttribute( aPois[s_iLBB]),
				new IFWMesh.SFWAttribute( aPois[s_iRBB]));
		
		this.create( aPlns[s_iFront], aPlns[s_iBack],
					 aPlns[s_iLeft], aPlns[s_iRight],
					 aPlns[s_iTop], aPlns[s_iBottom]);
	}
	
	public void create( CFWPoint poiLTFI, CFWPoint poiRBBI, Color colI) throws Exception	{
		//1)generate points of box
		CFWPoint[] aPois = generPois( poiLTFI, poiRBBI);
		if(aPois.length != 8)	{
			throw new Exception("E:box's points generate error!");
		}
		
		//2)fill in
		IFWMesh[] aPlns = new IFWMesh[6];
		aPlns[s_iFront] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRTF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLBF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRBF], colI));
		aPlns[s_iBack] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iRTB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLTB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRBB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLBB], colI));
		aPlns[s_iLeft] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLTF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLBB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLBF], colI));
		aPlns[s_iRight] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iRTF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRTB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRBF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRBB], colI));
		aPlns[s_iTop] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRTB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLTF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRTF], colI));
		aPlns[s_iBottom] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLBF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRBF], colI),
				new IFWMesh.SFWAttribute( aPois[s_iLBB], colI),
				new IFWMesh.SFWAttribute( aPois[s_iRBB], colI));
		
		this.create( aPlns[s_iFront], aPlns[s_iBack],
					 aPlns[s_iLeft], aPlns[s_iRight],
					 aPlns[s_iTop], aPlns[s_iBottom]);
		this.m_strSknDat = aPlns[s_iFront].getSknId();
	}
	/**
	 * +_+: LTF.X < RBB.X && LTF.Y > RBB.Y && LTF.Z > RBB.Z
	 * @param poiLTFI
	 * @param poiRBBI
	 * @param strTexI
	 * @throws Exception 
	 */
	public void create( CFWPoint poiLTFI, CFWPoint poiRBBI, String strTexI) throws Exception	{
		//1)generate points of box
		CFWPoint[] aPois = generPois( poiLTFI, poiRBBI);
		if(aPois.length != 8)	{
			throw new Exception("E:box's points generate error!");
		}
		
		//2)fill in
		IFWMesh[] aPlns = new IFWMesh[6];
		aPlns[s_iFront] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTF], 0, 0),
				new IFWMesh.SFWAttribute( aPois[s_iRTF], 1, 0),
				new IFWMesh.SFWAttribute( aPois[s_iLBF], 0, 1),
				new IFWMesh.SFWAttribute( aPois[s_iRBF], 1, 1));
		aPlns[s_iBack] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iRTB], 0, 0),
				new IFWMesh.SFWAttribute( aPois[s_iLTB], 1, 0),
				new IFWMesh.SFWAttribute( aPois[s_iRBB], 0, 1),
				new IFWMesh.SFWAttribute( aPois[s_iLBB], 1, 1));
		aPlns[s_iLeft] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTB], 0, 0),
				new IFWMesh.SFWAttribute( aPois[s_iLTF], 1, 0),
				new IFWMesh.SFWAttribute( aPois[s_iLBB], 0, 1),
				new IFWMesh.SFWAttribute( aPois[s_iLBF], 1, 1));
		aPlns[s_iRight] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iRTF], 0, 0),
				new IFWMesh.SFWAttribute( aPois[s_iRTB], 1, 0),
				new IFWMesh.SFWAttribute( aPois[s_iRBF], 0, 1),
				new IFWMesh.SFWAttribute( aPois[s_iRBB], 1, 1));
		aPlns[s_iTop] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLTB], 0, 0),
				new IFWMesh.SFWAttribute( aPois[s_iRTB], 1, 0),
				new IFWMesh.SFWAttribute( aPois[s_iLTF], 0, 1),
				new IFWMesh.SFWAttribute( aPois[s_iRTF], 1, 1));
		aPlns[s_iBottom] = CFWCMesh.createRectangle(
				new IFWMesh.SFWAttribute( aPois[s_iLBF], 0, 0),
				new IFWMesh.SFWAttribute( aPois[s_iRBF], 1, 0),
				new IFWMesh.SFWAttribute( aPois[s_iLBB], 0, 1),
				new IFWMesh.SFWAttribute( aPois[s_iRBB], 1, 1));
		
		this.create( aPlns[s_iFront], aPlns[s_iBack],
					 aPlns[s_iLeft], aPlns[s_iRight],
					 aPlns[s_iTop], aPlns[s_iBottom]);
		this.setSkin(strTexI);
	}
	
	protected CFWPoint[] generPois( CFWPoint poiLTFI, CFWPoint poiRBBI) throws Exception	{
		//data check
		if(poiLTFI.m_fX > poiRBBI.m_fX
		&& poiLTFI.m_fY < poiRBBI.m_fY
		&& poiLTFI.m_fZ < poiRBBI.m_fZ)	{
			CFWPoint poiTmp = poiLTFI;
			poiLTFI = poiRBBI;
			poiRBBI = poiTmp;
		}
		if(poiLTFI.m_fX >= poiRBBI.m_fX
		|| poiLTFI.m_fY <= poiRBBI.m_fY
		|| poiLTFI.m_fZ <= poiRBBI.m_fZ)	{
			throw new Exception("W:given param can't build a box!");
		}
		
		//generate other points
		CFWPoint[] aPoisRet = new CFWPoint[8];
		aPoisRet[s_iLTF] = poiLTFI;
		aPoisRet[s_iRTF] = new CFWPoint( poiRBBI.m_fX, poiLTFI.m_fY, poiLTFI.m_fZ);
		aPoisRet[s_iRBF] = new CFWPoint( poiRBBI.m_fX, poiRBBI.m_fY, poiLTFI.m_fZ);
		aPoisRet[s_iLBF] = new CFWPoint( poiLTFI.m_fX, poiRBBI.m_fY, poiLTFI.m_fZ);
		
		aPoisRet[s_iRTB] = new CFWPoint( poiRBBI.m_fX, poiLTFI.m_fY, poiRBBI.m_fZ);
		aPoisRet[s_iLTB] = new CFWPoint( poiLTFI.m_fX, poiLTFI.m_fY, poiRBBI.m_fZ);
		aPoisRet[s_iRBB] = poiRBBI;
		aPoisRet[s_iLBB] = new CFWPoint( poiLTFI.m_fX, poiRBBI.m_fY, poiRBBI.m_fZ);
		
		return(aPoisRet);
	}
	
	public void create(
			IFWMesh mshFntI, IFWMesh mshBckI,
			IFWMesh mshLftI, IFWMesh mshRgtI,
			IFWMesh mshTopI, IFWMesh mshBtmI) throws Exception	{
		if(mshFntI.getMshType() != IFWMesh.s_iPlane
		|| mshBckI.getMshType() != IFWMesh.s_iPlane
		|| mshLftI.getMshType() != IFWMesh.s_iPlane
		|| mshRgtI.getMshType() != IFWMesh.s_iPlane
		|| mshTopI.getMshType() != IFWMesh.s_iPlane
		|| mshBtmI.getMshType() != IFWMesh.s_iPlane)	{
			throw new Exception("W:error param for box, should be plane");
		}
		//@_@, points' location check
		
		m_SixFc[s_iFront] = mshFntI;
		m_SixFc[s_iBack] = mshBckI;
		m_SixFc[s_iLeft] = mshLftI;
		m_SixFc[s_iRight] = mshRgtI;
		m_SixFc[s_iTop] = mshTopI;
		m_SixFc[s_iBottom] = mshBtmI;
		
		this.getEveFace();
		super.generAdjInfo();
		super.generPoiNor();
	}

	public Vector<SFWFace> getEveFace() {
		for( int i = 0; i < m_SixFc.length; ++i)	{
			m_aFcLst.addAll(m_SixFc[i].getEveFace());
		}
		return(m_aFcLst);
	}

	public void setSkin(int iSubMshI, String strSknI) throws Exception {
		if(!CFWMath.isBetweenTwoNum( iSubMshI, s_iFront, s_iBottom, true))	{
			throw new Exception("W:sub mesh's id should inside 6(the box has 6 faces)");
		}
		
		m_SixFc[iSubMshI].setSkin(strSknI);
	}

	public void setSkin(String strSknI) {
		this.m_strSknDat = strSknI;
		for( int i = 0; i < m_SixFc.length; ++i)	{
			m_SixFc[i].setSkin(strSknI);
		}
	}
}

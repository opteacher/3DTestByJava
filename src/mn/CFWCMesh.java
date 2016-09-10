package mn;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import eg.CFWPoint;
import eg.CFWPool;
import eg.CFWVector;
import eg.IFWMesh;
import eg.IFWObject;
import eg.IFWSkin;

public class CFWCMesh implements IFWMesh {

	protected int m_iPoiType;
	protected int m_iMshType;
	protected int m_iTexType;
	protected Vector<SFWVertex> m_aVerLst;
	protected Vector<Integer> m_aIndLst;
	protected Vector<SFWFace> m_aFcLst;
	protected String m_strSknDat;
	
	public CFWCMesh()	{
		m_iPoiType = s_iVertex;
		m_iMshType = s_iTriangle;
		m_strSknDat = "";
		
		m_aVerLst = new Vector<SFWVertex>();
		m_aIndLst = new Vector<Integer>();
		m_aFcLst = new Vector<SFWFace>();
	}
	
	public Vector<SFWFace> getEveFace()	{
		if(!m_aFcLst.isEmpty())	{
			return(m_aFcLst);
		}
		
		for( int i = 0; i < m_aIndLst.size(); i += 3)	{
			int iIndA = m_aIndLst.get(i);
			int iIndB = m_aIndLst.get(i+1);
			int iIndC = m_aIndLst.get(i+2);
			
			CFWPoint poiA = m_aVerLst.get(iIndA).getLoc();
			CFWPoint poiB = m_aVerLst.get(iIndB).getLoc();
			CFWPoint poiC = m_aVerLst.get(iIndC).getLoc();
			
			CFWVector vecBA = new CFWVector( poiB, poiA);
			CFWVector vecBC = new CFWVector( poiB, poiC);
			
			SFWFace fcRet = new SFWFace(
					m_aVerLst.get(iIndA),
					m_aVerLst.get(iIndB),
					m_aVerLst.get(iIndC), m_iPoiType);
			fcRet.m_vecNor = vecBC.cross(vecBA);
			fcRet.m_vecNor.nor();
			
			m_aFcLst.addElement(fcRet);
		}
		
		return(m_aFcLst);
	}
	
	/**
	 * generate point normal vector
	 */
	public void generPoiNor()	{
		if(m_aFcLst.isEmpty())	{
			this.getEveFace();
		}
		HashMap<CFWPoint, Vector<Integer>> mpColcRes = new HashMap<CFWPoint, Vector<Integer>>();
		for( int i = 0; i < m_aFcLst.size(); ++i)	{
			SFWFace fcCur = m_aFcLst.get(i);
			CFWPoint poiLs[] = new CFWPoint[3];
			poiLs[0] = fcCur.m_verA.getLoc();
			poiLs[1] = fcCur.m_verB.getLoc();
			poiLs[2] = fcCur.m_verC.getLoc();
			
			for( int j = 0; j < poiLs.length; ++j)	{
				Set<Entry<CFWPoint, Vector<Integer>>> colcTmp = mpColcRes.entrySet();
				Iterator<Entry<CFWPoint, Vector<Integer>>> iter = colcTmp.iterator();
				boolean bInclude = false;
				while(iter.hasNext())	{
					Map.Entry<CFWPoint, Vector<Integer>> etyTmp = iter.next();
					
					if(etyTmp.getKey().equals(poiLs[j]))	{
						etyTmp.getValue().add(i);
						bInclude = true;
						break;
					}
				}
				
				if(!bInclude)	{
					Vector<Integer> vecNewTmp = new Vector<Integer>();
					vecNewTmp.add(i);
					
					mpColcRes.put( poiLs[j], vecNewTmp);
				}
			}
		}
		
		Set<Entry<CFWPoint, Vector<Integer>>> colcTmp = mpColcRes.entrySet();
		Iterator<Entry<CFWPoint, Vector<Integer>>> iter = colcTmp.iterator();
		while(iter.hasNext())	{
			Map.Entry<CFWPoint, Vector<Integer>> etyCur = (Entry<CFWPoint, Vector<Integer>>)iter.next();
			
			Vector<Integer> vecFcInfo = etyCur.getValue();
			for( int j = 0; j < vecFcInfo.size(); ++j)	{
				CFWVector vecNorCur = m_aFcLst.get(vecFcInfo.get(j)).m_vecNor;
				
				boolean bMixNor = true;
				for( int t = 0; t < vecFcInfo.size(); ++t)	{
					if(j == t)	continue;
					
					CFWVector vecNorTmp = m_aFcLst.get(vecFcInfo.get(t)).m_vecNor;
					if(!vecNorCur.isSameDir_VerticalOut(vecNorTmp))	{
						bMixNor = false;
						break;
					}
				}
				
				if(bMixNor)	{
					CFWVector vecNorMix = new CFWVector();
					for( int t = 0; t < vecFcInfo.size(); ++t)	{
						CFWVector vecNorTmp = m_aFcLst.get(vecFcInfo.get(t)).m_vecNor;
						vecNorMix = vecNorMix.plus(vecNorTmp);
					}
					vecNorMix.nor();
					
					CFWPoint poiCur = etyCur.getKey();
					int iFcCur = vecFcInfo.get(j);
					SFWFace fcCur = m_aFcLst.get(iFcCur);
					if(poiCur.equals(fcCur.m_verA.getLoc()))	{
						fcCur.m_verA.m_vecNor = vecNorMix;
					}
					else if(poiCur.equals(fcCur.m_verB.getLoc()))	{
						fcCur.m_verB.m_vecNor = vecNorMix;
					}
					else if(poiCur.equals(fcCur.m_verC.getLoc()))	{
						fcCur.m_verC.m_vecNor = vecNorMix;
					}
					else	{
						//throw new Exception("E:");
					}
				}
				else	{
					int iFcCur = vecFcInfo.get(j);
					SFWFace fcCur = m_aFcLst.get(iFcCur);
					m_aFcLst.get(iFcCur).m_verA.m_vecNor = fcCur.m_vecNor;
					m_aFcLst.get(iFcCur).m_verB.m_vecNor = fcCur.m_vecNor;
					m_aFcLst.get(iFcCur).m_verC.m_vecNor = fcCur.m_vecNor;
				}
			}
		}
	}
	
	/**
	 * generate Adjacency information
	 */
	public void generAdjInfo()	{
		if(m_aFcLst.isEmpty())	{
			this.getEveFace();
		}
		
		for( int i = 0; i < m_aFcLst.size(); ++i)	{
			SFWFace fcCur = m_aFcLst.get(i);
			
			Vector<SFWFace> vecTmp = new Vector<SFWFace>(m_aFcLst);
			vecTmp.remove(fcCur);
			for( int j = 0; j < vecTmp.size(); ++j)	{
				fcCur.chkBeside(vecTmp.get(j));
			}
		}
	}
	
	/**
	 * A -> B -> C: clockwise
	 * @param poiAI
	 * @param poiBI
	 * @param poiCI
	 * @return
	 */
	static public IFWMesh createTriangle(
			SFWAttribute poiAI,
			SFWAttribute poiBI,
			SFWAttribute poiCI)	{
		//1.data check
		if(poiAI.m_iPoiType != poiBI.m_iPoiType
		|| poiBI.m_iPoiType != poiCI.m_iPoiType)	{
			System.out.println("insert points should be the same type!");
			return null;
		}
		//2.insert points
		CFWCMesh mshRet = new CFWCMesh();
		mshRet.m_iPoiType = poiAI.m_iPoiType;
		if(mshRet.m_iPoiType == IFWMesh.s_iVerCol)	{
			mshRet.m_strSknDat = "";
			try {
				//it just use the color of point A to create skin's material
				IFWSkin sknNew = new CFWCSkin( poiAI.m_color, 0.02f);
				mshRet.m_strSknDat = CFWPool.getInstance().addObjToPool((IFWObject)sknNew);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		mshRet.m_iMshType = IFWMesh.s_iTriangle;
		
		mshRet.m_aVerLst = new Vector<SFWVertex>();
		mshRet.m_aVerLst.addElement(poiToVertex(poiAI));
		mshRet.m_aVerLst.addElement(poiToVertex(poiBI));
		mshRet.m_aVerLst.addElement(poiToVertex(poiCI));

		mshRet.m_aIndLst = new Vector<Integer>();
		mshRet.m_aIndLst.addElement(0);
		mshRet.m_aIndLst.addElement(1);
		mshRet.m_aIndLst.addElement(2);
		
		mshRet.getEveFace();
		mshRet.generPoiNor();
		
		return(mshRet);
	}
	
	static public IFWMesh createRectangle(
			SFWAttribute poiLTI, SFWAttribute poiRTI,
			SFWAttribute poiLBI, SFWAttribute poiRBI)	{
		//1.data check
		if(poiLTI.m_iPoiType != poiRTI.m_iPoiType
		|| poiLBI.m_iPoiType != poiRBI.m_iPoiType)	{
			System.out.println("insert points should be the same type!");
			return null;
		}
		//2.insert points
		CFWCMesh mshRet = new CFWCMesh();
		mshRet.m_iPoiType = poiLTI.m_iPoiType;
		if(mshRet.m_iPoiType == IFWMesh.s_iVerCol)	{
			mshRet.m_strSknDat = "";
			try {
				//it just use the color of point left/top to create skin's material
				IFWSkin sknNew = new CFWCSkin( poiLTI.m_color, 0.02f);
				mshRet.m_strSknDat = CFWPool.getInstance().addObjToPool((IFWObject)sknNew);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		mshRet.m_iMshType = IFWMesh.s_iPlane;
		
		mshRet.m_aVerLst = new Vector<SFWVertex>();
		mshRet.m_aVerLst.addElement(poiToVertex(poiLTI));
		mshRet.m_aVerLst.addElement(poiToVertex(poiRTI));
		mshRet.m_aVerLst.addElement(poiToVertex(poiLBI));
		mshRet.m_aVerLst.addElement(poiToVertex(poiRBI));
		
		mshRet.m_aIndLst = new Vector<Integer>();
		mshRet.m_aIndLst.addElement(0);
		mshRet.m_aIndLst.addElement(2);
		mshRet.m_aIndLst.addElement(1);
		
		mshRet.m_aIndLst.addElement(1);
		mshRet.m_aIndLst.addElement(2);
		mshRet.m_aIndLst.addElement(3);
		
		mshRet.getEveFace();
		mshRet.generPoiNor();
		mshRet.generAdjInfo();
		
		for( int i = 0; i < mshRet.m_aFcLst.size(); ++i)	{
			SFWFace fcTmp = mshRet.m_aFcLst.get(i);
			if(fcTmp.m_fcAB != null)	{
				fcTmp.m_bDrawAB = false;
			}
			if(fcTmp.m_fcBC != null)	{
				fcTmp.m_bDrawBC = false;
			}
			if(fcTmp.m_fcCA != null)	{
				fcTmp.m_bDrawCA = false;
			}
		}
		
		return(mshRet);
	}
	
	static public SFWVertex poiToVertex(IFWMesh.SFWAttribute attrI)	{
		IFWMesh.SFWVertex verRet = null;
		switch(attrI.m_iPoiType)	{
		case IFWMesh.s_iVertex:
			verRet = new IFWMesh.SFWVertex(
					attrI.m_poiLoc.m_fX,
					attrI.m_poiLoc.m_fY,
					attrI.m_poiLoc.m_fZ);
			return(verRet);
		case IFWMesh.s_iVerCol:
			verRet = new IFWMesh.SFWVerCol(
					attrI.m_poiLoc.m_fX,
					attrI.m_poiLoc.m_fY,
					attrI.m_poiLoc.m_fZ,
					attrI.m_color);
			return(verRet);
		case IFWMesh.s_iVerUV:
			verRet = new IFWMesh.SFWVerUV(
					attrI.m_poiLoc.m_fX,
					attrI.m_poiLoc.m_fY,
					attrI.m_poiLoc.m_fZ,
					attrI.m_fU, attrI.m_fV);
			return(verRet);
		}
		System.out.println("error point type!");
		return(verRet);
	}
	
	public void setSkin(String strSknI)	{
		m_iPoiType = IFWMesh.s_iVerUV;
		m_strSknDat = strSknI;
	}
	
	public void setColor(Color colI)	{
		m_iPoiType = IFWMesh.s_iVerCol;
		
		for( int i = 0; i < m_aVerLst.size(); ++i)	{
			SFWVertex verTmp = m_aVerLst.get(i);
			m_aVerLst.set( i, new SFWVerCol( verTmp.m_fX, verTmp.m_fY, verTmp.m_fZ, colI));
		}
		
		for( int i = 0; i < m_aFcLst.size(); ++i)	{
			SFWFace fTmp = m_aFcLst.get(i);
			fTmp.m_iPoiType = IFWMesh.s_iVerCol;
		}
	}
	
	public int getMshType()	{
		return(this.m_iMshType);
	}
	
	public String getSknId()	{
		return(this.m_strSknDat);
	}
}

package eg;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import eg.CFWScene.SFWInfoFace;
import eg.CFWShadowObj.SFWInfoShadow;
import eg.IFWMesh.SFWFace;
import eg.IFWMesh;

public class CFWSysRender {
	protected IFWDevice m_devCur;
	protected IFWCamera m_camCur;
	protected CFWScene m_sceCur;
	
	protected CFWMatrix m_matView;
	protected CFWMatrix m_matProj;
	protected CFWMatrix m_matFmt;
	
	private boolean m_bEnableDeep;
	private boolean m_bEnableLgt;
	private boolean m_bDrawShadow;
	
	protected String m_strMshCur;
	protected Vector<CFWShadowObj> m_aShadows;
	/**
	 * Single mode
	 */
	static private CFWSysRender s_Instance = null;
	static public CFWSysRender getInstance()	{
		if(null == s_Instance)	{
			System.out.println("the render system hasn't be initlized!");
			return null;
		}
		return(s_Instance);
	}
	static public CFWSysRender getInstance( IFWDevice devI, IFWCamera camI)	{
		if(null == s_Instance)
			s_Instance = new CFWSysRender( devI, camI);
		return(s_Instance);
	}
	/**
	 * Constructor
	 * @param devI
	 * @param camI
	 */
	protected CFWSysRender( IFWDevice devI, IFWCamera camI)	{
		m_devCur = devI; m_camCur = camI;
		try	{
			CFWSysGui.getInstance( devI, camI);
		}
		catch(Exception e)	{
			e.printStackTrace();
		}
		m_aShadows = new Vector<CFWShadowObj>();

		m_bEnableDeep = true;
		m_bEnableLgt = true;
		m_bDrawShadow = false;
		
		m_strMshCur = "";
	}

	public void update(CFWScene sceI)	{
		CFWProgChk pgChk = new CFWProgChk();
		pgChk.funStart(this.getClass() + "::update(CFWScene sceI)");
		//1.data check
		if(null == m_devCur || null == m_camCur)	{
			System.out.println("device or camera can't use!");
			return;
		}
		
		//2.clear the screen
		m_devCur.clsBackBuf();
		
		//3.compose matrix
		CFWMatrix matTrsf = new CFWMatrix();
		m_matFmt = m_camCur.format();
		m_matProj = m_camCur.project();
		m_matView = m_camCur.view();
		matTrsf = m_matView;//world -> view
		CFWMatrix matView = m_matView;
		matTrsf = matTrsf.dotRight(m_matProj);//view -> project
		CFWMatrix matVwProj = matTrsf;
		matTrsf = matTrsf.dotRight(m_matFmt);//project -> port
		CFWMatrix matProjFmt = m_matProj.dotRight(m_matFmt);
		
		//4.draw world coord
		try {
			CFWSysGui.getInstance().drawWldCoord( new CFWCoord(), matTrsf);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		//5.check number object for rendering
		if(sceI.isEmpty())	{
			//System.out.println("no render object!");
			//return;
		}
		else	{
			m_sceCur = sceI;
			Vector<SFWInfoFace> vecInfoFc = m_sceCur.getAllFacesFromSce();
			if(vecInfoFc.isEmpty())	{
				System.out.println("no face can be rendered!");
				return;
			}
			
			HashMap<String, IFWDevice.SFWLight> mpLgts = null;
			CFWMatrix matSceTrsf = m_sceCur.getMatSceTrsf();
			if(m_bEnableLgt)	{
				mpLgts = m_sceCur.getAllLights();
				//build shadow objects
				buildShadowObj( mpLgts, vecInfoFc, matSceTrsf.dotRight(m_matView));
			}

			//6.take the points from array, and do transform
			// get face in the mesh
			for( int i = 0; i < vecInfoFc.size(); ++i)	{
				CFWScene.SFWInfoFace MshInfoFcTmp = (CFWScene.SFWInfoFace)vecInfoFc.get(i);
				
				if(null == MshInfoFcTmp
				|| MshInfoFcTmp.m_strMshBelog.isEmpty())	{
					System.out.println("mesh has no name in scene!");
					return;
				}
				//
				m_strMshCur = MshInfoFcTmp.m_strMshBelog;
				CFWMatrix matWld = m_sceCur.getMatMshTrsf(m_strMshCur);//transfer mesh to scene
				matWld = matWld.dotRight(matSceTrsf);//transfer scene to world
				
				for( int j = 0; j < MshInfoFcTmp.m_aInfoFc.size(); ++j)	{
					Vector<IFWMesh.SFWFace> aInfoFc = MshInfoFcTmp.m_aInfoFc;
					IFWMesh.SFWFace infoFcTmp = aInfoFc.get(j);
					
					//take all points out for transform
					CFWPoint poiA = infoFcTmp.m_verA.getLoc();
					CFWPoint poiB = infoFcTmp.m_verB.getLoc();
					CFWPoint poiC = infoFcTmp.m_verC.getLoc();
					
					matTrsf.dotLeft(matWld);
					matView.dotLeft(matWld);
					matVwProj.dotLeft(matWld);
					
					//CFWPoint poiATf = matTrsf.multiPoiLeft(poiA);
					//CFWPoint poiBTf = matTrsf.multiPoiLeft(poiB);
					//CFWPoint poiCTf = matTrsf.multiPoiLeft(poiC);
					
					CFWPoint poiAVw = matView.multiPoiLeft(poiA);
					CFWPoint poiBVw = matView.multiPoiLeft(poiB);
					CFWPoint poiCVw = matView.multiPoiLeft(poiC);
					
					CFWPoint poiAPf = matVwProj.multiPoiLeft(poiA);
					CFWPoint poiBPf = matVwProj.multiPoiLeft(poiB);
					CFWPoint poiCPf = matVwProj.multiPoiLeft(poiC);
					
					if(poiAVw.m_fZ >= 0)	{
						poiAPf.m_fY = -poiAPf.m_fY;
					}
					if(poiBVw.m_fZ >= 0)	{
						poiBPf.m_fY = -poiBPf.m_fY;
					}
					if(poiCVw.m_fZ >= 0)	{
						poiCPf.m_fY = -poiCPf.m_fY;
					}
					
					//don't display the face back to the camera
					CFWTriangle tglCurVw = null;
					try {
						tglCurVw = new CFWTriangle( poiAVw, poiBVw, poiCVw);
					} catch (Exception e2) {
						e2.printStackTrace();
						System.exit(-1);
					}
					
					//get every point inside project triangle
					CFWTriangle tglCur = null;
					Vector<CFWPoint> vecPois = new Vector<CFWPoint>();
					try	{
						if(IFWMesh.s_iVerCol == infoFcTmp.m_iPoiType
						|| IFWMesh.s_iVerUV == infoFcTmp.m_iPoiType)	{
							tglCur = new CFWTriangle( poiAPf, poiBPf, poiCPf, CFWTriangle.s_iPlnXY);
							vecPois = tglCur.getEvePoisInside();
						}
					}
					catch(Exception e)	{
						e.printStackTrace();
						continue;
					}
					
					//get mesh skin
					String strTexHsh = m_sceCur.getMshOfRObj(m_strMshCur).getSknId();
					IFWSkin sknCur = null;
					try	{
						IFWObject objTmp = CFWPool.getInstance().getObjFmPool(strTexHsh);
						if(objTmp.getClass().equals(IFWSkin.class))	{
							throw new Exception("W:the object of the id is not a skin object");
						}
						sknCur = (IFWSkin)objTmp;
					}
					catch(Exception e)	{
						if(infoFcTmp.m_iPoiType != IFWMesh.s_iVertex)	{
							continue;
						}
					}
					
					//check the face direction
					CFWVector vecTglNor = tglCurVw.getNorVec();
					CFWVector vecLook = new CFWVector( 0.0f, 0.0f, -1.0f);
					if(!vecTglNor.isSameDir_VerticalOut(vecLook))
						continue;//continue;
					
					switch(infoFcTmp.m_iPoiType)	{
					case IFWMesh.s_iVerCol:
						Color colA = ((IFWMesh.SFWVerCol)infoFcTmp.m_verA).m_color;
						Color colB = ((IFWMesh.SFWVerCol)infoFcTmp.m_verB).m_color;
						Color colC = ((IFWMesh.SFWVerCol)infoFcTmp.m_verC).m_color;
						
						try {
							for( int t = 0; t < vecPois.size(); ++t)	{
								if(vecPois.get(t).getClass().equals(CFWPoint.class))	{
									CFWPoint poiTmp = (CFWPoint)vecPois.get(t);
									//out of the view box, don't display
									CFWPoint poiInVw = getPoiLocVw( poiTmp, tglCurVw);
									if(!this.m_camCur.getVwCentra().isInside(poiInVw))	{
										continue;
									}
									
									Color colCurPixel = colA;
									if(colA != colB || colB != colC || colC != colA)	{
										CFWPoint poiCur = new CFWPoint( poiTmp.m_fX, poiTmp.m_fY, 0);
										colCurPixel = getMixCol( poiCur, poiAPf, poiBPf, poiCPf, colA, colB, colC);
									}
									
									poiTmp = m_matFmt.multiPoiLeft(poiTmp);
									poiTmp.m_fZ = poiInVw.m_fZ;
									
									colCurPixel = this.addLgtEffect( mpLgts, infoFcTmp, matView,
											poiInVw, colCurPixel);
									
									this.renderPixel( poiTmp, colCurPixel);
								}
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						
						break;
					case IFWMesh.s_iVerUV:
						//(0,0) left and top
						//(1,0) left and bottom
						//(0,1) right and top
						//(1,1) right and bottom
						if(!infoFcTmp.m_verA.getClass().equals(IFWMesh.SFWVerUV.class)
						|| !infoFcTmp.m_verB.getClass().equals(IFWMesh.SFWVerUV.class)
						|| !infoFcTmp.m_verC.getClass().equals(IFWMesh.SFWVerUV.class))	{
							System.out.println("vertex type error!");
							System.exit(-1);
						}
						IFWMesh.SFWVerUV verUVA = (IFWMesh.SFWVerUV)infoFcTmp.m_verA;
						IFWMesh.SFWVerUV verUVB = (IFWMesh.SFWVerUV)infoFcTmp.m_verB;
						IFWMesh.SFWVerUV verUVC = (IFWMesh.SFWVerUV)infoFcTmp.m_verC;
						CFWPoint poiASkn = new CFWPoint( verUVA.m_fU, verUVA.m_fV, 0);
						CFWPoint poiBSkn = new CFWPoint( verUVB.m_fU, verUVB.m_fV, 0);
						CFWPoint poiCSkn = new CFWPoint( verUVC.m_fU, verUVC.m_fV, 0);
						
						try	{
							CFWTriangle tglTex = new CFWTriangle( poiASkn, poiBSkn, poiCSkn);
							
							for( int t = 0; t < vecPois.size(); ++t)	{
								if(vecPois.get(t).getClass().equals(CFWPoint.class))	{
									CFWPoint poiTmp = (CFWPoint)vecPois.get(t);
									//out of the view box, don't display
									CFWPoint poiInVw = getPoiLocVw( poiTmp, tglCurVw);
									if(!this.m_camCur.getVwCentra().isInside(poiInVw))	{
										continue;
									}

									try	{
										CFWPoint poiOnTex = tglTex.getSameLocFmTgl( poiInVw, tglCurVw);
										Color colCurPixel = sknCur.getColFromImg( poiOnTex.m_fX, poiOnTex.m_fY);
										
										poiTmp = m_matFmt.multiPoiLeft(poiTmp);
										poiTmp.m_fZ = poiInVw.m_fZ;
										
										colCurPixel = this.addLgtEffect( mpLgts, infoFcTmp, matView,
												poiInVw, colCurPixel);

										this.renderPixel( poiTmp, colCurPixel);
									}
									catch(Exception e)	{
										//e.printStackTrace();
										continue;
									}
								}
							}
						}
						catch(Exception e)	{
							e.printStackTrace();
						}
						
						break;
					case IFWMesh.s_iVertex:
						//use negative color of the back ground color
						Color colDefault = CFWMath.getNegCol(m_devCur.getBackGround());
						
						//check depth test
						try	{
							boolean bDrawDiagonl = CFWSysGui.getInstance().enableDiagonl();
							if(bDrawDiagonl || infoFcTmp.m_bDrawAB)	{
								this.drawLine( poiAVw, poiBVw, matProjFmt, colDefault);
							}
							if(bDrawDiagonl || infoFcTmp.m_bDrawBC)	{
								this.drawLine( poiBVw, poiCVw, matProjFmt, colDefault);
							}
							if(bDrawDiagonl || infoFcTmp.m_bDrawCA)	{
								this.drawLine( poiCVw, poiAVw, matProjFmt, colDefault);
							}
						}
						catch(Exception e)	{
							e.printStackTrace();
						}
					}
					
					try	{
						CFWSysGui.getInstance().drawEdge( tglCurVw, infoFcTmp, matProjFmt);
						CFWSysGui.getInstance().drawPoiNor( infoFcTmp, matTrsf);
					}
					catch(Exception e)	{
						e.printStackTrace();
					}
				}
			}
		}
		pgChk.funEnd();
	}
	
	protected void buildShadowObj( HashMap<String, IFWDevice.SFWLight> mpLgtsI,
			Vector<CFWScene.SFWInfoFace> vecInfoFcI, CFWMatrix matVwI)	{
		if(m_bDrawShadow)	{
			try	{
				//process light
				HashMap<String, IFWDevice.SFWLight> mpLgtsTrsf = new HashMap<String, IFWDevice.SFWLight>();
				Iterator<Entry<String, IFWDevice.SFWLight>> iterBuoy = mpLgtsI.entrySet().iterator();
				while(iterBuoy.hasNext())	{
					Entry<String, IFWDevice.SFWLight> iterCur = iterBuoy.next();
					IFWDevice.SFWLight lgtTmp = new IFWDevice.SFWLight(iterCur.getValue());

					mpLgtsTrsf.put( iterCur.getKey(), lgtTmp.transfer(matVwI));
				}
				
				//build
				m_aShadows.clear();
				for( int i = 0; i < vecInfoFcI.size(); ++i)	{
					CFWScene.SFWInfoFace MshInfoFcTmp = vecInfoFcI.get(i);
					
					CFWMatrix matWld = m_sceCur.getMatMshTrsf(MshInfoFcTmp.m_strMshBelog);;
					matWld = matWld.dotRight(m_sceCur.getMatSceTrsf());
					matWld = matWld.dotRight(matVwI);
					
					for( int j = 0; j < MshInfoFcTmp.m_aInfoFc.size(); ++j)	{
						IFWMesh.SFWFace infoFcTmp = MshInfoFcTmp.m_aInfoFc.get(j);
						if(IFWMesh.s_iVertex == infoFcTmp.m_iPoiType)	{
							continue;
						}
						
						CFWPoint poiA = matWld.multiPoiLeft(infoFcTmp.m_verA.getLoc());
						CFWPoint poiB = matWld.multiPoiLeft(infoFcTmp.m_verB.getLoc());
						CFWPoint poiC = matWld.multiPoiLeft(infoFcTmp.m_verC.getLoc());
						
						CFWTriangle tglWld = new CFWTriangle( poiA, poiB, poiC);
	
						iterBuoy = mpLgtsTrsf.entrySet().iterator();
						while(iterBuoy.hasNext())	{
							Entry<String, IFWDevice.SFWLight> iterCur = iterBuoy.next();
							
							CFWShadowObj objShadow = new CFWShadowObj();
							objShadow.buildShadowObj( iterCur.getKey(), infoFcTmp, iterCur.getValue(), 
									this.m_camCur.getVwCentra(), tglWld);
							
							this.m_aShadows.add(objShadow);
						}
					}
				}
			}
			catch(Exception e)	{
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}
	
	protected float generShadowDepth( CFWPoint poiVwI, CFWVector vecPoiNorI, IFWMesh.SFWFace fcBelongI)	{
		float fColMulti = 1;
		if(m_bDrawShadow)	{
			for( int k = 0; k < m_aShadows.size(); ++k)	{
				Vector<CFWShadowObj.SFWInfoShadow> vecInfoSdo = m_aShadows.get(k).m_aShadows;
				for( int t = 0; t < vecInfoSdo.size(); ++t)	{
					SFWInfoShadow shdo = vecInfoSdo.get(t);
					if(m_aShadows.get(k).getFcIndex().equals(fcBelongI))	{
						continue;
					}
					
					CFWVector vecLgtToPoi = null;
					try	{
						IFWObject objFrmPool = CFWPool.getInstance().getObjFmPool(shdo.m_strLgt);
						if(!objFrmPool.getClass().equals(IFWDevice.SFWLight.class))	{
							throw new Exception("W:got object is not a light!");
						}
						
						IFWDevice.SFWLight lgtCur = (IFWDevice.SFWLight)objFrmPool;
						CFWMatrix matTmp = this.m_sceCur.getMatSceTrsf().dotRight(m_matView);
						IFWDevice.SFWLight lgtTmp = lgtCur.transfer(matTmp);

						switch(lgtTmp.m_iType)	{
						case IFWDevice.SFWLight.s_iLgtDir:
							if(!lgtTmp.getDirLgtCtaOutside().isInside(poiVwI))	{
								return(fColMulti);
							}
						case IFWDevice.SFWLight.s_iLgtPoi:
							vecLgtToPoi = new CFWVector( lgtTmp.m_poiPos, poiVwI);
							vecLgtToPoi.nor();
							break;
						case IFWDevice.SFWLight.s_iLgtPll:
							vecLgtToPoi = lgtTmp.m_vecDir;
							break;
						}
					}
					catch(Exception e)	{
						e.printStackTrace();
						return(fColMulti);
					}
										
					//if(!vecLgtToPoi.isSameDir_VerticalOut(vecPoiNorI))	{
						if(shdo.m_cone.isInside(poiVwI))	{
							fColMulti *= CFWShadowObj.s_fPamShdo;
						}
					//}
				}
			}
		}
		
		return(fColMulti);
	}
	
	protected void drawLine( CFWPoint poiBegI, CFWPoint poiEndI, CFWMatrix matTrafI, Color colI) throws Exception	{
		CFWSegLn lnBE = this.m_camCur.getVwCentra().cutLn( poiBegI, poiEndI);
		CFWPoint poiBScBE = doMultiProjFmt( lnBE.m_poiBeg, matTrafI);
		CFWPoint poiEScBE = doMultiProjFmt( lnBE.m_poiEnd, matTrafI);
		
		if(m_bEnableDeep)	{
			m_devCur.setLine( (int)poiBScBE.m_fX, (int)poiBScBE.m_fY, poiBScBE.m_fZ,
							  (int)poiEScBE.m_fX, (int)poiEScBE.m_fY, poiEScBE.m_fZ, colI);
		}
		else	{
			m_devCur.setLine( (int)poiBScBE.m_fX, (int)poiBScBE.m_fY, IFWDevice.s_fDefDepth,
					  		  (int)poiEScBE.m_fX, (int)poiEScBE.m_fY, IFWDevice.s_fDefDepth, colI);
		}
	}
	
	static protected CFWPoint doMultiProjFmt( CFWPoint poiI, CFWMatrix matTrafI)	{
		CFWPoint poiRet = new CFWPoint(poiI);
		poiRet = matTrafI.multiPoiLeft(poiRet);
		poiRet.m_fX = CFWMath.regulateFloat(poiRet.m_fX);
		poiRet.m_fY = CFWMath.regulateFloat(poiRet.m_fY);
		poiRet.m_fZ = poiI.m_fZ;
		return(poiRet);
	}
	
	public CFWPoint getPoiLocVw( CFWPoint poiI, CFWTriangle tglVwI) throws Exception	{
		CFWPoint poiRet = new CFWPoint();
		float fCamD = m_camCur.getDisOfProjPln();
		float fPlnD = tglVwI.getD();
		CFWVector vecTglNor = tglVwI.getNorVec();
		float fValBtm = vecTglNor.dot(new CFWVector( poiI.m_fX, poiI.m_fY, fCamD)).sun();
		if(0 == fValBtm)	{
			throw new Exception("E:error happened when calculate Z axis");
		}
		poiRet.m_fZ = (fPlnD*fCamD) / fValBtm;
		poiRet.m_fX = (poiRet.m_fZ*poiI.m_fX) / fCamD;
		poiRet.m_fY = (poiRet.m_fZ*poiI.m_fY) / fCamD;
		
		return(poiRet);
	}
	
	protected void renderPixel( CFWPoint poiI, Color colI)	{
		if(m_bEnableDeep)	{
			m_devCur.setPixel( (int)poiI.m_fX, (int)poiI.m_fY,
					poiI.m_fZ, colI);
		}
		else	{
			m_devCur.setPixel( (int)poiI.m_fX, (int)poiI.m_fY, IFWDevice.s_fDefDepth, colI);
		}
	}
	
	//	direction light: Lfin = Kdif*Ldif*cosB/(A0 + A1*dis + A2*dis*dis)*cosA + Kspl*Lspl*(cosQ)^n + Kamb*Lamb
	//	point light:     Lfin = Kdif*Ldif*1/(A0 + A1*dis + A2*dis*dis)*cosA + Kspl*Lspl*(cosQ)^n + Kamb*Lamb
	//	point light:     Lfin = Kdif*Ldif*1/A0*cosA + Kspl*Lspl*(cosQ)^n + Kamb*Lamb
	protected Color addLgtEffect( HashMap<String, IFWDevice.SFWLight> mpLgtsI, SFWFace fcI, 
			CFWMatrix matVwI, CFWPoint poiI, Color colI)	{
		
		Color colFin = colI;
		if(this.m_bEnableLgt)	{
			if(mpLgtsI.isEmpty())	{
				return(colI);
			}
			
			CFWVector vecPoiNor = null;
			try	{
				vecPoiNor = fcI.getPoiNor( poiI, matVwI);
			}
			catch(Exception e)	{
				e.printStackTrace();
				return(colI);
			}
			
			Set<Entry<String, IFWDevice.SFWLight>> setTmp = mpLgtsI.entrySet();
			Iterator<Entry<String, IFWDevice.SFWLight>> iter = setTmp.iterator();
			while(iter.hasNext())	{
				Entry<String, IFWDevice.SFWLight> etyTmp = iter.next();
				IFWDevice.SFWLight lgtCur = etyTmp.getValue();
				CFWMatrix matTmp = m_sceCur.getMatSceTrsf().dotRight(m_matView);
				IFWDevice.SFWLight lgtTmp = lgtCur.transfer(matTmp);
				
				if(this.m_devCur.isLgtEnable(etyTmp.getKey()))	{
					CFWVector vecDirLgt = new CFWVector( lgtTmp.m_poiPos, poiI); vecDirLgt.nor();
					float fDisToLgt = new CFWVector( lgtTmp.m_poiPos, poiI).getLength();
					float fCosA = vecPoiNor.cosWithVec(vecDirLgt);
					if(fCosA < 0)	{
						fCosA = vecPoiNor.cosWithVec(vecDirLgt.getNegVec());
					}
					
					float fValUp = 1;
					float fValDown = 0;
					switch(lgtTmp.m_iType)	{
					case IFWDevice.SFWLight.s_iLgtDir:
						lgtTmp.m_vecDir.nor();
						fValUp = vecDirLgt.cosWithVec(lgtTmp.m_vecDir);
						fValDown = lgtTmp.m_fAttenuation0 +
								   lgtTmp.m_fAttenuation1*fDisToLgt +
								   lgtTmp.m_fAttenuation2*fDisToLgt*fDisToLgt;
						break;
					case IFWDevice.SFWLight.s_iLgtPoi:
						fValDown = lgtTmp.m_fAttenuation0 +
						   		   lgtTmp.m_fAttenuation1*fDisToLgt +
						   		   lgtTmp.m_fAttenuation2*fDisToLgt*fDisToLgt;
						break;
					case IFWDevice.SFWLight.s_iLgtPll:
						fValDown = lgtTmp.m_fAttenuation0;
						break;
					}
					float fPwrLgt = fValUp/fValDown;
					
					CFWVector vecRefLgt = new CFWVector(vecPoiNor.multi(2).sub(vecDirLgt));
					CFWVector vecLook = new CFWVector( 0.0f, 0.0f, -1.0f);
					float fCosQ = vecRefLgt.cosWithVec(vecLook);
					
					//@_@:fKDif + fKSpl + fKAmd = 1
					//according to the location 
					float fKDif = 0.85f;
					float fKSpl = 0.85f;
					float fKAmd = 0.001f;
					/*if(fCosA > 0.99f)	{
						fKSpl = 5.0f;
						fKDif = 500;
						fKAmd = 500;
					}*/
					
					try	{
						float fPwrBright = (float)Math.pow( fCosQ, lgtTmp.m_iSplLvl);
						//	Kdif*Ldif*valUp/valDn*cosA
						Color colDifLgt = CFWMath.colMultiVal( lgtTmp.m_colDif, fPwrLgt * fCosA * fKDif);
						//	Kspl*Lspl*(cosQ)^n
						Color colSplLgt = CFWMath.colMultiVal( lgtTmp.m_colSpc, fKSpl * fPwrBright);
						//	Kamb*Lamb
						Color colAmdLgt = CFWMath.colMultiVal( lgtTmp.m_colAmb, fKAmd);
						
						Color colLgt = CFWMath.mixTwoColor( CFWMath.mixTwoColor( colDifLgt, colSplLgt), colAmdLgt);
						
						colFin = CFWMath.mixTwoColor( colFin, colLgt);
					}
					catch(Exception e)	{
						e.printStackTrace();
					}
				}
			}
			
			//add shadow effect
			if(this.m_bDrawShadow)	{
				float fValShdo = generShadowDepth( poiI, vecPoiNor, fcI);
				if(-1 == fValShdo)	{
					colFin = Color.black;
				}
				else	{
					colFin = CFWMath.colMultiVal( colFin, fValShdo);
				}
			}
		}
		
		return(colFin);
	}
	
	protected Color getMixCol( CFWPoint poiCurI, CFWPoint poiAPfI, CFWPoint poiBPfI, CFWPoint poiCPfI,
			Color colAI, Color colBI, Color colCI)	{
		
		CFWRay vecACur = new CFWRay( poiAPfI, poiCurI);
		
		CFWSegLn slnBC = new CFWSegLn( poiBPfI, poiCPfI);
		
		CFWPoint poiOnBC = null;
		try {
			poiOnBC = vecACur.intersectsLn(slnBC);
		} catch (Exception e) {
			e.printStackTrace();
			return(colAI);
		}

		CFWSegLn lnBC = new CFWSegLn( poiBPfI, poiCPfI);
		CFWSegLn lnAPoi = new CFWSegLn( poiAPfI, poiOnBC);
		int iColPoiBC = lnBC.getMixColorFromSegLn( poiOnBC, colBI, colCI);
		int iColPoiRet = lnAPoi.getMixColorFromSegLn( poiCurI, colAI, new Color(iColPoiBC));
		
		return(new Color(iColPoiRet));
	}
	
	public boolean isEnableDepth()	{
		return(this.m_bEnableDeep);
	}
}

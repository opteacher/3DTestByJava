package mn;

import eg.CFWMatrix;
import eg.CFWPoint;
import eg.CFWRay;
import eg.CFWVector;
import eg.IFWCamera;

public class CFWFCamera extends CFWCCamera {
	protected boolean m_bFocusEnable;
	
	public CFWFCamera()	{
		m_bFocusEnable = false;
	}
	
	public void enableFocus(boolean bEnableFocusI)	{
		m_bFocusEnable = bEnableFocusI;
	}
	
	public void rollByX(float fAngI)	{
		CFWVector vecAxisX = m_codCam.m_vecAxis[IFWCamera.s_iRight];
		CFWVector vecAxisY = m_codCam.m_vecAxis[IFWCamera.s_iUp];
		CFWVector vecAxisZ = m_codCam.m_vecAxis[IFWCamera.s_iLook];
		CFWMatrix matRotX = new CFWMatrix();
		if(checkFocusMode())	{
			CFWRay rayAxis = new CFWRay( m_poiTar, m_codCam.m_vecAxis[IFWCamera.s_iRight]);
			matRotX = CFWMatrix.rotate3D( fAngI, rayAxis);
			m_codCam.m_poiCenter = matRotX.multiPoiLeft(m_codCam.m_poiCenter);
		}
		else	{
			matRotX = CFWMatrix.rotate3D( fAngI, vecAxisX);
		}
		m_codCam.m_vecAxis[IFWCamera.s_iUp] = matRotX.multiVecLeft(vecAxisY);
		m_codCam.m_vecAxis[IFWCamera.s_iLook] = matRotX.multiVecLeft(vecAxisZ);
	}
	public void rollByY(float fAngI)	{
		CFWVector vecAxisX = m_codCam.m_vecAxis[IFWCamera.s_iRight];
		CFWVector vecAxisY = m_codCam.m_vecAxis[IFWCamera.s_iUp];
		CFWVector vecAxisZ = m_codCam.m_vecAxis[IFWCamera.s_iLook];
		CFWMatrix matRotY = new CFWMatrix();
		if(checkFocusMode())	{
			CFWRay rayAxis = new CFWRay( m_poiTar, m_codCam.m_vecAxis[IFWCamera.s_iUp]);
			matRotY = CFWMatrix.rotate3D( fAngI, rayAxis);
			m_codCam.m_poiCenter = matRotY.multiPoiLeft(m_codCam.m_poiCenter);
		}
		else	{
			matRotY = CFWMatrix.rotate3D( fAngI, vecAxisY);
		}
		m_codCam.m_vecAxis[IFWCamera.s_iRight] = matRotY.multiVecLeft(vecAxisX);
		m_codCam.m_vecAxis[IFWCamera.s_iLook] = matRotY.multiVecLeft(vecAxisZ);				
	}
	public void rollByZ(float fAngI)	{
		if(checkFocusMode())
			return;
		CFWVector vecAxisX = m_codCam.m_vecAxis[IFWCamera.s_iRight];
		CFWVector vecAxisY = m_codCam.m_vecAxis[IFWCamera.s_iUp];
		CFWVector vecAxisZ = m_codCam.m_vecAxis[IFWCamera.s_iLook];
		CFWMatrix matRotZ = CFWMatrix.rotate3D( fAngI, vecAxisZ);
		m_codCam.m_vecAxis[IFWCamera.s_iRight] = matRotZ.multiVecLeft(vecAxisX);
		m_codCam.m_vecAxis[IFWCamera.s_iUp] = matRotZ.multiVecLeft(vecAxisY);
	}
	public void moveByX(float fDisI)	{
		CFWVector vecAxisX = m_codCam.m_vecAxis[IFWCamera.s_iRight];
		CFWPoint poiPlusTmp = new CFWPoint( vecAxisX.m_fX, 0.0f, vecAxisX.m_fZ);
		m_codCam.m_poiCenter = m_codCam.m_poiCenter.plus(poiPlusTmp.multi(fDisI));
		if(!checkFocusMode())	{
			m_poiTar = m_poiTar.plus(poiPlusTmp.multi(fDisI));
		}
	}
	public void moveByY(float fDisI)	{
		m_codCam.m_poiCenter.m_fY += fDisI;
		if(!checkFocusMode())	{
			m_poiTar.m_fY += fDisI;
		}
	}
	public void moveByZ(float fDisI)	{
		CFWVector vecAxisZ = m_codCam.m_vecAxis[IFWCamera.s_iLook];
		CFWPoint poiPlusTmp = new CFWPoint( vecAxisZ.m_fX, 0.0f, vecAxisZ.m_fZ);
		m_codCam.m_poiCenter = m_codCam.m_poiCenter.plus(poiPlusTmp.multi(fDisI));
		if(!checkFocusMode())	{
			m_poiTar = m_poiTar.plus(poiPlusTmp.multi(fDisI));
		}
	}
	
	/**
	 * if want to enable focus mode, there should be a target point
	 * @return
	 */
	protected boolean checkFocusMode()	{
		if(!m_bFocusEnable)
			return false;
		//1.if target point sub center point is not equals to look vector, return false
		CFWVector vecTmp = new CFWVector(m_poiTar.sub(m_codCam.m_poiCenter));
		vecTmp.nor();
		if(!vecTmp.equals(m_codCam.m_vecAxis[IFWCamera.s_iLook]))	{
			System.out.println("target isn't exist, camera can't enable focus mode");
			return false;
		}
		else
			return true;
	}
}

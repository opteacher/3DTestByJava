package eg;

import java.util.Vector;

public interface IFWMshSet extends IFWMesh {
	public Vector<SFWFace> getEveFace();
	public void setSkin( int iSubMshI, String strSknI) throws Exception;
}

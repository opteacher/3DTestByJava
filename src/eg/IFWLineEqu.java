package eg;

public interface IFWLineEqu {
	static public final int s_iLess = -1;
	static public final int s_iEqual = 0;
	static public final int s_iBigger = 1;
	
	public void setFunType(int iFunTpI);
	public void enableEquals(boolean bEnableI);//when check bigger and less, whether include equals
	public float calculate( float fXI, float fYI, float fZI)	;
	public boolean isFillToFun( float fXI, float fYI, float fZI);
}

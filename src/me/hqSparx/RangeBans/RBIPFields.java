package me.hqSparx.RangeBans;

public class RBIPFields {
	public byte[] bMin = new byte[4];
	public byte[] bMax = new byte[4];
	public String Address;

	public RBIPFields(byte[] min, byte[] max, String addr) {
		for (int i = 0; i < 4; ++i) {
			this.bMin[i] = min[i];
			this.bMax[i] = max[i];
		}
		
		this.Address = addr;
	}

	public int rUns(Byte signed) {
		if (signed<0)
			return (signed+256);
		else 
			return signed;
	}

	public boolean checkmin(byte a, byte b, byte c, byte d) {
		if(rUns(a) < rUns(bMin[0]) || rUns(b) < rUns(bMin[1]) 
			|| rUns(c) < rUns(bMin[2]) || rUns(d) < rUns(bMin[3]))
				return false;
		
		return true;
	}
	
	public boolean checkmax(byte a, byte b, byte c, byte d) {
		if(rUns(a) > rUns(bMax[0]) && rUns(b) > rUns(bMax[1])
			&& rUns(c) > rUns(bMax[2]) && rUns(d) > rUns(bMax[3]))
				return false;
		
		return true;
	}

}
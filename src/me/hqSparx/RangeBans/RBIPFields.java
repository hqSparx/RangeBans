package me.hqSparx.RangeBans;


public class RBIPFields
{
public byte[] bMin = new byte[4];
public byte[] bMax = new byte[4];
public String Address;

public RBIPFields(byte[] min, byte[] max, String addr)
{
this.bMin[0] = min[0];
this.bMin[1] = min[1];
this.bMin[2] = min[2];
this.bMin[3] = min[3];

this.bMax[0] = max[0];
this.bMax[1] = max[1];
this.bMax[2] = max[2];
this.bMax[3] = max[3];

this.Address = addr;
}

public int rUns(Byte signed){
	if(signed<0) return (256+signed);
	else return signed;
	}

public boolean checkmin(byte a, byte b, byte c, byte d)
{
	if(rUns(a) >= rUns(bMin[0])){
	if(rUns(b) >= rUns(bMin[1])){
	if(rUns(c) >= rUns(bMin[2])){
	if(rUns(d) >= rUns(bMin[3])){
		return true;
	}}}}
	
	return false;
	}
public boolean checkmax(byte a, byte b, byte c, byte d)
{
	if(rUns(a) <= rUns(bMax[0])){
	if(rUns(b) <= rUns(bMax[1])){
	if(rUns(c) <= rUns(bMax[2])){
	if(rUns(d) <= rUns(bMax[3])){
		return true;
	}}}}
	
	return false;
	}

}
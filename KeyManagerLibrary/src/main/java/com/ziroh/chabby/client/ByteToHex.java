package com.ziroh.chabby.client;

class ByteToHex 
{
	public String GetHexString(byte[] Digest)
    {
        if (Digest.length == 0 || Digest == null)
        {
            throw new IllegalArgumentException("Digest cannot be null or of Zero length");
        }
//        System.Runtime.Remoting.Metadata.W3cXsd2001.SoapHexBinary hexConverted = new System.Runtime.Remoting.Metadata.W3cXsd2001.SoapHexBinary(Digest);
//        return hexConverted.ToString();
        
        StringBuilder builder =new StringBuilder();
        for(byte i : Digest)
        {
        	builder.append(String.format("%02X", i & 0xff));
        }
        return builder.toString();
        
    }
}

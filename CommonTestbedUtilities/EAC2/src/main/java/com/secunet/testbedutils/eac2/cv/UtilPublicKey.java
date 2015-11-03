package com.secunet.testbedutils.eac2.cv;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.spec.DHPublicKeySpec;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.JCEECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve.Fp;
import org.bouncycastle.util.BigIntegers;

import com.secunet.testbedutils.eac2.DHDomainParameter;
import com.secunet.testbedutils.eac2.DHDomainParameter.Type;
import com.secunet.testbedutils.utilities.CommonUtil;

public class UtilPublicKey {
    public static byte[] getRawKey(ECPublicKey pub) throws IOException {
        
        ECPoint point = pub.getW();
        byte[] x = CommonUtil.removeLeadingZeros(point.getAffineX().toByteArray());
        byte[] y = CommonUtil.removeLeadingZeros(point.getAffineY().toByteArray());
        
        boolean xPad = false;
        boolean yPad = false;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (x.length != y.length) {
            if (x.length < y.length) {
                xPad = true;
            } else if (x.length > y.length) {
                yPad = true;
            }
        }
        
        if (xPad) {
            for (int i = 0; i < y.length - x.length; i++) {
                out.write(0x00);
            }
            out.write(x);
            out.write(y);
        } else if (yPad) {
            out.write(x);
            for (int i = 0; i < x.length - y.length; i++) {
                out.write(0x00);
            }
            out.write(y);
        } else {
            out.write(x);
            out.write(y);
        }
        
        return out.toByteArray();
    }
    
    public static byte[] toDataObjectBytes(PublicKey publicKey, ECParameterSpec ecSpec, int version, ASN1ObjectIdentifier identifier) {
    	BCECPublicKey pk = (BCECPublicKey) publicKey;
        ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
        
        TLVObject p = new TLVObject(0x81, BigIntegers.asUnsignedByteArray(((Fp) ecSpec.getCurve()).getQ()));
        TLVObject a = new TLVObject(0x82, BigIntegers.asUnsignedByteArray(ecSpec.getCurve().getA().toBigInteger()));
        TLVObject b = new TLVObject(0x83, BigIntegers.asUnsignedByteArray(ecSpec.getCurve().getB().toBigInteger()));
        TLVObject G = new TLVObject(0x84, ecSpec.getG().getEncoded(false));
        TLVObject r = new TLVObject(0x85, BigIntegers.asUnsignedByteArray(ecSpec.getN()));
        
        TLVObject Y = new TLVObject(0x86, pk.getQ().getEncoded(false));
        
        try {
			dataBuffer.write(identifier.getEncoded());
			 if (version == 1) {
		            dataBuffer.write(p.toBytes());
		            dataBuffer.write(a.toBytes());
		            dataBuffer.write(b.toBytes());
		            dataBuffer.write(G.toBytes());
		            dataBuffer.write(r.toBytes());
		        }
		        dataBuffer.write(Y.toBytes());
		        
		        // only include cofactor if it was originally given by domain
		        // parameters
		        if (version == 1 && ecSpec.getH() != null) {
		            TLVObject f = new TLVObject(0x87, BigIntegers.asUnsignedByteArray(ecSpec.getH()));
		            dataBuffer.write(f.toBytes());
		        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return new TLVObject(0x7F49, dataBuffer.toByteArray()).toBytes();
    }
    
    
    
    public static byte[] toDataObjectBytes(PublicKey publicKey, DHDomainParameter domainParameter, int version)
            throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        switch (domainParameter.getType()) {
            case DH: {
                /*
                	DH
                	Prime modulus p			0x81	Unsigned Integer
                	Order of the subgroup q	0x82	Unsigned Integer
                	Generator g				0x83	Unsigned Integer
                	Public value y			0x84	Unsigned Integer
                */
                // this case is not tested!!!
                KeyFactory kf = KeyFactory.getInstance(domainParameter.getType().toString());
                DHPublicKeySpec keySpec = kf.getKeySpec(publicKey, DHPublicKeySpec.class);
                
                ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
                
                TLVObject p = new TLVObject(0x81, BigIntegers.asUnsignedByteArray(keySpec.getP()));
                TLVObject q = new TLVObject(0x82, BigIntegers.asUnsignedByteArray(null));
                TLVObject g = new TLVObject(0x83, BigIntegers.asUnsignedByteArray(keySpec.getG()));
                TLVObject y = new TLVObject(0x84, BigIntegers.asUnsignedByteArray(keySpec.getY()));
                
                dataBuffer.write(domainParameter.getProtocol().getEncoded());
                dataBuffer.write(p.toBytes());
                dataBuffer.write(q.toBytes());
                dataBuffer.write(g.toBytes());
                dataBuffer.write(y.toBytes());
                
                return new TLVObject(0x7F49, dataBuffer.toByteArray()).toBytes();
            }
            case ECDH: {
                /*
                	ECDH
                	Object Identifier			0x06	Object Identifier		m
                	Prime modulus p				0x81	Unsigned Integer		c
                	First coefficient a			0x82	Unsigned Integer		c
                	Second coefficient b		0x83	Unsigned Integer		c
                	Base point G				0x84	Elliptic Curve Point	c
                	Order of the base point r	0x85	Unsigned Integer		c
                	Public point Y				0x86	Elliptic Curve Point	m
                	Cofactor f					0x87	Unsigned Integer		c
                */
                BCECPublicKey pk = (BCECPublicKey) publicKey;
                ECParameterSpec eps = pk.getParameters();
                ByteArrayOutputStream dataBuffer = new ByteArrayOutputStream();
                
                TLVObject p = new TLVObject(0x81, BigIntegers.asUnsignedByteArray(((Fp) eps.getCurve()).getQ()));
                TLVObject a = new TLVObject(0x82, BigIntegers.asUnsignedByteArray(eps.getCurve().getA().toBigInteger()));
                TLVObject b = new TLVObject(0x83, BigIntegers.asUnsignedByteArray(eps.getCurve().getB().toBigInteger()));
                TLVObject G = new TLVObject(0x84, eps.getG().getEncoded(false));
                TLVObject r = new TLVObject(0x85, BigIntegers.asUnsignedByteArray(eps.getN()));
                
                TLVObject Y = new TLVObject(0x86, pk.getQ().getEncoded(false));
                
                dataBuffer.write(domainParameter.getProtocol().getEncoded());
                if (version == 1) {
                    dataBuffer.write(p.toBytes());
                    dataBuffer.write(a.toBytes());
                    dataBuffer.write(b.toBytes());
                    dataBuffer.write(G.toBytes());
                    dataBuffer.write(r.toBytes());
                }
                dataBuffer.write(Y.toBytes());
                
                // only include cofactor if it was originally given by domain
                // parameters
                if (version == 1 && domainParameter.isCofactorGiven()) {
                    TLVObject f = new TLVObject(0x87, BigIntegers.asUnsignedByteArray(eps.getH()));
                    dataBuffer.write(f.toBytes());
                }
                
                return new TLVObject(0x7F49, dataBuffer.toByteArray()).toBytes();
            }
            default: {
                // ???
                break;
            }
        }
        return null;
    }
    
    public static byte[] toCompressedBytes(PublicKey publicKey, Type domainParameterType) {
        switch (domainParameterType) {
            case DH: {
                // this case is not tested!!!
                try {
                    MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                    return sha1.digest(publicKey.getEncoded());
                } catch (NoSuchAlgorithmException e) {
                    // this should never happen
                    return null;
                }
            }
            case ECDH: {
                JCEECPublicKey pk = (JCEECPublicKey) publicKey;
                return BigIntegers.asUnsignedByteArray(pk.getQ().normalize().getXCoord().toBigInteger());
            }
            default: {
                // ???
                break;
            }
        }
        return null;
    }
}

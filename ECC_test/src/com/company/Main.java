package com.company;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.spec.ECPoint;
import java.util.Scanner;

public class Main {
    //橢圓曲線E: y^2=x^3+a*x+b (mod p)
    //static EllipticCurve E;
    static BigInteger a=BigInteger.valueOf(0);
    static BigInteger b=BigInteger.valueOf(7);
    static BigInteger p=new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F",16);
    final static BigInteger ZERO=BigInteger.ZERO;
    final static BigInteger TWO=BigInteger.valueOf(2);
    final static BigInteger THREE=BigInteger.valueOf(3);
    final static ECPoint Inf=ECPoint.POINT_INFINITY;
    public static void main(String[] args) {
        BigInteger xP=new BigInteger("79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798",16);
        BigInteger yP=new BigInteger("483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8",16);
        ECPoint P=new ECPoint(xP,yP);
        BigInteger n = new BigInteger("15299045857177923538922334779025185825298755729161660540366820699847482526746");

        //E=new EllipticCurve(Fp,a,b);
        System.out.println("橢圓曲線E/Fp:");
        System.out.println("a="+a);
        System.out.println("b="+b);

        System.out.println("點G(=P)座標:");
        System.out.println("xP="+xP);
        System.out.println("yP="+yP);
        System.out.println("");
        System.out.println("私鑰："+n);
        ECPoint Q=multiply(n,P);
        System.out.println("共鑰座標:");
        System.out.println("xQ="+Q.getAffineX());
        System.out.println("yQ="+Q.getAffineY());
        while (true) {
            int tect;
            System.out.println("encrypt data 1 or decrypt data 2 ");
            Scanner input1 = new Scanner(System.in);
            tect = input1.nextInt();
            switch (tect)
            {
                case 1:
                    String message;
                    System.out.println("encrypt data:");
                    Scanner input2 = new Scanner (System.in);
                    message = input2.next();
                    BigInteger encryptInt = new BigInteger("1"+toBinary(message, 8), 2);
                    String encryptData= encryptInt.toString();
                    int DataLong = encryptData.length();
                    String PointX_Data= encryptData.substring(0, DataLong/2);
                    String PointY_Data= encryptData.substring(DataLong/2, DataLong);
                    String bug=PointY_Data;
                    for(int i=0;i<PointY_Data.length();i++){
                        if(PointY_Data.substring(i,i+1).equals("0")){
                            PointX_Data=PointX_Data+"0";
                            bug = bug.substring(i+1,bug.length());
                        }else {
                            break;
                        }
                    }
                    PointY_Data=bug;
                    System.out.println("编码後座標:");
                    System.out.println("x="+PointX_Data);
                    System.out.println("y="+PointY_Data);
                    ECPoint EncryptPoint=new ECPoint(new BigInteger(PointX_Data), new BigInteger(PointY_Data));
                    System.out.println("("+EncryptPoint.getAffineX()+","+EncryptPoint.getAffineY()+")");
                    SecureRandom rnd=new SecureRandom();
                    System.out.println("");
                    BigInteger RandomInteger=new BigInteger(200,rnd);//产生一个随机数r (r<n)随机数
                    System.out.println("随机数:"+RandomInteger);
                    System.out.println("");
                    ECPoint C1=multiply(RandomInteger,P);
                    ECPoint C2=add(EncryptPoint,multiply(RandomInteger,Q));
                    System.out.println("C1 x="+C1.getAffineX());
                    System.out.println("C1 y="+C1.getAffineY());
                    System.out.println("("+C1.getAffineX()+","+C1.getAffineY()+")");
                    System.out.println("");
                    System.out.println("C2 x="+C2.getAffineX());
                    System.out.println("C2 y="+C2.getAffineY());
                    System.out.println("("+C2.getAffineX()+","+C2.getAffineY()+")");

                    break;
                case 2:
                    System.out.println("decrypt data:");
                    System.out.println("C1 x=");
                    Scanner input3 = new Scanner (System.in);
                    String C1_x = input3.next();

                    System.out.println("C1 y=");
                    Scanner input4 = new Scanner (System.in);
                    String C1_y = input4.next();

                    System.out.println("C2 x=");
                    Scanner input5 = new Scanner (System.in);
                    String C2_x = input5.next();

                    System.out.println("C2 y=");
                    Scanner input6 = new Scanner (System.in);
                    String C2_y = input6.next();
                    //M=C2-kC1
                    ECPoint DecryptPoint_C1=new ECPoint(new BigInteger(C1_x), new BigInteger(C1_y));
                    ECPoint DecryptPoint_C2=new ECPoint(new BigInteger(C2_x), new BigInteger(C2_y));
                    DecryptPoint_C1=multiply(n,DecryptPoint_C1);
                    ECPoint DecryptPoint_MinusC1=new ECPoint(new BigInteger(String.valueOf(DecryptPoint_C1.getAffineX())), new BigInteger("-"+(DecryptPoint_C1.getAffineY())));
                    ECPoint DecryptDataPoint=add(DecryptPoint_C2,DecryptPoint_MinusC1);
                    System.out.println("C2 x="+DecryptDataPoint.getAffineX());
                    System.out.println("C2 y="+DecryptDataPoint.getAffineY());
                    System.out.println("("+DecryptDataPoint.getAffineX()+","+DecryptDataPoint.getAffineY()+")");
                    BigInteger DecryptData = new BigInteger(String.valueOf(DecryptDataPoint.getAffineX())+DecryptDataPoint.getAffineY());
                    String binaryString = DecryptData.toString(2);
                    binaryString = binaryString.substring(1, binaryString.length());
                    String decryptMessage="";
                    for(int i=0;i<=binaryString.length()-8;i+=8 ){
                        decryptMessage+=(char)Integer.parseInt(binaryString.substring(i, i+8),2);
                    }
                    System.out.println("decrypt data  = "+decryptMessage);
                    break;
            }
        }
    }
    public static String toBinary(String str, int bits) {
        String result = "";
        String tmpStr;
        int tmpInt;
        char[] messChar = str.toCharArray();

        for (int i = 0; i < messChar.length; i++) {
            tmpStr = Integer.toBinaryString(messChar[i]);
            tmpInt = tmpStr.length();
            if(tmpInt != bits) {
                tmpInt = bits - tmpInt;
                if (tmpInt == bits) {
                    result += tmpStr;
                } else if (tmpInt > 0) {
                    for (int j = 0; j < tmpInt; j++) {
                        result += "0";
                    }
                    result += tmpStr;
                } else {
                    System.err.println("argument 'bits' is too small");
                }
            } else {
                result += tmpStr;
            }
            //result += " ";
        }
        return result;
    }
    public static ECPoint multiply(BigInteger n, ECPoint P)
    {
        ECPoint R=Inf;
        while (!n.equals(ZERO))
        {
            if(n.testBit(0))// n%2==1
                R=add(R,P);
            n=n.shiftRight(1);// n=n>>1
            P=add(P,P);
        }
        return R;
    }
    public static ECPoint add(ECPoint P, ECPoint Q)
    {//P+Q
        BigInteger m;// 穿越P,Q直線斜率
        if (P.equals(Inf)){
            return Q;
        }
        else if (Q.equals(Inf)){
            return P;
        }
        else if (P.equals(Q) && P.getAffineY().equals(ZERO)){
            return Inf;
        }
        else if (P.getAffineX().equals(Q.getAffineX()) && !P.getAffineY().equals(Q.getAffineY())){
            return Inf;
        }
        else if (P.equals(Q)){
            //m=(3*x(P)^2+a)*(2*y(P))^(-1)%p
            m=THREE.multiply(P.getAffineX().modPow(TWO,p)).add(a).multiply(TWO.multiply(P.getAffineY()).modInverse(p)).mod(p);
        }
        else{
            //m=(y(Q)-y(P))*(x(Q)-x(P))^(-1)%p
            m=Q.getAffineY().subtract(P.getAffineY()).multiply(Q.getAffineX().subtract(P.getAffineX()).modInverse(p)).mod(p);
        }
        //x(R)=m^2-x(P)-x(Q)%p
        BigInteger xR=m.modPow(TWO,p).subtract(P.getAffineX()).subtract(Q.getAffineX()).mod(p);

        //y(R)=m*(x(P)-x(R))-y(P)%p
        BigInteger yR=m.multiply(P.getAffineX().subtract(xR)).subtract(P.getAffineY()).mod(p);
        return new ECPoint(xR,yR);
    }
}

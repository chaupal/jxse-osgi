/*
 * Copyright (c) 2015 Nebarti, LLC. All rights reserved.
 *
 * based on work from DawningStreams, Inc. 2010
 *
 */

package net.chaupal.practical.jxta.examples.security;

import net.chaupal.practical.jxta.tools.Tools;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import net.jxta.impl.membership.pse.PSEUtils;

public class ExportingAndImportingPrivateKeysAndX509Certificates {
    
    public static final String Name = "Example_600";

    public static void main(String[] args) {
        
        try {
        
            // Certificate and Private Key
            X509Certificate theX509Certificate;
            PrivateKey thePrivateKey;

            // Initialization
            PSEUtils.IssuerInfo forPSE = PSEUtils.genCert(Name, null);
        
            theX509Certificate = forPSE.cert;
            thePrivateKey = forPSE.issuerPkey;
        
            // String encoded certificate & private key
            String base64_X509Certificate = PSEUtils.base64Encode(theX509Certificate.getEncoded());
            
            String base64_ThePrivateKey = PSEUtils.base64Encode(thePrivateKey.getEncoded());
                    
            // Printing Results
            System.out.println("------------------------------");
            System.out.println(base64_X509Certificate);
            System.out.println("------------------------------");
            System.out.println(base64_ThePrivateKey);
            System.out.println(thePrivateKey.getFormat());
            System.out.println("------------------------------");
            
            // Recreating certificate & private key
            X509Certificate recreatedX509Certificate;
            PrivateKey recreatedPrivateKey;
            
            // Recreating the X509 certificate
            byte[] temp = PSEUtils.base64Decode(new StringReader(base64_X509Certificate));

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
            recreatedX509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(temp));
            
            System.out.println("-X509-Original-------------");
            System.out.println(theX509Certificate.toString());
            System.out.println("-X509-Recreated------------");
            System.out.println(recreatedX509Certificate.toString());
            System.out.println("---------------------------");
            
            // Restoring the private key
            temp = PSEUtils.base64Decode(new StringReader(base64_ThePrivateKey));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec MyPKCS8EncodedKeySpec = new PKCS8EncodedKeySpec(temp);
            recreatedPrivateKey = keyFactory.generatePrivate(MyPKCS8EncodedKeySpec);
            
            System.out.println("-Private-Key-Original-------------");
            System.out.println(thePrivateKey.toString());
            System.out.println("-Private-Key-Recreated------------");
            System.out.println(recreatedPrivateKey.toString());
            System.out.println("----------------------------------");

        } catch (Exception Ex) {
            
            Tools.PopErrorMessage(Name, Ex.toString());
            
        }

    }

}
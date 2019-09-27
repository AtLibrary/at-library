package ru.bcs.at.library.core.steps;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class BizTalkSteps {

    public static void main(String[] argv) throws Exception {
        createCopyOnNetwork("global.bcs", "molokotinpa", "L!67184392!l", "/home/molokotinpa/temp/auth.txt", "file://172.17.123.1/temp/auth2.txt");
    }

    public static boolean createCopyOnNetwork(String domain, String username, String password, String src, String dest) throws Exception
    {
        //FileInputStream in = null;
        SmbFileOutputStream out = null;
        BufferedInputStream inBuf = null;
        try{
            //jcifs.Config.setProperty("jcifs.smb.client.disablePlainTextPasswords","true");
            NtlmPasswordAuthentication authentication = new NtlmPasswordAuthentication(domain,username,password); // replace with actual values
            SmbFile file = new SmbFile(dest, authentication); // note the different format
            //in = new FileInputStream(src);
            inBuf = new BufferedInputStream(new FileInputStream(src));
            out = (SmbFileOutputStream)file.getOutputStream();
            byte[] buf = new byte[5242880];
            int len;
            while ((len = inBuf.read(buf)) > 0){
                out.write(buf, 0, len);
            }
        }
        catch(Exception ex)
        {
            throw ex;
        }
        finally{
            try{
                if(inBuf!=null)
                    inBuf.close();
                if(out!=null)
                    out.close();
            }
            catch(Exception ex)
            {}
        }
        System.out.print("\n File copied to destination");
        return true;
    }

}

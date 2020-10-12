package com.kttdevelopment.rexedia.utility;

import com.kttdevelopment.core.tests.TestUtil;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class FileUtilityTests {

    @Test
    public void testFileName(){
        // test with ext
        Assert.assertEquals("filename", FileUtility.getFileName("filename.ext"));
        // test 'fake' ext
        Assert.assertEquals("filename.no", FileUtility.getFileName("filename.no.ext"));
        // test no ext
        Assert.assertEquals("filename", FileUtility.getFileName("filename"));
    }

    @Test
    public void testExtension(){
        // test with ext
        Assert.assertEquals("ext",FileUtility.getFileExtension("filename.ext"));
        // test 'fake' ext
        Assert.assertEquals("ext",FileUtility.getFileExtension("filename.no.ext"));
        // test no ext
        Assert.assertEquals("",FileUtility.getFileExtension("filename"));
    }

    @Test
    public void testUnblockedFile(){
        // test unblocked
        final String
            name = String.valueOf(System.currentTimeMillis()),
            ext = "txt";
        final File resources = new File("src/test/resources");
        File file = new File(resources,name + '.' + ext);
        Assert.assertEquals(file,FileUtility.getFreeFile(file));
        // test blocked
        TestUtil.createTestFile(file);
        final File file2;
        Assert.assertEquals(file2 = new File(resources,name + " (0)." + ext), FileUtility.getFreeFile(file));
        // test double blocked
        TestUtil.createTestFile(file2);
        Assert.assertEquals(new File(resources,name + " (1)." + ext), FileUtility.getFreeFile(file));
    }

}

package com.kttdevelopment.rexedia.utility;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

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

    @Rule
    public final TemporaryFolder dir = new TemporaryFolder(new File("."));

    @Test
    public void testUnblockedFile() throws IOException{
        // test unblocked
        final String
            name = String.valueOf(System.currentTimeMillis()),
            ext = "txt";
        File file = new File(dir.getRoot(),name + '.' + ext);
        Assert.assertEquals(file,FileUtility.getFreeFile(file));
        // test blocked
        dir.newFile(file.getName());
        Assert.assertEquals(new File(dir.getRoot(),name + " (0)." + ext), FileUtility.getFreeFile(file));
        // test double blocked
        dir.newFile(name + " (0)." + ext);
        Assert.assertEquals(new File(dir.getRoot(),name + " (1)." + ext), FileUtility.getFreeFile(file));
    }

}

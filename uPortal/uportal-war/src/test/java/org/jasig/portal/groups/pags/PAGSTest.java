/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portal.groups.pags;

import java.util.Random;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.groups.pags.testers.IntegerEQTester;
import org.jasig.portal.groups.pags.testers.IntegerGETester;
import org.jasig.portal.groups.pags.testers.IntegerGTTester;
import org.jasig.portal.groups.pags.testers.IntegerLETester;
import org.jasig.portal.groups.pags.testers.IntegerLTTester;
import org.jasig.portal.groups.pags.testers.RegexTester;
import org.jasig.portal.groups.pags.testers.StringEqualsIgnoreCaseTester;
import org.jasig.portal.groups.pags.testers.StringEqualsTester;
import org.jasig.portal.groups.pags.testers.ValueExistsTester;
import org.jasig.portal.groups.pags.testers.ValueMissingTester;
import org.jasig.portal.security.IPerson;
import org.jasig.portal.security.provider.PersonImpl;

/**
 * Tests the PAGS testers.  
 * @author: Dan Ellentuck
 */
public class PAGSTest extends TestCase {
    private static final Log LOG = LogFactory.getLog(PAGSTest.class);
    
    private static Class IPERSON_CLASS;
    private static String CR = "\n";
    private Random random = new Random();
    private String[] attributeNames = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten"};
    private String[] randomStrings;
    private String[] randomIntStrings;
    private String INTEGER_MAX = Integer.MAX_VALUE + "";
    private String INTEGER_MIN = Integer.MIN_VALUE + "";
    private String key1 = null;
    private String key2 = null;
    private String key3 = null;
    private String missingKey = null;
    private Vector intStringVector = null;
    private Vector stringVector = null;
    private int testVectorSize = 10;
    
    
/**
 * PAGSTester constructor.
 */
public PAGSTest(String name) {
    super(name);
}


/**
 * @return org.jasig.portal.groups.IEntity
 */
private IPerson getIPerson(String key) 
{
    IPerson ip = new PersonImpl();
    ip.setAttribute(IPerson.USERNAME, key);
    return  ip;
}
/**
*  @return java.lang.String
 * @param length int
 */
private String getRandomString(java.util.Random r, int length) {

    char[] chars = new char[length];

    for(int i=0; i<length; i++)
    {
        int diff = ( r.nextInt(25) );
        int charValue =  (int)'A' + diff;
        chars[i] = (char) charValue;
    }
    return new String(chars);
}
/**
 * @param msg java.lang.String
 */
private static void print(String msg)
{
    LOG.debug(msg);
}
/**
 */
protected void setUp()
{
    try {
        if ( IPERSON_CLASS == null )
            { IPERSON_CLASS = Class.forName("org.jasig.portal.security.IPerson"); }

        randomStrings = new String[100];
        for (int idx=0; idx<100; idx++)
        {
            int length = random.nextInt(10) + 1;  // between 1 and 11.
            randomStrings[idx] = getRandomString(random, length); 
        }
        
        randomIntStrings = new String[100];
        for (int idx=0; idx<100; idx++)
        {
            int randomInt = random.nextInt(99999) + 1;  // between 1 and 100000.
            randomIntStrings[idx] = randomInt + ""; 
        }

        intStringVector = new Vector();
        for (int idx=2; idx<testVectorSize; idx++)
            { intStringVector.add(randomIntStrings[idx]); }
            
        stringVector = new Vector();
        for (int idx=2; idx<testVectorSize; idx++)
            { stringVector.add(randomStrings[idx]); }
 
        key1 = attributeNames[0];
        key2 = attributeNames[1];
        key3 = attributeNames[2];
        missingKey = attributeNames[3];
            
    }
    catch (Exception ex) { print("GroupsTester.setUp(): " + ex.getMessage());}
 }
/**
 */
protected void tearDown()
{
}
/**
 */
public void testStringEqualsTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testStringEqualsTester() *****" + CR);
    String msg = null;
   
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
        
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomStrings[0]);
    newPerson.setAttribute(key2, randomStrings[1]);
    newPerson.setAttribute(key3, stringVector);

    msg = "Testing IPerson " + newPerson;
    print(msg);

    IPersonTester tester1 = new StringEqualsTester(key1, randomStrings[0]);
    IPersonTester tester2 = new StringEqualsTester(key2, randomStrings[1]);
    IPersonTester tester3 = new StringEqualsTester(key3, randomStrings[testVectorSize - 1]);
    IPersonTester tester4 = new StringEqualsTester(key3, randomStrings[0] + "x");
    IPersonTester tester5 = new StringEqualsTester(missingKey, randomStrings[0]);

    msg = "Testing " + tester1;
    assertTrue(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertTrue(msg, tester3.test(newPerson));
    msg = "Testing " + tester4;
    assertFalse(msg, tester4.test(newPerson));
    msg = "Testing " + tester5;
    assertFalse(msg, tester5.test(newPerson));
 

    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testStringEqualsTester() *****" + CR);

}
public void testStringEqualsIgnoreCaseTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testStringEqualsIgnoreCaseTester() *****" + CR);
    String msg = null;
   
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
        
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomStrings[0]);
    newPerson.setAttribute(key2, randomStrings[1].toLowerCase());
 
    msg = "Testing IPerson " + newPerson;
    print(msg);

    String testKey = randomStrings[0].toLowerCase();
    IPersonTester tester1 = new StringEqualsIgnoreCaseTester(key1, testKey);
    IPersonTester tester2 = new StringEqualsIgnoreCaseTester(key2, randomStrings[1]);

    msg = "Testing " + tester1;
    assertTrue(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
 
    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testStringEqualsIgnoreCaseTester() *****" + CR);

}
/**
 */
public void testIntegerEQTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testIntegerEQTester() *****" + CR);
    String msg = null;
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
        
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomIntStrings[0]);
    newPerson.setAttribute(key2, randomIntStrings[1]);
    newPerson.setAttribute(key3, intStringVector);


    msg = "Testing IPerson " + newPerson;
    print(msg);

    IPersonTester tester1 = new IntegerEQTester(key1, randomIntStrings[0]);
    IPersonTester tester2 = new IntegerEQTester(key2, randomIntStrings[1]);
    IPersonTester tester3 = new IntegerEQTester(key3, randomIntStrings[testVectorSize - 1]);
    IPersonTester tester4 = new IntegerEQTester(key3, "0");

    msg = "Testing " + tester1;
    assertTrue(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertTrue(msg, tester3.test(newPerson));
    msg = "Testing " + tester4;
    assertFalse(msg, tester4.test(newPerson));

    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testIntegerEQTester() *****" + CR);

}
/**
 */
public void testIntegerGTTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testIntegerGTTester() *****" + CR);
    String msg = null;
    
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
       
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomIntStrings[0]);
    newPerson.setAttribute(key2, randomIntStrings[1]);
    newPerson.setAttribute(key3, intStringVector);

    msg = "Testing IPerson " + newPerson;
    print(msg);

    // test value == attribute value.  Should answer false.
    IPersonTester tester1 = new IntegerGTTester(key1, randomIntStrings[0]);
    // test value < attribute value.  Should answer true.
    IPersonTester tester2 = new IntegerGTTester(key2, "0");
    // test value > any attribute value.  Should answer false.
    IPersonTester tester3 = new IntegerGTTester(key3, INTEGER_MAX);
    // test value < any attribute value.  Should answer true.
    IPersonTester tester4 = new IntegerGTTester(key3, "" + INTEGER_MIN);
    // attribute value not present.  Should answer false.
    IPersonTester tester5 = new IntegerGTTester(missingKey, "0");
  
    msg = "Testing " + tester1;
    assertFalse(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertFalse(msg, tester3.test(newPerson));
    msg = "Testing " + tester4;
    assertTrue(msg, tester4.test(newPerson));
    msg = "Testing " + tester5;
    assertFalse(msg, tester5.test(newPerson));

    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testIntegerGTTester() *****" + CR);

}
public void testIntegerLTTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testIntegerLTTester() *****" + CR);
    String msg = null;
   
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
       
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomIntStrings[0]);
    newPerson.setAttribute(key2, randomIntStrings[1]);
    newPerson.setAttribute(key3, intStringVector);

    msg = "Testing IPerson " + newPerson;
    print(msg);

    // test value == attribute value.  Should answer false.
    IPersonTester tester1 = new IntegerLTTester(key1, randomIntStrings[0]);
    // test value < attribute value.  Should answer false.
    IPersonTester tester2 = new IntegerLTTester(key2, "0");
    // test value > any attribute value.  Should answer true.
    IPersonTester tester3 = new IntegerLTTester(key3, "" + INTEGER_MAX);
    // test value < any attribute value.  Should answer false.
    IPersonTester tester4 = new IntegerLTTester(key3, "" + INTEGER_MIN);
    // test value > attribute value.  Should answer true.
    IPersonTester tester5 = new IntegerLTTester(key1, "" + INTEGER_MAX);
  
    msg = "Testing " + tester1;
    assertFalse(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertFalse(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertTrue(msg, tester3.test(newPerson));
    msg = "Testing " + tester4;
    assertFalse(msg, tester4.test(newPerson));
    msg = "Testing " + tester5;
    assertTrue(msg, tester5.test(newPerson));

    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testIntegerLTTester() *****" + CR);

}
public void testIntegerGETester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testIntegerGETester() *****" + CR);
    String msg = null;
    
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
       
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomIntStrings[0]);
    newPerson.setAttribute(key2, randomIntStrings[1]);
    newPerson.setAttribute(key3, intStringVector);

    msg = "Testing IPerson " + newPerson;
    print(msg);

    // test value == attribute value.  Should answer true.
    IPersonTester tester1 = new IntegerGETester(key1, randomIntStrings[0]);
    // test value < attribute value.  Should answer true.
    IPersonTester tester2 = new IntegerGETester(key2, "0");
    // test value > any attribute value.  Should answer false.
    IPersonTester tester3 = new IntegerGETester(key3, "" + INTEGER_MAX);
    // test value < any attribute value.  Should answer true.
    IPersonTester tester4 = new IntegerGETester(key3, "" + INTEGER_MIN);
    // test value > attribute value.  Should answer false.
    IPersonTester tester5 = new IntegerGETester(key1, "" + INTEGER_MAX);
  
    msg = "Testing " + tester1;
    assertTrue(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertFalse(msg, tester3.test(newPerson));
    msg = "Testing " + tester4;
    assertTrue(msg, tester4.test(newPerson));
    msg = "Testing " + tester5;
    assertFalse(msg, tester5.test(newPerson));

    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testIntegerGETester() *****" + CR);

}
public void testIntegerLETester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testIntegerLETester() *****" + CR);
    String msg = null;
   
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
    
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomIntStrings[0]);
    newPerson.setAttribute(key2, randomIntStrings[1]);
    newPerson.setAttribute(key3, intStringVector);


    // test value == attribute value.  Should answer true.
    IPersonTester tester1 = new IntegerLETester(key1, randomIntStrings[0]);
    // test value < attribute value.  Should answer false.
    IPersonTester tester2 = new IntegerLETester(key2, "0");
    // test value > any attribute value.  Should answer true.
    IPersonTester tester3 = new IntegerLETester(key3, "" + INTEGER_MAX);
    // test value < any attribute value.  Should answer false.
    IPersonTester tester4 = new IntegerLETester(key3, "" + INTEGER_MIN);
    // test value > attribute value.  Should answer true.
    IPersonTester tester5 = new IntegerLETester(key1, "" + INTEGER_MAX);
    
    msg = "Testing IPerson " + newPerson;
    print(msg);
  
    msg = "Testing " + tester1;
    assertTrue(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertFalse(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertTrue(msg, tester3.test(newPerson));
    msg = "Testing " + tester4;
    assertFalse(msg, tester4.test(newPerson));
    msg = "Testing " + tester5;
    assertTrue(msg, tester5.test(newPerson));

    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testIntegerLETester() *****" + CR);

}
public void testRegexTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testRegexTester() *****" + CR);
    String msg = null;
   
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
        
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomStrings[0]);
    newPerson.setAttribute(key2, randomStrings[1]);
    newPerson.setAttribute(key3, stringVector);
    
    msg = "Testing IPerson " + newPerson;
    print(msg);

    //  test equals attribute.  Should return true.
    IPersonTester tester1 = new RegexTester(key1, randomStrings[0]);
    
    // test and attribute begin with same String.  Should return true.
    String testValue2 = randomStrings[0].substring(0,1) + ".*";
    IPersonTester tester2 = new RegexTester(key1, testValue2);
    
    // test and attribute begin with different String.  Should return false.
    String testValue3 = "^[0]";
    IPersonTester tester3 = new RegexTester(key1, testValue3);
    
    //  test and attribute end with same String.  Should return true.
     String testValue4 = ".*" + randomStrings[1].substring(randomStrings[1].length() - 1) + "$";
     IPersonTester tester4 = new RegexTester(key2, testValue4);
    
     // test and attribute end with different String.  Should return false.
     String testValue5 = ".*9$";
     IPersonTester tester5 = new RegexTester(key2, testValue5);
     
     // test value is same as last String in Vector.  Should return true.
     String testValue6 = "^" + ((String)stringVector.lastElement()) + "$";
     IPersonTester tester6 = new RegexTester(key3, testValue6);
     
    msg = "Testing " + tester1;
    assertTrue(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertFalse(msg, tester3.test(newPerson));
    msg = "Testing " + tester4;
    assertTrue(msg, tester4.test(newPerson));
    msg = "Testing " + tester5;
    assertFalse(msg, tester5.test(newPerson));
    msg = "Testing " + tester6;
    assertTrue(msg, tester6.test(newPerson));
 

    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testRegexTester() *****" + CR);

}

public void testValueExistsTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testValueExistsTester() *****" + CR);
    String msg = null;
   
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
        
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomStrings[0]);
    newPerson.setAttribute(key2, randomStrings[1].toLowerCase());
 
    msg = "Testing IPerson " + newPerson;
    print(msg);

    String testKey = randomStrings[0].toLowerCase();
    // These tests should pass since the IPerson attribute exists.
    IPersonTester tester1 = new ValueExistsTester(key1, testKey);
    IPersonTester tester2 = new ValueExistsTester(key2, null);
    // This test should fail; the IPerson does not have a value for key3.
    IPersonTester tester3 = new ValueExistsTester(key3, new String());
    
    msg = "Testing " + tester1;
    assertTrue(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertFalse(msg, tester3.test(newPerson));
 
    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testValueExistsTester() *****" + CR);
}
public void testValueMissingTester() throws Exception
{
    print(CR + "***** ENTERING PAGSTester.testValueMissingTester() *****" + CR);
    String msg = null;
   
    msg = "Creating a new IPerson";
    print(msg);
    IPerson newPerson = getIPerson("de3");
    assertNotNull(msg, newPerson);
        
    print("Adding attributes to IPerson.");
    newPerson.setAttribute(key1, randomStrings[0]);
    newPerson.setAttribute(key2, new String());
 
    msg = "Testing IPerson " + newPerson;
    print(msg);

    String testKey = randomStrings[0].toLowerCase();
    // This test should fail since the IPerson attribute exists.
    IPersonTester tester1 = new ValueMissingTester(key1, randomStrings[0]);
    // This test should pass; the attribute is blank.
    IPersonTester tester2 = new ValueMissingTester(key2, testKey);
    // This test should pass; the IPerson does not have a value for key3.
    IPersonTester tester3 = new ValueMissingTester(key3, testKey);
    
    msg = "Testing " + tester1;
    assertFalse(msg, tester1.test(newPerson));
    msg = "Testing " + tester2;
    assertTrue(msg, tester2.test(newPerson));
    msg = "Testing " + tester3;
    assertTrue(msg, tester3.test(newPerson));
 
    print("Success!");
    print(CR + "***** LEAVING PAGSTester.testValueMissingTester() *****" + CR);

}

}
package com.cxwudi.niconico_videodownloader;

import com.cxwudi.niconico_videodownloader.entity.Vsong;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class VsongTest {

    @Test
    void testVsongAtt() {
        Vsong vsong = new Vsong("sm23379461", "My PV");
        String a = vsong.getSubDir();
        a = "asdasd";
        vsong.setSubDir(a);
        assertTrue(true);
    }

    @Test
    void testString() {
        String a, b;
        a = b = new String("aaa");
        b = "bbb";
        System.out.println(a);
        String c = new String("aaa");
        assertTrue( c != a);
        assertTrue (c != "aaa");
        assertTrue(true);

    }

    @Test
    void testVsong() {
        java.util.TreeSet<Vsong> set = new java.util.TreeSet<>(), set2 = new java.util.TreeSet<>(), pointer = null;
        System.out.println("add first song: " + set.add(new Vsong("sm27384957", "40mP MV")));
        System.out.println("add duplicated song: " + set.add(new Vsong("sm27384957", "40mP duplicate")));
        System.out.println("add second song: " + set.add(new Vsong("sm30772034", "LamazeP MV")));
        System.out.println("add third song: " + set.add(new Vsong("sm25446788", "MARETU MV")));
        System.out.println("the set is \n" + set);
        set2.add(new Vsong("sm27384957", "40mP MV"));
        set2.add(new Vsong("sm29987635", "Hachi PV"));
        set2.addAll(set);
        System.out.println("merge two set that contains same songs with different status: \n" + set2);
        pointer = set2;
        pointer.add(new Vsong("sm29882986", "Deco*27 PV"));
        System.out.println("pointer and reference theory applied in Java: \n" + set2);
        assertTrue(true);

    }

}

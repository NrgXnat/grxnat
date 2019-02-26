package org.nrg.xnat.util.test

import org.nrg.xnat.util.ListUtils
import org.testng.annotations.Test

import static org.testng.AssertJUnit.assertEquals

class TestListUtils {

    @Test
    void testCommonCase() {
        final List<Integer> from = [1, 3, 5, 7]
        final List<Integer> into = [2, 4, 6, 8]
        ListUtils.copyInto(from, into)
        assertEquals([1, 3, 5, 7], into)
    }

    @Test
    void testEmptyTarget() {
        final List<String> from = ['a', 'b', 'c']
        final List<String> into = []
        ListUtils.copyInto(from, into)
        assertEquals(['a', 'b', 'c'], into)
    }

    @Test
    void testSameList() {
        final List<String> from = ['Claudine', 'Sarikah', 'Dali', 'Conway', 'Isabelle']
        final List<String> into = ['Claudine', 'Sarikah', 'Dali', 'Conway', 'Isabelle']
        ListUtils.copyInto(from, into)
        assertEquals(['Claudine', 'Sarikah', 'Dali', 'Conway', 'Isabelle'], into)
    }

    @Test
    void testSameListObject() {
        final List<Integer> from = [1, 2, 3]
        final List<Integer> into = from
        ListUtils.copyInto(from, into)
        assertEquals([1, 2, 3], into)
    }

}

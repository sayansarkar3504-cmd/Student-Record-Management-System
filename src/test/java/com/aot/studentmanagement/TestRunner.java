package com.aot.studentmanagement;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Lightweight test runner to execute and report unit tests in environments
 * without an external Maven test plugin.
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  PCC-CS503 STUDENT RECORD MANAGEMENT SYSTEM - TEST SUITE   ");
        System.out.println("  Institution: Academy of Technology | CSE Department       ");
        System.out.println("============================================================\n");

        Class<?> testClass = StudentManagementTest.class;
        List<Method> testMethods = new ArrayList<>();

        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                testMethods.add(m);
            }
        }

        // Sort by @Order if present
        testMethods.sort(Comparator.comparingInt(m -> {
            if (m.isAnnotationPresent(Order.class)) {
                return m.getAnnotation(Order.class).value();
            }
            return 999;
        }));

        int passed = 0;
        int failed = 0;
        int total = testMethods.size();

        for (Method m : testMethods) {
            String name = m.getName();
            if (m.isAnnotationPresent(DisplayName.class)) {
                name = m.getAnnotation(DisplayName.class).value();
            }

            try {
                Object instance = testClass.getDeclaredConstructor().newInstance();
                m.setAccessible(true);
                m.invoke(instance);
                System.out.printf("  [PASS] ✓ %s%n", name);
                passed++;
            } catch (Throwable t) {
                System.out.printf("  [FAIL] ✗ %s%n", name);
                Throwable cause = (t.getCause() != null) ? t.getCause() : t;
                System.out.printf("         Error: %s%n", cause.getMessage());
                failed++;
            }
        }

        System.out.println("\n------------------------------------------------------------");
        System.out.printf("  TEST EXECUTION SUMMARY: %d Total | %d Passed | %d Failed%n", total, passed, failed);
        System.out.println("------------------------------------------------------------");

        if (failed == 0) {
            System.out.println("  >>> ALL UNIT TESTS PASSED SUCCESSFULLY! <<<\n");
        } else {
            System.err.printf("  >>> %d TESTS FAILED! <<<%n%n", failed);
            System.exit(1);
        }
    }
}


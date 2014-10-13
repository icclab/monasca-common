package com.hpcloud.mon.common.model.alarm;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.hpcloud.mon.common.model.metric.MetricDefinition;

@Test
public class AlarmExpressionTest {
    public void shouldParseExpression() {
        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3 and hpcs.compute{instance_id=5,metric_name=system_log,device=1} REGEXP \"^[a-z]$\"");
        List<AlarmSubExpression> alarms = expr.getSubExpressions();

        AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("device", "1")
                        .build()), AlarmOperator.GT, 5, 1, 3);
        AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .build()), AlarmOperator.LT, 4, 2, 3);
        AlarmSubExpression expected3 = new AlarmSubExpression(null,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "system_log")
                        .put("device", "1")
                        .build()), AlarmOperator.REGEXP, "^[a-z]$", AlarmSubExpression.DEFAULT_PERIOD, AlarmSubExpression.DEFAULT_PERIODS);

        assertEquals(alarms.get(0), expected1);
        assertEquals(alarms.get(1), expected2);
        assertEquals(alarms.get(2), expected3);
    }

    public void shouldParseString() {


        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1, url=\"https://www.google.com/?startpage=3&happygoing\"}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem, specialchars=\"!@#$%^&*()~<>{}[],.\"}, 2) < 4 times 3");
        List<AlarmSubExpression> alarms = expr.getSubExpressions();

        AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("url", "\"https://www.google.com/?startpage=3&happygoing\"")
                        .put("device", "1")
                        .build()), AlarmOperator.GT, 5, 1, 3);

        AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .put("specialchars", "\"!@#$%^&*()~<>{}[],.\"")
                        .build()), AlarmOperator.LT, 4, 2, 3);

        assertEquals(alarms.get(0), expected1);
        assertEquals(alarms.get(1), expected2);
    }

    public void shouldParseComplexWithoutQuotes() {


        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1, url=https%3A%2F%2Fwww.google.com%2F%3Fstartpage%3D3%26happygoing}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem, specialchars=a!@#/\\$%^*~}, 2) < 4 times 3");
        List<AlarmSubExpression> alarms = expr.getSubExpressions();


        AlarmExpression containsDirectories = new AlarmExpression("avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1,global=$_globalVariable,special=__useSparingly,dos=\\system32\\, windows=C:\\system32\\}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem,$globalVariable=global,__useSparingly=special,unix=/opt/vertica/bin/}, 2) < 4 times 3");
        List<AlarmSubExpression> alarmsContainsDirectories = containsDirectories.getSubExpressions();

        AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("url", "https%3A%2F%2Fwww.google.com%2F%3Fstartpage%3D3%26happygoing")
                        .put("device", "1")
                        .build()), AlarmOperator.GT, 5, 1, 3);

        AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .put("specialchars", "a!@#/\\$%^*~")
                        .build()), AlarmOperator.LT, 4, 2, 3);

        AlarmSubExpression expected3 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("device", "1")
                        .put("global", "$_globalVariable")
                        .put("special", "__useSparingly")
                        .put("dos", "\\system32\\")
                        .put("windows", "C:\\system32\\")
                        .build()), AlarmOperator.GT, 5, 1, 3);

        AlarmSubExpression expected4 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .put("$globalVariable", "global")
                        .put("__useSparingly", "special")
                        .put("unix", "/opt/vertica/bin/")
                        .build()), AlarmOperator.LT, 4, 2, 3);
        assertEquals(alarms.get(0), expected1);
        assertEquals(alarms.get(1), expected2);
        assertEquals(alarmsContainsDirectories.get(0), expected3);
        assertEquals(alarmsContainsDirectories.get(1), expected4);
    }

    public void shouldParseExpressionWithoutType() {
        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
        List<AlarmSubExpression> alarms = expr.getSubExpressions();

        AlarmSubExpression expected1 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("instance_id", "5")
                        .put("metric_name", "cpu")
                        .put("device", "1")
                        .build()), AlarmOperator.GT, 5, 1, 3);
        AlarmSubExpression expected2 = new AlarmSubExpression(AggregateFunction.AVG,
                new MetricDefinition("hpcs.compute", ImmutableMap.<String, String>builder()
                        .put("flavor_id", "3")
                        .put("metric_name", "mem")
                        .build()), AlarmOperator.LT, 4, 2, 3);

        assertEquals(alarms.get(0), expected1);
        assertEquals(alarms.get(1), expected2);
    }

    public void shouldEvaluateExpression() {
        AlarmExpression expr = new AlarmExpression(
                "sum(hpcs.compute{instance_id=5,metric_name=disk}, 1) > 33 or (avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3)");
        List<AlarmSubExpression> alarms = expr.getSubExpressions();

        AlarmSubExpression alarm1 = alarms.get(0);
        AlarmSubExpression alarm2 = alarms.get(1);
        AlarmSubExpression alarm3 = alarms.get(2);

        assertTrue(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
                .put(alarm1, true)
                .put(alarm2, false)
                .put(alarm3, false)
                .build()));

        assertTrue(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
                .put(alarm1, false)
                .put(alarm2, true)
                .put(alarm3, true)
                .build()));

        assertFalse(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
                .put(alarm1, false)
                .put(alarm2, false)
                .put(alarm3, true)
                .build()));

        assertFalse(expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
                .put(alarm1, false)
                .put(alarm2, true)
                .put(alarm3, false)
                .build()));
    }

    public void shouldDefaultPeriodAndPeriods() {
        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=1}) > 5");
        AlarmSubExpression alarm = expr.getSubExpressions().get(0);
        assertEquals(alarm.getPeriod(), 60);
        assertEquals(alarm.getPeriods(), 1);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowOnEvaluateInvalidSubExpressions() {
        AlarmExpression expr = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=2}, 1) > 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
        expr.evaluate(ImmutableMap.<AlarmSubExpression, Boolean>builder()
                .put(
                        new AlarmSubExpression(AggregateFunction.AVG, new MetricDefinition("hpcs.compute",
                                ImmutableMap.<String, String>builder()
                                        .put("flavor_id", "3")
                                        .put("metric_name", "mem")
                                        .build()), AlarmOperator.LT, 4, 2, 3), true)
                .build());
    }

    public void shouldGetAlarmExpressionTree() {
        Object expr = AlarmExpression.of(
                "(avg(foo) > 1 and avg(bar) < 2 and avg(baz) > 3) or (avg(foo) > 4 and avg(bar) < 5 and avg(baz) > 6)")
                .getExpressionTree();
        assertEquals(
                expr.toString(),
                "((avg(foo) > 1.0 AND avg(bar) < 2.0 AND avg(baz) > 3.0) OR (avg(foo) > 4.0 AND avg(bar) < 5.0 AND avg(baz) > 6.0))");

        expr = AlarmExpression.of(
                "(avg(foo) > 1 and (avg(bar) < 2 or avg(baz) > 3)) and (avg(foo) > 4 or avg(bar) < 5 or avg(baz) > 6)")
                .getExpressionTree();
        assertEquals(
                expr.toString(),
                "(avg(foo) > 1.0 AND (avg(bar) < 2.0 OR avg(baz) > 3.0) AND (avg(foo) > 4.0 OR avg(bar) < 5.0 OR avg(baz) > 6.0))");
    }

    @Test(enabled = false)
    public void testExpressionEquality() {
        AlarmExpression expr1 = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=a}, 1) lt 5 times 3 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
        AlarmExpression expr2 = new AlarmExpression(
                "avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) gt 3  times 3 && avg(hpcs.compute{instance_id=5,metric_name=cpu,device=a}, 1) lt 5 times 3");
        assertEquals(expr1, expr2);

        AlarmExpression expr3 = new AlarmExpression(
                "avg(hpcs.compute{instance_id=5,metric_name=cpu,device=a}, 1) lt 5 times 444 and avg(hpcs.compute{flavor_id=3,metric_name=mem}, 2) < 4 times 3");
        assertNotEquals(expr1, expr3);
    }
}

/**
 *
 * Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Administrative Contact: dnet-project-office@cs.kuleuven.be
 * Technical Contact: bart.vanbrabant@cs.kuleuven.be
 */
package gcroes.thesis.docproc.jee.monitoring;

import static gcroes.thesis.docproc.jee.config.Config.cfg;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.MBeanServer;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;

public class Metrics {

    private static MetricRegistry registery;

    private static Logger logger = Logger.getLogger(Metrics.class.getName());

    static synchronized void init() {
        if (registery != null) {
            return;
        }

        try {
            registery = new MetricRegistry();
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

            registery.registerAll(new BufferPoolMetricSet(mbs));
            registery.register("gc", new GarbageCollectorMetricSet());
            registery.register(
                    "jvm.memory",
                    new MemoryUsageGaugeSet(
                            ManagementFactory.getMemoryMXBean(), Collections
                                    .<MemoryPoolMXBean> emptyList()));

            if (cfg().getProperty("taskworker.metrics.console", false)) {
                ConsoleReporter
                        .forRegistry(registery)
                        .build()
                        .start(cfg().getProperty(
                                "taskworker.metrics.console.interval", 30),
                                TimeUnit.SECONDS);
            }

            if (cfg().getProperty("taskworker.metrics.graphite", false)) {
                final Graphite graphite = new Graphite(new InetSocketAddress(
                        cfg().getProperty("taskworker.metrics.graphite.host",
                                "localhost"), Integer.parseInt(cfg()
                                .getProperty(
                                        "taskworker.metrics.graphite.port",
                                        "2003"))));

                final GraphiteReporter reporter = GraphiteReporter
                        .forRegistry(registery)
                        .prefixedWith(
                                cfg().getProperty(
                                        "taskworker.metrics.graphite.prefix",
                                        "localhost"))
                        .convertRatesTo(TimeUnit.SECONDS)
                        .convertDurationsTo(TimeUnit.MILLISECONDS)
                        .filter(MetricFilter.ALL).build(graphite);

                reporter.start(
                        cfg().getProperty(
                                "taskworker.metrics.graphite.interval", 30),
                        TimeUnit.SECONDS);
                logger.info("Started metrics graphite reporter");
            }
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "metrics failed to load", e);
            throw e;
        }

    }

    /**
     * naming:
     * 
     * [type].[typeInstance|*].[temporalScope|*].[metric].[submetric]
     * 
     * @return
     */
    private static MetricRegistry getRegistry() {
        if (registery == null) {
            init();
        }

        return registery;
    }

    public static Timer timer(String name) {
        return getRegistry().timer(name);
    }

}
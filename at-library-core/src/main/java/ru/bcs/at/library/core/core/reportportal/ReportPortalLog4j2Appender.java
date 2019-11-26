///*
// * Copyright 2017 EPAM Systems
// *
// *
// * This file is part of EPAM Report Portal.
// * https://github.com/reportportal/logger-java-log4j
// *
// * Report Portal is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Report Portal is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
// */
//package ru.bcs.at.library.core.core.reportportal;
//
//import com.epam.reportportal.cucumber.Utils;
//import cucumber.api.Scenario;
//import org.apache.logging.log4j.core.Filter;
//import org.apache.logging.log4j.core.Layout;
//import org.apache.logging.log4j.core.LogEvent;
//import org.apache.logging.log4j.core.appender.AbstractAppender;
//import org.apache.logging.log4j.core.config.plugins.Plugin;
//import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
//import org.apache.logging.log4j.core.config.plugins.PluginElement;
//import org.apache.logging.log4j.core.config.plugins.PluginFactory;
//import ru.bcs.at.library.core.cucumber.api.CoreScenario;
//
//import java.io.Serializable;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * Log4j2 appender for report portal
// */
//@Plugin(name = "ReportPortalLog4j2Appender", category = "Core", elementType = "appender", printObject = true)
//public class ReportPortalLog4j2Appender extends AbstractAppender {
//    private static final List<String> INTERNAL_PACKAGES = Arrays.asList(
//            ReportPortalLog4j2Appender.class.getCanonicalName());
//
//    protected ReportPortalLog4j2Appender(String name, Filter filter, Layout<? extends Serializable> layout) {
//        super(name, filter, layout);
//    }
//
//    @PluginFactory
//    public static ReportPortalLog4j2Appender createAppender(@PluginAttribute("name") String name,
//                                                            @PluginElement("filter") Filter filter,
//                                                            @PluginElement("layout") Layout<? extends Serializable> layout) {
//
//        if (name == null) {
//            LOGGER.error("No name provided for ReportPortalLog4j2Appender");
//            return null;
//        }
//
//        if (layout == null) {
//            LOGGER.error("No layout provided for ReportPortalLog4j2Appender");
//            return null;
//        }
//        return new ReportPortalLog4j2Appender(name, filter, layout);
//    }
//
//    @Override
//    public void append(final LogEvent logEvent) {
//
//        final LogEvent event = logEvent.toImmutable();
//        if (null == event.getMessage()) {
//            return;
//        }
//        //make sure we are not logging themselves
//        if (isInternal(event.getLoggerName())) {
//            return;
//        }
//
//        Scenario scenario = CoreScenario.getInstance().getScenario();
//        if (scenario == null) {
//            return; // RP еще не проинициализирован
//        }
//        scenario.write(Utils.makeLogDef(event.getMessage().getFormattedMessage(),
//                logEvent.getLevel().name().toUpperCase()));
//
//    }
//
//    private boolean isInternal(String loggerName) {
//        if (null == loggerName) {
//            return false;
//        }
//        for (String packagePrefix : INTERNAL_PACKAGES) {
//            if (loggerName.startsWith(packagePrefix)) {
//                return true; //**QUIT**
//            }
//        }
//        return false;
//    }
//
//}

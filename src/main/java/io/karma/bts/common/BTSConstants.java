package io.karma.bts.common;

public final class BTSConstants {
    public static final String MODID = "bts";
    public static final String NAME = "Beyond The Seas";
    public static final String VERSION = "1.5.0";
    public static final String MC_VERSION = "1.12.2";
    public static final String DEPS = "required:forge@[14.23.5.2854,);";
    public static final boolean ENABLE_CL_WARNINGS = System.getProperty("io.karma.bts.debug.logCLWarnings", "false").equalsIgnoreCase("true");

    // @formatter:off
    private BTSConstants() {}
    // @formatter:on
}
